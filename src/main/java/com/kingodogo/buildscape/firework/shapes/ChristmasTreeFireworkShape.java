package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ChristmasTreeFireworkShape extends CustomFireworkShape {

    private static final int COLOR_STAR_CENTER = 0xFFFFFF;
    private static final int COLOR_STAR_GOLD = 0xFFD700;
    private static final int COLOR_STAR_YELLOW = 0xFFFF00;
    private static final int COLOR_TRUNK_DARK = 0x5C4033;
    private static final int COLOR_TRUNK_LIGHT = 0x8B4513;
    private static final int COLOR_LEAF_DARK = 0x114418;
    private static final int COLOR_LEAF_MID = 0x227733;
    private static final int COLOR_LEAF_LIGHT = 0x33BB55;

    private static final int[] LIGHT_COLORS = new int[]{
            0xFF3030,
            0x36D35A,
            0x3498FF,
            0xFFE45C,
            0x35E5E5,
            0xFF55C7,
            0xFF9D32
    };

    private static final int[] ORNAMENT_COLORS = new int[]{
            0xFF0033,
            0xFFD700,
            0x0066FF,
            0xAA00FF,
            0x00FFFF
    };

    public ChristmasTreeFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.45D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        for (int y = -14; y <= -8; y += 2) {
            for (int x = -3; x <= 3; x += 2) {
                for (int z = -3; z <= 3; z += 2) {
                    int color = (x == 0 && z == 0) ? COLOR_TRUNK_LIGHT : COLOR_TRUNK_DARK;
                    points.add(new FireworkPoint(x, y, z, color));
                }
            }
        }

        int[][] layers = new int[][]{
                {-8, -5, 14, 8},
                {-5, -2, 12, 7},
                {-2, 1, 10, 6},
                {1, 4, 8, 5},
                {4, 7, 6, 4},
                {7, 10, 4, 3},
                {10, 12, 2, 2}
        };

        int lightIdx = 0;
        int ornamentIdx = 0;

        for (int layerIdx = 0; layerIdx < layers.length; layerIdx++) {
            int[] l = layers[layerIdx];
            int yStart = l[0];
            int yEnd = l[1];
            int maxRx = l[2];
            int maxRz = l[3];

            for (int y = yStart; y <= yEnd; y += 1) {
                double progress = (double) (y - yStart) / (yEnd - yStart + 0.001);
                double currentRx = maxRx * (1.0 - progress * 0.4);
                double currentRz = maxRz * (1.0 - progress * 0.4);

                for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 10) {
                    double fx = Math.cos(angle) * currentRx;
                    double fz = Math.sin(angle) * currentRz;

                    int leafColor;
                    if (progress > 0.7) {
                        leafColor = COLOR_LEAF_LIGHT;
                    } else if (Math.abs(fz) < 2) {
                        leafColor = COLOR_LEAF_MID;
                    } else {
                        leafColor = COLOR_LEAF_DARK;
                    }

                    points.add(new FireworkPoint(fx, y, fz, leafColor));
                    points.add(new FireworkPoint(fx * 0.6, y, fz * 0.6, COLOR_LEAF_DARK));

                    if (y % 2 == 0 && (int) (angle * 10) % 3 == 0) {
                        int lightColor = LIGHT_COLORS[lightIdx % LIGHT_COLORS.length];
                        lightIdx++;
                        points.add(new FireworkPoint(fx * 1.05, y + 0.2, fz * 1.05, lightColor, true, 1.08f));
                    }

                    if (y % 3 == 0 && (int) (angle * 10) % 5 == 0) {
                        int ornamentColor = ORNAMENT_COLORS[ornamentIdx % ORNAMENT_COLORS.length];
                        ornamentIdx++;
                        points.add(new FireworkPoint(fx * 1.08, y - 0.4, fz * 1.08, ornamentColor, true, 1.1f));
                        points.add(new FireworkPoint(fx * 1.08, y - 0.7, fz * 1.08, ornamentColor, true, 1.1f));
                    }
                }
            }
        }

        double starY = 14.5;
        points.add(new FireworkPoint(0, starY, 0, COLOR_STAR_CENTER, true, 1.2f));
        points.add(new FireworkPoint(0, starY, 1, COLOR_STAR_GOLD, true, 1.2f));
        points.add(new FireworkPoint(0, starY, -1, COLOR_STAR_GOLD, true, 1.2f));

        double starSize = 2.5;
        for (double d = 0.5; d <= starSize; d += 0.8) {
            points.add(new FireworkPoint(0, starY + d, 0, COLOR_STAR_YELLOW, true, 1.22f));
            points.add(new FireworkPoint(0, starY - d, 0, COLOR_STAR_GOLD, true, 1.2f));
            points.add(new FireworkPoint(d, starY, 0, COLOR_STAR_YELLOW, true, 1.2f));
            points.add(new FireworkPoint(-d, starY, 0, COLOR_STAR_YELLOW, true, 1.2f));
            points.add(new FireworkPoint(0, starY, d, COLOR_STAR_GOLD, true, 1.2f));
            points.add(new FireworkPoint(0, starY, -d, COLOR_STAR_GOLD, true, 1.2f));
        }

        points.add(new FireworkPoint(0, starY + starSize + 0.8, 0, COLOR_STAR_CENTER, true, 1.3f));
        points.add(new FireworkPoint(1.2, starY + 1.2, 0, COLOR_STAR_YELLOW, true, 1.25f));
        points.add(new FireworkPoint(-1.2, starY + 1.2, 0, COLOR_STAR_YELLOW, true, 1.25f));

        return points;
    }
}
