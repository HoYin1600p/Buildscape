package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.FireworkStarItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InfinitePhoenixFireworkStarItem extends FireworkStarItem {

    public InfinitePhoenixFireworkStarItem(Item.Properties properties) {
        super(properties);
    }

    public static ItemStack createDefaultStack() {
        ItemStack stack = new ItemStack(ModItems.INFINITE_PHOENIX_FIREWORK_STAR.get());
        CompoundTag explosionTag = stack.getOrCreateTagElement("Explosion");
        explosionTag.putByte("Type", CustomFireworkShapeRegistry.PHOENIX_ID);
        explosionTag.putIntArray("Colors", new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00});
        explosionTag.putBoolean("Flicker", true);
        explosionTag.putBoolean("Trail", true);
        return stack;
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack stack) {
        return stack.copy();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(new TextComponent("§d§lInfinite Uses").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
    }
}
