package org.aussiebox.starexpress.mixin.muzzler;

import dev.doctor4t.wathe.cca.GameWorldComponent;
import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.index.WatheSounds;
import dev.doctor4t.wathe.record.ShopPurchaseTracker;
import dev.doctor4t.wathe.util.ShopEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.aussiebox.starexpress.StarryExpressConstants;
import org.aussiebox.starexpress.StarryExpressRoles;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerShopComponent.class)
public abstract class MuzzlerShopComponentMixin {
    @Shadow
    public int balance;

    @Shadow @Final
    private Player player;

    @Shadow public abstract void sync();

    @Inject(method = "tryBuy", at = @At("HEAD"), cancellable = true)
    void tryBuy(int index, CallbackInfo ci) {
        GameWorldComponent gameWorldComponent = GameWorldComponent.KEY.get(player.level());
        if (gameWorldComponent.isRole(player, StarryExpressRoles.MUZZLER)) {

            if (index >= 0 && index < StarryExpressConstants.MUZZLER_SHOP.size()) {
                ShopEntry entry = StarryExpressConstants.MUZZLER_SHOP.get(index);
                if (FabricLoader.getInstance().isDevelopmentEnvironment() && this.balance < entry.price()) {
                    this.balance = entry.price() * 10;
                }

                if (this.balance >= entry.price() && !this.player.getCooldowns().isOnCooldown(entry.stack().getItem()) && entry.onBuy(this.player)) {
                    /*
                     * Muzzler 的服务端商店在这里已经改写成了自定义商品列表，
                     * 所以必须把真实购买结果回填给 Wathe 的回放追踪器，
                     * 否则回放仍会按原版固定商店索引误判商品。
                     */
                    ShopPurchaseTracker.captureSuccessfulPurchase(this.player, entry, index, entry.price());
                    this.balance -= entry.price();
                    Player var6 = this.player;
                    if (var6 instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer)var6;
                        player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(WatheSounds.UI_SHOP_BUY), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                } else {
                    this.player.displayClientMessage(Component.literal("Purchase Failed").withStyle(ChatFormatting.DARK_RED), true);
                    Player var4 = this.player;
                    if (var4 instanceof ServerPlayer) {
                        ServerPlayer player = (ServerPlayer)var4;
                        player.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(WatheSounds.UI_SHOP_BUY_FAIL), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 1.0F, 0.9F + this.player.getRandom().nextFloat() * 0.2F, player.getRandom().nextLong()));
                    }
                }

                this.sync();
            }

            ci.cancel();
        }
    }

}
