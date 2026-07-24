package org.aussiebox.starexpress.client.inventory;

import dev.doctor4t.wathe.api.client.inventory.InventoryButtonApi;
import dev.doctor4t.wathe.api.client.inventory.InventoryButtonContext;
import dev.doctor4t.wathe.api.client.inventory.InventoryButtonExtension;
import dev.doctor4t.wathe.api.client.inventory.InventoryScreenType;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.client.gui.widget.GuidebookButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * StarryExpress 背包图鉴按钮接入 Wathe InventoryButtonApi。
 *
 * <p>旧版分别 mixin 普通背包、Wathe 限制背包和创造背包来追加同一个按钮。
 * 现在由 Wathe 按 screen type 统一调度，这样其它扩展也能在同一个生命周期里做分页、
 * 动态替换和坐标刷新，不需要多个 mod 互相叠加 screen mixin。</p>
 */
public final class StarryInventoryButtons {
    private StarryInventoryButtons() {
    }

    public static void register() {
        InventoryButtonApi.registerProvider(
                StarryExpress.id("inventory/guidebook"),
                InventoryButtonApi.DEFAULT_PRIORITY,
                StarryInventoryButtons::createGuidebookButton
        );
    }

    private static @Nullable InventoryButtonExtension createGuidebookButton(@NotNull InventoryButtonContext context) {
        return supportsScreen(context.type()) ? new GuidebookExtension() : null;
    }

    private static boolean supportsScreen(@NotNull InventoryScreenType type) {
        return type == InventoryScreenType.LIMITED
                || type == InventoryScreenType.VANILLA
                || type == InventoryScreenType.CREATIVE;
    }

    private static final class GuidebookExtension implements InventoryButtonExtension {
        @Override
        public void init(@NotNull InventoryButtonContext context) {
            /*
             * Starry 的图鉴入口本身不依赖职业和局内状态，三种背包界面都固定放在左上角。
             * 通过 API 添加后，Wathe 会负责在 screen 关闭时统一禁用并清理按钮生命周期。
             */
            context.addWidget(new GuidebookButtonWidget(10, 10));
        }
    }
}
