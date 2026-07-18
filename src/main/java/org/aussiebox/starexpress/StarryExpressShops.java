package org.aussiebox.starexpress;

import dev.doctor4t.wathe.api.economy.EconomyApi;
import dev.doctor4t.wathe.api.shop.ShopApi;
import dev.doctor4t.wathe.api.shop.ShopPrice;
import dev.doctor4t.wathe.game.GameConstants;
import dev.doctor4t.wathe.util.ShopEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class StarryExpressShops {
    private static final Map<Item, Integer> ITEM_PRICES = new HashMap<>();
    private static final Map<Item, ShopPrice> ITEM_SHOP_PRICES = new HashMap<>();

    static {
        for (ShopEntry entry : GameConstants.SHOP_ENTRIES) {
            /*
             * StarryExpress 主要通过修改器继承 Wathe 默认杀手商店。
             * 这里仍然缓存一份默认商品价格，给旧的静态商店列表或后续职业扩展复用：
             * ITEM_PRICES 是旧版金币兼容值，ITEM_SHOP_PRICES 是完整多货币价格。
             */
            ITEM_SHOP_PRICES.put(entry.stack().getItem(), entry.shopPrice());
            ITEM_PRICES.put(entry.stack().getItem(), entry.price());
        }
    }

    private StarryExpressShops() {
    }

    public static int getBaseItemPrice(@NotNull Item item, int defaultValue) {
        /*
         * 默认只读取第 0 组支付方案里的金币价格。
         * StarryExpress 旧静态商店如果被重新启用，也不会因为默认杀手商店多了任务币而变成不可购买。
         */
        return getBaseCurrencyPrice(item, 0, EconomyApi.MONEY, ITEM_PRICES.getOrDefault(item, defaultValue));
    }

    /**
     * 按“支付方案索引 + 货币 id”读取 Wathe 默认价格。
     *
     * <p>例如疯魔模式可以拆开读取第 0 组金币、第 0 组任务币、第 1 组金币、第 1 组任务币。
     * 这样扩展商店可以自己决定只用金币、只用任务币，或者重新组合一组新价格。</p>
     */
    public static int getBaseCurrencyPrice(
            @NotNull Item item,
            int optionIndex,
            @NotNull ResourceLocation currency,
            int defaultValue
    ) {
        return ShopApi.getDefaultCurrencyPrice(item, optionIndex, currency, defaultValue);
    }

    /**
     * 读取 Wathe 默认商店中某个物品的完整价格。
     *
     * <p>直接返回 {@link ShopPrice} 可以保留任务币、多货币 AND、多个支付方案 OR。
     * 只有明确想完整继承默认杀手商品价格时才应该用它；
     * 一般扩展职业更适合用 {@link #getBaseCurrencyPrice(Item, int, ResourceLocation, int)}
     * 拆开读取某个方案里的某一种货币。</p>
     */
    public static @NotNull ShopPrice getBaseItemShopPrice(@NotNull Item item, int defaultValue) {
        ShopPrice price = ITEM_SHOP_PRICES.get(item);
        return price == null ? ShopPrice.money(getBaseItemPrice(item, defaultValue)) : price;
    }
}
