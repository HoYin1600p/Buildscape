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
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class SilkTouchOnlyGlassBlock extends AbstractGlassBlock {

    public SilkTouchOnlyGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public float[] getBeaconColorMultiplier(
            BlockState state,
            LevelReader level,
            BlockPos pos,
            BlockPos beaconPos) {
        int color = state.getMapColor(level, pos).col;
        return new float[]{
                ((color >> 16) & 0xFF) / 255.0f,
                ((color >> 8) & 0xFF) / 255.0f,
                (color & 0xFF) / 255.0f
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(
            BlockState state,
            BlockState adjacentBlockState,
            Direction side) {
        return (adjacentBlockState.is(this) ||
                super.skipRendering(state, adjacentBlockState, side));
    }

    public boolean shouldDisplayFluidOverlay(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            FluidState fluidState) {
        return true;
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.getItem() instanceof DyeItem dyeItem) {
            String dyeColorName = dyeItem.getDyeColor().getName();
            String currentName = ForgeRegistries.BLOCKS.getKey(this).getPath();

            String newName = null;
            if (currentName.startsWith("factory_") && currentName.endsWith("_glass")) {
                newName = "factory_" + dyeColorName + "_glass";
            } else if (currentName.endsWith("_mosaic_glass")) {
                newName = dyeColorName + "_mosaic_glass";
            } else if (currentName.endsWith("_glazed_glass")) {
                newName = dyeColorName + "_glazed_glass";
            }

            if (newName != null) {
                Block newBlock = ForgeRegistries.BLOCKS.getValue(
                        new ResourceLocation("buildscape", newName));
                if (newBlock != null && newBlock != this) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, newBlock.defaultBlockState(), 3);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        level.playSound(
                                null,
                                pos,
                                SoundEvents.DYE_USE,
                                SoundSource.BLOCKS,
                                1.0f,
                                1.0f);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

}
