package com.kingodogo.buildscape.firework;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CustomFireworkRenderer {

    @FunctionalInterface
    public interface SparkSpawner {
        void spawnSpark(double x, double y, double z, double vx, double vy, double vz, int[] colors, int[] fadeColors, boolean trail, boolean flicker);
    }

    public static void renderExplosion(
            CustomFireworkShape shape,
            double centerX, double centerY, double centerZ,
            int[] itemColors, int[] fadeColors,
            boolean trail, boolean flicker,
            SparkSpawner sparkSpawner
    ) {
        renderExplosion(shape, centerX, centerY, centerZ, itemColors, fadeColors, trail, flicker, 0.0F, sparkSpawner);
    }

    public static void renderExplosion(
            CustomFireworkShape shape,
            double centerX, double centerY, double centerZ,
            int[] itemColors, int[] fadeColors,
            boolean trail, boolean flicker,
            float yaw,
            SparkSpawner sparkSpawner
    ) {
        List<FireworkPoint> points = shape.generatePoints();
        double scale = shape.getBaseScale();

        int[] defaultColors = (itemColors != null && itemColors.length > 0) ? itemColors : new int[]{0xFFD700};

        double rad = Math.toRadians(yaw);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        for (FireworkPoint pt : points) {
            Vec3 pos = pt.getPosition();

            // Rotate point horizontally around Y-axis by shooter's yaw
            double rx = pos.x * cos - pos.z * sin;
            double ry = pos.y;
            double rz = pos.x * sin + pos.z * cos;
            Vec3 rotatedPos = new Vec3(rx, ry, rz);

            Vec3 scaledPos = rotatedPos.scale(scale);
            double length = scaledPos.length();

            Vec3 direction = length > 0.0001 ? scaledPos.normalize() : new Vec3(0, 1, 0);
            double speed = length * 0.08D * pt.getSpeedScale();

            Vec3 velocity = direction.scale(speed);

            int[] pointColors;
            if (pt.getColorOverride() != -1) {
                pointColors = new int[]{pt.getColorOverride()};
            } else {
                pointColors = defaultColors;
            }

            sparkSpawner.spawnSpark(
                    centerX, centerY, centerZ,
                    velocity.x, velocity.y, velocity.z,
                    pointColors, fadeColors,
                    trail, flicker
            );
        }
    }
}
