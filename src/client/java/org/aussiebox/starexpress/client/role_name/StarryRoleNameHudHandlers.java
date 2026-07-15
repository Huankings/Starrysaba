package org.aussiebox.starexpress.client.role_name;

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
 * StarryExpress 接入 Wathe RoleName HUD API 的统一注册处。
 *
 * <p>这里替代旧的 GetTargetMixin 与 SilencedHudMixin：
 * Wathe 已经在 RoleNameRenderer 内算好了准心玩家，所以扩展只需要读取 Context。</p>
 */
public final class StarryRoleNameHudHandlers {
    private StarryRoleNameHudHandlers() {
    }

    public static void register() {
        RoleNameHudApi.registerExtraHud(
                StarryExpress.id("role_name/muzzler_silenced_tip"),
                RoleNameHudApi.DEFAULT_PRIORITY,
                context -> {
                    Player target = context.targetPlayer();
                    /*
                     * 仍然同步旧的 StarryExpressClient.target 字段，
                     * 这样其它尚未迁移的客户端 UI 如果读取它，也能拿到同一帧 Wathe 判定出的准心目标。
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
