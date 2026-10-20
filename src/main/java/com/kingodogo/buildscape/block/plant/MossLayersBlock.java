package com.kingodogo.buildscape.block.plant;

import com.kingodogo.buildscape.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
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
import java.util.Random;
import java.util.function.Supplier;

public class MossLayersBlock extends SnowLayerBlock {

    private final Supplier<Block> fullBlock;

    public MossLayersBlock(Properties properties) {
        this(properties, () -> Blocks.MOSS_BLOCK);
    }

    public MossLayersBlock(Properties properties, Supplier<Block> fullBlock) {
        super(properties);
        this.fullBlock = fullBlock;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos.below());
        if (!blockState.is(Blocks.ICE) &&
                !blockState.is(Blocks.PACKED_ICE) &&
                !blockState.is(Blocks.BARRIER)) {
            if (!blockState.is(Blocks.HONEY_BLOCK) && !blockState.is(Blocks.SOUL_SAND)) {
                return (Block.isFaceFull(
                        blockState.getCollisionShape(level, pos.below()),
                        Direction.UP) ||
                        (blockState.is(this) && blockState.getValue(LAYERS) == 8));
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

        Item heldItem = context.getItemInHand().getItem();
        if (heldItem instanceof BlockItem blockItem) {
            Block heldBlock = blockItem.getBlock();
            if (heldBlock instanceof SnowLayerBlock && heldBlock != this) {
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

    public void playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player) {
        if (state.getValue(LAYERS) == 8) {
            level.setBlock(pos, fullBlock.get().defaultBlockState(), 3);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(LAYERS) == 8;
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            Random random) {
        if (state.getValue(LAYERS) == 8) {
            level.setBlock(pos, fullBlock.get().defaultBlockState(), 3);
        }
    }

    @Override
    public void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void wasExploded(Level level, BlockPos pos, Explosion explosion) {
        super.wasExploded(level, pos, explosion);
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return getCollisionShape(state, level, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
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
            CollisionContext context) {
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
            PathComputationType type) {
        if (type == PathComputationType.LAND) {
            return state.getValue(LAYERS) < 5;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(
            BlockState state,
            LootContext.Builder builder) {
        int layerCount = state.getValue(LAYERS);

        return List.of(new ItemStack(ModItems.MOSS_LAYERS.get(), layerCount));
    }

}
