package com.kingodogo.buildscape.trophy;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;

public enum TrophyTier {
    FALLBACK("Fallback", ChatFormatting.WHITE, SoundType.METAL, Rarity.COMMON),
    STONE("Stone", ChatFormatting.GRAY, SoundType.STONE, Rarity.COMMON),
    GOLD("Gold", ChatFormatting.GOLD, SoundType.METAL, Rarity.UNCOMMON),
    EMERALD("Emerald", ChatFormatting.GREEN, SoundType.METAL, Rarity.RARE),
    DIAMOND("Diamond", ChatFormatting.AQUA, SoundType.METAL, Rarity.RARE),
    NETHERITE("Netherite", ChatFormatting.DARK_GRAY, SoundType.NETHERITE_BLOCK, Rarity.EPIC),
    SPECIAL("Special", ChatFormatting.LIGHT_PURPLE, SoundType.METAL, Rarity.RARE);

    private final String name;
    private final ChatFormatting formatting;
    private final SoundType defaultSound;
    private final Rarity defaultRarity;

    TrophyTier(String name, ChatFormatting formatting, SoundType defaultSound, Rarity defaultRarity) {
        this.name = name;
        this.formatting = formatting;
        this.defaultSound = defaultSound;
        this.defaultRarity = defaultRarity;
    }

    public String getName() {
        return name;
    }

    public ChatFormatting getFormatting() {
        return formatting;
    }

    public SoundType getDefaultSound() {
        return defaultSound;
    }

    public Rarity getDefaultRarity() {
        return defaultRarity;
    }
}
