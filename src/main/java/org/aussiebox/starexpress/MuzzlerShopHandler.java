package org.aussiebox.starexpress;

import dev.doctor4t.wathe.api.shop.ShopContext;
import dev.doctor4t.wathe.index.WatheItems;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.world.item.Item;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 静语者商店修改器。
 *
 * <p>Muzzler 只需要把默认杀手商店里的左轮位置替换成胶带。
 * 其他默认商品、默认价格，以及 BLACKOUT / PSYCHO_MODE 这种“购买即触发”的特殊行为，
 * 都继续由 Wathe 默认杀手商店提供。</p>
 */
public final class MuzzlerShopHandler {
    private static final int TAPE_PRICE = 75;

    private MuzzlerShopHandler() {
    }

    public static void modifyShop(@NotNull ShopContext context, @NotNull List<ShopEntry> entries) {
        if (context.role() != StarryExpressRoles.MUZZLER) {
            return;
        }

        /*
         * 不直接 add/remove 重排整张表，而是精确替换 REVOLVER 所在格子。
         * 这样客户端看到的位置仍是“默认手枪位”，但商品变成 Muzzler 的专属胶带。
         * 其他没有被替换的默认条目继续保留原 ShopPrice，所以匕首、开锁器、疯魔模式
         * 会自动读取 Wathe 当前的任务币/多货币价格。
         */
        replaceItem(entries, WatheItems.REVOLVER, new ShopEntry(
                StarryExpressItems.TAPE.getDefaultInstance(),
                TAPE_PRICE,
                ShopEntry.Type.WEAPON
        ));
    }

    private static void replaceItem(@NotNull List<ShopEntry> entries, @NotNull Item item, @NotNull ShopEntry replacement) {
        int index = indexOfItem(entries, item);
        if (index >= 0) {
            entries.set(index, replacement);
        }
    }

    private static int indexOfItem(@NotNull List<ShopEntry> entries, @NotNull Item item) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).stack().is(item)) {
                return i;
            }
        }
        return -1;
    }
}
