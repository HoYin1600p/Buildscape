package com.kingodogo.buildscape.variantengine.client;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.registry.BlockRegistryScanner;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class VariantClientRegistry {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BuildScape.LOGGER.info("VariantEngine: Synchronizing RenderLayers for generated variants...");
        
        event.enqueueWork(() -> {
            for (BlockFamily family : BlockRegistryScanner.getDetectedFamilies()) {
                Block parent = family.getBaseBlock();
                
                // CRITICAL FIX: Identify the BEST render type from the parent's default state.
                // We check multiple sources because some blocks don't declare their render layer correctly early on.
                RenderType layer = ItemBlockRenderTypes.getChunkRenderType(parent.defaultBlockState());
                
                // AGGRESSIVE HEURISTIC: Force transparency if keywords match, even if parent reported 'solid'
                String name = parent.getRegistryName().getPath().toLowerCase();
                boolean isTranslucent = name.contains("glass") || name.contains("pane") || name.contains("translucent") || 
                                      name.contains("tinted") || name.contains("ornament") || name.contains("mist") || 
                                      name.contains("star") || name.contains("lights") || name.contains("lamp");
                                      
                boolean isCutout = name.contains("leaves") || name.contains("sapling") || name.contains("hedge") || 
                                   name.contains("bars") || name.contains("mesh") || name.contains("petal") || 
                                   name.contains("icicle") || name.contains("vines");
                
                if (isTranslucent) layer = RenderType.translucent();
                else if (isCutout) layer = RenderType.cutout();
                
                final RenderType finalLayer = layer;
                family.getVariants().values().forEach(variant -> {
                    if (variant.getRegistryName().getNamespace().equals(BuildScape.MODID)) {
                        // Applying the render layer to both the block and its state for maximum world transparency
                        ItemBlockRenderTypes.setRenderLayer(variant, finalLayer);
                        BuildScape.LOGGER.info("VariantEngine: Applied {} layer to variant {}", finalLayer, variant.getRegistryName());
                    }
                });
            }
        });
    }
}
