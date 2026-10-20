package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SnowOverlayBlock extends Block {

    protected static final VoxelShape SHAPE = Block.box(
            0.0,
            0.0,
            0.0,
            16.0,
            1.0,
            16.0);

    public SnowOverlayBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        if (blockState.isAir() || blockState.is(Blocks.BARRIER)) {
            return false;
        }
        return true;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos) {
        return !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(
                        state,
                        direction,
                        neighborState,
                        level,
                        pos,
                        neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) {
            BlockPos placePos = context.getClickedPos();
            BlockState clickedState = context.getLevel().getBlockState(placePos);
            if (!clickedState.canBeReplaced(context)) {
                placePos = placePos.relative(context.getClickedFace());
            }
            if (this.canSurvive(state, context.getLevel(), placePos)) {
                return state;
            }
        }
        return null;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        return true;
    }

    public boolean isPathfindable(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            PathComputationType type) {
        return type == PathComputationType.LAND;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

}
