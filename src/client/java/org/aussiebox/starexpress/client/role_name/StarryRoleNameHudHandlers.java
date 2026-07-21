package org.aussiebox.starexpress.client.role_name;

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
        // 静语者的“被堵住嘴”名字提示已经搬到 NoellesRoles，这里不再注册 Starry 旧 handler。
    }
}
