package com.kingodogo.buildscape.variantengine.family;

import com.kingodogo.buildscape.variantengine.builder.BlockShape;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a group of related blocks (e.g. Marble, Marble Slab, Marble Stairs).
 */
public class BlockFamily {
    private final Block baseBlock;
    private final Map<BlockShape, Block> variants = new HashMap<>();

    public BlockFamily(Block baseBlock) {
        this.baseBlock = baseBlock;
        this.variants.put(BlockShape.BASE, baseBlock);
    }

    public Block getBaseBlock() {
        return baseBlock;
    }

    public void addVariant(BlockShape shape, Block block) {
        variants.put(shape, block);
    }

    public Optional<Block> getVariant(BlockShape shape) {
        return Optional.ofNullable(variants.get(shape));
    }

    public boolean hasVariant(BlockShape shape) {
        return variants.containsKey(shape);
    }

    public Map<BlockShape, Block> getVariants() {
        return variants;
    }
}
