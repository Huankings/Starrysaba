package org.aussiebox.starexpress;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerMoodComponent;
import dev.doctor4t.wathe.api.task.TaskCompletionApi;
import dev.doctor4t.wathe.game.GameFunctions;
import dev.doctor4t.wathe.record.GameRecordManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.aussiebox.starexpress.config.StarryExpressServerConfig;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.aussiebox.starexpress.packet.AbilityC2SPacket;
import org.aussiebox.starexpress.record.StarryExpressReplayFormatters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarryExpress implements ModInitializer {

    public static String MOD_ID = "starexpress";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StarryExpressServerConfig CONFIG = StarryExpressServerConfig.createAndLoad();

    public static final SimpleParticleType STARSTRUCK_SPARKLE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModBlockEntities.init();
        ModBlocks.init();
        StarryExpressItems.init();

        StarryExpressCommands.init();
        StarryExpressRoles.init();
        StarryExpressModifiers.init();
        StarryExpressReplayFormatters.register();
        registerTaskCompletionApi();

        PayloadTypeRegistry.playC2S().register(AbilityC2SPacket.TYPE, AbilityC2SPacket.CODEC);

        registerPackets();
        registerEvents();
        registerParticles();
    }

    private static void registerTaskCompletionApi() {
        /*
         * 星界使者的冷却缩减只应该在任务真正完成时触发。
         * 旧 mixin 监听 setMood，因此只要心情值上涨就可能误判；
         * 现在改用 Wathe 任务完成 API，假如后续任务奖励心情的实现再次调整，也不会影响这个职业效果。
         */
        TaskCompletionApi.AFTER_TASK_COMPLETE.register(context -> {
            if (context.role() != StarryExpressRoles.STARSTRUCK) {
                return;
            }
            if (!CONFIG.starstruckConfig.taskReducesCooldown()) {
                return;
            }

            AbilityComponent ability = AbilityComponent.KEY.get(context.player());
            ability.changeCooldown(-(CONFIG.starstruckConfig.taskCooldownReduction() * 20));
        });
    }

    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(AbilityC2SPacket.TYPE, (payload, context) -> {
            AbilityComponent abilityComponent = AbilityComponent.KEY.get(context.player());
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(context.player().level());

            if (!GameFunctions.isPlayerAliveAndSurvival(context.player())) return;

            if (gameWorldComponent.isRole(context.player(), StarryExpressRoles.STARSTRUCK) && abilityComponent.cooldown <= 0) {
                abilityComponent.setCooldown(CONFIG.starstruckConfig.abilityCooldown() * 20);
                StarstruckComponent.KEY.get(context.player()).setTicks(CONFIG.starstruckConfig.abilityDuration() * 20);
                GameRecordManager.recordSkillUse(context.player(), id("starstruck_ability"), null, null);

                ServerLevel level = context.player().serverLevel();
                level.playSound(null, BlockPos.containing(context.player().position()), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0F, 1.0F);
                level.sendParticles(STARSTRUCK_SPARKLE, context.player().getX(), context.player().getY(), context.player().getZ(), 75,  0.5,  1.5,  0.5,  0.0);
            }

        });
    }

    public void registerEvents() {

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) -> {

            if (!(entity instanceof Player victim)) return InteractionResult.PASS;
            if (CONFIG.muzzlerConfig.tapeTearCheckCount() == 0) return InteractionResult.PASS;

            if (!player.getMainHandItem().is(StarryExpressItems.TAPE)) {
                SilenceComponent victimSilence = SilenceComponent.KEY.get(victim);
                if (!victimSilence.isSilenced()) return InteractionResult.PASS;
                if (SilenceComponent.KEY.get(player).isSilenced()) return InteractionResult.PASS;

                victimSilence.setTearChecks(victimSilence.getTearChecks() + 1);
                victim.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(), ModSounds.ITEM_TAPE_APPLY, SoundSource.PLAYERS, 1.0F, 2.0F);

                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer && victim instanceof net.minecraft.server.level.ServerPlayer serverVictim) {
                    GameRecordManager.event("global_event")
                            .world(serverPlayer.serverLevel())
                            .actor(serverPlayer)
                            .target(serverVictim)
                            .put("event", id("tape_removed").toString())
                            .putUuid("silencer", victimSilence.getSilencer())
                            .record();
                }

                if (victimSilence.getTearChecks() >= CONFIG.muzzlerConfig.tapeTearCheckCount()) victimSilence.setSilenced(false);

                victimSilence.sync();

                PlayerMoodComponent victimMood = PlayerMoodComponent.KEY.get(victim);

                victimMood.setMood(victimMood.getMood() - CONFIG.muzzlerConfig.tapeTearMoodChange());
                victimMood.sync();

                if (victimMood.getMood() <= 0.0F && CONFIG.muzzlerConfig.killIfCheckedAtZero()) {
                    CompoundTag extraDeathData = new CompoundTag();
                    if (victimSilence.getSilencer() != null) {
                        extraDeathData.putUUID("silencer", victimSilence.getSilencer());
                        extraDeathData.putUUID("replay_actor", victimSilence.getSilencer());
                    }
                    extraDeathData.putUUID("remover", player.getUUID());
                    GameFunctions.killPlayer(victim, true, victim.level().getPlayerByUUID(victimSilence.getSilencer()), StarryExpressConstants.SILENCED_TAPE_REMOVED_DEATH_REASON, extraDeathData);
                }

                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        });

    }

    public void registerParticles() {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, id("starstruck_sparkle"), STARSTRUCK_SPARKLE);
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, key);
    }

}
