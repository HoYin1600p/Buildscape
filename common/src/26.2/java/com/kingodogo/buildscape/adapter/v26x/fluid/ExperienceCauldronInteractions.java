package com.kingodogo.buildscape.adapter.v26x.fluid;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.CommonId;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

/** The reference experience-only transfers for an already filled cauldron. */
public final class ExperienceCauldronInteractions {
    public enum Transfer { TAKE_BUCKET, TAKE_BOTTLE, ADD_BUCKET, ADD_BOTTLE }

    private ExperienceCauldronInteractions() {}

    /** -1 means that the transfer cannot occur at this level. */
    public static int nextLevel(int level, Transfer transfer) {
        if (level < 1 || level > 3) throw new IllegalArgumentException("Invalid cauldron level: " + level);
        return switch (transfer) {
            case TAKE_BUCKET -> level == 3 ? 0 : -1;
            case TAKE_BOTTLE -> level - 1;
            case ADD_BUCKET -> level < 3 ? 3 : -1;
            case ADD_BOTTLE -> level < 3 ? level + 1 : -1;
        };
    }

    public static InteractionResult use(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand) {
        Item xpBucket = Services.PLATFORM.getItem(new CommonId("buildscape", "experience_bucket"));
        Transfer transfer;
        Item result;
        SoundEvent sound;
        if (stack.is(Items.BUCKET)) {
            transfer = Transfer.TAKE_BUCKET;
            result = xpBucket;
            sound = SoundEvents.BUCKET_FILL;
        } else if (stack.is(Items.GLASS_BOTTLE)) {
            transfer = Transfer.TAKE_BOTTLE;
            result = Items.EXPERIENCE_BOTTLE;
            sound = SoundEvents.BOTTLE_FILL;
        } else if (stack.is(xpBucket)) {
            transfer = Transfer.ADD_BUCKET;
            result = Items.BUCKET;
            sound = SoundEvents.BUCKET_EMPTY;
        } else if (stack.is(Items.EXPERIENCE_BOTTLE)) {
            transfer = Transfer.ADD_BOTTLE;
            result = Items.GLASS_BOTTLE;
            sound = SoundEvents.BOTTLE_EMPTY;
        } else {
            return InteractionResult.PASS;
        }
        int next = nextLevel(state.getValue(LayeredCauldronBlock.LEVEL), transfer);
        if (next < 0) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            boolean filling = transfer == Transfer.ADD_BUCKET || transfer == Transfer.ADD_BOTTLE;
            player.awardStat(filling ? Stats.FILL_CAULDRON : Stats.USE_CAULDRON);
            level.setBlockAndUpdate(pos, next == 0 ? Blocks.CAULDRON.defaultBlockState()
                    : state.setValue(LayeredCauldronBlock.LEVEL, next));
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack returned = new ItemStack(result);
                if (stack.isEmpty()) {
                    player.setItemInHand(hand, returned);
                } else if (!player.getInventory().add(returned)) {
                    player.drop(returned, false);
                }
            }
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }
}
