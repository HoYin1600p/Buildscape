package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;
public abstract class DryGrassBlock extends BushBlock {
    protected static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    private final Supplier<Block> tallDryGrassSupplier;

    public DryGrassBlock(BlockBehaviour.Properties properties, Supplier<Block> tallDryGrassSupplier) {
        super(properties);
        this.tallDryGrassSupplier = tallDryGrassSupplier;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
    }

    public void growTall(ServerLevel level, BlockPos pos) {
        Block tall = tallDryGrassSupplier != null ? tallDryGrassSupplier.get() : null;
        if (tall != null && tall != Blocks.AIR) {
            level.setBlock(pos, tall.defaultBlockState(), 3);
        }
    }
}
