package org.aussiebox.starexpress.client.roles.starstruck;

import dev.doctor4t.wathe.api.client.mood.MoodHudApi;
import dev.doctor4t.wathe.api.client.mood.MoodHudContext;
import dev.doctor4t.wathe.api.client.mood.MoodHudStyle;
import dev.doctor4t.wathe.game.GameConstants;
import net.minecraft.resources.ResourceLocation;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.aussiebox.starexpress.util.StarryExpressUtil;

import java.util.List;

public final class StarstruckMoodHud {
    private static final ResourceLocation ABILITY_HAPPY = StarryExpress.id("hud/starstruck/ability_happy");
    private static final ResourceLocation ABILITY_MID = StarryExpress.id("hud/starstruck/ability_mid");
    private static final ResourceLocation ABILITY_DEPRESSIVE = StarryExpress.id("hud/starstruck/ability_depressive");
    private static final ResourceLocation ABILITY_SPARKLES = StarryExpress.id("hud/starstruck/ability_sparkles");
    private static final ResourceLocation HAPPY = StarryExpress.id("hud/starstruck/happy");
    private static final ResourceLocation MID = StarryExpress.id("hud/starstruck/mid");
    private static final ResourceLocation DEPRESSIVE = StarryExpress.id("hud/starstruck/depressive");
    private static final ResourceLocation SPARKLES = StarryExpress.id("hud/starstruck/sparkles");

    private StarstruckMoodHud() {
    }

    public static void register() {
        MoodHudApi.registerRoleStyle(StarryExpressRoles.STARSTRUCK, MoodHudStyle
                .builder(StarstruckMoodHud::getMoodSprite)
                .arrows()
                .overlays(StarstruckMoodHud::getOverlays)
                .bar(StarstruckMoodHud::renderMoodBar)
                .build());
    }

    private static ResourceLocation getMoodSprite(MoodHudContext context) {
        boolean abilityActive = StarstruckComponent.KEY.get(context.player()).ticks > 0;
        if (context.moodRender() < GameConstants.DEPRESSIVE_MOOD_THRESHOLD) {
            return abilityActive ? ABILITY_DEPRESSIVE : DEPRESSIVE;
        }
        if (context.moodRender() < GameConstants.MID_MOOD_THRESHOLD) {
            return abilityActive ? ABILITY_MID : MID;
        }
        return abilityActive ? ABILITY_HAPPY : HAPPY;
    }

    private static List<ResourceLocation> getOverlays(MoodHudContext context) {
        StarstruckComponent starstruck = StarstruckComponent.KEY.get(context.player());
        if (starstruck.ticks > 0) {
            return List.of(ABILITY_SPARKLES);
        }
        if (AbilityComponent.KEY.get(context.player()).cooldown == 0) {
            return List.of(SPARKLES);
        }
        return List.of();
    }

    private static void renderMoodBar(MoodHudContext context, int width, float alpha) {
        if (width <= 0 || alpha <= 0.0F) {
            return;
        }

        /*
         * 星界使者保留原本紫色渐变：心情越高越靠近亮紫。
         * 图标本身使用 GUI sprite id，所以 sparkles 的 .png.mcmeta 动画会由 atlas 自动播放。
         */
        int colour = StarryExpressUtil.lerpColor(0x271BAD, 0x6156E6, context.moodRender());
        context.drawContext().fill(0, 0, width, 1, colour | ((int) (alpha * 255.0F) << 24));
    }
}
