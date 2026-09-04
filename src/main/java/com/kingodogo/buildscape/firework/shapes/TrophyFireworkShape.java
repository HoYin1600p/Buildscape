package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TrophyFireworkShape extends CustomFireworkShape {

    private static final int COLOR_GOLD_PRIMARY = 0xFFD700;
    private static final int COLOR_GOLD_HIGHLIGHT = 0xFFFF88;
    private static final int COLOR_GOLD_SHADOW = 0xCC7700;
    private static final int COLOR_BASE_DARK = 0x8B5A2B;
    private static final int COLOR_EMBLEM_CYAN = 0x00FFFF;
    private static final int COLOR_SPARKLE = 0xFFFFFF;

    public TrophyFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.36D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        for (int x = -8; x <= 8; x += 2) {
            for (int z = -5; z <= 5; z += 2) {
                points.add(new FireworkPoint(x, -11, z, COLOR_BASE_DARK));
                points.add(new FireworkPoint(x, -10, z, COLOR_BASE_DARK));
            }
        }
        for (int x = -6; x <= 6; x += 2) {
            for (int z = -4; z <= 4; z += 2) {
                for (int y = -9; y <= -7; y += 2) {
                    points.add(new FireworkPoint(x, y, z, COLOR_GOLD_SHADOW));
                }
            }
        }

        for (int y = -7; y <= -2; y += 1) {
            double radius = 1.8 + Math.sin((y + 7) * 0.4) * 0.5;
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 4) {
                double sx = Math.cos(angle) * radius;
                double sz = Math.sin(angle) * radius;
                points.add(new FireworkPoint(sx, y, sz, COLOR_GOLD_PRIMARY));
            }
        }

        for (int y = -2; y <= 0; y += 1) {
            double radius = 2.5 + (y + 2) * 1.5;
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 6) {
                double cx = Math.cos(angle) * radius;
                double cz = Math.sin(angle) * radius;
                points.add(new FireworkPoint(cx, y, cz, COLOR_GOLD_PRIMARY));
            }
        }

        for (int y = 0; y <= 8; y += 2) {
            double progress = y / 8.0;
            double radiusX = 5.5 + Math.pow(progress, 1.2) * 4.5;
            double radiusZ = 4.0 + Math.pow(progress, 1.2) * 3.0;

            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 12) {
                double cx = Math.cos(angle) * radiusX;
                double cz = Math.sin(angle) * radiusZ;

                int color;
                if (y == 8) {
                    color = COLOR_GOLD_HIGHLIGHT;
                } else if (cz < 0) {
                    color = COLOR_GOLD_SHADOW;
                } else {
                    color = COLOR_GOLD_PRIMARY;
                }
                points.add(new FireworkPoint(cx, y, cz, color));
            }
        }

        double rimX = 10.0;
        double rimZ = 7.0;
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            double rx = Math.cos(angle) * rimX;
            double rz = Math.sin(angle) * rimZ;
            points.add(new FireworkPoint(rx, 8.5, rz, COLOR_GOLD_HIGHLIGHT));
        }

        addTrophyHandle(points, -1, 10.0, 7.0, 1.0, 15.0);
        addTrophyHandle(points, 1, 10.0, 7.0, 1.0, 15.0);

        points.add(new FireworkPoint(0, 4, 4.8, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(1, 4, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(-1, 4, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(0, 5, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(0, 3, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));

        points.add(new FireworkPoint(-15.5, 7.5, 0, COLOR_SPARKLE, true, 1.2f));
        points.add(new FireworkPoint(15.5, 7.5, 0, COLOR_SPARKLE, true, 1.2f));
        points.add(new FireworkPoint(0, 9.8, 0, COLOR_SPARKLE, true, 1.25f));

        return points;
    }

    private void addTrophyHandle(List<FireworkPoint> points, int sideSign, double attachTopX, double attachTopY, double attachBotY, double maxOutwardX) {
        for (double t = 0; t <= 1.0; t += 0.08) {
            double angle = t * Math.PI;
            double handleX = sideSign * (attachTopX + Math.sin(angle) * (maxOutwardX - attachTopX));
            double handleY = attachBotY + t * (attachTopY - attachBotY);

            points.add(new FireworkPoint(handleX, handleY, 0, COLOR_GOLD_HIGHLIGHT));
            points.add(new FireworkPoint(handleX, handleY, 1.2, COLOR_GOLD_PRIMARY));
            points.add(new FireworkPoint(handleX, handleY, -1.2, COLOR_GOLD_SHADOW));
        }
    }
}
