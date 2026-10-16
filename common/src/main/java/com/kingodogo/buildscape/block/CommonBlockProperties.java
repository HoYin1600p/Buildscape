package com.kingodogo.buildscape.block;
public class CommonBlockProperties {
    private float hardness = 1.5f;
    private float resistance = 1.5f;
    private String sound = "STONE";
    private String mapColor = null;
    private String material = "STONE";
    private boolean requiresCorrectTool = false;
    private boolean noCollision = false;
    private boolean noOcclusion = false;
    private int lightLevel = 0;
    private String copyFrom = null;
    private boolean tintedGlass = false;
    private boolean glass = false;

    public static CommonBlockProperties of() {
        return new CommonBlockProperties();
    }

    public static CommonBlockProperties copy(String source) {
        return new CommonBlockProperties().setCopyFrom(source);
    }

    public CommonBlockProperties tintedGlass() {
        this.tintedGlass = true;
        return this;
    }

    public CommonBlockProperties glass() {
        this.glass = true;
        return this;
    }

    public CommonBlockProperties strength(float hardness) {
        this.hardness = hardness;
        this.resistance = hardness;
        return this;
    }

    public CommonBlockProperties strength(float hardness, float resistance) {
        this.hardness = hardness;
        this.resistance = resistance;
        return this;
    }

    public CommonBlockProperties sound(String sound) {
        this.sound = sound;
        return this;
    }

    public CommonBlockProperties mapColor(String mapColor) {
        this.mapColor = mapColor;
        return this;
    }

    public CommonBlockProperties material(String material) {
        this.material = material;
        return this;
    }

    public CommonBlockProperties requiresCorrectToolForDrops() {
        this.requiresCorrectTool = true;
        return this;
    }

    public CommonBlockProperties noCollision() {
        this.noCollision = true;
        return this;
    }

    public CommonBlockProperties noOcclusion() {
        this.noOcclusion = true;
        return this;
    }

    public CommonBlockProperties lightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
        return this;
    }

    public CommonBlockProperties setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
        return this;
    }

    public float getHardness() { return hardness; }
    public float getResistance() { return resistance; }
    public String getSound() { return sound; }
    public String getMapColor() { return mapColor; }
    public String getMaterial() { return material; }
    public boolean requiresCorrectTool() { return requiresCorrectTool; }
    public boolean isNoCollision() { return noCollision; }
    public boolean isNoOcclusion() { return noOcclusion; }
    public int getLightLevel() { return lightLevel; }
    public String getCopyFrom() { return copyFrom; }
    public boolean isTintedGlass() { return tintedGlass; }
    public boolean isGlass() { return glass; }
}
