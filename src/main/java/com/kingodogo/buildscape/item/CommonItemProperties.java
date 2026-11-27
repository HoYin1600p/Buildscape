package com.kingodogo.buildscape.item;
public class CommonItemProperties {
    private int maxStackSize = 64;
    private int durability = 0;
    private boolean fireResistant = false;
    private String rarity = "COMMON";
    private String itemType = "Item";
    private String tier = null;
    private String craftRemainder = null;

    public static CommonItemProperties of() {
        return new CommonItemProperties();
    }

    public CommonItemProperties craftRemainder(String craftRemainder) {
        this.craftRemainder = craftRemainder;
        return this;
    }

    public CommonItemProperties stacksTo(int maxStackSize) {
        this.maxStackSize = maxStackSize;
        return this;
    }

    public CommonItemProperties durability(int durability) {
        this.durability = durability;
        return this;
    }

    public CommonItemProperties fireResistant() {
        this.fireResistant = true;
        return this;
    }

    public CommonItemProperties rarity(String rarity) {
        this.rarity = rarity;
        return this;
    }

    public CommonItemProperties itemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    public CommonItemProperties tier(String tier) {
        this.tier = tier;
        return this;
    }

    public int getMaxStackSize() { return maxStackSize; }
    public int getDurability() { return durability; }
    public boolean isFireResistant() { return fireResistant; }
    public String getRarity() { return rarity; }
    public String getItemType() { return itemType; }
    public String getTier() { return tier; }
    public String getCraftRemainder() { return craftRemainder; }
}
