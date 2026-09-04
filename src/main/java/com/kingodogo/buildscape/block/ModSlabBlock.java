package com.kingodogo.buildscape.block;

import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ModSlabBlock extends SlabBlock {

    private final Block baseBlock;

    public ModSlabBlock(
            Block baseBlock,
            BlockBehaviour.Properties properties
    ) {
        super(safeProperties(baseBlock, properties));
        this.baseBlock = baseBlock;
    }

    protected ModSlabBlock(
            Block baseBlock,
            BlockBehaviour.Properties properties,
            boolean preserveProperties
    ) {
        super(preserveProperties ? properties : safeProperties(baseBlock, properties));
        this.baseBlock = baseBlock;
    }

    public ModSlabBlock(
            BlockBehaviour.Properties properties
    ) {
        super(properties);
        this.baseBlock = null;
    }

    public Block getBaseBlock() {
        return this.baseBlock;
    }

    private static BlockBehaviour.Properties safeProperties(
            Block baseBlock,
            BlockBehaviour.Properties properties
    ) {
        if (baseBlock == null || baseBlock.defaultBlockState().getProperties().isEmpty()) {
            return properties;
        }

        BlockState baseState = baseBlock.defaultBlockState();
        SoundType soundType = baseBlock.getSoundType(baseState);
        BlockBehaviour.Properties safeProperties = BlockBehaviour.Properties
                .of(baseState.getMaterial())
                .strength(safeDestroySpeed(baseBlock), baseBlock.getExplosionResistance())
                .sound(soundType);

        if (baseState.requiresCorrectToolForDrops()) {
            safeProperties.requiresCorrectToolForDrops();
        }

        return safeProperties;
    }

    private static float safeDestroySpeed(Block baseBlock) {
        if (baseBlock == Blocks.AZALEA_LEAVES || baseBlock == Blocks.FLOWERING_AZALEA_LEAVES) {
            return 0.2f;
        }

        if (baseBlock == Blocks.REDSTONE_LAMP) {
            return 0.3f;
        }

        if (baseBlock == Blocks.HAY_BLOCK) {
            return 0.5f;
        }

        if (baseBlock == Blocks.CARVED_PUMPKIN || baseBlock == Blocks.TARGET) {
            return 1.0f;
        }

        if (baseBlock == Blocks.PURPUR_PILLAR) {
            return 1.5f;
        }

        return 2.0f;
    }

    private boolean isGlassLike() {
        return this.baseBlock instanceof AbstractGlassBlock && !this.isTintedGlassLike();
    }

    private boolean isTintedGlassLike() {
        return this.baseBlock == Blocks.TINTED_GLASS;
    }

    @Override
    public float[] getBeaconColorMultiplier(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos, net.minecraft.core.BlockPos beaconPos) {
        return null;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        return !this.isTintedGlassLike() && (this.isGlassLike() || super.propagatesSkylightDown(state, level, pos));
    }

    @Override
    public int getLightBlock(BlockState state, net.minecraft.world.level.BlockGetter level, net.minecraft.core.BlockPos pos) {
        if (this.isTintedGlassLike()) {
            return 15;
        }

        return super.getLightBlock(state, level, pos);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !this.isGlassLike() && super.useShapeForLightOcclusion(state);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, net.minecraft.core.Direction side) {
        return (this.isGlassLike() && adjacentBlockState.is(this)) || super.skipRendering(state, adjacentBlockState, side);
    }
}
