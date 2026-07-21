package org.aussiebox.starexpress.client.visibility;

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
        // 静语者和胶带已搬到 NoellesRoles，手持隐藏规则也由 NoellesRoles 注册。
    }
}
