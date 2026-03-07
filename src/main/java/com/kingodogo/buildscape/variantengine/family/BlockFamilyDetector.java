package com.kingodogo.buildscape.variantengine.family;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockFamilyDetector {

    /**
     * Attempts to find or create a family for a given block based on naming conventions and properties.
     */
    public static BlockFamily detectFamily(Block block) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;

        // Apply filters: no BuildScape blocks, NO fluids, no EntityBlocks (machines)
        if (id.getNamespace().equals(com.kingodogo.buildscape.BuildScape.MODID)) return null;
        if (block instanceof net.minecraft.world.level.block.LiquidBlock) return null;
        if (block instanceof net.minecraft.world.level.block.EntityBlock) return null;

        Block base = findBaseBlock(block);
        if (base == null || base == net.minecraft.world.level.block.Blocks.AIR) return null;

        BlockFamily family = new BlockFamily(base);

        // Scan for other vanilla/modded relatives
        for (BlockShape shape : BlockShape.values()) {
            if (shape == BlockShape.BASE) continue;
            ResourceLocation variantId = getPredictedId(base.getRegistryName(), shape);
            Block variant = ForgeRegistries.BLOCKS.getValue(variantId);
            if (variant != null && variant != net.minecraft.world.level.block.Blocks.AIR) {
                family.addVariant(shape, variant);
            }
        }

        return family;
    }

    private static Block findBaseBlock(Block block) {
        ResourceLocation id = block.getRegistryName();
        if (id == null) return null;
        String path = id.getPath();

        if (block instanceof SlabBlock || path.endsWith("_slab")) {
            String baseName = path.endsWith("_slab") ? path.substring(0, path.length() - 5) : path;
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id.getNamespace(), baseName));
        }
        if (block instanceof StairBlock || path.endsWith("_stairs")) {
            String baseName = path.endsWith("_stairs") ? path.substring(0, path.length() - 7) : path;
            return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(id.getNamespace(), baseName));
        }

        // If it's a full block, it's the base
        return block;
    }

    private static ResourceLocation getPredictedId(ResourceLocation baseId, BlockShape shape) {
        String base = baseId.getPath();
        String namespace = baseId.getNamespace();
        if (shape == BlockShape.SLAB) return new ResourceLocation(namespace, base + "_slab");
        if (shape == BlockShape.STAIRS) return new ResourceLocation(namespace, base + "_stairs");
        if (shape == BlockShape.VERTICAL_SLAB) return new ResourceLocation(namespace, "vertical_" + base + "_slab");
        if (shape == BlockShape.VERTICAL_STAIRS) return new ResourceLocation(namespace, "vertical_" + base + "_stairs");
        return baseId;
    }
}
