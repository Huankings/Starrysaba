package org.aussiebox.starexpress.client.role_name;

import org.aussiebox.starexpress.client.role_name.roles.muzzler.MuzzlerSilencedTipHudHandler;

/**
 * StarryExpress 接入 Wathe RoleName HUD API 的总入口。
 *
 * <p>这里只聚合注册；具体职业 HUD 逻辑放在对应 handler 中，
 * 后续新增职业提示时不用再把所有逻辑塞进同一个类。</p>
 */
public final class StarryRoleNameHudHandlers {
    private StarryRoleNameHudHandlers() {
    }

    public static void register() {
        MuzzlerSilencedTipHudHandler.register();
    }
}
