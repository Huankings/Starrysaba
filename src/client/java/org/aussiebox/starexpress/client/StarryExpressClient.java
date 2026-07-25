package org.aussiebox.starexpress.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.client.inventory.StarryInventoryButtons;
import org.aussiebox.starexpress.client.render.blockentity.PlushBlockEntityRenderer;

public class StarryExpressClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * StarryExpress 客户端只保留绿皮书按钮和装饰方块渲染。
         * 过敏患者本能和配置入口已经迁移到 NoellesRoles，不再在这里重复注册。
         */
        // 图鉴按钮统一接入 Wathe 背包按钮 API，覆盖 LIMITED / VANILLA / CREATIVE 三种背包界面。
        StarryInventoryButtons.register();

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.CIRCUITWEAVER_PLUSH);
        BlockEntityRenderers.register(ModBlockEntities.PLUSH, PlushBlockEntityRenderer::new);
    }
}
