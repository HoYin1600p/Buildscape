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

    private static final List<BlockFamily> DETECTED_FAMILIES = new ArrayList<>();

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        BuildScape.LOGGER.info("VariantEngine: Scanning registries for block families...");

        // Use ForgeRegistries.BLOCKS as a source (it contains blocks registered before us)
        for (Block block : ForgeRegistries.BLOCKS) {
            BlockFamily family = BlockFamilyDetector.detectFamily(block);
            if (family != null) {
                DETECTED_FAMILIES.add(family);
            }
        }

        BuildScape.LOGGER.info("VariantEngine: Found {} block families for completion.", DETECTED_FAMILIES.size());

        // Now register missing variants
        VariantRegistrar.registerMissingBlocks(event.getRegistry(), DETECTED_FAMILIES);
    }

    public static List<BlockFamily> getDetectedFamilies() {
        return DETECTED_FAMILIES;
    }
}
