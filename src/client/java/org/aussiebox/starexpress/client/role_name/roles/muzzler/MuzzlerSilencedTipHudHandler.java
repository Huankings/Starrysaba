package org.aussiebox.starexpress.client.role_name.roles.muzzler;

import dev.doctor4t.wathe.api.client.gui.RoleNameHudApi;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpress;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.aussiebox.starexpress.cca.SilenceComponent;
import org.aussiebox.starexpress.client.StarryExpressClient;

/**
 * Muzzler 职业的静音目标提示。
 *
 * <p>Wathe 已经在 RoleNameRenderer 内算好了准心玩家；这里直接读取 Context，
 * 如果目标被静音且超过配置延迟，就在准心上方显示 Muzzler 的静音提示。</p>
 */
public final class MuzzlerSilencedTipHudHandler {
    private MuzzlerSilencedTipHudHandler() {
    }

    public static void register() {
        RoleNameHudApi.registerExtraHud(
                StarryExpress.id("role_name/muzzler_silenced_tip"),
                RoleNameHudApi.DEFAULT_PRIORITY,
                context -> {
                    Player target = context.targetPlayer();
                    /*
                     * 仍然同步旧的 StarryExpressClient.target 字段。
                     * 其它尚未迁移到 Wathe API 的客户端 UI 如果读取它，也能拿到同一帧的准心目标。
                     */
                    StarryExpressClient.target = target;
                    if (target == null) {
                        return;
                    }

                    SilenceComponent victimSilence = SilenceComponent.KEY.get(target);
                    if (!victimSilence.isSilenced()
                            || victimSilence.getSilencedTicks() < StarryExpress.CONFIG.muzzlerConfig.displaySilencedTipDelay() * 20) {
                        return;
                    }

                    renderSilencedTip(context.renderer(), context.drawContext());
                }
        );
    }

    private static void renderSilencedTip(Font renderer, GuiGraphics context) {
        Component text = Component.translatable("tip.starexpress.muzzler.silenced");

        /*
         * 保留旧 SilencedHudMixin 的绘制位置：
         * 准心上方 37.5px 起点、0.6 倍缩放、使用 Muzzler 职业色。
         */
        context.pose().pushPose();
        context.pose().translate(context.guiWidth() / 2.0F, context.guiHeight() / 2.0f - 37.5F, 0.0F);
        context.pose().scale(0.6F, 0.6F, 1.0F);

        context.drawString(
                renderer,
                text,
                -renderer.width(text) / 2,
                32,
                StarryExpressRoles.MUZZLER.color()
        );

        context.pose().popPose();
    }
}
