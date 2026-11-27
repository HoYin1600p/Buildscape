package com.kingodogo.buildscape.client.renderer;

import java.util.Locale;

/** Keyword and placement rules from the 1.18.2 pillar item renderer. */
public final class PillarItemDisplay {
    private PillarItemDisplay() {}

    public enum Mode { DEFAULT, ITEM, FIXED }

    public static Mode parse(String customName, boolean weaponOrTool) {
        if (customName == null) return Mode.DEFAULT;
        String name = customName.trim().replaceAll("\u00a7[0-9A-FK-ORa-fk-or]", "").toLowerCase(Locale.ROOT);
        if (name.equals("item")) return Mode.ITEM;
        return weaponOrTool && name.contains("fixed") ? Mode.FIXED : Mode.DEFAULT;
    }

    public record ToolTransform(double translateY, float rotateZ, float scale) {}

    public static ToolTransform fixedTool(boolean swordLike, double lengthX, double lengthY) {
        double length = Math.sqrt(lengthX * lengthX + lengthY * lengthY);
        if (length < 0.1) length = 1.0;
        float scale = 0.8F;
        double translateY = (swordLike ? -0.5 : -0.55) + Math.max(0, length - 0.85) * 0.7 * scale;
        double tipY = 1.4625 + translateY - length / 2 * scale;
        if (tipY < 0.05) translateY += 0.05 - tipY;
        return new ToolTransform(translateY, swordLike ? 135 : 190, scale);
    }
}
