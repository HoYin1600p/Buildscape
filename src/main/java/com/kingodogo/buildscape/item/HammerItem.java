package com.kingodogo.buildscape.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import javax.annotation.Nullable;

public class HammerItem extends Item {

    public enum HammerTier {
        IRON(1024, false),
        DIAMOND(2048, true),
        NETHERITE(4096, true);

        private final int durability;
        private final boolean canReplaceObsidianLevel;

        HammerTier(int durability, boolean canReplaceObsidianLevel) {
            this.durability = durability;
            this.canReplaceObsidianLevel = canReplaceObsidianLevel;
        }

        public int getDurability() {
            return durability;
        }

        public boolean canReplaceObsidianLevel() {
            return canReplaceObsidianLevel;
        }

        public Item getHarvestToolItem() {
            return switch (this) {
                case IRON -> Items.IRON_PICKAXE;
                case DIAMOND -> Items.DIAMOND_PICKAXE;
                case NETHERITE -> Items.NETHERITE_PICKAXE;
            };
        }
    }

    private final HammerTier tier;

    public HammerItem(HammerTier tier, Properties properties) {
        super(properties.stacksTo(1).durability(tier.getDurability()));
        this.tier = tier;
    }

    public HammerTier getHammerTier() {
        return tier;
    }

    public Item getHarvestToolItem() {
        return tier.getHarvestToolItem();
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        // Forge's break event uses the held item to decide whether the block awards XP.
        return getHarvestToolItem().isCorrectToolForDrops(state);
    }

    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.SILK_TOUCH;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return switch (tier) {
            case IRON -> repair.is(Items.IRON_INGOT);
            case DIAMOND -> repair.is(Items.DIAMOND);
            case NETHERITE -> repair.is(Items.NETHERITE_INGOT);
        };
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.MAIN_HAND) {
            ItemStack offHand = context.getPlayer().getOffhandItem();
            if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND) {
            ItemStack offHand = player.getOffhandItem();
            if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
                return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TranslatableComponent("tooltip.buildscape.hammer.desc1").withStyle(ChatFormatting.GRAY));
        if (tier == HammerTier.IRON) {
            tooltip.add(new TranslatableComponent("tooltip.buildscape.hammer.desc2").withStyle(ChatFormatting.RED));
        }
        tooltip.add(new TranslatableComponent("tooltip.buildscape.hammer.desc3").withStyle(ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
