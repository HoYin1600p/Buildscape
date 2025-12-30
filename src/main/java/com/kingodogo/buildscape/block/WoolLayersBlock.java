package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.item.ModItems;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
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

        if (i == 1) {
            return true;
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
        return switch (colorName) {
            case "black" -> List.of(
                    new ItemStack(ModItems.BLACK_CARPET_LAYERS.get(), layerCount)
            );
            case "blue" -> List.of(
                    new ItemStack(ModItems.BLUE_CARPET_LAYERS.get(), layerCount)
            );
            case "brown" -> List.of(
                    new ItemStack(ModItems.BROWN_CARPET_LAYERS.get(), layerCount)
            );
            case "cyan" -> List.of(
                    new ItemStack(ModItems.CYAN_CARPET_LAYERS.get(), layerCount)
            );
            case "gray" -> List.of(
                    new ItemStack(ModItems.GRAY_CARPET_LAYERS.get(), layerCount)
            );
            case "green" -> List.of(
                    new ItemStack(ModItems.GREEN_CARPET_LAYERS.get(), layerCount)
            );
            case "light_blue" -> List.of(
                    new ItemStack(ModItems.LIGHT_BLUE_CARPET_LAYERS.get(), layerCount)
            );
            case "light_gray" -> List.of(
                    new ItemStack(ModItems.LIGHT_GRAY_CARPET_LAYERS.get(), layerCount)
            );
            case "lime" -> List.of(
                    new ItemStack(ModItems.LIME_CARPET_LAYERS.get(), layerCount)
            );
            case "magenta" -> List.of(
                    new ItemStack(ModItems.MAGENTA_CARPET_LAYERS.get(), layerCount)
            );
            case "orange" -> List.of(
                    new ItemStack(ModItems.ORANGE_CARPET_LAYERS.get(), layerCount)
            );
            case "pink" -> List.of(
                    new ItemStack(ModItems.PINK_CARPET_LAYERS.get(), layerCount)
            );
            case "purple" -> List.of(
                    new ItemStack(ModItems.PURPLE_CARPET_LAYERS.get(), layerCount)
            );
            case "red" -> List.of(
                    new ItemStack(ModItems.RED_CARPET_LAYERS.get(), layerCount)
            );
            case "white" -> List.of(
                    new ItemStack(ModItems.WHITE_CARPET_LAYERS.get(), layerCount)
            );
            case "yellow" -> List.of(
                    new ItemStack(ModItems.YELLOW_CARPET_LAYERS.get(), layerCount)
            );
            default -> List.of(
                    new ItemStack(ModItems.WHITE_CARPET_LAYERS.get(), layerCount)
            );
        };
    }

    @Override
    public float getDestroyProgress(
            BlockState state,
            Player player,
            BlockGetter level,
            BlockPos pos
    ) {
        float destroySpeed = state.getDestroySpeed(level, pos);
        if (destroySpeed == -1.0F) {
            return 0.0F;
        }

        int efficiencyLevel =
                net.minecraft.world.item.enchantment.EnchantmentHelper.getBlockEfficiency(
                        player
                );
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
}
