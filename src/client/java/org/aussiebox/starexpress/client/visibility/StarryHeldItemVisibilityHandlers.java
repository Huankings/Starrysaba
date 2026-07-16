package org.aussiebox.starexpress.client.visibility;

import dev.doctor4t.wathe.api.client.invisibility.HeldItemInvisibilityApi;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.item.StarryExpressItems;

/**
 * StarryExpress 接入 Wathe 手持物不可见 API 的统一注册处。
 *
 * <p>旧版通过 Mixin 直接改 ItemInHandLayer，只按物品隐藏。
 * 现在按你的确认，改成只有 Muzzler 拿着 Tape 时才对其他局内存活玩家不可见。</p>
 */
public final class StarryHeldItemVisibilityHandlers {
    private StarryHeldItemVisibilityHandlers() {
    }

    public static void register() {
        // 胶带是 Muzzler 的专属隐蔽物品，Wathe 会统一处理主手/副手和手臂姿势。
        HeldItemInvisibilityApi.registerHiddenItem(StarryExpressRoles.MUZZLER, StarryExpressItems.TAPE);
    }
}
