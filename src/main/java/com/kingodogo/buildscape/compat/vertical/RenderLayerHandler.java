package com.kingodogo.buildscape.compat.vertical;

import com.kingodogo.buildscape.BuildScape;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RenderLayerHandler {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            VerticalRegistry.VERTICAL_SLABS.forEach((parent, vertical) -> {
                setRenderLayer(parent, vertical);
            });

            VerticalRegistry.VERTICAL_STAIRS.forEach((parent, vertical) -> {
                setRenderLayer(parent, vertical);
            });
        });
    }

    private static void setRenderLayer(Block parent, Block vertical) {
        try {
            RenderType type = ItemBlockRenderTypes.getChunkRenderType(parent.defaultBlockState());
            if (type != null) {
                ItemBlockRenderTypes.setRenderLayer(vertical, type);
            }
        } catch (Exception e) {
            BuildScape.LOGGER.warn("Failed to copy render layer from " + parent.getRegistryName() + " to " + vertical.getRegistryName());
        }
    }
}
