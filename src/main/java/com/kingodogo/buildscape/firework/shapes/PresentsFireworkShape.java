package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PresentsFireworkShape extends CustomFireworkShape {

    private static final int COLOR_GOLD_ACCENT = 0xFFD700;
    private static final int COLOR_WHITE_HIGHLIGHT = 0xFFFFFF;
    private static final int COLOR_BOW_CENTER = 0xFFF066;

    public PresentsFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.40D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        int width = 8;
        int heightMin = -8;
        int heightMax = 6;
        int depth = 8;

        for (int x = -width; x <= width; x += 2) {
            for (int y = heightMin; y <= heightMax; y += 2) {
                for (int z = -depth; z <= depth; z += 2) {
                    boolean isFront = (z == depth);
                    boolean isBack = (z == -depth);
                    boolean isLeft = (x == -width);
                    boolean isRight = (x == width);
                    boolean isTop = (y == heightMax);
                    boolean isBottom = (y == heightMin);

                    if (isFront || isBack || isLeft || isRight || isTop || isBottom) {
                        boolean isVertRibbon = (Math.abs(x) <= 2);
                        boolean isHorizRibbon = (Math.abs(y - (heightMin + heightMax) / 2) <= 1);
                        boolean isTopRibbonZ = (isTop && Math.abs(z) <= 2);
                        boolean isTopRibbonX = (isTop && Math.abs(x) <= 2);

                        if (isVertRibbon || isHorizRibbon || isTopRibbonZ || isTopRibbonX) {
                            int ribbonColor = (isFront && isTop) ? COLOR_WHITE_HIGHLIGHT : COLOR_GOLD_ACCENT;
                            points.add(new FireworkPoint(x, y, z, ribbonColor, true, 1.05f));
                        } else {
                            points.add(new FireworkPoint(x, y, z));
                        }
                    }
                }
            }
        }

        double bowBaseY = heightMax + 1.0;

        points.add(new FireworkPoint(0, bowBaseY + 0.5, 0, COLOR_BOW_CENTER, true, 1.15f));
        points.add(new FireworkPoint(0, bowBaseY + 1.2, 0, COLOR_BOW_CENTER, true, 1.15f));

        addBowLoop(points, -1, bowBaseY);

        addBowLoop(points, 1, bowBaseY);

        for (double t = 0; t <= 1.0; t += 0.2) {
            double tailY = bowBaseY - t * 4.0;
            points.add(new FireworkPoint(-2.5 - t * 1.5, tailY, depth + 0.5, COLOR_GOLD_ACCENT, true, 1.1f));
            points.add(new FireworkPoint(2.5 + t * 1.5, tailY, depth + 0.5, COLOR_GOLD_ACCENT, true, 1.1f));
        }

        points.add(new FireworkPoint(-width - 1, heightMax + 1, depth + 1, COLOR_WHITE_HIGHLIGHT, true, 1.2f));
        points.add(new FireworkPoint(width + 1, heightMax + 1, depth + 1, COLOR_WHITE_HIGHLIGHT, true, 1.2f));
        points.add(new FireworkPoint(0, bowBaseY + 4.5, 0, COLOR_WHITE_HIGHLIGHT, true, 1.25f));

        return points;
    }

    private void addBowLoop(List<FireworkPoint> points, int sideSign, double baseY) {
        for (double t = 0; t <= Math.PI; t += Math.PI / 8) {
            double lx = sideSign * Math.sin(t) * 5.5;
            double ly = baseY + (1.0 - Math.cos(t)) * 2.0;

            points.add(new FireworkPoint(lx, ly, 0, COLOR_GOLD_ACCENT, true, 1.12f));
            points.add(new FireworkPoint(lx, ly, 1.5, COLOR_GOLD_ACCENT, true, 1.1f));
            points.add(new FireworkPoint(lx, ly, -1.5, COLOR_GOLD_ACCENT, true, 1.1f));
        }
    }
}
