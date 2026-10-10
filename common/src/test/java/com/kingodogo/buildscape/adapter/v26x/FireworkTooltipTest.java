package com.kingodogo.buildscape.adapter.v26x;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FireworkTooltipTest {
    @Test void allCustomShapesReplaceTheVanillaShapeAndKeepTrailAndTwinkle() {
        String[] names = {"cake", "crown", "trophy", "christmas_tree", "presents", "candy_cane", "phoenix", "snowflake"};
        MixinFactory factory = new MixinFactory();
        for (int i = 0; i < names.length; i++) {
            CompoundTag explosion = new CompoundTag();
            explosion.putByte("Type", (byte) (5 + i));
            explosion.putBoolean("Flicker", true);
            explosion.putBoolean("Trail", true);
            var tooltip = new ArrayList<Component>();
            assertTrue(factory.appendFireworkCustomHoverText(explosion, tooltip));
            assertEquals(Component.translatable("item.minecraft.firework_star.shape." + names[i]).withStyle(ChatFormatting.GRAY), tooltip.get(0));
            assertEquals(Component.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY), tooltip.get(1));
            assertEquals(Component.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY), tooltip.get(2));
        }
    }

    @Test void ordinaryAndUnknownShapesKeepVanillaTooltipHandling() {
        for (byte type : new byte[] {0, 1, 2, 3, 4, 99}) {
            CompoundTag explosion = new CompoundTag();
            explosion.putByte("Type", type);
            var tooltip = new ArrayList<Component>();
            assertFalse(new MixinFactory().appendFireworkCustomHoverText(explosion, tooltip));
            assertTrue(tooltip.isEmpty());
        }
    }
}
