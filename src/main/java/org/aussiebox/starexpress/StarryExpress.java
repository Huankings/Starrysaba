package org.aussiebox.starexpress;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.config.StarryExpressServerConfig;
import org.aussiebox.starexpress.item.StarryExpressItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarryExpress implements ModInitializer {

    public static String MOD_ID = "starexpress";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final StarryExpressServerConfig CONFIG = StarryExpressServerConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ModSounds.init();
        ModBlockEntities.init();
        ModBlocks.init();
        StarryExpressItems.init();

        /*
         * 星界使者和静语者已经完整搬到 NoellesRoles。
         * StarryExpress 这里只继续注册自己仍然拥有的 allergic 词条、装饰方块和绿皮书相关内容。
         */
        StarryExpressRoles.init();
        StarryExpressModifiers.init();
    }

    public static ResourceLocation id(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, key);
    }

}
