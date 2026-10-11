package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
public class LeafLayersBlock extends SnowLayerBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public LeafLayersBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        if (!blockState.is(Blocks.ICE) && !blockState.is(Blocks.PACKED_ICE) && !blockState.is(Blocks.BARRIER)) {
            if (!blockState.is(Blocks.HONEY_BLOCK) && !blockState.is(Blocks.SOUL_SAND)) {
                return Block.isFaceFull(blockState.getCollisionShape(level, pos.below()), Direction.UP)
                        || (blockState.is(this) && blockState.getValue(LAYERS) == 8);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        int i = state.getValue(LAYERS);
        if (context.getItemInHand().is(this.asItem()) && i < 8) {
            if (context.replacingClickedOnBlock()) {
                return context.getClickedFace() == Direction.UP;
            } else {
                return true;
            }
        }

        net.minecraft.world.item.Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof net.minecraft.world.item.BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();
            if (heldBlock instanceof LeafLayersBlock && heldBlock != this) {
                return false;
            }
            if (heldBlock instanceof SnowLayerBlock && !(heldBlock instanceof LeafLayersBlock)) {
                return false;
            }
            if (!(heldBlock instanceof LeafLayersBlock || heldBlock instanceof SnowLayerBlock)) {
                return false;
            }
        } else {
            return false;
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
        if (blockState.is(this)) {
            int i = blockState.getValue(LAYERS);
            return blockState.setValue(LAYERS, Math.min(8, i + 1))
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
        } else {
            BlockState state = super.getStateForPlacement(context);
            return state != null
                    ? state.setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                    : null;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int i = state.getValue(LAYERS);
        if (i == 8) {
            return Shapes.block();
        } else {
            return Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double) (i * 2) / 16.0D, 1.0D);
        }
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel = Services.PLATFORM.getEfficiencyLevel(player.getMainHandItem());
        ItemStack tool = player.getMainHandItem();

        float speedMultiplier = 1.0F;
        if (!tool.isEmpty()) {
            speedMultiplier = tool.getDestroySpeed(state);
        }

        if (speedMultiplier > 1.0F) {
            int efficiencyBonus = efficiencyLevel > 0
                    ? efficiencyLevel * efficiencyLevel + 1
                    : 0;
            speedMultiplier += (float) efficiencyBonus;
        }

        float difficultyModifier = player.hasCorrectToolForDrops(state)
                ? 30.0F
                : 100.0F;
        return speedMultiplier / destroySpeed / difficultyModifier;
    }

    public boolean isCommonPathfindable(BlockState state, PathComputationType type) {
        return type == PathComputationType.LAND && state.getValue(LAYERS) < 5;
    }
}
