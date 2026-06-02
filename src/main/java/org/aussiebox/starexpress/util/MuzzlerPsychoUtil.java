package org.aussiebox.starexpress.util;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerPsychoComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.aussiebox.starexpress.StarryExpressRoles;

/**
 * 统一收口“静语者购买疯魔模式后需要静音处理”的判定。
 *
 * <p>这里故意不去改 Wathe 的疯魔主流程，而是把“谁属于静语者疯魔”
 * 与“当前世界里是否还存在其他正常疯魔玩家”抽成公共工具方法。
 *
 * <p>这样服务端屏蔽球棒命中音效、客户端屏蔽 psycho_drone 背景音时，
 * 都能复用同一套规则，避免两边各写一份条件后逐渐跑偏。
 */
public final class MuzzlerPsychoUtil {

    private MuzzlerPsychoUtil() {
    }

    /**
     * 判断某个玩家当前是否正处于“静语者触发的疯魔状态”。
     *
     * <p>成立条件必须同时满足：
     * 1. 玩家职业是静语者；
     * 2. 玩家的疯魔计时仍大于 0。
     *
     * <p>只有这种情况下，我们才需要额外屏蔽 Wathe 原版附带的疯魔音效。
     */
    public static boolean isMuzzlerPsycho(Player player) {
        if (player == null) {
            return false;
        }

        Level level = player.level();
        GameWorldComponent game = GameWorldComponent.KEY.get(level);
        if (!game.isRole(player, StarryExpressRoles.MUZZLER)) {
            return false;
        }

        return PlayerPsychoComponent.KEY.get(player).getPsychoTicks() > 0;
    }

    /**
     * 判断当前世界里是否还存在“非静语者来源”的疯魔玩家。
     *
     * <p>静语者购买疯魔后不应播放 psycho_drone，
     * 但如果同一时间场上还有别的职业在正常疯魔，
     * 那么这条全局环境音依旧应该保留。
     *
     * <p>因此这里不是简单判断 world 里有没有疯魔，
     * 而是要精确判断：是否至少存在一名疯魔中的玩家，并且该玩家不是静语者。
     */
    public static boolean hasNonMuzzlerPsycho(Level level) {
        if (level == null) {
            return false;
        }

        GameWorldComponent game = GameWorldComponent.KEY.get(level);
        for (Player player : level.players()) {
            PlayerPsychoComponent psycho = PlayerPsychoComponent.KEY.get(player);
            if (psycho.getPsychoTicks() <= 0) {
                continue;
            }

            if (!game.isRole(player, StarryExpressRoles.MUZZLER)) {
                return true;
            }
        }

        return false;
    }
}
