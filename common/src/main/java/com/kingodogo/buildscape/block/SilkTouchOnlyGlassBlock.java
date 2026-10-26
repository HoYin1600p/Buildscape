package com.kingodogo.buildscape.block;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public class SilkTouchOnlyGlassBlock extends HalfTransparentBlock implements ICommonInteractable, net.minecraft.world.level.block.BeaconBeamBlock {

    /** Marks the block as a beam colour source for vanilla's scan (Fabric); the real colour is its map colour. */
    @Override
    public net.minecraft.world.item.DyeColor getColor() { return net.minecraft.world.item.DyeColor.WHITE; }

    public SilkTouchOnlyGlassBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    public boolean shouldDisplayFluidOverlay(BlockState state, BlockGetter level, BlockPos pos, FluidState fluidState) {
        return true;
    }

    @Override
    public InteractionResult onInteract(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        net.minecraft.world.item.DyeColor dyeColor = Services.PLATFORM.getDyeColor(heldItem);
        if (dyeColor != null) {
            String dyeColorName = dyeColor.getName();
            CommonId currentId = Services.PLATFORM.getBlockId(this);
            String currentName = currentId != null ? currentId.getPath() : "";

            String newName = null;
            if (currentName.startsWith("factory_") && currentName.endsWith("_glass")) {
                newName = "factory_" + dyeColorName + "_glass";
            } else if (currentName.endsWith("_mosaic_glass")) {
                newName = dyeColorName + "_mosaic_glass";
            } else if (currentName.endsWith("_glazed_glass")) {
                newName = dyeColorName + "_glazed_glass";
            }

            if (newName != null) {
                Block newBlock = Services.PLATFORM.getBlock(new CommonId("buildscape", newName));
                if (newBlock != null && newBlock != this) {
                    if (!level.isClientSide()) {
                        level.setBlock(pos, newBlock.defaultBlockState(), 3);
                        if (!player.getAbilities().instabuild) {
                            heldItem.shrink(1);
                        }
                        Services.PLATFORM.playBlockSound(level, pos, new CommonId("minecraft", "item.dye.use"));
                    }
                    return Services.PLATFORM.sidedSuccess(level.isClientSide());
                }
            }
        }
        return InteractionResult.PASS;
    }
}
