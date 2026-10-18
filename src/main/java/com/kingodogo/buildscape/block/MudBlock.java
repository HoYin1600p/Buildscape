package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class MudBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(
            0.0D,
            0.0D,
            0.0D,
            16.0D,
            15.0D,
            16.0D
    );

    public MudBlock(BlockBehaviour.Properties properties) {
        super(properties.randomTicks());
    }

    private boolean canDry(Level level, BlockPos pos) {
        BlockState dripstone = level.getBlockState(pos.below(2));
        return level.dimension() != Level.NETHER
                && !level.getBlockState(pos.below()).isAir()
                && dripstone.is(Blocks.POINTED_DRIPSTONE)
                && dripstone.getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN;
    }

    private void scheduleDrying(Level level, BlockPos pos) {
        if (!level.isClientSide && canDry(level, pos)) {
            level.scheduleTick(pos, this, 20 + level.random.nextInt(21));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        scheduleDrying(level, pos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock,
                                BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        scheduleDrying(level, pos);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        scheduleDrying(level, pos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (canDry(level, pos)) {
            level.setBlock(pos, Blocks.CLAY.defaultBlockState(), 3);
        }
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }
}
