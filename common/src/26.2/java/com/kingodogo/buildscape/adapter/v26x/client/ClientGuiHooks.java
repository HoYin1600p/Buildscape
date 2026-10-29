package com.kingodogo.buildscape.adapter.v26x.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Maps immediate GUI state to scoped extraction strata and per-vertex colour. */
public final class ClientGuiHooks {
    private static final ThreadLocal<GuiGraphicsExtractor> GRAPHICS = new ThreadLocal<>();
    private static final ThreadLocal<Integer> TINT = ThreadLocal.withInitial(() -> -1);
    private ClientGuiHooks() {}

    public static GuiGraphicsExtractor current() { return GRAPHICS.get(); }

    public static void withGraphics(GuiGraphicsExtractor graphics, Runnable action) {
        GuiGraphicsExtractor previous = GRAPHICS.get();
        int tint = TINT.get();
        GRAPHICS.set(graphics);
        try { action.run(); }
        finally {
            if (previous == null) GRAPHICS.remove(); else GRAPHICS.set(previous);
            TINT.set(tint);
        }
    }

    public static void beginOverlay() {
        GuiGraphicsExtractor graphics = current();
        if (graphics != null) {
            graphics.pose().pushMatrix();
            graphics.nextStratum();
        }
    }

    public static void endOverlay() {
        GuiGraphicsExtractor graphics = current();
        if (graphics != null) {
            graphics.pose().popMatrix();
            graphics.nextStratum();
        }
    }

    // 26.2 has no global shader colour; GUI pipelines consume a colour on each draw.
    public static void resetShaderColor() { TINT.set(-1); }

    public static int color(int color) {
        int tint = TINT.get();
        if (tint == -1) return color;
        int result = 0;
        for (int shift = 0; shift <= 24; shift += 8) {
            result |= (((color >>> shift) & 255) * ((tint >>> shift) & 255) / 255) << shift;
        }
        return result;
    }
}
