package com.kingodogo.buildscape.variantengine.registry;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.variantengine.family.BlockFamily;
import com.kingodogo.buildscape.variantengine.family.BlockFamilyDetector;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlockRegistryScanner {

    private static final java.util.Map<Block, BlockFamily> DETECTED_FAMILIES = new java.util.HashMap<>();

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        DETECTED_FAMILIES.clear();
        BuildScape.LOGGER.info("VariantEngine: Scanning registries for block families...");

        // Use ForgeRegistries.BLOCKS as a source (it contains blocks registered before us)
        for (Block block : ForgeRegistries.BLOCKS) {
            BlockFamily detected = BlockFamilyDetector.detectFamily(block);
            if (detected != null) {
                BlockFamily family = DETECTED_FAMILIES.computeIfAbsent(detected.getBaseBlock(), k -> new BlockFamily(k));
                
                // Merge found variants into the canonical family
                detected.getVariants().forEach(family::addVariant);
                
                // Populate BiMaps for existing variants so they're grouped in creative tabs
                for (java.util.Map.Entry<com.kingodogo.buildscape.variantengine.builder.BlockShape, Block> entry : family.getVariants().entrySet()) {
                    com.kingodogo.buildscape.variantengine.util.BlockBiMaps.setBlockOf(entry.getKey(), family.getBaseBlock(), entry.getValue());
                }
            }
        }

        BuildScape.LOGGER.info("VariantEngine: Found {} unique block families for completion.", DETECTED_FAMILIES.size());

        // Now register missing variants
        VariantRegistrar.registerMissingBlocks(event.getRegistry(), new ArrayList<>(DETECTED_FAMILIES.values()));
    }

    public static List<BlockFamily> getDetectedFamilies() {
        return new ArrayList<>(DETECTED_FAMILIES.values());
    }
}
