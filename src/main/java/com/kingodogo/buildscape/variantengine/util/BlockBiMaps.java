package com.kingodogo.buildscape.variantengine.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Handles mapping between base blocks and their variants.
 */
public final class BlockBiMaps {
    public static final LinkedHashSet<Block> BASE_BLOCKS = new LinkedHashSet<>();
    private static final Map<BlockShape, BiMap<Block, Block>> SHAPE_TO_BI_MAP = new HashMap<>();
    private BlockBiMaps() {
    }

    /**
     * Get the BiMap for a specific shape.
     */
    public static @NotNull BiMap<Block, Block> of(@NotNull BlockShape shape) {
        return SHAPE_TO_BI_MAP.computeIfAbsent(shape, shape1 -> HashBiMap.create());
    }

    /**
     * Get the variant block for a given shape and base block.
     */
    @Nullable
    public static Block getBlockOf(@NotNull BlockShape shape, @Nullable Block baseBlock) {
        if (baseBlock == null) return null;
        return of(shape).get(baseBlock);
    }

    /**
     * Map a generated variant to its base block.
     */
    public static void setBlockOf(@NotNull BlockShape shape, @NotNull Block baseBlock, @NotNull Block block) {
        final BiMap<Block, Block> biMap = of(shape);
        biMap.put(baseBlock, block);
        BASE_BLOCKS.add(baseBlock);
    }

    /**
     * Get the base block for a generated variant.
     */
    @Nullable
    public static Block getBaseBlockOf(@NotNull BlockShape shape, @Nullable Block variantBlock) {
        if (variantBlock == null) return null;
        return of(shape).inverse().get(variantBlock);
    }
}
