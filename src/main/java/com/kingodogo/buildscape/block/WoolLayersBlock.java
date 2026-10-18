package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class WoolLayersBlock extends SnowLayerBlock {

    private final String colorName;

    public WoolLayersBlock(Properties properties, String colorName) {
        super(properties);
        this.colorName = colorName;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (
                level.getFluidState(pos).getType() ==
                        net.minecraft.world.level.material.Fluids.WATER
        ) {
            return false;
        }

        BlockState blockState = level.getBlockState(pos.below());
        if (
                !blockState.is(Blocks.ICE) &&
                        !blockState.is(Blocks.PACKED_ICE) &&
                        !blockState.is(Blocks.BARRIER)
        ) {
            if (
                    !blockState.is(Blocks.HONEY_BLOCK) && !blockState.is(Blocks.SOUL_SAND)
            ) {
                return (
                        Block.isFaceFull(
                                blockState.getCollisionShape(level, pos.below()),
                                Direction.UP
                        ) ||
                                (blockState.is(this) && blockState.getValue(LAYERS) == 8)
                );
            } else {
                return true;
            }
        } else {
            return false;
        }
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
        if (heldItem instanceof net.minecraft.world.item.BlockItem) {
            net.minecraft.world.level.block.Block heldBlock =
                    ((net.minecraft.world.item.BlockItem) heldItem).getBlock();
            if (heldBlock instanceof WoolLayersBlock && heldBlock != this) {
                return false;
            }
            if (
                    heldBlock instanceof SnowLayerBlock &&
                            !(heldBlock instanceof WoolLayersBlock)
            ) {
                return false;
            }
        }

        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = context
                .getLevel()
                .getBlockState(context.getClickedPos());
        if (blockState.is(this)) {
            int i = blockState.getValue(LAYERS);
            return blockState.setValue(LAYERS, Math.min(8, i + 1));
        } else {
            return super.getStateForPlacement(context);
        }
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        int i = state.getValue(LAYERS);
        if (i == 8) {
            return Shapes.block();
        } else {
            return Shapes.box(0.0, 0.0, 0.0, 1.0, (double) (i * 2) / 16.0, 1.0);
        }
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    public boolean isPathfindable(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            PathComputationType type
    ) {
        if (type == PathComputationType.LAND) {
            return state.getValue(LAYERS) < 5;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder
    ) {
        int layerCount = state.getValue(LAYERS);
        return List.of(new ItemStack(this.asItem(), layerCount));
    }

}
