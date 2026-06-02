package org.aussiebox.starexpress.mixin.muzzler;

import dev.doctor4t.wathe.index.WatheSounds;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import org.aussiebox.starexpress.util.MuzzlerPsychoUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 屏蔽静语者在疯魔状态下挥动球棒时的命中音效。
 *
 * <p>上一次的实现错误地去 mixin 了 Wathe 自己的 mixin 类，
 * 这类 class 只参与字节码注入，不是游戏运行时真正执行攻击逻辑的目标类，
 * 因此最终根本没有拦到真实的声音播放。
 *
 * <p>这次改成直接拦截 Minecraft 运行时的 {@link Level#playSound}。
 * Wathe 的球棒命中音效最终一定会从这里发出去，所以无论是服务端给旁人广播，
 * 还是本地客户端给自己播放，只要传入的是 bat_hit 且“声源玩家”是静语者疯魔，
 * 都会在这里被统一取消。
 *
 * <p>这样既满足“静语者击打时完全无 bat_hit 声音”，
 * 也不会影响其他职业正常使用疯魔模式时的原版反馈。
 */
@Mixin(value = Level.class, priority = 1500)
public class MuzzlerBatHitSoundMixin {

    @Inject(
            method = "playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void starexpress$muteBatHitForMuzzlerPsycho(
            Player player,
            double x,
            double y,
            double z,
            SoundEvent sound,
            SoundSource source,
            float volume,
            float pitch,
            CallbackInfo ci
    ) {
        /*
         * bat_hit 在 Wathe 里本身就是一个很专门的事件音效。
         * 这里先按声音类型收窄范围，再按“是否为静语者疯魔”决定是否静音，
         * 可以保证不误伤别的普通声音。
         */
        if (sound != WatheSounds.ITEM_BAT_HIT) {
            return;
        }

        if (MuzzlerPsychoUtil.isMuzzlerPsycho(player)) {
            ci.cancel();
        }
    }
}
