package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.config.BuildscapeClientConfig;
import java.util.Arrays;
import java.util.List;

// Modular GUI theme registry and helper for Buildscape configuration screens.
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

        public boolean isLight() {
            return "light".equalsIgnoreCase(this.id);
        }

        public boolean isDark() {
            return "dark".equalsIgnoreCase(this.id);
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
            0x70000000, // overlay
            0xFFF0F2F6, // panelBg
            0xFFE2E5EC, // chassisBody
            0xFFE8ECF2, // innerDark (recessed container basin)
            0xFFDDE2EB, // headerBg (distinct clean header bar)
            0xFF181C24, // frameBlack (crisp dark outer perimeter line)
            0xFFFFFFFF, // borderGradientStart (bright specular top-left highlight)
            0xFFC8CFDC, // borderGradientEnd (smooth bevel blend)
            0xFFA6AFBE, // borderShadow (soft bottom bevel shadow)
            0xFFD2D8E3, // chassisBand
            0xFFB2BAC8, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF0F172A, // textWhite (crisp deep dark graphite text - primary headings & values)
            0xFF334155, // textSilver (readable secondary text - coordinates & labels)
            0xFF64748B, // textMuted (soft helper text - descriptions & inactive badges)
            0xFFFFFFFF, // rowBg (pure white elevated card)
            0xFFF6F8FC, // rowBgHover (elevated hover)
            0xFFCDD3DE, // rowBorder (clean 1px card border)
            0xFF0284C7, // rowBorderHover (crisp accent hover highlight)
            0xFFDCFCE7, // toggleOnBg
            0xFF22C55E, // toggleOnBorder
            0xFF15803D, // toggleOnText
            0xFFF1F3F7, // toggleOffBg
            0xFFBAC1CF, // toggleOffBorder
            0xFF64748B, // toggleOffText
            0xFFFFFFFF, // btnBg (clean white elevated button)
            0xFFF0F4FA, // btnBgHover
            0xFFE2E7F2, // btnBgActive
            0xFFBFC6D4, // btnBorder
            0xFF0284C7, // btnBorderHover
            0xFFE0E5EE, // slotBg
            0xFFEEF2F8, // slotBgHover
            0xFFBDC5D3, // slotBorder
            0xFF0284C7, // accent (vibrant azure blue)
            0x400284C7  // accentGlow
    );

    private static final List<Theme> THEMES = Arrays.asList(DARK, LIGHT);
    private static int activeThemeIndex = 0;

    static {
        try {
            String saved = BuildscapeClientConfig.get().getUiTheme();
            setThemeById(saved);
        } catch (Exception ignored) {}
    }

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

    public static void setThemeById(String id) {
        if (id == null) return;
        for (int i = 0; i < THEMES.size(); i++) {
            if (THEMES.get(i).id.equalsIgnoreCase(id)) {
                activeThemeIndex = i;
                return;
            }
        }
    }

    public static Theme getById(String id) {
        for (Theme t : THEMES) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        return DARK;
    }
}
