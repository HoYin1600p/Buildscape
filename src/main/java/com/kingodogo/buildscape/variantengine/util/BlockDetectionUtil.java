package com.kingodogo.buildscape.variantengine.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockDetectionUtil {

    public static boolean isSlab(Block block) {
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) return false;
        ResourceLocation id = block.getRegistryName();
        if (id == null) return false;
        String path = id.getPath().toLowerCase();

        // 1. High-fidelity checks
        if (block instanceof SlabBlock || block.defaultBlockState().hasProperty(BlockStateProperties.SLAB_TYPE)) return true;
        if (block.defaultBlockState().is(net.minecraft.tags.BlockTags.SLABS)) return true;

        // 2. Strict Word-Boundary Keyword Check (Prevents 'yellow_concrete' from matching)
        // Matches "slab" but not "slabby" or "aslab"
        if (path.matches(".*\\bslab\\b.*") || path.endsWith("_slab") || path.startsWith("slab_")) return true;
        
        return checkClassHierarchyName(block, "Slab");
    }

    public static boolean isStair(Block block) {
        if (block == null || block == net.minecraft.world.level.block.Blocks.AIR) return false;
        ResourceLocation id = block.getRegistryName();
        if (id == null) return false;
        String path = id.getPath().toLowerCase();
        
        // 1. High-fidelity checks
        if (block instanceof StairBlock || block.defaultBlockState().hasProperty(BlockStateProperties.STAIRS_SHAPE)) return true;
        if (block.defaultBlockState().is(net.minecraft.tags.BlockTags.STAIRS)) return true;

        // 2. Strict Word-Boundary Keyword Check
        if (path.matches(".*\\bstair(s)?\\b.*") || path.endsWith("_stair") || path.endsWith("_stairs")) return true;
        
        return checkClassHierarchyName(block, "Stair");
    }

    public static boolean checkClassHierarchyName(Block block, String keyword) {
        Class<?> clazz = block.getClass();
        while (clazz != null && clazz != Object.class) {
            if (clazz.getSimpleName().contains(keyword)) return true;
            clazz = clazz.getSuperclass();
        }
        return false;
    }
}
