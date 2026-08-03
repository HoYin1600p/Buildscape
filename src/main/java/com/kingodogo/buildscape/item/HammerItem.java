package com.kingodogo.buildscape.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

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
    }

    private final HammerTier tier;

    public HammerItem(HammerTier tier, Properties properties) {
        super(properties.stacksTo(1).durability(tier.getDurability()));
        this.tier = tier;
    }

    public HammerTier getHammerTier() {
        return tier;
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
            case IRON -> repair.is(net.minecraft.world.item.Items.IRON_INGOT);
            case DIAMOND -> repair.is(net.minecraft.world.item.Items.DIAMOND);
            case NETHERITE -> repair.is(net.minecraft.world.item.Items.NETHERITE_INGOT);
        };
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Override
    public net.minecraft.world.InteractionResult useOn(net.minecraft.world.item.context.UseOnContext context) {
        if (context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND) {
            ItemStack offHand = context.getPlayer().getOffhandItem();
            if (!offHand.isEmpty() && offHand.getItem() instanceof net.minecraft.world.item.BlockItem) {
                return net.minecraft.world.InteractionResult.sidedSuccess(context.getLevel().isClientSide());
            }
        }
        return net.minecraft.world.InteractionResult.PASS;
    }

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(net.minecraft.world.level.Level level, net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand) {
        if (hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
            ItemStack offHand = player.getOffhandItem();
            if (!offHand.isEmpty() && offHand.getItem() instanceof net.minecraft.world.item.BlockItem) {
                return net.minecraft.world.InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
            }
        }
        return net.minecraft.world.InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public void appendHoverText(net.minecraft.world.item.ItemStack stack, @javax.annotation.Nullable net.minecraft.world.level.Level level, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.hammer.desc1").withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.hammer.desc2").withStyle(net.minecraft.ChatFormatting.RED));
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("tooltip.buildscape.hammer.desc3").withStyle(net.minecraft.ChatFormatting.AQUA));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
