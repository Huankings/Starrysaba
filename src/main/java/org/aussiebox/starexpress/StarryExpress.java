package org.aussiebox.starexpress;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarryExpress implements ModInitializer {

    public static String MOD_ID = "starexpress";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModBlockEntities.init();
        ModBlocks.init();
        StarryExpressItems.init();

        // StarryExpress 现在只保留装饰方块、物品和绿皮书入口；过敏患者词条已迁移到 NoellesRoles。
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, key);
    }

}
