package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CakeFireworkShape extends CustomFireworkShape {

    private static final int COLOR_FROSTING = 0xFFFDD0;
    private static final int COLOR_FROSTING_TOP = 0xFFFFFF;
    private static final int COLOR_CAKE_BODY = 0x8B4513;
    private static final int COLOR_CAKE_ACCENT = 0xCD853F;
    private static final int COLOR_DECORATION_RED = 0xFF2D55;
    private static final int COLOR_DECORATION_PINK = 0xFF69B4;
    private static final int COLOR_CANDLE_BODY = 0xFFD700;
    private static final int COLOR_CANDLE_WHITE = 0xF8F8FF;
    private static final int COLOR_FLAME_INNER = 0xFFFF00;
    private static final int COLOR_FLAME_OUTER = 0xFF4500;

    public CakeFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.35D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        int r1 = 10;
        for (int x = -r1; x <= r1; x += 2) {
            for (int z = -r1; z <= r1; z += 2) {
                for (int y = -7; y <= -2; y += 2) {
                    boolean isOuterX = (Math.abs(x) >= r1 - 1);
                    boolean isOuterZ = (Math.abs(z) >= r1 - 1);
                    boolean isBottomEdge = (y == -7);
                    boolean isTopRim = (y == -2);

                    if (isOuterX || isOuterZ || isBottomEdge || isTopRim) {
                        int color;
                        if (isTopRim && (isOuterX || isOuterZ)) {
                            color = (x + z) % 4 == 0 ? COLOR_DECORATION_RED : COLOR_FROSTING;
                        } else if (isOuterX && isOuterZ) {
                            color = COLOR_CAKE_ACCENT;
                        } else {
                            color = (y % 4 == 0) ? COLOR_CAKE_BODY : COLOR_CAKE_ACCENT;
                        }
                        points.add(new FireworkPoint(x, y, z, color));
                    }
                }
            }
        }

        int r2 = 7;
        for (int x = -r2; x <= r2; x += 2) {
            for (int z = -r2; z <= r2; z += 2) {
                for (int y = -2; y <= 3; y += 2) {
                    boolean isOuterX = (Math.abs(x) >= r2 - 1);
                    boolean isOuterZ = (Math.abs(z) >= r2 - 1);
                    boolean isTopRim = (y == 3);

                    if (isOuterX || isOuterZ || isTopRim) {
                        int color;
                        if (isTopRim && (isOuterX || isOuterZ)) {
                            color = (x * z) % 3 == 0 ? COLOR_DECORATION_PINK : COLOR_FROSTING_TOP;
                        } else {
                            color = (x + y + z) % 2 == 0 ? COLOR_CAKE_BODY : COLOR_CAKE_ACCENT;
                        }
                        points.add(new FireworkPoint(x, y, z, color));
                    }
                }
            }
        }

        int r3 = 4;
        for (int x = -r3; x <= r3; x += 2) {
            for (int z = -r3; z <= r3; z += 2) {
                for (int y = 3; y <= 7; y += 2) {
                    boolean isOuterX = (Math.abs(x) >= r3 - 1);
                    boolean isOuterZ = (Math.abs(z) >= r3 - 1);
                    boolean isTopSurface = (y == 7);

                    if (isOuterX || isOuterZ || isTopSurface) {
                        int color = isTopSurface ? COLOR_FROSTING_TOP : COLOR_FROSTING;
                        points.add(new FireworkPoint(x, y, z, color));
                    }
                }
            }
        }

        addCandle(points, 0, 0, 7, 12, COLOR_CANDLE_BODY);
        addCandle(points, -3, 0, 7, 11, COLOR_CANDLE_WHITE);
        addCandle(points, 3, 0, 7, 11, COLOR_CANDLE_WHITE);

        return points;
    }

    private void addCandle(List<FireworkPoint> points, double cx, double cz, double yStart, double yEnd, int candleColor) {
        for (double y = yStart; y <= yEnd; y += 1.2) {
            points.add(new FireworkPoint(cx, y, cz, candleColor));
            points.add(new FireworkPoint(cx + 0.5, y, cz, candleColor));
            points.add(new FireworkPoint(cx, y, cz + 0.5, candleColor));
        }

        double flameY = yEnd + 1.2;
        points.add(new FireworkPoint(cx, flameY, cz, COLOR_FLAME_INNER, true, 1.15f));
        points.add(new FireworkPoint(cx + 0.6, flameY + 0.8, cz, COLOR_FLAME_OUTER, true, 1.2f));
        points.add(new FireworkPoint(cx - 0.6, flameY + 0.8, cz, COLOR_FLAME_OUTER, true, 1.2f));
        points.add(new FireworkPoint(cx, flameY + 1.4, cz, COLOR_FLAME_INNER, true, 1.25f));
    }
}
