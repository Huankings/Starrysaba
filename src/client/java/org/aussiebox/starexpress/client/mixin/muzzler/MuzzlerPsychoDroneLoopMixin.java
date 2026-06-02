package org.aussiebox.starexpress.client.mixin.muzzler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.doctor4t.ratatouille.client.util.ambience.BackgroundAmbientLoop;
import dev.doctor4t.ratatouille.client.util.ambience.BackgroundAmbience;
import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.client.player.LocalPlayer;
import org.aussiebox.starexpress.util.MuzzlerPsychoUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * 让已经开始播放的 psycho_drone 也能在“场上只剩静语者疯魔”时自然淡出。
 *
 * <p>Ratatouille 的背景环境音分成两层：
 * 1. {@link dev.doctor4t.ratatouille.client.util.ambience.BackgroundAmbience#tryStarting}
 *    负责“是否开始/补播一个循环音实例”；
 * 2. {@link BackgroundAmbientLoop#tick()} 负责循环音运行过程中每 tick 的
 *    持续播放、淡入、淡出与最终停止。
 *
 * <p>上一次只改了第 1 层，所以遇到：
 * “别的杀手先疯魔 -> 静语者后开疯魔 -> 别的杀手结束，只剩静语者”
 * 这种情况时，已经开始播放的循环音仍会继续按照原版 isPsychoActive() 判定，
 * 看见 world 里还有疯魔就一直维持播放，导致无法淡出。
 *
 * <p>这里补上第 2 层拦截后，运行中的 psycho_drone 循环音也会改用
 * “当前是否存在非静语者疯魔玩家”来判断，从而在只剩静语者时开始正常淡出。
 */
@Mixin(value = BackgroundAmbientLoop.class, priority = 1500)
public class MuzzlerPsychoDroneLoopMixin {

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/doctor4t/ratatouille/client/util/ambience/BackgroundAmbience$PlayPredicate;shouldPlay(Lnet/minecraft/client/player/LocalPlayer;)Z"
            )
    )
    private boolean starexpress$fadeOutPsychoDroneForMuzzler(
            BackgroundAmbience.PlayPredicate predicate,
            LocalPlayer player,
            Operation<Boolean> original
    ) {
        boolean shouldPlay = original.call(predicate, player);
        if (!shouldPlay) {
            return false;
        }

        BackgroundAmbientLoop loop = (BackgroundAmbientLoop) (Object) this;
        if (!loop.getLocation().equals(WatheSounds.AMBIENT_PSYCHO_DRONE.getLocation())) {
            return true;
        }

        return MuzzlerPsychoUtil.hasNonMuzzlerPsycho(player.level());
    }
}
