package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CrownFireworkShape extends CustomFireworkShape {

    private static final int COLOR_GOLD_PRIMARY = 0xFFD700;
    private static final int COLOR_GOLD_HIGHLIGHT = 0xFFFF77;
    private static final int COLOR_GOLD_SHADOW = 0xB8860B;
    private static final int COLOR_RUBY = 0xFF0044;
    private static final int COLOR_SAPPHIRE = 0x0066FF;
    private static final int COLOR_AMETHYST = 0xA000FF;
    private static final int COLOR_DIAMOND = 0x00FFFF;
    private static final int COLOR_EMERALD = 0x00FF66;

    public CrownFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.38D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        int widthX = 12;
        int depthZ = 5;

        for (int y = -6; y <= -2; y += 2) {
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
                double x = Math.cos(angle) * widthX;
                double z = Math.sin(angle) * depthZ;

                int color;
                if (y == -2) {
                    color = COLOR_GOLD_HIGHLIGHT;
                } else if (y == -6) {
                    color = COLOR_GOLD_SHADOW;
                } else {
                    color = COLOR_GOLD_PRIMARY;
                }

                points.add(new FireworkPoint(x, y, z, color));
            }
        }

        addJewelCluster(points, 0, -4, depthZ + 0.5, COLOR_RUBY);
        addJewelCluster(points, -6, -4, depthZ * 0.7, COLOR_DIAMOND);
        addJewelCluster(points, 6, -4, depthZ * 0.7, COLOR_SAPPHIRE);
        addJewelCluster(points, -10, -4, 0, COLOR_AMETHYST);
        addJewelCluster(points, 10, -4, 0, COLOR_EMERALD);

        addCrownSpike(points, 0, 0, 11, COLOR_RUBY);

        addCrownSpike(points, -6, 2, 8, COLOR_DIAMOND);

        addCrownSpike(points, 6, 2, 8, COLOR_SAPPHIRE);

        addCrownSpike(points, -11, 0, 6, COLOR_AMETHYST);

        addCrownSpike(points, 11, 0, 6, COLOR_EMERALD);

        for (double t = 0; t <= 1.0; t += 0.1) {
            double y = -2 + t * 13;
            double zFront = Math.sin(t * Math.PI) * depthZ * 0.8;
            double zBack = -zFront;

            points.add(new FireworkPoint(0, y, zFront, COLOR_GOLD_PRIMARY));
            points.add(new FireworkPoint(0, y, zBack, COLOR_GOLD_SHADOW));
        }

        return points;
    }

    private void addCrownSpike(List<FireworkPoint> points, double baseX, double baseZ, double tipY, int tipJewelColor) {
        double startY = -2.0;
        double height = tipY - startY;

        for (double y = startY; y <= tipY; y += 1.5) {
            double progress = (y - startY) / height;
            double widthScale = (1.0 - progress * 0.7);

            double xF = baseX * widthScale;
            double zF = baseZ + (baseZ >= 0 ? 1.5 : -1.5) * widthScale;
            int color = progress > 0.8 ? COLOR_GOLD_HIGHLIGHT : COLOR_GOLD_PRIMARY;

            points.add(new FireworkPoint(xF, y, zF, color));
            points.add(new FireworkPoint(xF, y, zF - 2.5 * widthScale, COLOR_GOLD_SHADOW));
        }

        points.add(new FireworkPoint(baseX, tipY + 0.8, baseZ, tipJewelColor, true, 1.2f));
        points.add(new FireworkPoint(baseX + 0.6, tipY + 0.8, baseZ, COLOR_GOLD_HIGHLIGHT, true, 1.15f));
        points.add(new FireworkPoint(baseX - 0.6, tipY + 0.8, baseZ, COLOR_GOLD_HIGHLIGHT, true, 1.15f));
        points.add(new FireworkPoint(baseX, tipY + 1.4, baseZ, COLOR_GOLD_HIGHLIGHT, true, 1.25f));
    }

    private void addJewelCluster(List<FireworkPoint> points, double x, double y, double z, int color) {
        points.add(new FireworkPoint(x, y, z, color, true, 1.05f));
        points.add(new FireworkPoint(x + 0.5, y, z, color, true, 1.05f));
        points.add(new FireworkPoint(x, y + 0.5, z, color, true, 1.05f));
    }
}
