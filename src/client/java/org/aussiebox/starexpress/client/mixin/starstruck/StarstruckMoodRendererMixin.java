package org.aussiebox.starexpress.client.mixin.starstruck;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.gui.MoodRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AbilityComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MoodRenderer.class)
public class StarstruckMoodRendererMixin {

    @Unique private static final ResourceLocation ABILITY_HAPPY = StarryExpress.id("mood/starstruck/ability_happy");
    @Unique private static final ResourceLocation ABILITY_MID = StarryExpress.id("mood/starstruck/ability_mid");
    @Unique private static final ResourceLocation ABILITY_DEPRESSIVE = StarryExpress.id("mood/starstruck/ability_depressive");
    @Unique private static final ResourceLocation HAPPY = StarryExpress.id("mood/starstruck/happy");
    @Unique private static final ResourceLocation MID = StarryExpress.id("mood/starstruck/mid");
    @Unique private static final ResourceLocation DEPRESSIVE = StarryExpress.id("mood/starstruck/depressive");
    @Unique private static final ResourceLocation SPARKLES = StarryExpress.id("mood/starstruck/sparkles");
    @Shadow public static float moodRender;

    @ModifyVariable(
            method = "renderCivilian",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"),
            name = "mood",
            argsOnly = true
    )
    private static ResourceLocation starexpress$renderStarstruckMoodTextures(ResourceLocation mood, GuiGraphics context) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return mood;

        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.level());
        if (!gameWorldComponent.isRole(player, StarryExpressRoles.STARSTRUCK)) return mood;
        StarstruckComponent starstruck = StarstruckComponent.KEY.get(player);

        if (starstruck.ticks > 0) {
            mood = ABILITY_HAPPY;
            if (moodRender < 0.2F) {
                mood = ABILITY_DEPRESSIVE;
            } else if (moodRender < 0.55F) {
                mood = ABILITY_MID;
            }
        } else {
            mood = HAPPY;
            if (moodRender < 0.2F) {
                mood = DEPRESSIVE;
            } else if (moodRender < 0.55F) {
                mood = MID;
            }
        }

        if (AbilityComponent.KEY.get(player).cooldown == 0)
            context.blitSprite(SPARKLES, 5, 6, 14, 17);

        return mood;
    }
}
