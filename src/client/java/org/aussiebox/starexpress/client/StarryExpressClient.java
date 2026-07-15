package org.aussiebox.starexpress.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.doctor4t.wathe.api.Role;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.entity.player.Player;
import org.agmas.noellesroles.client.NoellesrolesClient;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.client.instinct.StarryInstinctHandlers;
import org.aussiebox.starexpress.client.particle.StarstruckSparkleParticle;
import org.aussiebox.starexpress.client.render.blockentity.PlushBlockEntityRenderer;
import org.aussiebox.starexpress.client.roles.starstruck.StarstruckMoodHud;
import org.aussiebox.starexpress.packet.AbilityC2SPacket;
import org.aussiebox.starexpress.packet.OpenConfigS2CPacket;
import org.lwjgl.glfw.GLFW;

public class StarryExpressClient implements ClientModInitializer {

    public static Player target;
    public static KeyMapping abilityBind;

    @Override
    public void onInitializeClient() {
        StarryInstinctHandlers.register();
        StarstruckMoodHud.register();

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.CIRCUITWEAVER_PLUSH);
        BlockEntityRenderers.register(ModBlockEntities.PLUSH, PlushBlockEntityRenderer::new);

        if (FabricLoader.getInstance().isModLoaded("noellesroles")) {
            abilityBind = NoellesrolesClient.abilityBind;
        } else {
            abilityBind = KeyBindingHelper.registerKeyBinding(new KeyMapping("key." + StarryExpress.MOD_ID + ".ability", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.wathe.keybinds"));
        }

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (abilityBind == null) return;
            if (abilityBind.isDown()) {
                client.execute(() -> {
                    if (Minecraft.getInstance().player == null) return;
                    GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(Minecraft.getInstance().player.level());

                    boolean sendPacket = false;
                    Role[] rolesWithAbility = new Role[] {
                            StarryExpressRoles.STARSTRUCK
                    };

                    for (Role role : rolesWithAbility) {
                        if (gameWorldComponent.isRole(Minecraft.getInstance().player, role)) sendPacket = true;
                    }

                    if (!sendPacket) return;
                    ClientPlayNetworking.send(new AbilityC2SPacket());
                });
            }
        });

        PayloadTypeRegistry.playS2C().register(OpenConfigS2CPacket.TYPE, OpenConfigS2CPacket.CODEC);

        registerPackets();
        registerParticles();
    }

    public void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OpenConfigS2CPacket.TYPE, (payload, context) -> {

            if (Minecraft.getInstance().player == null) return;

            ConfigScreen screen = (ConfigScreen) ConfigScreenProviders.get("starexpress");
            if (Minecraft.getInstance().player.hasPermissions(2)) Minecraft.getInstance().setScreen(screen);

        });
    }

    public void registerParticles() {
        ParticleFactoryRegistry.getInstance().register(StarryExpress.STARSTRUCK_SPARKLE, StarstruckSparkleParticle.Provider::new);
    }
}
