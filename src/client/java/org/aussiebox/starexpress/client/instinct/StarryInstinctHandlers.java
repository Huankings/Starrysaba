package org.aussiebox.starexpress.client.instinct;

import dev.doctor4t.wathe.api.instinct.InstinctApi;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.aussiebox.starexpress.cca.StarstruckComponent;

import java.awt.Color;

public final class StarryInstinctHandlers {
    private static final int PRIORITY_STATUS_INSTINCT_COLOR = 100;

    private StarryInstinctHandlers() {
    }

    public static void register() {
        registerAvailability();
        registerHighlights();
    }

    private static void registerAvailability() {
        InstinctApi.registerAvailability(StarryExpress.id("instinct/allergic"), InstinctApi.DEFAULT_PRIORITY, viewer -> {
            AllergicComponent allergy = AllergicComponent.KEY.get(viewer);
            if (allergy.isAllergic() && allergy.getGlowTicks() > 0) {
                return InstinctApi.AvailabilityResult.ENABLE;
            }
            return InstinctApi.AvailabilityResult.PASS;
        });

        InstinctApi.registerAvailability(StarryExpress.id("instinct/starstruck"), InstinctApi.DEFAULT_PRIORITY, viewer -> {
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(viewer.level());
            StarstruckComponent component = StarstruckComponent.KEY.get(viewer);
            if (gameWorldComponent.isRole(viewer, StarryExpressRoles.STARSTRUCK) && component.ticks > 0) {
                return InstinctApi.AvailabilityResult.ENABLE;
            }
            return InstinctApi.AvailabilityResult.PASS;
        });
    }

    private static void registerHighlights() {
        InstinctApi.registerHighlight(StarryExpress.id("instinct/allergic_color"), PRIORITY_STATUS_INSTINCT_COLOR, (viewer, target) -> {
            if (!(target instanceof Player targetPlayer) || GameFunctions.isPlayerSpectatingOrCreative(targetPlayer)) {
                return InstinctApi.HighlightResult.pass();
            }
            AllergicComponent allergy = AllergicComponent.KEY.get(viewer);
            if (allergy.isAllergic() && allergy.getGlowTicks() > 0 && WatheClient.isInstinctEnabled()) {
                /*
                 * Allergic 的旧实现会先把状态转换成 isInstinctEnabled=true，再给玩家染绿。
                 * 这里显式检查 isInstinctEnabled()，让 Convener 这类高优先级 DISABLE 能压住它。
                 */
                return InstinctApi.HighlightResult.color(Color.GREEN.getRGB());
            }
            return InstinctApi.HighlightResult.pass();
        });

        InstinctApi.registerHighlight(StarryExpress.id("instinct/starstruck_color"), PRIORITY_STATUS_INSTINCT_COLOR, (viewer, target) -> {
            if (!(target instanceof Player targetPlayer) || GameFunctions.isPlayerSpectatingOrCreative(targetPlayer)) {
                return InstinctApi.HighlightResult.pass();
            }
            GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(viewer.level());
            StarstruckComponent component = StarstruckComponent.KEY.get(viewer);
            if (gameWorldComponent.isRole(viewer, StarryExpressRoles.STARSTRUCK)
                    && component.ticks > 0
                    && WatheClient.isInstinctEnabled()) {
                return InstinctApi.HighlightResult.color(StarryExpressRoles.STARSTRUCK.color());
            }
            return InstinctApi.HighlightResult.pass();
        });
    }
}
