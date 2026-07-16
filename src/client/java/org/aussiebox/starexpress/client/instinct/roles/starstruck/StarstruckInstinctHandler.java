package org.aussiebox.starexpress.client.instinct.roles.starstruck;

import dev.doctor4t.wathe.api.instinct.InstinctApi;
import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.StarstruckComponent;
import org.aussiebox.starexpress.client.instinct.StarryInstinctHandlers;

public final class StarstruckInstinctHandler {
    private StarstruckInstinctHandler() {
    }

    public static void register() {
        InstinctApi.registerAvailability(StarryExpress.id("instinct/starstruck"), InstinctApi.DEFAULT_PRIORITY, viewer -> {
            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(viewer.level());
            StarstruckComponent component = StarstruckComponent.KEY.get(viewer);
            if (gameWorld.isRole(viewer, StarryExpressRoles.STARSTRUCK) && component.ticks > 0) {
                /*
                 * Starstruck 技能期间临时开启本能资格。
                 * 资格和颜色分开注册，后续只改颜色时不需要再碰开启条件。
                 */
                return InstinctApi.AvailabilityResult.ENABLE;
            }
            return InstinctApi.AvailabilityResult.PASS;
        });

        InstinctApi.registerHighlight(StarryExpress.id("instinct/starstruck_color"), StarryInstinctHandlers.PRIORITY_STATUS_INSTINCT_COLOR, (viewer, target) -> {
            if (!(target instanceof Player targetPlayer) || GameFunctions.isPlayerSpectatingOrCreative(targetPlayer)) {
                return InstinctApi.HighlightResult.pass();
            }

            GameWorldComponent gameWorld = GameWorldComponent.KEY.get(viewer.level());
            StarstruckComponent component = StarstruckComponent.KEY.get(viewer);
            if (gameWorld.isRole(viewer, StarryExpressRoles.STARSTRUCK)
                    && component.ticks > 0
                    && WatheClient.isInstinctEnabled()) {
                /*
                 * Starstruck 的颜色显示依赖最终本能资格。
                 * 这样其它扩展用 availability DISABLE 压住本能时，这里不会绕过压制继续发光。
                 */
                return InstinctApi.HighlightResult.color(StarryExpressRoles.STARSTRUCK.color());
            }
            return InstinctApi.HighlightResult.pass();
        });
    }
}
