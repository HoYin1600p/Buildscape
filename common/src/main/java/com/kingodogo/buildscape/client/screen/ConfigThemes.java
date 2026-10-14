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
            return "light".equalsIgnoreCase(this.id) || (this.id != null && this.id.toLowerCase(java.util.Locale.ROOT).startsWith("light"));
        }

        public boolean isDark() {
            return !isLight();
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

    public static final Theme LIGHT_BLUE = new Theme(
            "light_blue", "Light Blue",
            0x70001020, // overlay
            0xFFEDF5FA, // panelBg
            0xFFDFECF5, // chassisBody
            0xFFE6F1F8, // innerDark
            0xFFD7E7F3, // headerBg
            0xFF102838, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFC2DCED, // borderGradientEnd
            0xFFA0C2D8, // borderShadow
            0xFFCCE0EE, // chassisBand
            0xFFABCCDE, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF0A2540, // textWhite (primary heading)
            0xFF1E3A5F, // textSilver (secondary text)
            0xFF4A7090, // textMuted (muted text)
            0xFFFFFFFF, // rowBg
            0xFFF0F8FF, // rowBgHover
            0xFFC8E0F0, // rowBorder
            0xFF0EA5E9, // rowBorderHover
            0xFFE0F2FE, // toggleOnBg
            0xFF0284C7, // toggleOnBorder
            0xFF0369A1, // toggleOnText
            0xFFF1F5F9, // toggleOffBg
            0xFFCBD5E1, // toggleOffBorder
            0xFF64748B, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFEDF7FC, // btnBgHover
            0xFFDCEEF9, // btnBgActive
            0xFFB8D8EC, // btnBorder
            0xFF0EA5E9, // btnBorderHover
            0xFFDEEDF6, // slotBg
            0xFFEAF4FB, // slotBgHover
            0xFFB6D4E8, // slotBorder
            0xFF0284C7, // accent
            0x500284C7  // accentGlow
    );

    public static final Theme LIGHT_RED = new Theme(
            "light_red", "Light Red",
            0x7020080C, // overlay
            0xFFFAF0F2, // panelBg
            0xFFF5DFE3, // chassisBody
            0xFFF7E6EA, // innerDark
            0xFFF0D5DA, // headerBg
            0xFF381418, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFECC2C8, // borderGradientEnd
            0xFFD89EA7, // borderShadow
            0xFFEED0D6, // chassisBand
            0xFFDEABB3, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF3D0C14, // textWhite
            0xFF5C1C26, // textSilver
            0xFF8F4753, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFFF1F3, // rowBgHover
            0xFFF0C8CE, // rowBorder
            0xFFE11D48, // rowBorderHover
            0xFFFFE4E6, // toggleOnBg
            0xFFE11D48, // toggleOnBorder
            0xFFBE123C, // toggleOnText
            0xFFF5EDEE, // toggleOffBg
            0xFFD5BEC2, // toggleOffBorder
            0xFF7D6065, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFCEDF0, // btnBgHover
            0xFFF9DCE1, // btnBgActive
            0xFFECB8C1, // btnBorder
            0xFFE11D48, // btnBorderHover
            0xFFF6DEE2, // slotBg
            0xFFFBEAF0, // slotBgHover
            0xFFE8B6BF, // slotBorder
            0xFFE11D48, // accent
            0x50E11D48  // accentGlow
    );

    public static final Theme LIGHT_ORANGE = new Theme(
            "light_orange", "Light Orange",
            0x70201004, // overlay
            0xFFFAF3EC, // panelBg
            0xFFF5E6D8, // chassisBody
            0xFFF8ECE0, // innerDark
            0xFFF2DDCB, // headerBg
            0xFF381E0C, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFECCBB3, // borderGradientEnd
            0xFFD8AA88, // borderShadow
            0xFFEED8C6, // chassisBand
            0xFFDEB597, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF3D2008, // textWhite
            0xFF5C3314, // textSilver
            0xFF8F5D36, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFFF6EE, // rowBgHover
            0xFFF0D4C0, // rowBorder
            0xFFEA580C, // rowBorderHover
            0xFFFFEDD5, // toggleOnBg
            0xFFEA580C, // toggleOnBorder
            0xFFC2410C, // toggleOnText
            0xFFF5EFE9, // toggleOffBg
            0xFFD5C4B5, // toggleOffBorder
            0xFF7D6B5A, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFCF2EA, // btnBgHover
            0xFFF9E4D4, // btnBgActive
            0xFFECC6AA, // btnBorder
            0xFFEA580C, // btnBorderHover
            0xFFF6E4D4, // slotBg
            0xFFFBEFE5, // slotBgHover
            0xFFE8C2A4, // slotBorder
            0xFFEA580C, // accent
            0x50EA580C  // accentGlow
    );

    public static final Theme LIGHT_GREEN = new Theme(
            "light_green", "Light Green",
            0x70001808, // overlay
            0xFFF0F7F2, // panelBg
            0xFFE0EFE4, // chassisBody
            0xFFE7F3EB, // innerDark
            0xFFD6EADC, // headerBg
            0xFF102816, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFC2DFC9, // borderGradientEnd
            0xFFA0C7AA, // borderShadow
            0xFFCDE3D3, // chassisBand
            0xFFABCEA3, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF0A2B14, // textWhite
            0xFF1B4024, // textSilver
            0xFF487252, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF2FBF4, // rowBgHover
            0xFFC5E4CE, // rowBorder
            0xFF16A34A, // rowBorderHover
            0xFFDCFCE7, // toggleOnBg
            0xFF16A34A, // toggleOnBorder
            0xFF15803D, // toggleOnText
            0xFFEFF6F1, // toggleOffBg
            0xFFB8D3C0, // toggleOffBorder
            0xFF5F8268, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFEEF9F1, // btnBgHover
            0xFFDDF2E3, // btnBgActive
            0xFFB8DEC2, // btnBorder
            0xFF16A34A, // btnBorderHover
            0xFFDEEFE3, // slotBg
            0xFFEAF6EE, // slotBgHover
            0xFFB6D8BF, // slotBorder
            0xFF16A34A, // accent
            0x5016A34A  // accentGlow
    );

    public static final Theme LIGHT_PURPLE = new Theme(
            "light_purple", "Light Purple",
            0x70180820, // overlay
            0xFFF5F0FA, // panelBg
            0xFFEBDFFA, // chassisBody
            0xFFEFE6F7, // innerDark
            0xFFE4D5F2, // headerBg
            0xFF241038, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFD7C2EC, // borderGradientEnd
            0xFFB89ED8, // borderShadow
            0xFFDFCEEE, // chassisBand
            0xFFC2ABDE, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF250A40, // textWhite
            0xFF3A1E5F, // textSilver
            0xFF704A90, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF9F2FF, // rowBgHover
            0xFFDFCBF0, // rowBorder
            0xFF9333EA, // rowBorderHover
            0xFFF3E8FF, // toggleOnBg
            0xFF9333EA, // toggleOnBorder
            0xFF7E22CE, // toggleOnText
            0xFFF4EFF8, // toggleOffBg
            0xFFCEBEDD, // toggleOffBorder
            0xFF7A658E, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFF6EEFC, // btnBgHover
            0xFFEEDDF8, // btnBgActive
            0xFFD2BBE8, // btnBorder
            0xFF9333EA, // btnBorderHover
            0xFFECDCF7, // slotBg
            0xFFF4EAFB, // slotBgHover
            0xFFCEB8E3, // slotBorder
            0xFF9333EA, // accent
            0x509333EA  // accentGlow
    );

    private static final List<Theme> THEMES = Arrays.asList(
            DARK,
            LIGHT,
            LIGHT_BLUE,
            LIGHT_RED,
            LIGHT_ORANGE,
            LIGHT_GREEN,
            LIGHT_PURPLE
    );
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
