package org.aussiebox.starexpress.client.instinct.allergic;

import dev.doctor4t.wathe.api.instinct.InstinctApi;
import dev.doctor4t.wathe.client.WatheClient;
import dev.doctor4t.wathe.game.GameFunctions;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.cca.AllergicComponent;
import org.aussiebox.starexpress.client.instinct.StarryInstinctHandlers;

import java.awt.Color;

public final class AllergicInstinctHandler {
    private AllergicInstinctHandler() {
    }

    public static void register() {
        InstinctApi.registerAvailability(StarryExpress.id("instinct/allergic"), InstinctApi.DEFAULT_PRIORITY, viewer -> {
            AllergicComponent allergy = AllergicComponent.KEY.get(viewer);
            if (allergy.isAllergic() && allergy.getGlowTicks() > 0) {
                /*
                 * 过敏状态会临时给予本能资格，旧实现也是直接让 isInstinctEnabled() 为 true。
                 * 现在放在 availability 层，方便 Convener 等高优先级禁用规则统一压制。
                 */
                return InstinctApi.AvailabilityResult.ENABLE;
            }
            return InstinctApi.AvailabilityResult.PASS;
        });

        InstinctApi.registerHighlight(StarryExpress.id("instinct/allergic_color"), StarryInstinctHandlers.PRIORITY_STATUS_INSTINCT_COLOR, (viewer, target) -> {
            if (!(target instanceof Player targetPlayer) || GameFunctions.isPlayerSpectatingOrCreative(targetPlayer)) {
                return InstinctApi.HighlightResult.pass();
            }

            AllergicComponent allergy = AllergicComponent.KEY.get(viewer);
            if (allergy.isAllergic() && allergy.getGlowTicks() > 0 && WatheClient.isInstinctEnabled()) {
                /*
                 * 颜色显示显式依赖 WatheClient.isInstinctEnabled()。
                 * 如果 availability 被其它扩展压成 DISABLE，即使过敏 tick 还在也不会继续透视。
                 */
                return InstinctApi.HighlightResult.color(Color.GREEN.getRGB());
            }
            return InstinctApi.HighlightResult.pass();
        });
    }
}
