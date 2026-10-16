package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;

import java.util.function.Consumer;

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

    public int getEnchantmentValue() {
        return 14;
    }

    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return switch (tier) {
            case IRON -> repair.is(Items.IRON_INGOT);
            case DIAMOND -> repair.is(Items.DIAMOND);
            case NETHERITE -> repair.is(Items.NETHERITE_INGOT);
        };
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    public boolean supportsEnchantment(ItemStack stack, Object enchantment) {
        return Services.PLATFORM.supportsHammerEnchantment(stack, enchantment);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getHand() == InteractionHand.MAIN_HAND && context.getPlayer() != null) {
            ItemStack offHand = context.getPlayer().getOffhandItem();
            if (!offHand.isEmpty() && offHand.getItem() instanceof BlockItem) {
                return Services.PLATFORM.sidedSuccess(context.getLevel().isClientSide());
            }
        }
        return InteractionResult.PASS;
    }

    public void appendHammerTooltip(Consumer<Component> tooltip) {
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.hammer.desc1").withStyle(ChatFormatting.GRAY));
        if (tier == HammerTier.IRON) {
            tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.hammer.desc2").withStyle(ChatFormatting.RED));
        }
        tooltip.accept(ComponentHelper.translatable("tooltip.buildscape.hammer.desc3").withStyle(ChatFormatting.AQUA));
    }
}
