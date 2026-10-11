package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SnowflakeFireworkShape extends CustomFireworkShape {

    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_ICY_WHITE = 0xE0F7FF;
    private static final int COLOR_LIGHT_BLUE = 0xA7E8FF;
    private static final int COLOR_BRIGHT_ICE_BLUE = 0x5AC8FF;
    private static final int COLOR_DARK_ICE_BLUE = 0x1E6B8C;

    public SnowflakeFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.45D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        points.add(new FireworkPoint(0, 0, 0, COLOR_WHITE, true, 1.18f));
        points.add(new FireworkPoint(0, 0, 0.6, COLOR_WHITE, true, 1.18f));
        points.add(new FireworkPoint(0, 0, -0.6, COLOR_WHITE, true, 1.18f));

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

        for (double angle : angles) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            for (double yArm = 3.5; yArm <= 18.0; yArm += 0.8) {
                double wx = -yArm * sin;
                double wy = yArm * cos;

                int color = (yArm > 14.0) ? COLOR_WHITE : COLOR_ICY_WHITE;
                points.add(new FireworkPoint(wx, wy, 0, color, false, 1.12f));

                if ((int) (yArm * 10) % 2 == 0) {
                    points.add(new FireworkPoint(wx, wy, 0.8, COLOR_DARK_ICE_BLUE, false, 1.12f));
                    points.add(new FireworkPoint(wx, wy, -0.8, COLOR_DARK_ICE_BLUE, false, 1.12f));
                }
            }

            addBranchPair(points, sin, cos, 6.5, 4.5, COLOR_LIGHT_BLUE);

            addBranchPair(points, sin, cos, 11.5, 3.5, COLOR_LIGHT_BLUE);

            addBranchPair(points, sin, cos, 15.0, 2.2, COLOR_BRIGHT_ICE_BLUE);

            double tipX = -18.0 * sin;
            double tipY = 18.0 * cos;
            points.add(new FireworkPoint(tipX, tipY, 0, COLOR_WHITE, true, 1.2f));

            double leftProngX = (-17.0 * sin) + (0.8 * cos);
            double leftProngY = (17.0 * cos) + (0.8 * sin);
            double rightProngX = (-17.0 * sin) - (0.8 * cos);
            double rightProngY = (17.0 * cos) - (0.8 * sin);
            points.add(new FireworkPoint(leftProngX, leftProngY, 0, COLOR_BRIGHT_ICE_BLUE, false, 1.2f));
            points.add(new FireworkPoint(rightProngX, rightProngY, 0, COLOR_BRIGHT_ICE_BLUE, false, 1.2f));

            double sparkX = -19.5 * sin;
            double sparkY = 19.5 * cos;
            points.add(new FireworkPoint(sparkX, sparkY, 0, COLOR_WHITE, true, 1.25f));
        }

        return points;
    }

    private void addBranchPair(List<FireworkPoint> points, double sin, double cos, double branchY, double length, int color) {
        double branchAngle = Math.PI / 4;

        for (int side : new int[]{-1, 1}) {
            for (double d = 0.8; d <= length; d += 0.8) {
                double xArm = side * d * Math.cos(branchAngle);
                double yArm = branchY + d * Math.sin(branchAngle);

                double wx = xArm * cos - yArm * sin;
                double wy = xArm * sin + yArm * cos;

                points.add(new FireworkPoint(wx, wy, 0, color, false, 1.18f));
            }

            double tipXArm = side * (length + 0.5) * Math.cos(branchAngle);
            double tipYArm = branchY + (length + 0.5) * Math.sin(branchAngle);
            double tipWx = tipXArm * cos - tipYArm * sin;
            double tipWy = tipXArm * sin + tipYArm * cos;
            points.add(new FireworkPoint(tipWx, tipWy, 0, COLOR_BRIGHT_ICE_BLUE, true, 1.2f));
        }
    }
}
