package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class TrophyFireworkShape extends CustomFireworkShape {

    // Color Palette
    private static final int COLOR_GOLD_PRIMARY = 0xFFD700; // Bright gold
    private static final int COLOR_GOLD_HIGHLIGHT = 0xFFFF88; // Bright yellow-white gold
    private static final int COLOR_GOLD_SHADOW = 0xCC7700; // Dark amber gold
    private static final int COLOR_BASE_DARK = 0x8B5A2B; // Bronze pedestal base
    private static final int COLOR_EMBLEM_CYAN = 0x00FFFF; // Bright cyan central gem
    private static final int COLOR_SPARKLE = 0xFFFFFF; // White sparkle stars

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

        // 1. Pedestal Base (Large rectangular/tiered base at bottom y = -11 to y = -7)
        // Bottom Slab (x in [-8, 8], z in [-5, 5], y = -11)
        for (int x = -8; x <= 8; x += 2) {
            for (int z = -5; z <= 5; z += 2) {
                points.add(new FireworkPoint(x, -11, z, COLOR_BASE_DARK));
                points.add(new FireworkPoint(x, -10, z, COLOR_BASE_DARK));
            }
        }
        // Middle Step (x in [-6, 6], z in [-4, 4], y = -9 to -7)
        for (int x = -6; x <= 6; x += 2) {
            for (int z = -4; z <= 4; z += 2) {
                for (int y = -9; y <= -7; y += 2) {
                    points.add(new FireworkPoint(x, y, z, COLOR_GOLD_SHADOW));
                }
            }
        }

        // 2. Narrow Stem (y = -7 to y = -2, stem width x in [-2, 2], z in [-2, 2])
        for (int y = -7; y <= -2; y += 1) {
            double radius = 1.8 + Math.sin((y + 7) * 0.4) * 0.5; // Slight waist pinch
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 4) {
                double sx = Math.cos(angle) * radius;
                double sz = Math.sin(angle) * radius;
                points.add(new FireworkPoint(sx, y, sz, COLOR_GOLD_PRIMARY));
            }
        }

        // 3. Cup Base / Node (Expanding upward from y = -2 to y = 0)
        for (int y = -2; y <= 0; y += 1) {
            double radius = 2.5 + (y + 2) * 1.5;
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 6) {
                double cx = Math.cos(angle) * radius;
                double cz = Math.sin(angle) * radius;
                points.add(new FireworkPoint(cx, y, cz, COLOR_GOLD_PRIMARY));
            }
        }

        // 4. Main Trophy Cup Body (Bowl curving outward from y = 0 to y = 8)
        for (int y = 0; y <= 8; y += 2) {
            double progress = y / 8.0;
            double radiusX = 5.5 + Math.pow(progress, 1.2) * 4.5; // Widens to ~10 at top
            double radiusZ = 4.0 + Math.pow(progress, 1.2) * 3.0; // Widens to ~7 at top

            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 12) {
                double cx = Math.cos(angle) * radiusX;
                double cz = Math.sin(angle) * radiusZ;

                int color;
                if (y == 8) {
                    color = COLOR_GOLD_HIGHLIGHT; // Top Rim
                } else if (cz < 0) {
                    color = COLOR_GOLD_SHADOW; // Rear depth
                } else {
                    color = COLOR_GOLD_PRIMARY;
                }
                points.add(new FireworkPoint(cx, y, cz, color));
            }
        }

        // Top Rim Highlight Ring (y = 8)
        double rimX = 10.0;
        double rimZ = 7.0;
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            double rx = Math.cos(angle) * rimX;
            double rz = Math.sin(angle) * rimZ;
            points.add(new FireworkPoint(rx, 8.5, rz, COLOR_GOLD_HIGHLIGHT));
        }

        // 5. Left & Right Curved Handles
        // Left Handle (Loops outward to x = -15, y from 1 to 7)
        addTrophyHandle(points, -1, 10.0, 7.0, 1.0, 15.0);
        // Right Handle (Loops outward to x = +15, y from 1 to 7)
        addTrophyHandle(points, 1, 10.0, 7.0, 1.0, 15.0);

        // 6. Central Emblem / Jewel (Cyan Diamond on front of cup at y = 4, z = 4.5)
        points.add(new FireworkPoint(0, 4, 4.8, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(1, 4, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(-1, 4, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(0, 5, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));
        points.add(new FireworkPoint(0, 3, 4.5, COLOR_EMBLEM_CYAN, true, 1.1f));

        // 7. Sparkle Stars (Secondary decorative glints around handles and top rim)
        points.add(new FireworkPoint(-15.5, 7.5, 0, COLOR_SPARKLE, true, 1.2f));
        points.add(new FireworkPoint(15.5, 7.5, 0, COLOR_SPARKLE, true, 1.2f));
        points.add(new FireworkPoint(0, 9.8, 0, COLOR_SPARKLE, true, 1.25f));

        return points;
    }

    private void addTrophyHandle(List<FireworkPoint> points, int sideSign, double attachTopX, double attachTopY, double attachBotY, double maxOutwardX) {
        // Curve parameter t from 0 to 1
        for (double t = 0; t <= 1.0; t += 0.08) {
            double angle = t * Math.PI;
            double handleX = sideSign * (attachTopX + Math.sin(angle) * (maxOutwardX - attachTopX));
            double handleY = attachBotY + t * (attachTopY - attachBotY);

            // Handle 3D thickness (front and back points)
            points.add(new FireworkPoint(handleX, handleY, 0, COLOR_GOLD_HIGHLIGHT));
            points.add(new FireworkPoint(handleX, handleY, 1.2, COLOR_GOLD_PRIMARY));
            points.add(new FireworkPoint(handleX, handleY, -1.2, COLOR_GOLD_SHADOW));
        }
    }
}
