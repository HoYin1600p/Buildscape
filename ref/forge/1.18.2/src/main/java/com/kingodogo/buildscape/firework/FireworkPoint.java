package com.kingodogo.buildscape.firework;

import net.minecraft.world.phys.Vec3;

public class FireworkPoint {
    private final Vec3 position;
    private final int colorOverride;
    private final boolean secondary;
    private final float speedScale;

    public FireworkPoint(double x, double y, double z) {
        this(new Vec3(x, y, z), -1, false, 1.0f);
    }

    public FireworkPoint(double x, double y, double z, int colorOverride) {
        this(new Vec3(x, y, z), colorOverride, false, 1.0f);
    }

    public FireworkPoint(double x, double y, double z, int colorOverride, boolean secondary) {
        this(new Vec3(x, y, z), colorOverride, secondary, 1.0f);
    }

    public FireworkPoint(double x, double y, double z, int colorOverride, boolean secondary, float speedScale) {
        this(new Vec3(x, y, z), colorOverride, secondary, speedScale);
    }

    public FireworkPoint(Vec3 position, int colorOverride, boolean secondary, float speedScale) {
        this.position = position;
        this.colorOverride = colorOverride;
        this.secondary = secondary;
        this.speedScale = speedScale;
    }

    public Vec3 getPosition() {
        return position;
    }

    public int getColorOverride() {
        return colorOverride;
    }

    public boolean isSecondary() {
        return secondary;
    }

    public float getSpeedScale() {
        return speedScale;
    }
}
