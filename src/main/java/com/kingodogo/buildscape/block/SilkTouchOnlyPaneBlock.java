package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class SilkTouchOnlyPaneBlock extends IronBarsBlock {

    public SilkTouchOnlyPaneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            BlockPos beaconPos
    ) {
        int color = state.getMapColor(level, pos).col;
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(
            BlockState state,
            BlockGetter reader,
            BlockPos pos
    ) {
        return true;
    }

    @Override
    public float getShadeBrightness(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return 1.0F;
    }

    @Override
    public net.minecraft.world.phys.shapes.VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            net.minecraft.world.phys.shapes.CollisionContext context
    ) {
        return net.minecraft.world.phys.shapes.Shapes.empty();
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side
    ) {
        return (
                adjacentBlockState.is(this) ||
                        super.skipRendering(state, adjacentBlockState, side)
        );
    }

    public boolean shouldDisplayFluidOverlay(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            FluidState fluidState
    ) {
        return true;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() instanceof DyeItem dyeItem) {
            String dyeColorName = dyeItem.getDyeColor().getName();
            String currentName = ForgeRegistries.BLOCKS.getKey(this).getPath();

            String newName = null;
            if (currentName.startsWith("factory_")) {
                // factory_white_glass_pane -> factory_red_glass_pane
                newName = "factory_" + dyeColorName + "_glass_pane";
            } else if (currentName.contains("_mosaic_glass_pane")) {
                // white_mosaic_glass_pane -> red_mosaic_glass_pane
                newName = dyeColorName + "_mosaic_glass_pane";
            } else if (currentName.contains("_glazed_glass_pane")) {
                // white_glazed_glass_pane -> red_glazed_glass_pane
                newName = dyeColorName + "_glazed_glass_pane";
            }

            if (newName != null) {
                Block newBlock = ForgeRegistries.BLOCKS.getValue(
                        new net.minecraft.resources.ResourceLocation("buildscape", newName)
                );
                if (newBlock != null && newBlock != this) {
                    if (!level.isClientSide) {
                        BlockState newState = newBlock.defaultBlockState();
                        // Preserve connections (IronBarsBlock uses North, South, East, West, Waterlogged)
                        if (state.hasProperty(NORTH))
                            newState = newState.setValue(NORTH, state.getValue(NORTH));
                        if (state.hasProperty(SOUTH))
                            newState = newState.setValue(SOUTH, state.getValue(SOUTH));
                        if (state.hasProperty(EAST))
                            newState = newState.setValue(EAST, state.getValue(EAST));
                        if (state.hasProperty(WEST))
                            newState = newState.setValue(WEST, state.getValue(WEST));
                        if (state.hasProperty(WATERLOGGED))
                            newState = newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));

                        level.setBlock(pos, newState, 3);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        level.playSound(
                                null,
                                pos,
                                net.minecraft.sounds.SoundEvents.DYE_USE,
                                net.minecraft.sounds.SoundSource.BLOCKS,
                                1.0f,
                                1.0f
                        );
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
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
