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
        List<FireworkPoint> points = shape.generatePoints();
        double scale = shape.getBaseScale();

        int[] defaultColors = (itemColors != null && itemColors.length > 0) ? itemColors : new int[]{0xFFD700};

        for (FireworkPoint pt : points) {
            Vec3 pos = pt.getPosition();

            Vec3 scaledPos = pos.scale(scale);
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
