package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PhoenixFireworkShape extends CustomFireworkShape {

    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_YELLOW = 0xFFF200;
    private static final int COLOR_GOLD = 0xFFB000;
    private static final int COLOR_ORANGE = 0xFF6500;
    private static final int COLOR_RED = 0xE52B00;
    private static final int COLOR_DARK_RED = 0x8F1600;

    public PhoenixFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.85D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        for (double y = 3; y <= 8; y += 0.8) {
            double radius = 3.5 - Math.abs(y - 5.5) * 0.4;
            for (double x = -radius; x <= radius; x += 1.0) {
                for (double z = -radius; z <= radius; z += 1.0) {
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist <= radius) {
                        int color = dist < 1.5 ? COLOR_WHITE : COLOR_YELLOW;
                        points.add(new FireworkPoint(x, y, z, color));
                    }
                }
            }
        }

        for (double y = -4; y <= 3; y += 1.0) {
            double radius = 2.5 * ((y + 5) / 8.0);
            for (double x = -radius; x <= radius; x += 1.0) {
                for (double z = -radius; z <= radius; z += 1.0) {
                    if (Math.sqrt(x * x + z * z) <= radius) {
                        int color = (y > 0) ? COLOR_GOLD : (y > -2 ? COLOR_ORANGE : COLOR_RED);
                        points.add(new FireworkPoint(x, y, z, color));
                        points.add(new FireworkPoint(x, y, z - 1.2, COLOR_DARK_RED));
                    }
                }
            }
        }

        for (double y = 8; y <= 14; y += 0.8) {
            double progress = (y - 8) / 6.0;
            double curveX = Math.sin(progress * Math.PI) * 0.8;
            double curveZ = Math.cos(progress * Math.PI) * 1.2;

            for (double dx = -1.0; dx <= 1.0; dx += 1.0) {
                for (double dz = -1.0; dz <= 1.0; dz += 1.0) {
                    int color = progress < 0.5 ? COLOR_WHITE : COLOR_YELLOW;
                    points.add(new FireworkPoint(curveX + dx, y, curveZ + dz, color));
                }
            }
        }

        double headY = 14.5;
        for (double hx = -1.2; hx <= 1.2; hx += 0.8) {
            for (double hy = 14.0; hy <= 15.5; hy += 0.7) {
                for (double hz = -1.2; hz <= 1.2; hz += 0.8) {
                    points.add(new FireworkPoint(hx, hy, hz, COLOR_YELLOW));
                }
            }
        }

        points.add(new FireworkPoint(0, 15.0, 2.0, COLOR_GOLD, true, 1.05f));
        points.add(new FireworkPoint(0, 15.3, 2.8, COLOR_YELLOW, true, 1.1f));
        points.add(new FireworkPoint(0, 15.5, 3.4, COLOR_WHITE, true, 1.15f));

        points.add(new FireworkPoint(0.9, 14.8, 1.0, COLOR_WHITE, true, 1.1f));
        points.add(new FireworkPoint(-0.9, 14.8, 1.0, COLOR_WHITE, true, 1.1f));

        double[][] crestFeathers = new double[][]{
                {0, 15.0, -1.0, 0, 19.5, -5.5, COLOR_WHITE},
                {-0.8, 14.8, -0.8, -1.5, 18.0, -4.5, COLOR_YELLOW},
                {0.8, 14.8, -0.8, 1.5, 18.0, -4.5, COLOR_YELLOW},
                {-1.5, 14.5, -0.5, -2.8, 16.5, -3.5, COLOR_GOLD},
                {1.5, 14.5, -0.5, 2.8, 16.5, -3.5, COLOR_GOLD}
        };

        for (double[] crest : crestFeathers) {
            double startX = crest[0], startY = crest[1], startZ = crest[2];
            double endX = crest[3], endY = crest[4], endZ = crest[5];
            int tipColor = (int) crest[6];

            for (double t = 0; t <= 1.0; t += 0.15) {
                double cx = startX + t * (endX - startX);
                double cy = startY + t * (endY - startY);
                double cz = startZ + t * (endZ - startZ);
                int color = t > 0.7 ? tipColor : COLOR_YELLOW;
                points.add(new FireworkPoint(cx, cy, cz, color, true, 1.1f + (float) t * 0.15f));
            }
        }

        for (int side : new int[]{-1, 1}) {
            double[][][] wingGroups = new double[][][]{
                    {{side * 2, 6, 0}, {side * 8, 14, 0.5}, {side * 10, 17, 0}},
                    {{side * 4, 8, 0}, {side * 14, 18, 1.0}, {side * 17, 22, 0.5}},
                    {{side * 6, 10, 0}, {side * 18, 22, 1.2}, {side * 24, 27, 0.8}},
                    {{side * 8, 10, 0}, {side * 22, 23, 1.5}, {side * 30, 28, 1.0}},
                    {{side * 7, 8, 0}, {side * 20, 19, 1.2}, {side * 27, 23, 0.8}},
                    {{side * 6, 6, 0}, {side * 18, 15, 1.0}, {side * 24, 18, 0.5}},
                    {{side * 5, 4, 0}, {side * 15, 11, 0.8}, {side * 20, 13, 0.3}},
                    {{side * 4, 2, 0}, {side * 11, 7, 0.5}, {side * 15, 8, 0.2}}
            };

            for (double[][] feather : wingGroups) {
                double[] p0 = feather[0];
                double[] p1 = feather[1];
                double[] p2 = feather[2];

                for (double t = 0; t <= 1.0; t += 0.05) {
                    double oneMinusT = 1.0 - t;
                    double fx = oneMinusT * oneMinusT * p0[0] + 2 * oneMinusT * t * p1[0] + t * t * p2[0];
                    double fy = oneMinusT * oneMinusT * p0[1] + 2 * oneMinusT * t * p1[1] + t * t * p2[1];
                    double fz = oneMinusT * oneMinusT * p0[2] + 2 * oneMinusT * t * p1[2] + t * t * p2[2];

                    int color;
                    if (t < 0.25) {
                        color = COLOR_WHITE;
                    } else if (t < 0.5) {
                        color = COLOR_YELLOW;
                    } else if (t < 0.75) {
                        color = COLOR_GOLD;
                    } else if (t < 0.9) {
                        color = COLOR_ORANGE;
                    } else {
                        color = COLOR_RED;
                    }

                    points.add(new FireworkPoint(fx, fy, fz, color));

                    points.add(new FireworkPoint(fx + side * 0.4, fy - 0.4, fz, color));
                    points.add(new FireworkPoint(fx - side * 0.4, fy + 0.4, fz, color));

                    points.add(new FireworkPoint(fx, fy, fz - 1.5, COLOR_DARK_RED));
                }

                double[] tip = feather[2];
                points.add(new FireworkPoint(tip[0], tip[1], tip[2], COLOR_WHITE, true, 1.25f));
                points.add(new FireworkPoint(tip[0] + side * 0.6, tip[1] + 0.6, tip[2], COLOR_YELLOW, true, 1.3f));
            }
        }

        double[][][] tailStrands = new double[][][]{
                {{0, -4, 0}, {0, -13, -2.5}, {0, -22, -4.0}},
                {{-1, -4, 0}, {-3, -12, -2.0}, {-5, -20, -3.5}},
                {{1, -4, 0}, {3, -12, -2.0}, {5, -20, -3.5}},
                {{-2, -4, 0}, {-6, -10, -1.5}, {-9, -17, -2.5}},
                {{2, -4, 0}, {6, -10, -1.5}, {9, -17, -2.5}},
                {{-3, -4, 0}, {-9, -8, -1.0}, {-14, -13, -1.5}},
                {{3, -4, 0}, {9, -8, -1.0}, {14, -13, -1.5}}
        };

        for (double[][] strand : tailStrands) {
            double[] p0 = strand[0];
            double[] p1 = strand[1];
            double[] p2 = strand[2];

            for (double t = 0; t <= 1.0; t += 0.05) {
                double oneMinusT = 1.0 - t;
                double tx = oneMinusT * oneMinusT * p0[0] + 2 * oneMinusT * t * p1[0] + t * t * p2[0];
                double ty = oneMinusT * oneMinusT * p0[1] + 2 * oneMinusT * t * p1[1] + t * t * p2[1];
                double tz = oneMinusT * oneMinusT * p0[2] + 2 * oneMinusT * t * p1[2] + t * t * p2[2];

                int color;
                if (t < 0.2) {
                    color = COLOR_GOLD;
                } else if (t < 0.5) {
                    color = COLOR_ORANGE;
                } else if (t < 0.85) {
                    color = COLOR_RED;
                } else {
                    color = COLOR_DARK_RED;
                }

                points.add(new FireworkPoint(tx, ty, tz, color));
                points.add(new FireworkPoint(tx + 0.3, ty, tz + 0.5, color));
            }

            double[] tailTip = strand[2];
            points.add(new FireworkPoint(tailTip[0], tailTip[1] - 0.8, tailTip[2], COLOR_YELLOW, true, 1.2f));
            points.add(new FireworkPoint(tailTip[0], tailTip[1] - 1.5, tailTip[2], COLOR_WHITE, true, 1.25f));
        }

        return points;
    }
}
