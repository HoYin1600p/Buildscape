package com.kingodogo.buildscape.client.screen;

import java.util.Arrays;
import java.util.List;

/**
 * Modular GUI theme registry and helper for Buildscape configuration screens.
 * Enables cycling and adding additional UI color palettes (Dark, Light, etc.).
 */
public class ConfigThemes {

    public static class Theme {
        public final String id;
        public final String displayName;

        public final int colorOverlay;
        public final int colorPanelBg;
        public final int colorChassisBody;
        public final int colorInnerDark;
        public final int colorHeaderBg;

        public final int colorFrameBlack;
        public final int colorBorderGradientStart;
        public final int colorBorderGradientEnd;
        public final int colorBorderShadow;

        public final int colorChassisBand;
        public final int colorBevelShadow;
        public final int colorBevelHighlight;

        public final int colorTextWhite;
        public final int colorTextSilver;
        public final int colorTextMuted;

        public final int colorRowBg;
        public final int colorRowBgHover;
        public final int colorRowBorder;
        public final int colorRowBorderHover;

        public final int colorToggleOnBg;
        public final int colorToggleOnBorder;
        public final int colorToggleOnText;

        public final int colorToggleOffBg;
        public final int colorToggleOffBorder;
        public final int colorToggleOffText;

        public final int colorBtnBg;
        public final int colorBtnBgHover;
        public final int colorBtnBgActive;
        public final int colorBtnBorder;
        public final int colorBtnBorderHover;

        public final int colorSlotBg;
        public final int colorSlotBgHover;
        public final int colorSlotBorder;

        public final int colorAccent;
        public final int colorAccentGlow;

        public Theme(String id, String displayName,
                     int colorOverlay, int colorPanelBg, int colorChassisBody, int colorInnerDark, int colorHeaderBg,
                     int colorFrameBlack, int colorBorderGradientStart, int colorBorderGradientEnd, int colorBorderShadow,
                     int colorChassisBand, int colorBevelShadow, int colorBevelHighlight,
                     int colorTextWhite, int colorTextSilver, int colorTextMuted,
                     int colorRowBg, int colorRowBgHover, int colorRowBorder, int colorRowBorderHover,
                     int colorToggleOnBg, int colorToggleOnBorder, int colorToggleOnText,
                     int colorToggleOffBg, int colorToggleOffBorder, int colorToggleOffText,
                     int colorBtnBg, int colorBtnBgHover, int colorBtnBgActive, int colorBtnBorder, int colorBtnBorderHover,
                     int colorSlotBg, int colorSlotBgHover, int colorSlotBorder,
                     int colorAccent, int colorAccentGlow) {
            this.id = id;
            this.displayName = displayName;
            this.colorOverlay = colorOverlay;
            this.colorPanelBg = colorPanelBg;
            this.colorChassisBody = colorChassisBody;
            this.colorInnerDark = colorInnerDark;
            this.colorHeaderBg = colorHeaderBg;
            this.colorFrameBlack = colorFrameBlack;
            this.colorBorderGradientStart = colorBorderGradientStart;
            this.colorBorderGradientEnd = colorBorderGradientEnd;
            this.colorBorderShadow = colorBorderShadow;
            this.colorChassisBand = colorChassisBand;
            this.colorBevelShadow = colorBevelShadow;
            this.colorBevelHighlight = colorBevelHighlight;
            this.colorTextWhite = colorTextWhite;
            this.colorTextSilver = colorTextSilver;
            this.colorTextMuted = colorTextMuted;
            this.colorRowBg = colorRowBg;
            this.colorRowBgHover = colorRowBgHover;
            this.colorRowBorder = colorRowBorder;
            this.colorRowBorderHover = colorRowBorderHover;
            this.colorToggleOnBg = colorToggleOnBg;
            this.colorToggleOnBorder = colorToggleOnBorder;
            this.colorToggleOnText = colorToggleOnText;
            this.colorToggleOffBg = colorToggleOffBg;
            this.colorToggleOffBorder = colorToggleOffBorder;
            this.colorToggleOffText = colorToggleOffText;
            this.colorBtnBg = colorBtnBg;
            this.colorBtnBgHover = colorBtnBgHover;
            this.colorBtnBgActive = colorBtnBgActive;
            this.colorBtnBorder = colorBtnBorder;
            this.colorBtnBorderHover = colorBtnBorderHover;
            this.colorSlotBg = colorSlotBg;
            this.colorSlotBgHover = colorSlotBgHover;
            this.colorSlotBorder = colorSlotBorder;
            this.colorAccent = colorAccent;
            this.colorAccentGlow = colorAccentGlow;
        }
    }

    public static final Theme DARK = new Theme(
            "dark", "Dark",
            0xD808080A, // overlay
            0xFF141416, // panelBg
            0xFF1E1E24, // chassisBody
            0xFF0F0F12, // innerDark
            0xFF18181D, // headerBg
            0xFF000000, // frameBlack
            0xFF9E9EB0, // borderGradientStart
            0xFF444450, // borderGradientEnd
            0xFF181820, // borderShadow
            0xFF22222A, // chassisBand
            0xFF0E0E12, // bevelShadow
            0xFF383848, // bevelHighlight
            0xFFFFFFFF, // textWhite
            0xFFCCCCCC, // textSilver
            0xFF84848C, // textMuted
            0xFF16161B, // rowBg
            0xFF202028, // rowBgHover
            0xFF24242C, // rowBorder
            0xFF4E4E5C, // rowBorderHover
            0xFF142E18, // toggleOnBg
            0xFF4ADE80, // toggleOnBorder
            0xFF4ADE80, // toggleOnText
            0xFF1C1C20, // toggleOffBg
            0xFF383842, // toggleOffBorder
            0xFF888890, // toggleOffText
            0xFF1A1A22, // btnBg
            0xFF262634, // btnBgHover
            0xFF2E2E3E, // btnBgActive
            0xFF383848, // btnBorder
            0xFF666678, // btnBorderHover
            0xFF121215, // slotBg
            0xFF22222A, // slotBgHover
            0xFF262630, // slotBorder
            0xFF3CDFFF, // accent
            0x603CDFFF  // accentGlow
    );

    public static final Theme LIGHT = new Theme(
            "light", "Light",
            0x60000000, // overlay
            0xFFECEEF4, // panelBg
            0xFFD8DCE6, // chassisBody
            0xFFFFFFFF, // innerDark
            0xFFE0E4EC, // headerBg
            0xFF5A6072, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFB4BAC8, // borderGradientEnd
            0xFF9AA0B0, // borderShadow
            0xFFCCD1DC, // chassisBand
            0xFFB4B9C6, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF1A1D24, // textWhite (crisp deep dark graphite text)
            0xFF4A5160, // textSilver (readable secondary text)
            0xFF747C8E, // textMuted (soft helper text)
            0xFFF4F6FA, // rowBg
            0xFFEAEFF8, // rowBgHover
            0xFFD0D5E0, // rowBorder
            0xFFA0A8B8, // rowBorderHover
            0xFFDCFCE7, // toggleOnBg
            0xFF22C55E, // toggleOnBorder
            0xFF15803D, // toggleOnText
            0xFFF0F2F6, // toggleOffBg
            0xFFB8BFCF, // toggleOffBorder
            0xFF6A7282, // toggleOffText
            0xFFE6E9F0, // btnBg
            0xFFF4F6FA, // btnBgHover
            0xFFD6DBE6, // btnBgActive
            0xFFB8BFCF, // btnBorder
            0xFF808898, // btnBorderHover
            0xFFF0F2F6, // slotBg
            0xFFFFFFFF, // slotBgHover
            0xFFD0D5E0, // slotBorder
            0xFF0284C7, // accent
            0x400284C7  // accentGlow
    );

    private static final List<Theme> THEMES = Arrays.asList(DARK, LIGHT);
    private static int activeThemeIndex = 0;

    public static List<Theme> getAllThemes() {
        return THEMES;
    }

    public static Theme getCurrentTheme() {
        if (activeThemeIndex < 0 || activeThemeIndex >= THEMES.size()) {
            activeThemeIndex = 0;
        }
        return THEMES.get(activeThemeIndex);
    }

    public static int getActiveThemeIndex() {
        return activeThemeIndex;
    }

    public static Theme cycleNextTheme() {
        activeThemeIndex = (activeThemeIndex + 1) % THEMES.size();
        return getCurrentTheme();
    }

    public static void setThemeIndex(int index) {
        if (index >= 0 && index < THEMES.size()) {
            activeThemeIndex = index;
        }
    }

    public static Theme getById(String id) {
        for (Theme t : THEMES) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        return DARK;
    }
}
