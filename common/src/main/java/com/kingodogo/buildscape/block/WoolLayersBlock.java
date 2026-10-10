package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
public class WoolLayersBlock extends SnowLayerBlock {

    private final String colorName;
    private final Supplier<Item> carpetItemSupplier;

    public WoolLayersBlock(BlockBehaviour.Properties properties, String colorName, Supplier<Item> carpetItemSupplier) {
        super(properties);
        this.colorName = colorName;
        this.carpetItemSupplier = carpetItemSupplier;
    }

    public String getColorName() {
        return this.colorName;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (level.getFluidState(pos).getType() == Fluids.WATER) {
            return false;
        }

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

        Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();
            if (heldBlock instanceof WoolLayersBlock && heldBlock != this) {
                return false;
            }
            if (heldBlock instanceof SnowLayerBlock && !(heldBlock instanceof WoolLayersBlock)) {
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

    public List<ItemStack> getReferenceDrops(BlockState state) {
        int layerCount = state.getValue(LAYERS);
        Item carpet = carpetItemSupplier != null ? carpetItemSupplier.get() : null;
        return Collections.singletonList(new ItemStack(carpet != null ? carpet : this.asItem(), layerCount));
    }
}
