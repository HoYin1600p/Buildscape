package com.kingodogo.buildscape.item;

import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class InfinitePhoenixFireworkStarItem extends Item {

    public InfinitePhoenixFireworkStarItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createDefaultStack() {
        Item item = Services.PLATFORM.getItem(com.kingodogo.buildscape.util.CommonId.of("buildscape", "infinite_phoenix_firework_star"));
        ItemStack stack = new ItemStack(item != null ? item : net.minecraft.world.item.Items.FIREWORK_STAR);
        Services.PLATFORM.configureFireworkStar(stack,
                com.kingodogo.buildscape.firework.CustomFireworkShapeRegistry.PHOENIX_ID,
                new int[]{0xFFFFFF, 0xFFF200, 0xFFB000, 0xFF6500, 0xE52B00}, true, true);
        return stack;
    }

    public static void appendInfiniteUsesTooltip(Consumer<Component> tooltip) {
        tooltip.accept(ComponentHelper.literal("§d§lInfinite Uses").withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD));
    }
}
