package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;
public class MossLayersBlock extends SnowLayerBlock {

    protected final Supplier<Block> fullBlockSupplier;

    public MossLayersBlock(BlockBehaviour.Properties properties) {
        this(properties, () -> Blocks.MOSS_BLOCK);
    }

    public MossLayersBlock(BlockBehaviour.Properties properties, Supplier<Block> fullBlockSupplier) {
        super(properties);
        this.fullBlockSupplier = fullBlockSupplier != null ? fullBlockSupplier : () -> Blocks.MOSS_BLOCK;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        if (!belowState.is(Blocks.ICE) && !belowState.is(Blocks.PACKED_ICE) && !belowState.is(Blocks.BARRIER)) {
            if (!belowState.is(Blocks.HONEY_BLOCK) && !belowState.is(Blocks.SOUL_SAND)) {
                return Block.isFaceFull(belowState.getCollisionShape(level, pos.below()), Direction.UP)
                        || (belowState.is(this) && belowState.getValue(LAYERS) == 8);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int layers = state.getValue(LAYERS);
        if (context.getItemInHand().is(this.asItem()) && layers < 8) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }
        net.minecraft.world.item.Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof net.minecraft.world.item.BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();
            if (heldBlock instanceof SnowLayerBlock && heldBlock != this) {
                return false;
            }
        }
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if (blockState.is(this)) {
            int layers = blockState.getValue(LAYERS);
            return blockState.setValue(LAYERS, Math.min(8, layers + 1));
        }
        return super.getStateForPlacement(context);
    }

    public void onPlayerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getValue(LAYERS) == 8) {
            Block fullBlock = fullBlockSupplier.get();
            if (fullBlock != null) {
                level.setBlock(pos, fullBlock.defaultBlockState(), 3);
            }
        }
    }

    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos) {
        if (state.getValue(LAYERS) == 8) {
            Block fullBlock = fullBlockSupplier.get();
            if (fullBlock != null) {
                level.setBlock(pos, fullBlock.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int layers = state.getValue(LAYERS);
        if (layers == 8) {
            return Shapes.block();
        }
        return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double) (layers * 2) / 16.0D, 1.0D);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    public boolean isCommonPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(LAYERS) < 5;
    }
}
