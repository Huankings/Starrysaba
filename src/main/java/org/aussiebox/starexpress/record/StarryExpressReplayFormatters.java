package org.aussiebox.starexpress.record;

import dev.doctor4t.wathe.record.GameRecordEvent;
import dev.doctor4t.wathe.record.GameRecordManager;
import dev.doctor4t.wathe.record.replay.ReplayGenerator;
import dev.doctor4t.wathe.record.replay.ReplayRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.aussiebox.starexpress.StarryExpress;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class StarryExpressReplayFormatters {
    private StarryExpressReplayFormatters() {
    }

    private static ResourceLocation id(String path) {
        return StarryExpress.id(path);
    }

    public static void register() {
        ReplayRegistry.registerSkillFormatter(id("starstruck_ability"), StarryExpressReplayFormatters::formatStarstruckAbility);

        ReplayRegistry.registerItemUseFormatter(id("tape"), StarryExpressReplayFormatters::formatTapeUse);

        ReplayRegistry.registerGlobalEventFormatter(id("starstruck_ability_end"), StarryExpressReplayFormatters::formatStarstruckAbilityEnd);
        ReplayRegistry.registerGlobalEventFormatter(id("tape_removed"), StarryExpressReplayFormatters::formatTapeRemoved);

        ReplayRegistry.registerDeathReasonFormatter(id("silenced_and_outside"), StarryExpressReplayFormatters::formatSilencedOutsideDeath);
        ReplayRegistry.registerDeathReasonFormatter(id("tape_removed_low_mood"), StarryExpressReplayFormatters::formatTapeRemovedLowMoodDeath);
    }

    private static @Nullable Component actorText(GameRecordEvent event, GameRecordManager.MatchRecord match) {
        UUID actorUuid = event.data().contains("actor") ? event.data().getUUID("actor") : null;
        return actorUuid == null ? null : ReplayGenerator.formatPlayerName(actorUuid, ReplayGenerator.getPlayerInfoCache(match));
    }

    private static @Nullable Component targetText(GameRecordEvent event, GameRecordManager.MatchRecord match) {
        UUID targetUuid = event.data().contains("target") ? event.data().getUUID("target") : null;
        return targetUuid == null ? null : ReplayGenerator.formatPlayerName(targetUuid, ReplayGenerator.getPlayerInfoCache(match));
    }

    private static @Nullable Component playerText(@Nullable UUID uuid, GameRecordManager.MatchRecord match) {
        return uuid == null ? null : ReplayGenerator.formatPlayerName(uuid, ReplayGenerator.getPlayerInfoCache(match));
    }

    private static @Nullable UUID uuid(CompoundTag data, String key) {
        return data.contains(key) ? data.getUUID(key) : null;
    }

    private static @Nullable Component formatStarstruckAbility(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component actor = actorText(event, match);
        return actor == null ? null : Component.translatable("replay.skill_use.starexpress.starstruck", actor);
    }

    private static @Nullable Component formatTapeUse(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component actor = actorText(event, match);
        Component target = targetText(event, match);
        if (actor == null || target == null) {
            return null;
        }
        return Component.translatable("replay.item_use.starexpress.tape", actor, target);
    }

    private static @Nullable Component formatStarstruckAbilityEnd(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component actor = actorText(event, match);
        return actor == null ? null : Component.translatable("replay.global.starexpress.starstruck_end", actor);
    }

    private static @Nullable Component formatTapeRemoved(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component remover = actorText(event, match);
        Component victim = targetText(event, match);
        Component silencer = playerText(uuid(event.data(), "silencer"), match);
        if (remover == null || victim == null || silencer == null) {
            return null;
        }
        return Component.translatable("replay.global.starexpress.tape_removed", remover, victim, silencer);
    }

    private static @Nullable Component formatSilencedOutsideDeath(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component victim = targetText(event, match);
        Component silencer = playerText(uuid(event.data(), "silencer"), match);
        if (victim == null || silencer == null) {
            return null;
        }
        return Component.translatable("replay.death.starexpress.silenced_and_outside.died", victim, silencer);
    }

    private static @Nullable Component formatTapeRemovedLowMoodDeath(GameRecordEvent event, GameRecordManager.MatchRecord match, ServerLevel world) {
        Component victim = targetText(event, match);
        Component remover = playerText(uuid(event.data(), "remover"), match);
        Component silencer = playerText(uuid(event.data(), "silencer"), match);
        if (victim == null || remover == null || silencer == null) {
            return null;
        }
        return Component.translatable("replay.death.starexpress.tape_removed_low_mood.died", victim, remover, silencer);
    }
}
