package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SnowflakeFireworkShape extends CustomFireworkShape {

    // Icy Winter Color Palette
    private static final int COLOR_WHITE = 0xFFFFFF;            // Pure white center & crystal tips
    private static final int COLOR_ICY_WHITE = 0xE0F7FF;        // Bright icy white primary shaft
    private static final int COLOR_LIGHT_BLUE = 0xA7E8FF;       // Soft light blue branches
    private static final int COLOR_BRIGHT_ICE_BLUE = 0x5AC8FF;  // Vibrant ice blue details
    private static final int COLOR_DARK_ICE_BLUE = 0x1E6B8C;    // Subtle Z-depth shadow blue

    public SnowflakeFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.45D; // Optimized scale for clear 6-arm expansion and readability
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        // 1. Distinctive Central Crystal Anchor
        points.add(new FireworkPoint(0, 0, 0, COLOR_WHITE, true, 1.18f));
        points.add(new FireworkPoint(0, 0, 0.6, COLOR_WHITE, true, 1.18f));
        points.add(new FireworkPoint(0, 0, -0.6, COLOR_WHITE, true, 1.18f));

        // Central 6-point diamond ring
        double[] angles = new double[]{0, Math.PI / 3, 2 * Math.PI / 3, Math.PI, 4 * Math.PI / 3, 5 * Math.PI / 3};

        for (double angle : angles) {
            double rx = Math.cos(angle) * 1.8;
            double ry = Math.sin(angle) * 1.8;
            points.add(new FireworkPoint(rx, ry, 0, COLOR_WHITE, false, 1.15f));

            double midAngle = angle + Math.PI / 6;
            double mx = Math.cos(midAngle) * 3.2;
            double my = Math.sin(midAngle) * 3.2;
            points.add(new FireworkPoint(mx, my, 0, COLOR_ICY_WHITE, false, 1.15f));
        }

        // 2. Six Primary Arms generated with perfect 6-fold radial symmetry
        for (double angle : angles) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // A) Central Shaft of the Arm (yArm from 3.5 to 18.0)
            for (double yArm = 3.5; yArm <= 18.0; yArm += 0.8) {
                double wx = -yArm * sin;
                double wy = yArm * cos;

                int color = (yArm > 14.0) ? COLOR_WHITE : COLOR_ICY_WHITE;
                points.add(new FireworkPoint(wx, wy, 0, color, false, 1.12f));

                // 3D Depth Layering (subtle z-offset)
                if ((int) (yArm * 10) % 2 == 0) {
                    points.add(new FireworkPoint(wx, wy, 0.8, COLOR_DARK_ICE_BLUE, false, 1.12f));
                    points.add(new FireworkPoint(wx, wy, -0.8, COLOR_DARK_ICE_BLUE, false, 1.12f));
                }
            }

            // B) Pair 1: Inner Large Secondary Branches (at yArm = 6.5)
            addBranchPair(points, sin, cos, 6.5, 4.5, COLOR_LIGHT_BLUE);

            // C) Pair 2: Middle Medium Secondary Branches (at yArm = 11.5)
            addBranchPair(points, sin, cos, 11.5, 3.5, COLOR_LIGHT_BLUE);

            // D) Pair 3: Outer Small Secondary Branches (at yArm = 15.0)
            addBranchPair(points, sin, cos, 15.0, 2.2, COLOR_BRIGHT_ICE_BLUE);

            // E) Crystalline Spear Tip (at yArm = 18.0)
            double tipX = -18.0 * sin;
            double tipY = 18.0 * cos;
            points.add(new FireworkPoint(tipX, tipY, 0, COLOR_WHITE, true, 1.2f));

            // Side prongs of spear tip
            double leftProngX = (-17.0 * sin) + (0.8 * cos);
            double leftProngY = (17.0 * cos) + (0.8 * sin);
            double rightProngX = (-17.0 * sin) - (0.8 * cos);
            double rightProngY = (17.0 * cos) - (0.8 * sin);
            points.add(new FireworkPoint(leftProngX, leftProngY, 0, COLOR_BRIGHT_ICE_BLUE, false, 1.2f));
            points.add(new FireworkPoint(rightProngX, rightProngY, 0, COLOR_BRIGHT_ICE_BLUE, false, 1.2f));

            // Outer Sparkle Tip
            double sparkX = -19.5 * sin;
            double sparkY = 19.5 * cos;
            points.add(new FireworkPoint(sparkX, sparkY, 0, COLOR_WHITE, true, 1.25f));
        }

        return points;
    }

    private void addBranchPair(List<FireworkPoint> points, double sin, double cos, double branchY, double length, int color) {
        double branchAngle = Math.PI / 4; // 45 degree diagonal branch

        for (int side : new int[]{-1, 1}) {
            for (double d = 0.8; d <= length; d += 0.8) {
                double xArm = side * d * Math.cos(branchAngle);
                double yArm = branchY + d * Math.sin(branchAngle);

                // Transform arm coordinates to world coordinates using rotation matrix
                double wx = xArm * cos - yArm * sin;
                double wy = xArm * sin + yArm * cos;

                points.add(new FireworkPoint(wx, wy, 0, color, false, 1.18f));
            }

            // Branch tip crystal detail point
            double tipXArm = side * (length + 0.5) * Math.cos(branchAngle);
            double tipYArm = branchY + (length + 0.5) * Math.sin(branchAngle);
            double tipWx = tipXArm * cos - tipYArm * sin;
            double tipWy = tipXArm * sin + tipYArm * cos;
            points.add(new FireworkPoint(tipWx, tipWy, 0, COLOR_BRIGHT_ICE_BLUE, true, 1.2f));
        }
    }
}
