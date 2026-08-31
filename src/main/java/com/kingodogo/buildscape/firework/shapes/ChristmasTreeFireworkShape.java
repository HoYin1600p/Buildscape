package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ChristmasTreeFireworkShape extends CustomFireworkShape {

    // Color Palette
    private static final int COLOR_STAR_CENTER = 0xFFFFFF; // Pure white star center
    private static final int COLOR_STAR_GOLD = 0xFFD700; // Bright gold star
    private static final int COLOR_STAR_YELLOW = 0xFFFF00; // Bright yellow star
    private static final int COLOR_TRUNK_DARK = 0x5C4033; // Dark wood trunk
    private static final int COLOR_TRUNK_LIGHT = 0x8B4513; // Warm brown trunk
    private static final int COLOR_LEAF_DARK = 0x114418; // Dark green interior foliage
    private static final int COLOR_LEAF_MID = 0x227733; // Forest green foliage
    private static final int COLOR_LEAF_LIGHT = 0x33BB55; // Bright green outer foliage tips

    // Christmas Lights Palette
    private static final int[] LIGHT_COLORS = new int[]{
            0xFF3030, // Red
            0x36D35A, // Green
            0x3498FF, // Blue
            0xFFE45C, // Yellow
            0x35E5E5, // Cyan
            0xFF55C7, // Magenta
            0xFF9D32  // Orange
    };

    // Ornaments Palette
    private static final int[] ORNAMENT_COLORS = new int[]{
            0xFF0033, // Bright red bauble
            0xFFD700, // Gold bauble
            0x0066FF, // Royal blue bauble
            0xAA00FF, // Purple bauble
            0x00FFFF  // Cyan bauble
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

        // 1. Trunk Base (x in [-3, 3], z in [-3, 3], y in [-14, -8])
        for (int y = -14; y <= -8; y += 2) {
            for (int x = -3; x <= 3; x += 2) {
                for (int z = -3; z <= 3; z += 2) {
                    int color = (x == 0 && z == 0) ? COLOR_TRUNK_LIGHT : COLOR_TRUNK_DARK;
                    points.add(new FireworkPoint(x, y, z, color));
                }
            }
        }

        // 2. 7 Tiered Foliage Layers (y in [-8, 12])
        // Layer definitions: {yStart, yEnd, maxRadiusX, maxRadiusZ}
        int[][] layers = new int[][]{
                {-8, -5, 14, 8}, // Tier 1: Lowest, largest branches
                {-5, -2, 12, 7}, // Tier 2
                {-2, 1, 10, 6},  // Tier 3
                {1, 4, 8, 5},    // Tier 4
                {4, 7, 6, 4},    // Tier 5
                {7, 10, 4, 3},   // Tier 6
                {10, 12, 2, 2}   // Tier 7: Top narrow cone
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

                    // Leaf point color based on depth
                    int leafColor;
                    if (progress > 0.7) {
                        leafColor = COLOR_LEAF_LIGHT; // Outer tip highlight
                    } else if (Math.abs(fz) < 2) {
                        leafColor = COLOR_LEAF_MID;
                    } else {
                        leafColor = COLOR_LEAF_DARK; // Rear/interior shadow
                    }

                    points.add(new FireworkPoint(fx, y, fz, leafColor));
                    // 3D interior depth point
                    points.add(new FireworkPoint(fx * 0.6, y, fz * 0.6, COLOR_LEAF_DARK));

                    // Add Christmas String Lights on outer foliage edges
                    if (y % 2 == 0 && (int) (angle * 10) % 3 == 0) {
                        int lightColor = LIGHT_COLORS[lightIdx % LIGHT_COLORS.length];
                        lightIdx++;
                        points.add(new FireworkPoint(fx * 1.05, y + 0.2, fz * 1.05, lightColor, true, 1.08f));
                    }

                    // Add Ornaments hanging from branch tips
                    if (y % 3 == 0 && (int) (angle * 10) % 5 == 0) {
                        int ornamentColor = ORNAMENT_COLORS[ornamentIdx % ORNAMENT_COLORS.length];
                        ornamentIdx++;
                        points.add(new FireworkPoint(fx * 1.08, y - 0.4, fz * 1.08, ornamentColor, true, 1.1f));
                        points.add(new FireworkPoint(fx * 1.08, y - 0.7, fz * 1.08, ornamentColor, true, 1.1f));
                    }
                }
            }
        }

        // 3. Large Top Star (at y = 13 to 17)
        double starY = 14.5;
        // Star Center Core
        points.add(new FireworkPoint(0, starY, 0, COLOR_STAR_CENTER, true, 1.2f));
        points.add(new FireworkPoint(0, starY, 1, COLOR_STAR_GOLD, true, 1.2f));
        points.add(new FireworkPoint(0, starY, -1, COLOR_STAR_GOLD, true, 1.2f));

        // 5 Star Points (Vertical, Horizontal, & Top Spire)
        double starSize = 2.5;
        for (double d = 0.5; d <= starSize; d += 0.8) {
            points.add(new FireworkPoint(0, starY + d, 0, COLOR_STAR_YELLOW, true, 1.22f)); // Top point
            points.add(new FireworkPoint(0, starY - d, 0, COLOR_STAR_GOLD, true, 1.2f));   // Bottom point
            points.add(new FireworkPoint(d, starY, 0, COLOR_STAR_YELLOW, true, 1.2f));    // Right point
            points.add(new FireworkPoint(-d, starY, 0, COLOR_STAR_YELLOW, true, 1.2f));   // Left point
            points.add(new FireworkPoint(0, starY, d, COLOR_STAR_GOLD, true, 1.2f));      // Front point
            points.add(new FireworkPoint(0, starY, -d, COLOR_STAR_GOLD, true, 1.2f));     // Back point
        }

        // Extra Top Sparkles
        points.add(new FireworkPoint(0, starY + starSize + 0.8, 0, COLOR_STAR_CENTER, true, 1.3f));
        points.add(new FireworkPoint(1.2, starY + 1.2, 0, COLOR_STAR_YELLOW, true, 1.25f));
        points.add(new FireworkPoint(-1.2, starY + 1.2, 0, COLOR_STAR_YELLOW, true, 1.25f));

        return points;
    }
}
