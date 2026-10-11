package com.kingodogo.buildscape.cosmetic.sign;

import com.kingodogo.buildscape.item.ModItems;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class SignFrameInteractionHandler {

    public static InteractionResult handleRightClick(Player player, Level level, InteractionHand hand, BlockPos pos) {
        if (player == null || level == null || pos == null) return InteractionResult.PASS;
        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (!SignFrameAttachment.isValidSign(state, be)) {
            return InteractionResult.PASS;
        }

        SignBlockEntity sign = (SignBlockEntity) be;
        ItemStack heldItem = player.getItemInHand(hand);

        boolean isShears = heldItem.getItem() instanceof net.minecraft.world.item.ShearsItem
                || heldItem.is(net.minecraft.world.item.Items.SHEARS);
        boolean signHasFrame = SignFrameAttachment.hasFrame(sign);

        if (isShears && signHasFrame) {
            if (!level.isClientSide()) {
                SignFrameType currentFrame = SignFrameAttachment.getFrame(sign);
                SignFrameAttachment.setFrame(sign, SignFrameType.NONE);

                if (!player.getAbilities().instabuild && currentFrame.getItem() != null) {
                    ItemStack returnStack = new ItemStack(currentFrame.getItem());
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }

                Services.PLATFORM.hurtAndBreak(heldItem, 1, player, hand);
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            player.swing(hand);
            return Services.PLATFORM.sidedSuccess(level.isClientSide());
        }

        if (SignFrameType.STRINGLIGHT.getItem() != null && heldItem.getItem() == SignFrameType.STRINGLIGHT.getItem()) {
            if (!signHasFrame) {
                if (!level.isClientSide()) {
                    SignFrameAttachment.setFrame(sign, SignFrameType.STRINGLIGHT);

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                player.swing(hand);
                return Services.PLATFORM.sidedSuccess(level.isClientSide());
            } else {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.PASS;
    }
}
