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
            return "light".equalsIgnoreCase(this.id) || (this.id != null && (this.id.toLowerCase(java.util.Locale.ROOT).startsWith("light") || "silver".equalsIgnoreCase(this.id)));
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

    public static final Theme GRAY = new Theme(
            "gray", "Gray",
            0xD8101012, // overlay
            0xFF24272E, // panelBg
            0xFF2D313A, // chassisBody
            0xFF1D2026, // innerDark
            0xFF262A32, // headerBg
            0xFF0D0E11, // frameBlack
            0xFF8A909C, // borderGradientStart
            0xFF484C55, // borderGradientEnd
            0xFF16181C, // borderShadow
            0xFF333742, // chassisBand
            0xFF1A1C21, // bevelShadow
            0xFF4C5260, // bevelHighlight
            0xFFF1F5F9, // textWhite
            0xFFCBD5E1, // textSilver
            0xFF848B98, // textMuted
            0xFF22252C, // rowBg
            0xFF2C3039, // rowBgHover
            0xFF3D424E, // rowBorder
            0xFF94A3B8, // rowBorderHover
            0xFF1E3A2F, // toggleOnBg
            0xFF34D399, // toggleOnBorder
            0xFF34D399, // toggleOnText
            0xFF262931, // toggleOffBg
            0xFF474C58, // toggleOffBorder
            0xFF94A3B8, // toggleOffText
            0xFF2A2E37, // btnBg
            0xFF353A45, // btnBgHover
            0xFF3E4452, // btnBgActive
            0xFF484E5D, // btnBorder
            0xFF94A3B8, // btnBorderHover
            0xFF1B1D22, // slotBg
            0xFF282B33, // slotBgHover
            0xFF373B46, // slotBorder
            0xFF94A3B8, // accent
            0x5094A3B8  // accentGlow
    );

    public static final Theme LIGHT_GRAY = new Theme(
            "light_gray", "Light Gray",
            0x70101418, // overlay
            0xFFEDEDF0, // panelBg
            0xFFDFDFE4, // chassisBody
            0xFFE6E6EB, // innerDark
            0xFFDCDCE2, // headerBg
            0xFF18181C, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFC8C8D0, // borderGradientEnd
            0xFFA8A8B2, // borderShadow
            0xFFD2D2DA, // chassisBand
            0xFFB4B4BE, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF18181C, // textWhite
            0xFF383842, // textSilver
            0xFF6A6A78, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF6F6F9, // rowBgHover
            0xFFCECED8, // rowBorder
            0xFF64748B, // rowBorderHover
            0xFFDCFCE7, // toggleOnBg
            0xFF16A34A, // toggleOnBorder
            0xFF15803D, // toggleOnText
            0xFFF0F0F4, // toggleOffBg
            0xFFC0C0CB, // toggleOffBorder
            0xFF64748B, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFEEEEF4, // btnBgHover
            0xFFE2E2EA, // btnBgActive
            0xFFC2C2CD, // btnBorder
            0xFF64748B, // btnBorderHover
            0xFFDFDFE7, // slotBg
            0xFFEBEBF2, // slotBgHover
            0xFFBCBCC8, // slotBorder
            0xFF64748B, // accent
            0x5064748B  // accentGlow
    );

    public static final Theme SILVER = new Theme(
            "silver", "Silver",
            0x75141820, // overlay
            0xFFE8EBF0, // panelBg
            0xFFDCE0E8, // chassisBody
            0xFFE2E6EE, // innerDark
            0xFFD4D9E2, // headerBg
            0xFF161922, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFBDC4D2, // borderGradientEnd
            0xFF9CA5B6, // borderShadow
            0xFFCCD2DF, // chassisBand
            0xFFAAB3C4, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF0F172A, // textWhite
            0xFF2B3648, // textSilver
            0xFF5B6980, // textMuted
            0xFFF8FAFC, // rowBg
            0xFFFFFFFF, // rowBgHover
            0xFFCBD3E0, // rowBorder
            0xFF38BDF8, // rowBorderHover
            0xFFE0F2FE, // toggleOnBg
            0xFF0284C7, // toggleOnBorder
            0xFF0369A1, // toggleOnText
            0xFFECEFF5, // toggleOffBg
            0xFFB8C2D3, // toggleOffBorder
            0xFF5B6980, // toggleOffText
            0xFFF8FAFC, // btnBg
            0xFFFFFFFF, // btnBgHover
            0xFFE0E5EE, // btnBgActive
            0xFFB8C2D3, // btnBorder
            0xFF38BDF8, // btnBorderHover
            0xFFD8DEE9, // slotBg
            0xFFE5EBF4, // slotBgHover
            0xFFB4BFD1, // slotBorder
            0xFF0284C7, // accent
            0x5038BDF8  // accentGlow
    );

    public static final Theme GOLD = new Theme(
            "gold", "Gold",
            0xD81A1202, // overlay
            0xFF1F1A10, // panelBg
            0xFF2D2517, // chassisBody
            0xFF17130B, // innerDark
            0xFF292113, // headerBg
            0xFF0B0904, // frameBlack
            0xFFFDE047, // borderGradientStart
            0xFFB45309, // borderGradientEnd
            0xFF1F1505, // borderShadow
            0xFF382E1C, // chassisBand
            0xFF140F06, // bevelShadow
            0xFFFBBF24, // bevelHighlight
            0xFFFEF3C7, // textWhite
            0xFFFDE68A, // textSilver
            0xFFB49A6C, // textMuted
            0xFF1E180E, // rowBg
            0xFF2A2214, // rowBgHover
            0xFF4D3D22, // rowBorder
            0xFFF59E0B, // rowBorderHover
            0xFF452E0B, // toggleOnBg
            0xFFF59E0B, // toggleOnBorder
            0xFFFCD34D, // toggleOnText
            0xFF1F1A12, // toggleOffBg
            0xFF3D321F, // toggleOffBorder
            0xFF8A7654, // toggleOffText
            0xFF282012, // btnBg
            0xFF362C1A, // btnBgHover
            0xFF453820, // btnBgActive
            0xFF5A4725, // btnBorder
            0xFFF59E0B, // btnBorderHover
            0xFF17130A, // slotBg
            0xFF272012, // slotBgHover
            0xFF3F331A, // slotBorder
            0xFFF59E0B, // accent
            0x60F59E0B  // accentGlow
    );

    public static final Theme CRIMSON = new Theme(
            "crimson", "Crimson",
            0xD8140406, // overlay
            0xFF1C0E12, // panelBg
            0xFF2A141A, // chassisBody
            0xFF150A0D, // innerDark
            0xFF241016, // headerBg
            0xFF0A0406, // frameBlack
            0xFFFB7185, // borderGradientStart
            0xFF881337, // borderGradientEnd
            0xFF1A050A, // borderShadow
            0xFF331820, // chassisBand
            0xFF120508, // bevelShadow
            0xFFF43F5E, // bevelHighlight
            0xFFFFF1F2, // textWhite
            0xFFFECDD3, // textSilver
            0xFFFDA4AF, // textMuted
            0xFF1E0E13, // rowBg
            0xFF29141B, // rowBgHover
            0xFF4C1D28, // rowBorder
            0xFFE11D48, // rowBorderHover
            0xFF4C0519, // toggleOnBg
            0xFFF43F5E, // toggleOnBorder
            0xFFFDA4AF, // toggleOnText
            0xFF1F0F14, // toggleOffBg
            0xFF3B1520, // toggleOffBorder
            0xFF8A5A66, // toggleOffText
            0xFF261218, // btnBg
            0xFF331821, // btnBgHover
            0xFF3D1C28, // btnBgActive
            0xFF541E2B, // btnBorder
            0xFFF43F5E, // btnBorderHover
            0xFF140A0D, // slotBg
            0xFF221016, // slotBgHover
            0xFF3B1520, // slotBorder
            0xFFE11D48, // accent
            0x60E11D48  // accentGlow
    );

    public static final Theme COBALT = new Theme(
            "cobalt", "Cobalt",
            0xD8040C1A, // overlay
            0xFF0E1626, // panelBg
            0xFF142036, // chassisBody
            0xFF0A101C, // innerDark
            0xFF121C30, // headerBg
            0xFF040810, // frameBlack
            0xFF60A5FA, // borderGradientStart
            0xFF1E3A8A, // borderGradientEnd
            0xFF060B14, // borderShadow
            0xFF182640, // chassisBand
            0xFF080D17, // bevelShadow
            0xFF3B82F6, // bevelHighlight
            0xFFEFF6FF, // textWhite
            0xFFBFDBFE, // textSilver
            0xFF60A5FA, // textMuted
            0xFF0F182A, // rowBg
            0xFF17243E, // rowBgHover
            0xFF1E355B, // rowBorder
            0xFF3B82F6, // rowBorderHover
            0xFF172554, // toggleOnBg
            0xFF3B82F6, // toggleOnBorder
            0xFF93C5FD, // toggleOnText
            0xFF10192C, // toggleOffBg
            0xFF1C2C4A, // toggleOffBorder
            0xFF576C90, // toggleOffText
            0xFF15223A, // btnBg
            0xFF1D2E4E, // btnBgHover
            0xFF243960, // btnBgActive
            0xFF2A4370, // btnBorder
            0xFF3B82F6, // btnBorderHover
            0xFF0B111D, // slotBg
            0xFF152035, // slotBgHover
            0xFF1D2F4E, // slotBorder
            0xFF2563EB, // accent
            0x603B82F6  // accentGlow
    );

    public static final Theme EMERALD = new Theme(
            "emerald", "Emerald",
            0xD804160A, // overlay
            0xFF0D1E12, // panelBg
            0xFF142B1B, // chassisBody
            0xFF09160D, // innerDark
            0xFF122618, // headerBg
            0xFF040B06, // frameBlack
            0xFF4ADE80, // borderGradientStart
            0xFF14532D, // borderGradientEnd
            0xFF060F09, // borderShadow
            0xFF183320, // chassisBand
            0xFF07110A, // bevelShadow
            0xFF22C55E, // bevelHighlight
            0xFFF0FDF4, // textWhite
            0xFFBBF7D0, // textSilver
            0xFF4ADE80, // textMuted
            0xFF0E2013, // rowBg
            0xFF16301D, // rowBgHover
            0xFF1D4728, // rowBorder
            0xFF22C55E, // rowBorderHover
            0xFF052E16, // toggleOnBg
            0xFF22C55E, // toggleOnBorder
            0xFF86EFAC, // toggleOnText
            0xFF0F2215, // toggleOffBg
            0xFF1C3A24, // toggleOffBorder
            0xFF5B8A68, // toggleOffText
            0xFF142B1A, // btnBg
            0xFF1B3A24, // btnBgHover
            0xFF22482D, // btnBgActive
            0xFF275434, // btnBorder
            0xFF22C55E, // btnBorderHover
            0xFF0A180E, // slotBg
            0xFF142B1B, // slotBgHover
            0xFF1C3A24, // slotBorder
            0xFF16A34A, // accent
            0x6022C55E  // accentGlow
    );

    public static final Theme AMETHYST = new Theme(
            "amethyst", "Amethyst",
            0xD810061A, // overlay
            0xFF180E24, // panelBg
            0xFF221434, // chassisBody
            0xFF110A1A, // innerDark
            0xFF1E122C, // headerBg
            0xFF08040C, // frameBlack
            0xFFC084FC, // borderGradientStart
            0xFF581C87, // borderGradientEnd
            0xFF0E0516, // borderShadow
            0xFF2A193E, // chassisBand
            0xFF0C0614, // bevelShadow
            0xFFA855F7, // bevelHighlight
            0xFFFAF5FF, // textWhite
            0xFFE9D5FF, // textSilver
            0xFFA855F7, // textMuted
            0xFF1A1028, // rowBg
            0xFF25173A, // rowBgHover
            0xFF3C205E, // rowBorder
            0xFFA855F7, // rowBorderHover
            0xFF3B0764, // toggleOnBg
            0xFFA855F7, // toggleOnBorder
            0xFFD8B4FE, // toggleOnText
            0xFF1C122B, // toggleOffBg
            0xFF321B4C, // toggleOffBorder
            0xFF7D5F9A, // toggleOffText
            0xFF211333, // btnBg
            0xFF2D1B46, // btnBgHover
            0xFF382257, // btnBgActive
            0xFF44296B, // btnBorder
            0xFFA855F7, // btnBorderHover
            0xFF110A1B, // slotBg
            0xFF201332, // slotBgHover
            0xFF2E1B46, // slotBorder
            0xFF9333EA, // accent
            0x60A855F7  // accentGlow
    );

    public static final Theme AMBER = new Theme(
            "amber", "Amber",
            0xD81A0C02, // overlay
            0xFF22130A, // panelBg
            0xFF301B0E, // chassisBody
            0xFF180D06, // innerDark
            0xFF29170C, // headerBg
            0xFF0C0602, // frameBlack
            0xFFFB923C, // borderGradientStart
            0xFF7C2D12, // borderGradientEnd
            0xFF140803, // borderShadow
            0xFF3B2111, // chassisBand
            0xFF100703, // bevelShadow
            0xFFF97316, // bevelHighlight
            0xFFFFF7ED, // textWhite
            0xFFFED7AA, // textSilver
            0xFFFB923C, // textMuted
            0xFF24140B, // rowBg
            0xFF321C0F, // rowBgHover
            0xFF542812, // rowBorder
            0xFFF97316, // rowBorderHover
            0xFF431407, // toggleOnBg
            0xFFF97316, // toggleOnBorder
            0xFFFDBA74, // toggleOnText
            0xFF25150C, // toggleOffBg
            0xFF442211, // toggleOffBorder
            0xFF946247, // toggleOffText
            0xFF2C190D, // btnBg
            0xFF3B2111, // btnBgHover
            0xFF482815, // btnBgActive
            0xFF5C331B, // btnBorder
            0xFFF97316, // btnBorderHover
            0xFF180D07, // slotBg
            0xFF2A170C, // slotBgHover
            0xFF3E1F0F, // slotBorder
            0xFFEA580C, // accent
            0x60F97316  // accentGlow
    );

    public static final Theme PRISMARINE = new Theme(
            "prismarine", "Prismarine",
            0xD8041618, // overlay
            0xFF0C1D20, // panelBg
            0xFF122B30, // chassisBody
            0xFF081518, // innerDark
            0xFF10252A, // headerBg
            0xFF040A0C, // frameBlack
            0xFF2DD4BF, // borderGradientStart
            0xFF134E4A, // borderGradientEnd
            0xFF051012, // borderShadow
            0xFF17343A, // chassisBand
            0xFF071215, // bevelShadow
            0xFF14B8A6, // bevelHighlight
            0xFFF0FDFA, // textWhite
            0xFF99F6E4, // textSilver
            0xFF2DD4BF, // textMuted
            0xFF0E2024, // rowBg
            0xFF152E34, // rowBgHover
            0xFF1A464E, // rowBorder
            0xFF14B8A6, // rowBorderHover
            0xFF042F2E, // toggleOnBg
            0xFF14B8A6, // toggleOnBorder
            0xFF5EEAD4, // toggleOnText
            0xFF102226, // toggleOffBg
            0xFF1B3940, // toggleOffBorder
            0xFF528088, // toggleOffText
            0xFF142B30, // btnBg
            0xFF1A373E, // btnBgHover
            0xFF21444C, // btnBgActive
            0xFF28535D, // btnBorder
            0xFF14B8A6, // btnBorderHover
            0xFF09171A, // slotBg
            0xFF132A2F, // slotBgHover
            0xFF1A3B42, // slotBorder
            0xFF0D9488, // accent
            0x6014B8A6  // accentGlow
    );

    public static final Theme OBSIDIAN = new Theme(
            "obsidian", "Obsidian",
            0xE0050308, // overlay
            0xFF0B0910, // panelBg
            0xFF120E1A, // chassisBody
            0xFF07050B, // innerDark
            0xFF100C17, // headerBg
            0xFF000000, // frameBlack
            0xFFA855F7, // borderGradientStart
            0xFF38BDF8, // borderGradientEnd
            0xFF050308, // borderShadow
            0xFF191324, // chassisBand
            0xFF08060D, // bevelShadow
            0xFF7C3AED, // bevelHighlight
            0xFFF8FAFC, // textWhite
            0xFFE2E8F0, // textSilver
            0xFFA855F7, // textMuted
            0xFF0D0A13, // rowBg
            0xFF171222, // rowBgHover
            0xFF2B1D3E, // rowBorder
            0xFFA855F7, // rowBorderHover
            0xFF240E3E, // toggleOnBg
            0xFFA855F7, // toggleOnBorder
            0xFFD8B4FE, // toggleOnText
            0xFF0E0B14, // toggleOffBg
            0xFF20172E, // toggleOffBorder
            0xFF6F588B, // toggleOffText
            0xFF140F1F, // btnBg
            0xFF1F1730, // btnBgHover
            0xFF2A1E40, // btnBgActive
            0xFF372654, // btnBorder
            0xFFA855F7, // btnBorderHover
            0xFF07050B, // slotBg
            0xFF130E1D, // slotBgHover
            0xFF20172E, // slotBorder
            0xFF8B5CF6, // accent
            0x60A855F7  // accentGlow
    );

    private static final List<Theme> THEMES = Arrays.asList(
            DARK,
            LIGHT,
            CRIMSON,
            COBALT,
            EMERALD,
            AMETHYST,
            AMBER,
            PRISMARINE,
            OBSIDIAN,
            GOLD,
            GRAY,
            LIGHT_GRAY,
            SILVER,
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

    public static Theme cyclePrevTheme() {
        activeThemeIndex = (activeThemeIndex - 1 + THEMES.size()) % THEMES.size();
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
