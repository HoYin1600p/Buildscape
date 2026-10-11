package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.util.RandomSource;

public class DryGrassBlock extends BushBlock implements BonemealableBlock {
    public static final com.mojang.serialization.MapCodec<DryGrassBlock> CODEC = simpleCodec(DryGrassBlock::new);

    @Override
    protected com.mojang.serialization.MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    protected static final VoxelShape SHAPE = box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public DryGrassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return super.mayPlaceOn(state, level, pos) || state.is(net.minecraft.world.level.block.Blocks.SAND) || state.is(net.minecraft.world.level.block.Blocks.RED_SAND);
    }

    @Override
    public boolean isValidBonemealTarget(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // TODO S6b: register tall_dry_grass before this bonemeal path is used.
        level.setBlock(pos, Services.PLATFORM.getBlock(new CommonId("buildscape", "tall_dry_grass")).defaultBlockState(), 3);
    }
}
