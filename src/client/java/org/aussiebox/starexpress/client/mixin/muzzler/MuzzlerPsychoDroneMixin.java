package org.aussiebox.starexpress.client.mixin.muzzler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.ratatouille.client.util.ambience.BackgroundAmbience;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import org.aussiebox.starexpress.util.MuzzlerPsychoUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 屏蔽静语者购买疯魔模式后自带的 psycho_drone 背景音。
 *
 * <p>Wathe 的这条环境音不是按“某个玩家自己疯魔了就只给自己播”，
 * 而是按“当前世界是否存在疯魔”来决定的全局背景音。
 *
 * <p>因此这里不能简单地把 {@code isPsychoActive()} 改成 false，
 * 否则会把其他职业正常触发的疯魔音乐也一起干掉。
 *
 * <p>正确做法是：
 * 1. 只在当前这条环境音实例确实是 psycho_drone 时介入；
 * 2. 不再看“本地玩家自己是不是静语者”，而是直接看“当前世界里是否至少存在一名
 *    非静语者疯魔玩家”；
 * 3. 否则沿用 Wathe 原版逻辑。
 */
@Mixin(value = BackgroundAmbience.class, priority = 1500)
public abstract class MuzzlerPsychoDroneMixin {

    @Shadow @Final
    private BackgroundAmbience.SoundFactory factory;

    @WrapOperation(
            method = "tryStarting",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/ratatouille/client/util/ambience/BackgroundAmbience$PlayPredicate;shouldPlay(Lnet/minecraft/client/player/LocalPlayer;)Z"
            )
    )
    private boolean starexpress$mutePsychoDroneForMuzzler(
            BackgroundAmbience.PlayPredicate predicate,
            LocalPlayer player,
            Operation<Boolean> original
    ) {
        boolean shouldPlay = original.call(predicate, player);
        if (!shouldPlay) {
            return false;
        }

        SoundInstance soundInstance = this.factory.create(player);
        if (soundInstance == null || !soundInstance.getLocation().equals(WatheSounds.AMBIENT_PSYCHO_DRONE.getLocation())) {
            return true;
        }

        /*
         * 这里必须按“整张地图当前有哪些疯魔玩家”来判断，
         * 不能只看本地玩家自己是谁。
         *
         * 因为 psycho_drone 是 Wathe 的全局疯魔环境音：
         * 只要 world 里存在疯魔，所有客户端都会尝试播放它。
         *
         * 所以想实现“静语者开的疯魔，对所有人都不播音乐”，
         * 这里就必须统一改成：
         * - 只要当前所有疯魔玩家全都是静语者，就一律静音；
         * - 只要场上还有任意一个非静语者疯魔玩家，就恢复原版播放。
         */
        return MuzzlerPsychoUtil.hasNonMuzzlerPsycho(player.level());
    }
}
