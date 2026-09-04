package com.kingodogo.buildscape.cosmetic.sign;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.item.ModItems;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles applying and removing cosmetic frames on signs.
 * Prioritizes normal sign behavior; only intercepts when applying or removing a frame.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SignFrameInteractionHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        BlockEntity be = level.getBlockEntity(pos);

        if (!SignFrameAttachment.isValidSign(state, be)) {
            return;
        }

        SignBlockEntity sign = (SignBlockEntity) be;
        Player player = event.getPlayer();
        ItemStack heldItem = event.getItemStack();
        InteractionHand hand = event.getHand();

        boolean isShears = heldItem.getItem() instanceof net.minecraft.world.item.ShearsItem
                || heldItem.is(net.minecraft.world.item.Items.SHEARS);
        boolean signHasFrame = SignFrameAttachment.hasFrame(sign);

        // CASE 1: REMOVING THE FRAME (Right-Click a framed sign with Shears)
        if (isShears && signHasFrame) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));

            if (!level.isClientSide()) {
                SignFrameType currentFrame = SignFrameAttachment.getFrame(sign);
                SignFrameAttachment.setFrame(sign, SignFrameType.NONE);

                if (!player.getAbilities().instabuild && currentFrame.getItem() != null) {
                    ItemStack returnStack = new ItemStack(currentFrame.getItem());
                    if (!player.getInventory().add(returnStack)) {
                        player.drop(returnStack, false);
                    }
                }

                heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
                level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(player, net.minecraft.world.level.gameevent.GameEvent.SHEAR, pos);
            }

            player.swing(hand);
            return;
        }

        // CASE 2: APPLYING THE STRINGLIGHT FRAME (Right-Click with Stringlight Frame item)
        if (heldItem.is(ModItems.STRINGLIGHT_FRAME.get())) {
            if (!signHasFrame) {
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);

                if (!level.isClientSide()) {
                    SignFrameAttachment.setFrame(sign, SignFrameType.STRINGLIGHT);

                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }

                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                player.swing(hand);
                return;
            } else {
                // Sign already has a frame attached; prevent applying a second frame
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.FAIL);
                return;
            }
        }

        // CASE 3: Normal sign interaction (editing, dye, glow ink, commands, etc.) - do not intercept!
    }
}
