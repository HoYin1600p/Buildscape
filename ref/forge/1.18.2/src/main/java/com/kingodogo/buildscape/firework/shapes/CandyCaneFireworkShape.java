package com.kingodogo.buildscape.firework.shapes;

import com.kingodogo.buildscape.firework.CustomFireworkShape;
import com.kingodogo.buildscape.firework.FireworkPoint;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CandyCaneFireworkShape extends CustomFireworkShape {

    private static final int COLOR_PURE_WHITE = 0xFFFFFF;

    public CandyCaneFireworkShape(ResourceLocation id, byte numericId) {
        super(id, numericId);
    }

    @Override
    public double getBaseScale() {
        return 0.42D;
    }

    @Override
    public List<FireworkPoint> generatePoints() {
        List<FireworkPoint> points = new ArrayList<>();

        double shaftX = -3.0;

        for (double y = -12; y <= 4; y += 0.8) {
            int stripeIndex = (int) Math.floor((y + 12) / 2.0);
            boolean isWhiteStripe = (stripeIndex % 2 == 0);

            for (double dx = -1.0; dx <= 1.0; dx += 1.0) {
                for (double dz = -1.0; dz <= 1.0; dz += 1.0) {
                    if (isWhiteStripe) {
                        points.add(new FireworkPoint(shaftX + dx, y, dz, COLOR_PURE_WHITE));
                    } else {
                        points.add(new FireworkPoint(shaftX + dx, y, dz));
                    }
                }
            }
        }

        double hookRadius = 4.0;
        double hookCenterX = 1.0;
        double hookCenterY = 5.0;

        for (double t = 0; t <= Math.PI * 1.1; t += Math.PI / 16) {
            double hx = hookCenterX - Math.cos(t) * hookRadius;
            double hy = hookCenterY + Math.sin(t) * hookRadius;

            int stripeIndex = (int) Math.floor(t * 5.0);
            boolean isWhiteStripe = (stripeIndex % 2 == 0);

            for (double dx = -0.8; dx <= 0.8; dx += 0.8) {
                for (double dz = -0.8; dz <= 0.8; dz += 0.8) {
                    if (isWhiteStripe) {
                        points.add(new FireworkPoint(hx + dx, hy, dz, COLOR_PURE_WHITE));
                    } else {
                        points.add(new FireworkPoint(hx + dx, hy, dz));
                    }
                }
            }
        }

        points.add(new FireworkPoint(hookCenterX + hookRadius + 0.5, hookCenterY - 0.5, 0, COLOR_PURE_WHITE, true, 1.15f));
        points.add(new FireworkPoint(hookCenterX, hookCenterY + hookRadius + 0.8, 0, COLOR_PURE_WHITE, true, 1.2f));

        return points;
    }
}
