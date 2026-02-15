package org.aussiebox.starexpress;

import dev.doctor4t.wathe.cca.PlayerShopComponent;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public interface StarryExpressConstants {
    List<ShopEntry> MUZZLER_SHOP = Util.make(new ArrayList<>(), (entries) -> {
        entries.add(new ShopEntry(WatheItems.KNIFE.getDefaultInstance(), 100, ShopEntry.Type.WEAPON));
        entries.add(new ShopEntry(WatheItems.REVOLVER.getDefaultInstance(), 300, ShopEntry.Type.WEAPON));
        entries.add(new ShopEntry(StarryExpressItems.TAPE.getDefaultInstance(), 350, ShopEntry.Type.WEAPON));
        entries.add(new ShopEntry(WatheItems.PSYCHO_MODE.getDefaultInstance(), 300, ShopEntry.Type.WEAPON) {
            public boolean onBuy(@NotNull Player player) {
                return PlayerShopComponent.usePsychoMode(player);
            }
        });
        entries.add(new ShopEntry(WatheItems.POISON_VIAL.getDefaultInstance(), 100, ShopEntry.Type.POISON));
        entries.add(new ShopEntry(WatheItems.SCORPION.getDefaultInstance(), 50, ShopEntry.Type.POISON));
        entries.add(new ShopEntry(WatheItems.FIRECRACKER.getDefaultInstance(), 10, ShopEntry.Type.TOOL));
        entries.add(new ShopEntry(WatheItems.CROWBAR.getDefaultInstance(), 25, ShopEntry.Type.TOOL));
        entries.add(new ShopEntry(WatheItems.BODY_BAG.getDefaultInstance(), 200, ShopEntry.Type.TOOL));
        entries.add(new ShopEntry(WatheItems.BLACKOUT.getDefaultInstance(), 200, ShopEntry.Type.TOOL) {
            public boolean onBuy(@NotNull Player player) {
                return PlayerShopComponent.useBlackout(player);
            }
        });
        entries.add(new ShopEntry(new ItemStack(WatheItems.NOTE, 4), 10, ShopEntry.Type.TOOL));
    });

    ResourceLocation SILENCED_OUTSIDE_DEATH_REASON = StarryExpress.id("silenced_and_outside");
    ResourceLocation SILENCED_TAPE_REMOVED_DEATH_REASON = StarryExpress.id("tape_removed_low_mood");
}
