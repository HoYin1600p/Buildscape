package com.kingodogo.buildscape.compat.vertical;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class VerticalScanner {

    public static List<Block> findSlabs(IForgeRegistry<Block> registry) {
        List<Block> slabs = new ArrayList<>();
        for (Block block : registry) {
            if (block instanceof SlabBlock && !(block instanceof VerticalSlabBlock)) {
                ResourceLocation id = block.getRegistryName();
                String path = id.getPath();
                if (path.contains("vertical") || path.contains("upright") || path.contains("standing")) continue;
                
                slabs.add(block);
            }
        }
        return slabs;
    }

    public static List<Block> findStairs(IForgeRegistry<Block> registry) {
        List<Block> stairs = new ArrayList<>();
        for (Block block : registry) {
            if (block instanceof StairBlock && !(block instanceof VerticalStairsBlock)) {
                ResourceLocation id = block.getRegistryName();
                String path = id.getPath();
                if (path.contains("vertical") || path.contains("upright") || path.contains("standing")) continue;
                
                stairs.add(block);
            }
        }
        return stairs;
    }
}
