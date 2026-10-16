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
        if (windowWidth <= 0 || windowHeight <= 0) return 1.0f;

        float scaleX = (float) windowWidth / (float) REQUIRED_CANVAS_W;
        float scaleY = (float) windowHeight / (float) REQUIRED_CANVAS_H;
        float fitScale = Math.min(scaleX, scaleY);

        if (fitScale < 1.0f) {
            return Math.max(0.35f, fitScale);
        }

        return 1.0f;
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
        int panelW = BASE_PANEL_W;
        int panelH = Math.min(TARGET_PANEL_H, Math.max(BASE_PANEL_H, effH - 60));

        int bevelExpansion = BEVEL_EXPANSION;
        int frameW = panelW + (bevelExpansion * 2);
        int frameH = panelH + (bevelExpansion * 2);

        int panelX = (effW - panelW) / 2;
        int panelY = (effH - panelH) / 2;

        int frameX = panelX - bevelExpansion;
        int frameY = panelY - bevelExpansion;
        int frameRight = frameX + frameW;

        int sidePanelTargetW = SIDE_PANEL_TARGET_W;
        int sidePanelH;
        int sidePanelY;
        int sidePanelX;
        int sidePanelW;

        if (sidePanelType == BuildScapeConfigScreen.SidePanelType.PRESETS) {
            int tabW = 30;
            int tabH = 28;
            int targetW = sidePanelTargetW;
            int targetH = Math.min(280, frameH - 44);
            sidePanelY = frameY + 36;
            sidePanelX = frameRight - 2;
            sidePanelW = (int) Math.round(tabW + (targetW - tabW) * sidePanelProgress);
            sidePanelH = (int) Math.round(tabH + (targetH - tabH) * sidePanelProgress);
        } else {
            sidePanelH = Math.min(280, frameH - 44);
            sidePanelY = frameY + (frameH - sidePanelH) / 2;
            int dockX = frameRight - 2;
            int slideOffset = (int) Math.round((1.0f - sidePanelProgress) * sidePanelTargetW);
            sidePanelX = dockX - slideOffset;
            sidePanelW = sidePanelTargetW;
        }

        return new LayoutResult(panelX, panelY, panelW, panelH,
                frameX, frameY, frameW, frameH, frameRight,
                sidePanelX, sidePanelY, sidePanelW, sidePanelH);
    }
}
