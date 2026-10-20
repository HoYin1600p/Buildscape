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
            if (this.id == null) {
                return false;
            }
            String lower = this.id.toLowerCase(java.util.Locale.ROOT);
            return "light".equals(lower)
                    || lower.startsWith("light_")
                    || "white".equals(lower);
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
            0x603CDFFF // accentGlow
    );

    public static final Theme LIGHT = new Theme(
            "light", "Light",
            0x70000000, // overlay
            0xFFE8EBF0, // panelBg (calm slate off-white, eye-protective)
            0xFFDCE0E7, // chassisBody
            0xFFE2E6ED, // innerDark (recessed container basin)
            0xFFD6DBE4, // headerBg
            0xFF161B22, // frameBlack
            0xFFF4F6F9, // borderGradientStart (soft non-glare highlight)
            0xFFC2C9D6, // borderGradientEnd
            0xFFA0A9B8, // borderShadow
            0xFFCCD2DE, // chassisBand
            0xFFAEB8C6, // bevelShadow
            0xFFF5F7FA, // bevelHighlight (gentle matte highlight)
            0xFF1E2633, // textWhite (crisp deep graphite - easy on eyes)
            0xFF3E4A5B, // textSilver (readable secondary labels)
            0xFF6A778B, // textMuted (soft helper text)
            0xFFF4F6F9, // rowBg (softer card background, zero glare)
            0xFFEAEFF5, // rowBgHover
            0xFFC4CBD7, // rowBorder
            0xFF0284C7, // rowBorderHover
            0xFFD7F5E3, // toggleOnBg
            0xFF16A34A, // toggleOnBorder
            0xFF14532D, // toggleOnText
            0xFFE5E9F0, // toggleOffBg
            0xFFB4BDCC, // toggleOffBorder
            0xFF647285, // toggleOffText
            0xFFECEFF5, // btnBg (softer elevated button)
            0xFFE2E7EF, // btnBgHover
            0xFFD5DCE7, // btnBgActive
            0xFFB8C1D0, // btnBorder
            0xFF0284C7, // btnBorderHover
            0xFFD8DEE8, // slotBg
            0xFFE5EBF4, // slotBgHover
            0xFFB4BED0, // slotBorder
            0xFF0284C7, // accent (calming azure blue)
            0x400284C7 // accentGlow
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
            0x500284C7 // accentGlow
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
            0x50E11D48 // accentGlow
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
            0x50EA580C // accentGlow
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
            0x5016A34A // accentGlow
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
            0x509333EA // accentGlow
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
            0x5094A3B8 // accentGlow
    );

    public static final Theme LIGHT_GRAY = new Theme(
            "light_gray", "Light Gray",
            0x70101418, // overlay
            0xFFEDEDF0, // panelBg
            0xFFDFDFE4, // chassisBody
            0xFFE6E6EB, // innerDark
            0xFFDCDCE2, // headerBg
            0xFF18181C, // frameBlack
            0xFFF5F5F8, // borderGradientStart (soft highlight)
            0xFFC8C8D0, // borderGradientEnd
            0xFFA8A8B2, // borderShadow
            0xFFD2D2DA, // chassisBand
            0xFFB4B4BE, // bevelShadow
            0xFFF5F5F8, // bevelHighlight (soft highlight)
            0xFF18181C, // textWhite
            0xFF383842, // textSilver
            0xFF6A6A78, // textMuted
            0xFFF3F3F6, // rowBg (softer card, zero glare)
            0xFFEAEAF0, // rowBgHover
            0xFFCECED8, // rowBorder
            0xFF64748B, // rowBorderHover
            0xFFDCFCE7, // toggleOnBg
            0xFF16A34A, // toggleOnBorder
            0xFF15803D, // toggleOnText
            0xFFF0F0F4, // toggleOffBg
            0xFFC0C0CB, // toggleOffBorder
            0xFF64748B, // toggleOffText
            0xFFEAEAF0, // btnBg (softer elevated button)
            0xFFDFDFE6, // btnBgHover
            0xFFD4D4DC, // btnBgActive
            0xFFC2C2CD, // btnBorder
            0xFF64748B, // btnBorderHover
            0xFFDFDFE7, // slotBg
            0xFFEBEBF2, // slotBgHover
            0xFFBCBCC8, // slotBorder
            0xFF64748B, // accent
            0x5064748B // accentGlow
    );

    public static final Theme SILVER = new Theme(
            "silver", "Silver",
            0xD8101418, // overlay
            0xFF1A1E24, // panelBg
            0xFF242830, // chassisBody
            0xFF14171C, // innerDark
            0xFF1E222A, // headerBg
            0xFF0A0C0E, // frameBlack
            0xFFCBD5E1, // borderGradientStart
            0xFF64748B, // borderGradientEnd
            0xFF101418, // borderShadow
            0xFF2C323C, // chassisBand
            0xFF12151A, // bevelShadow
            0xFF94A3B8, // bevelHighlight
            0xFFF8FAFC, // textWhite
            0xFFCBD5E1, // textSilver
            0xFF94A3B8, // textMuted
            0xFF1C2026, // rowBg
            0xFF262C34, // rowBgHover
            0xFF3A424E, // rowBorder
            0xFF38BDF8, // rowBorderHover
            0xFF1E3A4A, // toggleOnBg
            0xFF38BDF8, // toggleOnBorder
            0xFF7DD3FC, // toggleOnText
            0xFF1E2228, // toggleOffBg
            0xFF3B4350, // toggleOffBorder
            0xFF7D8898, // toggleOffText
            0xFF222830, // btnBg
            0xFF2C3440, // btnBgHover
            0xFF36404E, // btnBgActive
            0xFF465264, // btnBorder
            0xFF38BDF8, // btnBorderHover
            0xFF14171C, // slotBg
            0xFF202630, // slotBgHover
            0xFF303844, // slotBorder
            0xFF38BDF8, // accent
            0x6038BDF8 // accentGlow
    );

    public static final Theme LIGHT_SILVER = new Theme(
            "light_silver", "Light Silver",
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
            0x5038BDF8 // accentGlow
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
            0x60F59E0B // accentGlow
    );

    public static final Theme LIGHT_GOLD = new Theme(
            "light_gold", "Light Gold",
            0x701A1402, // overlay
            0xFFFEFDF6, // panelBg
            0xFFFAF3DC, // chassisBody
            0xFFFDF8E8, // innerDark
            0xFFF6ECC4, // headerBg
            0xFF2A2004, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFE9DC9F, // borderGradientEnd
            0xFFC8B56A, // borderShadow
            0xFFEFE4B5, // chassisBand
            0xFFD2C07D, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF332402, // textWhite
            0xFF543E08, // textSilver
            0xFF85681A, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFEFBF0, // rowBgHover
            0xFFECDC9E, // rowBorder
            0xFFD97706, // rowBorderHover
            0xFFFEF3C7, // toggleOnBg
            0xFFD97706, // toggleOnBorder
            0xFFB45309, // toggleOnText
            0xFFF7F2E2, // toggleOffBg
            0xFFD5C8A8, // toggleOffBorder
            0xFF7D7254, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFEFAEA, // btnBgHover
            0xFFFAF0D2, // btnBgActive
            0xFFE3D094, // btnBorder
            0xFFD97706, // btnBorderHover
            0xFFF7EED0, // slotBg
            0xFFFCF6E4, // slotBgHover
            0xFFDECB8E, // slotBorder
            0xFFD97706, // accent
            0x50D97706 // accentGlow
    );

    public static final Theme RED = new Theme(
            "red", "Red",
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
            0x60E11D48 // accentGlow
    );

    public static final Theme BLUE = new Theme(
            "blue", "Blue",
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
            0x603B82F6 // accentGlow
    );

    public static final Theme GREEN = new Theme(
            "green", "Green",
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
            0x6022C55E // accentGlow
    );

    public static final Theme PURPLE = new Theme(
            "purple", "Purple",
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
            0x60A855F7 // accentGlow
    );

    public static final Theme ORANGE = new Theme(
            "orange", "Orange",
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
            0x60F97316 // accentGlow
    );

    public static final Theme CYAN = new Theme(
            "cyan", "Cyan",
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
            0x6014B8A6 // accentGlow
    );

    public static final Theme LIGHT_CYAN = new Theme(
            "light_cyan", "Light Cyan",
            0x7000181A, // overlay
            0xFFF0FDFD, // panelBg
            0xFFE0F7F6, // chassisBody
            0xFFE8FAF9, // innerDark
            0xFFD2F1EF, // headerBg
            0xFF082224, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFBBEAE7, // borderGradientEnd
            0xFF93D6D2, // borderShadow
            0xFFC7EDE9, // chassisBand
            0xFFA3DED9, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF082B2E, // textWhite
            0xFF154A4E, // textSilver
            0xFF367A80, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF0FDFD, // rowBgHover
            0xFFBEECE8, // rowBorder
            0xFF0D9488, // rowBorderHover
            0xFFCCFBF1, // toggleOnBg
            0xFF0D9488, // toggleOnBorder
            0xFF0F766E, // toggleOnText
            0xFFEDF8F7, // toggleOffBg
            0xFFB8D8D5, // toggleOffBorder
            0xFF587D7A, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFEDFAF8, // btnBgHover
            0xFFDCF4F1, // btnBgActive
            0xFFB4E4E0, // btnBorder
            0xFF0D9488, // btnBorderHover
            0xFFDDF3F1, // slotBg
            0xFFEAF8F6, // slotBgHover
            0xFFB0DFDA, // slotBorder
            0xFF0D9488, // accent
            0x500D9488 // accentGlow
    );

    public static final Theme BLACK = new Theme(
            "black", "Black",
            0xE8050507, // overlay
            0xFF0C0C0E, // panelBg
            0xFF141418, // chassisBody
            0xFF08080A, // innerDark
            0xFF101014, // headerBg
            0xFF000000, // frameBlack
            0xFF71717A, // borderGradientStart
            0xFF27272A, // borderGradientEnd
            0xFF050507, // borderShadow
            0xFF18181D, // chassisBand
            0xFF08080A, // bevelShadow
            0xFF3F3F46, // bevelHighlight
            0xFFF4F4F5, // textWhite
            0xFFD4D4D8, // textSilver
            0xFFA1A1AA, // textMuted
            0xFF0E0E12, // rowBg
            0xFF17171D, // rowBgHover
            0xFF27272A, // rowBorder
            0xFF71717A, // rowBorderHover
            0xFF18181B, // toggleOnBg
            0xFF22C55E, // toggleOnBorder
            0xFF4ADE80, // toggleOnText
            0xFF121215, // toggleOffBg
            0xFF27272A, // toggleOffBorder
            0xFF71717A, // toggleOffText
            0xFF16161A, // btnBg
            0xFF1F1F26, // btnBgHover
            0xFF272730, // btnBgActive
            0xFF33333D, // btnBorder
            0xFF71717A, // btnBorderHover
            0xFF08080A, // slotBg
            0xFF131317, // slotBgHover
            0xFF202026, // slotBorder
            0xFFE4E4E7, // accent
            0x60E4E4E7 // accentGlow
    );

    public static final Theme WHITE = new Theme(
            "white", "White",
            0x70000000, // overlay
            0xFFF2F1ED, // panelBg (warm eye-protective alabaster paper, zero glare)
            0xFFE6E5E0, // chassisBody
            0xFFECEBE6, // innerDark
            0xFFDFDDD7, // headerBg
            0xFF1C1B19, // frameBlack
            0xFFF9F8F5, // borderGradientStart (soft non-glare highlight)
            0xFFC8C6BF, // borderGradientEnd
            0xFFA2A098, // borderShadow
            0xFFD4D2CB, // chassisBand
            0xFFB3B1A9, // bevelShadow
            0xFFFBFBFA, // bevelHighlight (soft matte highlight)
            0xFF1F1E1B, // textWhite (crisp dark espresso ink)
            0xFF42403B, // textSilver (readable secondary labels)
            0xFF6E6C65, // textMuted (soft helper text)
            0xFFF8F7F4, // rowBg (softer warm card background, zero glare)
            0xFFEEEEEA, // rowBgHover
            0xFFCAC8C0, // rowBorder
            0xFF0284C7, // rowBorderHover
            0xFFD5F3DF, // toggleOnBg
            0xFF16A34A, // toggleOnBorder
            0xFF14532D, // toggleOnText
            0xFFE7E6E1, // toggleOffBg
            0xFFB9B7AF, // toggleOffBorder
            0xFF6A6861, // toggleOffText
            0xFFECEBE6, // btnBg (soft elevated warm button)
            0xFFE3E1DB, // btnBgHover
            0xFFD7D5CE, // btnBgActive
            0xFFBEBCB4, // btnBorder
            0xFF0284C7, // btnBorderHover
            0xFFDDDCD5, // slotBg
            0xFFE9E8E2, // slotBgHover
            0xFFB7B5AC, // slotBorder
            0xFF0284C7, // accent
            0x500284C7 // accentGlow
    );

    public static final Theme PINK = new Theme(
            "pink", "Pink",
            0xD81C0612, // overlay
            0xFF220E18, // panelBg
            0xFF301422, // chassisBody
            0xFF180A12, // innerDark
            0xFF28101C, // headerBg
            0xFF0A0307, // frameBlack
            0xFFF472B6, // borderGradientStart
            0xFF831843, // borderGradientEnd
            0xFF160610, // borderShadow
            0xFF3A182A, // chassisBand
            0xFF12050D, // bevelShadow
            0xFFEC4899, // bevelHighlight
            0xFFFDF2F8, // textWhite
            0xFFFBCFE8, // textSilver
            0xFFF472B6, // textMuted
            0xFF240E19, // rowBg
            0xFF331424, // rowBgHover
            0xFF561A3C, // rowBorder
            0xFFEC4899, // rowBorderHover
            0xFF4C0528, // toggleOnBg
            0xFFEC4899, // toggleOnBorder
            0xFFF9A8D4, // toggleOnText
            0xFF220F19, // toggleOffBg
            0xFF421C2F, // toggleOffBorder
            0xFF8B5A73, // toggleOffText
            0xFF2C1220, // btnBg
            0xFF3A182B, // btnBgHover
            0xFF471C35, // btnBgActive
            0xFF5C2243, // btnBorder
            0xFFEC4899, // btnBorderHover
            0xFF170911, // slotBg
            0xFF2A101E, // slotBgHover
            0xFF40182E, // slotBorder
            0xFFEC4899, // accent
            0x60EC4899 // accentGlow
    );

    public static final Theme LIGHT_PINK = new Theme(
            "light_pink", "Light Pink",
            0x70200814, // overlay
            0xFFFFF0F5, // panelBg
            0xFFFCE7F0, // chassisBody
            0xFFFFF5F9, // innerDark
            0xFFFAD1E2, // headerBg
            0xFF330818, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFF4B8D1, // borderGradientEnd
            0xFFE594B8, // borderShadow
            0xFFF6C8DC, // chassisBand
            0xFFEAA6C4, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF38081A, // textWhite
            0xFF601732, // textSilver
            0xFF964567, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFFF0F6, // rowBgHover
            0xFFF7CADA, // rowBorder
            0xFFEC4899, // rowBorderHover
            0xFFFCE7F3, // toggleOnBg
            0xFFEC4899, // toggleOnBorder
            0xFFBE185D, // toggleOnText
            0xFFF9EEF3, // toggleOffBg
            0xFFE0C0CE, // toggleOffBorder
            0xFF8A6273, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFDF2F7, // btnBgHover
            0xFFFCE7F3, // btnBgActive
            0xFFF4B8D1, // btnBorder
            0xFFEC4899, // btnBorderHover
            0xFFFCECF2, // slotBg
            0xFFFDF4F8, // slotBgHover
            0xFFF0BDD2, // slotBorder
            0xFFEC4899, // accent
            0x50EC4899 // accentGlow
    );

    public static final Theme BROWN = new Theme(
            "brown", "Brown",
            0xD81A1008, // overlay
            0xFF221710, // panelBg
            0xFF2E2018, // chassisBody
            0xFF19100B, // innerDark
            0xFF281C15, // headerBg
            0xFF0A0704, // frameBlack
            0xFFA16207, // borderGradientStart
            0xFF451A03, // borderGradientEnd
            0xFF150C06, // borderShadow
            0xFF36261D, // chassisBand
            0xFF140D08, // bevelShadow
            0xFFB45309, // bevelHighlight
            0xFFFEF3C7, // textWhite
            0xFFE7D0B7, // textSilver
            0xFFA3866E, // textMuted
            0xFF231811, // rowBg
            0xFF2F2119, // rowBgHover
            0xFF4E3628, // rowBorder
            0xFFB45309, // rowBorderHover
            0xFF451A03, // toggleOnBg
            0xFFD97706, // toggleOnBorder
            0xFFFDE68A, // toggleOnText
            0xFF241913, // toggleOffBg
            0xFF432F23, // toggleOffBorder
            0xFF886A55, // toggleOffText
            0xFF2A1E16, // btnBg
            0xFF37281E, // btnBgHover
            0xFF443226, // btnBgActive
            0xFF5A3E2F, // btnBorder
            0xFFB45309, // btnBorderHover
            0xFF17100B, // slotBg
            0xFF261B13, // slotBgHover
            0xFF3E2B20, // slotBorder
            0xFFD97706, // accent
            0x60D97706 // accentGlow
    );

    public static final Theme LIGHT_BROWN = new Theme(
            "light_brown", "Light Brown",
            0x70181008, // overlay
            0xFFF7F2EE, // panelBg
            0xFFEAE1D8, // chassisBody
            0xFFF2ECE5, // innerDark
            0xFFE2D6CB, // headerBg
            0xFF281C14, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFCBB9A7, // borderGradientEnd
            0xFFAA9581, // borderShadow
            0xFFD8C9B9, // chassisBand
            0xFFBCA693, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF2A1D15, // textWhite
            0xFF4F392B, // textSilver
            0xFF7D6553, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF5EFEA, // rowBgHover
            0xFFD6C6B7, // rowBorder
            0xFF92400E, // rowBorderHover
            0xFFFEF3C7, // toggleOnBg
            0xFFB45309, // toggleOnBorder
            0xFF78350F, // toggleOnText
            0xFFEDE5DE, // toggleOffBg
            0xFFC8B7A8, // toggleOffBorder
            0xFF736050, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFF7F1EB, // btnBgHover
            0xFFEBE2D9, // btnBgActive
            0xFFCAB8A8, // btnBorder
            0xFF92400E, // btnBorderHover
            0xFFEFE7E0, // slotBg
            0xFFF7F2EC, // slotBgHover
            0xFFC6B4A3, // slotBorder
            0xFF92400E, // accent
            0x5092400E // accentGlow
    );

    public static final Theme YELLOW = new Theme(
            "yellow", "Yellow",
            0xD81A1602, // overlay
            0xFF201D0B, // panelBg
            0xFF2C2812, // chassisBody
            0xFF171507, // innerDark
            0xFF282410, // headerBg
            0xFF0A0902, // frameBlack
            0xFFFACC15, // borderGradientStart
            0xFF854D0E, // borderGradientEnd
            0xFF141203, // borderShadow
            0xFF363216, // chassisBand
            0xFF131105, // bevelShadow
            0xFFEAB308, // bevelHighlight
            0xFFFEFCE8, // textWhite
            0xFFFEF08A, // textSilver
            0xFFB3A85C, // textMuted
            0xFF211E0C, // rowBg
            0xFF2C2812, // rowBgHover
            0xFF4D461E, // rowBorder
            0xFFEAB308, // rowBorderHover
            0xFF423805, // toggleOnBg
            0xFFEAB308, // toggleOnBorder
            0xFFFEF08A, // toggleOnText
            0xFF211E0F, // toggleOffBg
            0xFF413B1F, // toggleOffBorder
            0xFF877F54, // toggleOffText
            0xFF292510, // btnBg
            0xFF363116, // btnBgHover
            0xFF443E1C, // btnBgActive
            0xFF5A5224, // btnBorder
            0xFFEAB308, // btnBorderHover
            0xFF161407, // slotBg
            0xFF25210D, // slotBgHover
            0xFF3E3718, // slotBorder
            0xFFEAB308, // accent
            0x60EAB308 // accentGlow
    );

    public static final Theme LIGHT_YELLOW = new Theme(
            "light_yellow", "Light Yellow",
            0x70181604, // overlay
            0xFFFEFDF5, // panelBg
            0xFFF9F5DE, // chassisBody
            0xFFFCF9EB, // innerDark
            0xFFF5EFC7, // headerBg
            0xFF2E2A0A, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFE8DFA8, // borderGradientEnd
            0xFFC7BC7A, // borderShadow
            0xFFEFE7BC, // chassisBand
            0xFFD1C588, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF332D06, // textWhite
            0xFF574E12, // textSilver
            0xFF82772E, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFEFCE8, // rowBgHover
            0xFFECE4B2, // rowBorder
            0xFFCA8A04, // rowBorderHover
            0xFFFEF9C3, // toggleOnBg
            0xFFCA8A04, // toggleOnBorder
            0xFF854D0E, // toggleOnText
            0xFFF7F3E2, // toggleOffBg
            0xFFD8D0A8, // toggleOffBorder
            0xFF7D754C, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFEFBE8, // btnBgHover
            0xFFF9F4D4, // btnBgActive
            0xFFE4DAA0, // btnBorder
            0xFFCA8A04, // btnBorderHover
            0xFFF6F0D0, // slotBg
            0xFFFCF8E3, // slotBgHover
            0xFFDECFA0, // slotBorder
            0xFFCA8A04, // accent
            0x50CA8A04 // accentGlow
    );

    public static final Theme LIME = new Theme(
            "lime", "Lime",
            0xD8081604, // overlay
            0xFF121F0D, // panelBg
            0xFF1A2D14, // chassisBody
            0xFF0C1608, // innerDark
            0xFF172812, // headerBg
            0xFF050B03, // frameBlack
            0xFF84CC16, // borderGradientStart
            0xFF365314, // borderGradientEnd
            0xFF081005, // borderShadow
            0xFF203718, // chassisBand
            0xFF0A1306, // bevelShadow
            0xFF65A30D, // bevelHighlight
            0xFFF7FEE7, // textWhite
            0xFFD9F99D, // textSilver
            0xFF84CC16, // textMuted
            0xFF13210E, // rowBg
            0xFF1C3115, // rowBgHover
            0xFF2C4C20, // rowBorder
            0xFF84CC16, // rowBorderHover
            0xFF1A2E05, // toggleOnBg
            0xFF84CC16, // toggleOnBorder
            0xFFBEF264, // toggleOnText
            0xFF132111, // toggleOffBg
            0xFF253B1F, // toggleOffBorder
            0xFF68855F, // toggleOffText
            0xFF192B13, // btnBg
            0xFF223A1A, // btnBgHover
            0xFF2B4A21, // btnBgActive
            0xFF375D2A, // btnBorder
            0xFF84CC16, // btnBorderHover
            0xFF0E180A, // slotBg
            0xFF1A2C14, // slotBgHover
            0xFF29461F, // slotBorder
            0xFF65A30D, // accent
            0x6084CC16 // accentGlow
    );

    public static final Theme LIGHT_LIME = new Theme(
            "light_lime", "Light Lime",
            0x70081804, // overlay
            0xFFF7FCF2, // panelBg
            0xFFEBF7E2, // chassisBody
            0xFFF1FAEA, // innerDark
            0xFFE0F3D3, // headerBg
            0xFF142B0A, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFCDEAB8, // borderGradientEnd
            0xFFA5CD88, // borderShadow
            0xFFD6EFC4, // chassisBand
            0xFFB4DC94, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF163308, // textWhite
            0xFF264F12, // textSilver
            0xFF507E34, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFF4FBEF, // rowBgHover
            0xFFCEE8B8, // rowBorder
            0xFF65A30D, // rowBorderHover
            0xFFECFCCB, // toggleOnBg
            0xFF65A30D, // toggleOnBorder
            0xFF3F6212, // toggleOnText
            0xFFF1F6EC, // toggleOffBg
            0xFFC2D4B8, // toggleOffBorder
            0xFF697C60, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFF2FAE9, // btnBgHover
            0xFFE6F5D9, // btnBgActive
            0xFFC2E4A6, // btnBorder
            0xFF65A30D, // btnBorderHover
            0xFFE5F3DC, // slotBg
            0xFFEFF8EA, // slotBgHover
            0xFFBEDE9E, // slotBorder
            0xFF65A30D, // accent
            0x5065A30D // accentGlow
    );

    public static final Theme MAGENTA = new Theme(
            "magenta", "Magenta",
            0xD81A0416, // overlay
            0xFF220C1F, // panelBg
            0xFF30122C, // chassisBody
            0xFF180816, // innerDark
            0xFF2A0F26, // headerBg
            0xFF0A0309, // frameBlack
            0xFFE879F9, // borderGradientStart
            0xFF701A75, // borderGradientEnd
            0xFF140412, // borderShadow
            0xFF3A1735, // chassisBand
            0xFF120511, // bevelShadow
            0xFFC026D3, // bevelHighlight
            0xFFFDF4FF, // textWhite
            0xFFF5D0FE, // textSilver
            0xFFE879F9, // textMuted
            0xFF230D20, // rowBg
            0xFF32132D, // rowBgHover
            0xFF561C4E, // rowBorder
            0xFFC026D3, // rowBorderHover
            0xFF4A044E, // toggleOnBg
            0xFFC026D3, // toggleOnBorder
            0xFFF0ABFC, // toggleOnText
            0xFF241021, // toggleOffBg
            0xFF441F3E, // toggleOffBorder
            0xFF885781, // toggleOffText
            0xFF2C1128, // btnBg
            0xFF3A1835, // btnBgHover
            0xFF481F43, // btnBgActive
            0xFF5E2457, // btnBorder
            0xFFC026D3, // btnBorderHover
            0xFF180816, // slotBg
            0xFF2B0F27, // slotBgHover
            0xFF42173D, // slotBorder
            0xFFC026D3, // accent
            0x60C026D3 // accentGlow
    );

    public static final Theme LIGHT_MAGENTA = new Theme(
            "light_magenta", "Light Magenta",
            0x701A0418, // overlay
            0xFFFDF4FB, // panelBg
            0xFFF8E5F4, // chassisBody
            0xFFFAECF7, // innerDark
            0xFFF2D5EC, // headerBg
            0xFF2E0929, // frameBlack
            0xFFFFFFFF, // borderGradientStart
            0xFFEBC4E4, // borderGradientEnd
            0xFFD69ECC, // borderShadow
            0xFFEDCEE7, // chassisBand
            0xFFDCABD3, // bevelShadow
            0xFFFFFFFF, // bevelHighlight
            0xFF360830, // textWhite
            0xFF56154F, // textSilver
            0xFF8A3C80, // textMuted
            0xFFFFFFFF, // rowBg
            0xFFFCF0FA, // rowBgHover
            0xFFECC8E6, // rowBorder
            0xFFC026D3, // rowBorderHover
            0xFFFAE8F7, // toggleOnBg
            0xFFC026D3, // toggleOnBorder
            0xFFA21CAF, // toggleOnText
            0xFFF8EFF6, // toggleOffBg
            0xFFD6BFD2, // toggleOffBorder
            0xFF82627E, // toggleOffText
            0xFFFFFFFF, // btnBg
            0xFFFDF0FB, // btnBgHover
            0xFFF8E0F4, // btnBgActive
            0xFFE6C0E0, // btnBorder
            0xFFC026D3, // btnBorderHover
            0xFFF5DEF1, // slotBg
            0xFFFCEBF8, // slotBgHover
            0xFFE0B8DC, // slotBorder
            0xFFC026D3, // accent
            0x50C026D3 // accentGlow
    );

    public static final List<Theme> BASE_THEMES = Arrays.asList(
            DARK,
            RED,
            PINK,
            MAGENTA,
            PURPLE,
            BLUE,
            CYAN,
            GREEN,
            LIME,
            YELLOW,
            ORANGE,
            BROWN,
            GRAY,
            BLACK,
            SILVER,
            GOLD);

    private static final List<Theme> THEMES = Arrays.asList(
            DARK,
            LIGHT,
            RED,
            LIGHT_RED,
            PINK,
            LIGHT_PINK,
            MAGENTA,
            LIGHT_MAGENTA,
            PURPLE,
            LIGHT_PURPLE,
            BLUE,
            LIGHT_BLUE,
            CYAN,
            LIGHT_CYAN,
            GREEN,
            LIGHT_GREEN,
            LIME,
            LIGHT_LIME,
            YELLOW,
            LIGHT_YELLOW,
            ORANGE,
            LIGHT_ORANGE,
            BROWN,
            LIGHT_BROWN,
            GRAY,
            LIGHT_GRAY,
            BLACK,
            WHITE,
            SILVER,
            LIGHT_SILVER,
            GOLD,
            LIGHT_GOLD);
    private static int activeThemeIndex = 0;

    static {
        try {
            String saved = BuildscapeClientConfig.get().getUiTheme();
            setThemeById(saved);
        } catch (Exception ignored) {
        }
    }

    public static List<Theme> getAllThemes() {
        return THEMES;
    }

    public static List<Theme> getBaseThemes() {
        return BASE_THEMES;
    }

    public static Theme getBaseTheme(Theme theme) {
        if (theme == null) {
            return DARK;
        }
        String id = theme.id.toLowerCase(java.util.Locale.ROOT);
        if ("light".equals(id)) {
            return DARK;
        }
        if ("white".equals(id)) {
            return BLACK;
        }
        if (id.startsWith("light_")) {
            String baseId = id.substring("light_".length());
            for (Theme base : BASE_THEMES) {
                if (base.id.equalsIgnoreCase(baseId)) {
                    return base;
                }
            }
        }
        for (Theme base : BASE_THEMES) {
            if (base.id.equalsIgnoreCase(id)) {
                return base;
            }
        }
        return DARK;
    }

    public static Theme getDarkVariant(Theme theme) {
        return getBaseTheme(theme);
    }

    public static Theme getLightVariant(Theme theme) {
        Theme base = getBaseTheme(theme);
        String baseId = base.id.toLowerCase(java.util.Locale.ROOT);
        if ("dark".equals(baseId)) {
            return LIGHT;
        }
        if ("black".equals(baseId)) {
            return WHITE;
        }
        if ("gold".equals(baseId)) {
            return LIGHT_GOLD;
        }
        String lightId = "light_" + baseId;
        for (Theme t : THEMES) {
            if (t.id.equalsIgnoreCase(lightId)) {
                return t;
            }
        }
        return LIGHT;
    }

    public static Theme getVariant(Theme baseTheme, boolean lightMode) {
        return lightMode ? getLightVariant(baseTheme) : getDarkVariant(baseTheme);
    }

    public static Theme getOppositeVariant(Theme theme) {
        if (theme == null || theme.isLight()) {
            return getDarkVariant(theme);
        } else {
            return getLightVariant(theme);
        }
    }

    public static Theme cycleNextBaseTheme(Theme currentTheme) {
        Theme base = getBaseTheme(currentTheme);
        int idx = BASE_THEMES.indexOf(base);
        if (idx < 0) {
            idx = 0;
        }
        int nextIdx = (idx + 1) % BASE_THEMES.size();
        return BASE_THEMES.get(nextIdx);
    }

    public static Theme cyclePrevBaseTheme(Theme currentTheme) {
        Theme base = getBaseTheme(currentTheme);
        int idx = BASE_THEMES.indexOf(base);
        if (idx < 0) {
            idx = 0;
        }
        int prevIdx = (idx - 1 + BASE_THEMES.size()) % BASE_THEMES.size();
        return BASE_THEMES.get(prevIdx);
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
        Theme curr = getCurrentTheme();
        boolean light = curr.isLight();
        Theme nextBase = cycleNextBaseTheme(curr);
        Theme next = getVariant(nextBase, light);
        setThemeById(next.id);
        return next;
    }

    public static Theme cyclePrevTheme() {
        Theme curr = getCurrentTheme();
        boolean light = curr.isLight();
        Theme prevBase = cyclePrevBaseTheme(curr);
        Theme prev = getVariant(prevBase, light);
        setThemeById(prev.id);
        return prev;
    }

    public static void setThemeIndex(int index) {
        if (index >= 0 && index < THEMES.size()) {
            activeThemeIndex = index;
        }
    }

    public static void setThemeById(String id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < THEMES.size(); i++) {
            if (THEMES.get(i).id.equalsIgnoreCase(id)) {
                activeThemeIndex = i;
                return;
            }
        }
    }

    public static Theme getById(String id) {
        if (id == null) {
            return DARK;
        }
        for (Theme t : THEMES) {
            if (t.id.equalsIgnoreCase(id)) {
                return t;
            }
        }
        return DARK;
    }
}
