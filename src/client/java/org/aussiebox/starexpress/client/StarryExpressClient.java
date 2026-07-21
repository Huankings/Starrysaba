package org.aussiebox.starexpress.client;

import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.owo.config.ui.ConfigScreenProviders;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import org.aussiebox.starexpress.block.ModBlocks;
import org.aussiebox.starexpress.block.entity.ModBlockEntities;
import org.aussiebox.starexpress.client.instinct.StarryInstinctHandlers;
import org.aussiebox.starexpress.client.render.blockentity.PlushBlockEntityRenderer;
import org.aussiebox.starexpress.packet.OpenConfigS2CPacket;

public class StarryExpressClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /*
         * 星界使者/静语者客户端 HUD、粒子和按键包已经搬到 NoellesRoles。
         * StarryExpress 客户端只继续注册 allergic 本能、装饰方块渲染和自己的配置界面网络包。
         */
        StarryInstinctHandlers.register();

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(), ModBlocks.CIRCUITWEAVER_PLUSH);
        BlockEntityRenderers.register(ModBlockEntities.PLUSH, PlushBlockEntityRenderer::new);

        PayloadTypeRegistry.playS2C().register(OpenConfigS2CPacket.TYPE, OpenConfigS2CPacket.CODEC);

        registerPackets();
    }

    public void registerPackets() {
        ClientPlayNetworking.registerGlobalReceiver(OpenConfigS2CPacket.TYPE, (payload, context) -> {

            if (Minecraft.getInstance().player == null) return;

            ConfigScreen screen = (ConfigScreen) ConfigScreenProviders.get("starexpress");
            if (Minecraft.getInstance().player.hasPermissions(2)) Minecraft.getInstance().setScreen(screen);

        });
    }
}
