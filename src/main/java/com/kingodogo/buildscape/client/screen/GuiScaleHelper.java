package com.kingodogo.buildscape.client.screen;

public final class GuiScaleHelper {

    public static final int BASE_PANEL_W = 560;
    public static final int BASE_PANEL_H = 312;
    public static final int TARGET_PANEL_H = 346;

    public static final int SIDE_PANEL_TARGET_W = 188;
    public static final int BEVEL_EXPANSION = 5;
    public static final int CREST_OVERHANG_TOP = 24;
    public static final int SCREEN_PADDING = 8;

    public static final int REQUIRED_CANVAS_W = 960;
    public static final int REQUIRED_CANVAS_H = 480;

    private GuiScaleHelper() {}

    public static float calculateUiScale(int windowWidth, int windowHeight, double mcGuiScale) {
        if (windowWidth <= 0 || windowHeight <= 0) {
            return 1.0f;
        }

        float scaleX = (float) windowWidth / (float) REQUIRED_CANVAS_W;
        float scaleY = (float) windowHeight / (float) REQUIRED_CANVAS_H;
        float fitScale = Math.min(scaleX, scaleY);

        if (fitScale < 1.0f) {
            return Math.max(0.35f, fitScale);
        }

        return 1.0f;
    }

    public static boolean isShrunk(float uiScale) {
        return Math.abs(uiScale - 1.0f) > 0.001f;
    }

    public static int getEffectiveWidth(int windowWidth, float uiScale) {
        return Math.abs(uiScale - 1.0f) > 0.001f ? (int) Math.ceil(windowWidth / uiScale) : windowWidth;
    }

    public static int getEffectiveHeight(int windowHeight, float uiScale) {
        return Math.abs(uiScale - 1.0f) > 0.001f ? (int) Math.ceil(windowHeight / uiScale) : windowHeight;
    }

    public static class LayoutResult {
        public final int panelX;
        public final int panelY;
        public final int panelW;
        public final int panelH;
        public final int frameX;
        public final int frameY;
        public final int frameW;
        public final int frameH;
        public final int frameRight;
        public final int sidePanelX;
        public final int sidePanelY;
        public final int sidePanelW;
        public final int sidePanelH;

        public LayoutResult(int panelX, int panelY, int panelW, int panelH,
                int frameX, int frameY, int frameW, int frameH, int frameRight,
                int sidePanelX, int sidePanelY, int sidePanelW, int sidePanelH) {
            this.panelX = panelX;
            this.panelY = panelY;
            this.panelW = panelW;
            this.panelH = panelH;
            this.frameX = frameX;
            this.frameY = frameY;
            this.frameW = frameW;
            this.frameH = frameH;
            this.frameRight = frameRight;
            this.sidePanelX = sidePanelX;
            this.sidePanelY = sidePanelY;
            this.sidePanelW = sidePanelW;
            this.sidePanelH = sidePanelH;
        }
    }

    public static LayoutResult calculateLayout(int effW, int effH, float sidePanelProgress,
            BuildScapeConfigScreen.SidePanelType sidePanelType) {
        int bevelExpansion = BEVEL_EXPANSION;

        // Gap on screen edges: 8px on left, right, bottom; top reserves room for crest overhang
        int paddingX = SCREEN_PADDING;
        int paddingBottom = SCREEN_PADDING;
        int paddingTop = CREST_OVERHANG_TOP + 4; // leaves 4px clearance above the crest badge

        int frameX = paddingX;
        int frameY = paddingTop;
        int frameH = Math.max(BASE_PANEL_H, effH - frameY - paddingBottom);
        int panelY = frameY + bevelExpansion;
        int panelH = frameH - bevelExpansion * 2;
        int panelX = frameX + bevelExpansion;

        int sidePanelTargetW = SIDE_PANEL_TARGET_W; // 188

        // Full width when closed (leaving paddingX on both sides)
        int fullFrameW = Math.max(BASE_PANEL_W, effW - paddingX * 2);

        // When side panel opens, main panel moves/shrinks to the left to make room on the right side
        int moveAmount = (int) Math.round((sidePanelTargetW - 2) * sidePanelProgress);
        int frameRight = (frameX + fullFrameW) - moveAmount;
        int frameW = frameRight - frameX;
        int panelW = frameW - bevelExpansion * 2;

        int sidePanelX = frameRight - 2;
        int sidePanelW;
        int sidePanelH;
        int sidePanelY;

        if (sidePanelType == BuildScapeConfigScreen.SidePanelType.PRESETS) {
            int tabW = 30;
            int tabH = 28;
            int targetH = Math.min(280, frameH - 44);
            sidePanelW = (int) Math.round(tabW + (sidePanelTargetW - tabW) * sidePanelProgress);
            sidePanelH = (int) Math.round(tabH + (targetH - tabH) * sidePanelProgress);
            sidePanelY = frameY + 36;
        } else {
            sidePanelW = Math.max(0, (frameX + fullFrameW) - sidePanelX);
            int targetH = Math.min(TARGET_PANEL_H, frameH - 58);
            sidePanelH = Math.max(280, targetH);
            sidePanelY = frameY + 36;
        }

        return new LayoutResult(panelX, panelY, panelW, panelH,
                frameX, frameY, frameW, frameH, frameRight,
                sidePanelX, sidePanelY, sidePanelW, sidePanelH);
    }
}
