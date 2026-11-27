package com.kingodogo.buildscape.trophy;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import com.kingodogo.buildscape.block.CustomSoundType;

public enum TrophyTier {
    FALLBACK("Fallback", ChatFormatting.WHITE, CustomSoundType.METAL, Rarity.COMMON),
    STONE("Stone", ChatFormatting.GRAY, CustomSoundType.METAL, Rarity.COMMON),
    GOLD("Gold", ChatFormatting.GOLD, CustomSoundType.METAL, Rarity.UNCOMMON),
    EMERALD("Emerald", ChatFormatting.GREEN, CustomSoundType.METAL, Rarity.RARE),
    DIAMOND("Diamond", ChatFormatting.AQUA, CustomSoundType.METAL, Rarity.RARE),
    NETHERITE("Netherite", ChatFormatting.DARK_GRAY, CustomSoundType.METAL, Rarity.EPIC),
    SPECIAL("Special", ChatFormatting.LIGHT_PURPLE, CustomSoundType.METAL, Rarity.RARE);

    private final String name;
    private final ChatFormatting formatting;
    private final CustomSoundType defaultSound;
    private final Rarity defaultRarity;

    TrophyTier(String name, ChatFormatting formatting, CustomSoundType defaultSound, Rarity defaultRarity) {
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

    public CustomSoundType getDefaultSound() {
        return defaultSound;
    }

    public Rarity getDefaultRarity() {
        return defaultRarity;
    }
}
