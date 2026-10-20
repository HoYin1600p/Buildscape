package com.kingodogo.buildscape.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
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
                newName = "factory_" + dyeColorName + "_glass_pane";
            } else if (currentName.contains("_mosaic_glass_pane")) {
                newName = dyeColorName + "_mosaic_glass_pane";
            } else if (currentName.contains("_glazed_glass_pane")) {
                newName = dyeColorName + "_glazed_glass_pane";
            }

            if (newName != null) {
                Block newBlock = ForgeRegistries.BLOCKS.getValue(
                        new ResourceLocation("buildscape", newName)
                );
                if (newBlock != null && newBlock != this) {
                    if (!level.isClientSide) {
                        BlockState newState = newBlock.defaultBlockState();
                        if (state.hasProperty(NORTH)) {
                            newState = newState.setValue(NORTH, state.getValue(NORTH));
                        }
                        if (state.hasProperty(SOUTH)) {
                            newState = newState.setValue(SOUTH, state.getValue(SOUTH));
                        }
                        if (state.hasProperty(EAST)) {
                            newState = newState.setValue(EAST, state.getValue(EAST));
                        }
                        if (state.hasProperty(WEST)) {
                            newState = newState.setValue(WEST, state.getValue(WEST));
                        }
                        if (state.hasProperty(WATERLOGGED)) {
                            newState = newState.setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                        }

                        level.setBlock(pos, newState, 3);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        level.playSound(
                                null,
                                pos,
                                SoundEvents.DYE_USE,
                                SoundSource.BLOCKS,
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

}
