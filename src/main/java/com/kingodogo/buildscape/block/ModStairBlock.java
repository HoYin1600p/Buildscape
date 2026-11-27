package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;
public class ModStairBlock extends StairBlock {
    @SuppressWarnings("unused")
    private final Supplier<?> dropItem;
    private final Block baseBlock;
    public ModStairBlock(BlockState baseState, BlockBehaviour.Properties properties) {
        super(safeBaseState(baseState), properties);
        this.dropItem = null;
        this.baseBlock = baseState.getBlock();
    }
    public ModStairBlock(BlockState baseState, BlockBehaviour.Properties properties, Supplier<?> dropItem) {
        super(safeBaseState(baseState), properties);
        this.dropItem = dropItem;
        this.baseBlock = baseState.getBlock();
    }
    private static BlockState safeBaseState(BlockState baseState) {
        if (baseState.getProperties().isEmpty()) {
            return baseState;
        }
        return Blocks.OAK_PLANKS.defaultBlockState();
    }
    public Block getBaseBlock() {
        return this.baseBlock;
    }
    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    public boolean isTintedBase() { return isTintedGlassLike(); }
}
