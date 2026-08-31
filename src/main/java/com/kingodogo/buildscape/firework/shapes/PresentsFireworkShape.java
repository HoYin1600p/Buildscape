package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class PresentsFireworkShape extends CustomFireworkShape {

    // Decorative Highlight Colors
    private static final int COLOR_GOLD_ACCENT = 0xFFD700; // Gold accent
    private static final int COLOR_WHITE_HIGHLIGHT = 0xFFFFFF; // White highlight
    private static final int COLOR_BOW_CENTER = 0xFFF066; // Bright knot center

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

        // One Single Large 3D Gift Box:
        // Width: x in [-8, 8], Height: y in [-8, 6], Depth: z in [-8, 8]
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

                    // Outer faces of the 3D gift box
                    if (isFront || isBack || isLeft || isRight || isTop || isBottom) {
                        boolean isVertRibbon = (Math.abs(x) <= 2);
                        boolean isHorizRibbon = (Math.abs(y - (heightMin + heightMax) / 2) <= 1);
                        boolean isTopRibbonZ = (isTop && Math.abs(z) <= 2);
                        boolean isTopRibbonX = (isTop && Math.abs(x) <= 2);

                        if (isVertRibbon || isHorizRibbon || isTopRibbonZ || isTopRibbonX) {
                            // Ribbon point (uses gold/white accents or item dye accent)
                            int ribbonColor = (isFront && isTop) ? COLOR_WHITE_HIGHLIGHT : COLOR_GOLD_ACCENT;
                            points.add(new FireworkPoint(x, y, z, ribbonColor, true, 1.05f));
                        } else {
                            // Gift Box wrapping (colorOverride = -1 so it dynamically uses item dye color!)
                            points.add(new FireworkPoint(x, y, z));
                        }
                    }
                }
            }
        }

        // Large 3D Ribbon Bow on top (at y = 7 to 12)
        double bowBaseY = heightMax + 1.0;

        // Center Knot
        points.add(new FireworkPoint(0, bowBaseY + 0.5, 0, COLOR_BOW_CENTER, true, 1.15f));
        points.add(new FireworkPoint(0, bowBaseY + 1.2, 0, COLOR_BOW_CENTER, true, 1.15f));

        // Left Bow Loop (x from 0 to -6, y loops up to bowBaseY + 3.5, z thickness [-2, 2])
        addBowLoop(points, -1, bowBaseY);

        // Right Bow Loop (x from 0 to +6, y loops up to bowBaseY + 3.5, z thickness [-2, 2])
        addBowLoop(points, 1, bowBaseY);

        // Hanging Ribbon Tails
        for (double t = 0; t <= 1.0; t += 0.2) {
            double tailY = bowBaseY - t * 4.0;
            points.add(new FireworkPoint(-2.5 - t * 1.5, tailY, depth + 0.5, COLOR_GOLD_ACCENT, true, 1.1f));
            points.add(new FireworkPoint(2.5 + t * 1.5, tailY, depth + 0.5, COLOR_GOLD_ACCENT, true, 1.1f));
        }

        // Festive Sparkles around corners and bow
        points.add(new FireworkPoint(-width - 1, heightMax + 1, depth + 1, COLOR_WHITE_HIGHLIGHT, true, 1.2f));
        points.add(new FireworkPoint(width + 1, heightMax + 1, depth + 1, COLOR_WHITE_HIGHLIGHT, true, 1.2f));
        points.add(new FireworkPoint(0, bowBaseY + 4.5, 0, COLOR_WHITE_HIGHLIGHT, true, 1.25f));

        return points;
    }

    private void addBowLoop(List<FireworkPoint> points, int sideSign, double baseY) {
        for (double t = 0; t <= Math.PI; t += Math.PI / 8) {
            double lx = sideSign * Math.sin(t) * 5.5;
            double ly = baseY + (1.0 - Math.cos(t)) * 2.0;

            // Loop 3D depth in Z
            points.add(new FireworkPoint(lx, ly, 0, COLOR_GOLD_ACCENT, true, 1.12f));
            points.add(new FireworkPoint(lx, ly, 1.5, COLOR_GOLD_ACCENT, true, 1.1f));
            points.add(new FireworkPoint(lx, ly, -1.5, COLOR_GOLD_ACCENT, true, 1.1f));
        }
    }
}
