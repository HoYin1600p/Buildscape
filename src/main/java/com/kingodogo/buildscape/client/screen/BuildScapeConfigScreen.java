package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.screen.widget.ConfigCategoryButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class BuildScapeConfigScreen extends Screen {
    private static final double SIDEBAR_WIDTH_PERCENT = 0.11; // 11% sidebar
    private static final double LEFT_GAP_PERCENT = 0.005; // 0.5% gap before sidebar
    private static final double GAP_SIDEBAR_PANEL_PERCENT = 0.005; // 0.5% gap after sidebar
    private static final double PANEL_GAP_PERCENT = 0.005; // 0.5% gap between panels
    private static final double RIGHT_GAP_PERCENT = 0.005; // 0.5% gap after right panel
    private static final double PANEL_HEIGHT_GAP_PERCENT = 0.005; // 0.5% gap height

    // Panel Widths: (100% - 0.5 - 11 - 0.5 - 0.5 - 0.5) / 2 = (100 - 13.0) / 2 = 87.0 / 2 = 43.5%
    private static final double LEFT_CONTENT_WIDTH_PERCENT = 0.435;
    private static final double RIGHT_CONTENT_WIDTH_PERCENT = 0.435;
    private static final double CONTENT_WIDTH_PERCENT = LEFT_CONTENT_WIDTH_PERCENT;
    private static final double RIGHT_PANEL_WIDTH_PERCENT = RIGHT_CONTENT_WIDTH_PERCENT;

    private static final int REFERENCE_WIDTH = 1920;
    private static final int REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_GUI_SCALE = 2.0;

    private static final int BASE_SPACING = 10;
    private static final int BASE_CATEGORY_BUTTON_HEIGHT = 20;
    private static final int BASE_CATEGORY_BUTTON_SPACING = 2;
    private static final int BASE_BUTTON_HEIGHT = 20;
    private static final int BASE_EDITBOX_HEIGHT = 20;

    private static final int MIN_BUTTON_HEIGHT = 16;
    private static final int MAX_BUTTON_HEIGHT = 32;
    private static final int MIN_SPACING = 6;
    private static final int MAX_SPACING = 20;

    private int calculatedSidebarWidth = 200;

    public static double calculateScaleFactor() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) {
            return 1.0;
        }

        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();

        double currentGuiScale = mc.getWindow().getGuiScale();

        int effectiveWidth = (int) (windowWidth / currentGuiScale);
        int effectiveHeight = (int) (windowHeight / currentGuiScale);

        double widthScale = (double) effectiveWidth / REFERENCE_WIDTH;
        double heightScale = (double) effectiveHeight / REFERENCE_HEIGHT;
        double resolutionScale = Math.min(widthScale, heightScale);

        double guiScaleFactor = currentGuiScale / REFERENCE_GUI_SCALE;

        double combinedScale = (resolutionScale * 0.7) + (guiScaleFactor * 0.3);

        combinedScale = Math.max(0.5, Math.min(2.0, combinedScale));

        return combinedScale;
    }

    public static int scaleSize(int baseSize) {
        double scaleFactor = calculateScaleFactor();
        int scaled = (int) (baseSize * scaleFactor);

        return (scaled / 2) * 2;
    }

    public static int scaleSizeConstrained(int baseSize, int minSize, int maxSize) {
        int scaled = scaleSize(baseSize);
        return Math.max(minSize, Math.min(maxSize, scaled));
    }

    public static int getScaledSpacing() {
        return scaleSizeConstrained(BASE_SPACING, MIN_SPACING, MAX_SPACING);
    }

    public static int getScaledCategoryButtonHeight() {
        return scaleSizeConstrained(BASE_CATEGORY_BUTTON_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }

    public static int getScaledCategoryButtonSpacing() {
        return scaleSize(BASE_CATEGORY_BUTTON_SPACING);
    }

    public static int getScaledButtonHeight() {
        return scaleSizeConstrained(BASE_BUTTON_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }

    public static int getScaledEditBoxHeight() {
        return scaleSizeConstrained(BASE_EDITBOX_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }

    public static int getEffectiveWidth() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null)
            return REFERENCE_WIDTH;
        int windowWidth = mc.getWindow().getWidth();
        double guiScale = mc.getWindow().getGuiScale();
        return (int) (windowWidth / guiScale);
    }

    public static int getEffectiveHeight() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null)
            return REFERENCE_HEIGHT;
        int windowHeight = mc.getWindow().getHeight();
        double guiScale = mc.getWindow().getGuiScale();
        return (int) (windowHeight / guiScale);
    }

    private final Screen parentScreen;
    private ConfigCategoryButton pillarItemsButton;
    private ConfigCategoryButton pillarParticlesButton;
    private ConfigCategoryButton pillarIdsButton;
    private ConfigCategoryButton worldSettingsButton;
    private ConfigCategoryButton reportButton;
    private AbstractConfigTab activeTab;
    private Button kofiButton;
    private Button editGuiButton;

    private int lastWindowWidth = -1;
    private int lastWindowHeight = -1;

    public BuildScapeConfigScreen(Screen parent) {
        super(new TranslatableComponent("buildscape.config.title"));
        this.parentScreen = parent;
    }

    private void recalculateDimensions() {
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();

        if (windowWidth == lastWindowWidth && windowHeight == lastWindowHeight) {
            return;
        }

        lastWindowWidth = windowWidth;
        lastWindowHeight = windowHeight;

        try {
            java.lang.reflect.Field widthField = Screen.class.getDeclaredField("width");
            java.lang.reflect.Field heightField = Screen.class.getDeclaredField("height");
            widthField.setAccessible(true);
            heightField.setAccessible(true);

            int guiScaledWidth = mc.getWindow().getGuiScaledWidth();
            int guiScaledHeight = mc.getWindow().getGuiScaledHeight();

            widthField.setInt(this, guiScaledWidth);
            heightField.setInt(this, guiScaledHeight);

            calculatedSidebarWidth = (int) (guiScaledWidth * SIDEBAR_WIDTH_PERCENT);
        } catch (Exception e) {
            // Silently handle - dimensions will be set correctly in init() via Minecraft's normal flow
        }
    }

    @Override
    protected void init() {
        recalculateDimensions();

        super.init();

        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);

        // Sidebar uses 0.5% padding on its LEFT (screen edge)
        // Sidebar Column = 11% of screen width
        // Buttons should have 0.5% gap on both sides within this 11% column.
        // So Button X = Left Gap (0.5%) + Button Left Margin (0.5%)?
        // User said: "buttons inside as we decided should leave 0.5% gap on both side of the nav bar"
        // This likely means the sidebar background is the 11% column (starting at 0.5% screen X).
        // And buttons are inside that with 0.5% padding relative to screen width?
        // Or relative to the sidebar itself? "on both side of the nav bar" implies the bar has padding.
        // Let's interpret: 
        // Sidebar Area: Starts at 0.5% screen X, Width 11% screen.
        // Buttons: Start at Sidebar X + 0.5% screen w. Width = Sidebar Width - 1% screen w.

        int sidebarAreaX = (int) (width * LEFT_GAP_PERCENT);
        int sidebarAreaWidth = calculatedSidebarWidth;

        int buttonMargin = (int) (width * 0.005); // 0.5% margin
        int buttonX = sidebarAreaX + buttonMargin;
        int buttonWidth = sidebarAreaWidth - (buttonMargin * 2);

        int sidebarY = getContentY(); // Start at 5% height
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing() + (int) (height * 0.005);

        pillarItemsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.category.items"),
                (button) -> {
                    if (checkOpAccessAndNotify()) setActiveTab(new PillarItemsConfigTab(this));
                });
        addRenderableWidget(pillarItemsButton);

        sidebarY += buttonHeight + spacing;
        pillarParticlesButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.category.particles"),
                (button) -> {
                    if (checkOpAccessAndNotify()) setActiveTab(new PillarParticlesConfigTab(this));
                });
        addRenderableWidget(pillarParticlesButton);

        sidebarY += buttonHeight + spacing;
        pillarIdsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.category.ids"),
                (button) -> {
                    if (checkOpAccessAndNotify()) setActiveTab(new PillarIdsConfigTab(this));
                });
        addRenderableWidget(pillarIdsButton);

        sidebarY += buttonHeight + spacing;
        worldSettingsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                new TranslatableComponent("buildscape.config.category.others"),
                (button) -> {
                    setActiveTab(new WorldSettingsConfigTab(this));
                });
        addRenderableWidget(worldSettingsButton);

        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int bottomPadding = scaleSize(10);
        int kofiY = height - frameHeight - bottomPadding;

        kofiButton = new net.minecraft.client.gui.components.Button(
                buttonX, kofiY,
                buttonWidth, frameHeight,
                new net.minecraft.network.chat.TextComponent("Ko-fi"),
                (button) -> openKofiLink());
        addRenderableWidget(kofiButton);

        int reportY = kofiY - getScaledCategoryButtonHeight() - spacing - scaleSize(6);
        reportButton = new ConfigCategoryButton(
                buttonX, reportY,
                buttonWidth, getScaledCategoryButtonHeight(),
                new net.minecraft.network.chat.TextComponent("Report"),
                (button) -> openReportLink());
        addRenderableWidget(reportButton);

        if (activeTab == null) {
            if (hasOpAccess()) {
                setActiveTab(new PillarItemsConfigTab(this));
            } else {
                setActiveTab(new WorldSettingsConfigTab(this));
            }
        } else {
            activeTab.init(); // Explicitly re-initialize the active tab to restore custom widgets if swapping back from another Screen (like ConfirmScreen)
            updateButtonStates();
        }

        updateCategoryButtonScales();
    }

    public boolean hasOpAccess() {
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        return Minecraft.getInstance().player.hasPermissions(2);
    }

    private boolean checkOpAccessAndNotify() {
        if (hasOpAccess()) return true;

        // Show message using ClientEvents overlay
        com.kingodogo.buildscape.client.ClientEvents.setOverlayMessage(
                new TranslatableComponent("buildscape.config.op_only")
        );
        return false;
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        lastWindowWidth = -1;
        lastWindowHeight = -1;

        recalculateDimensions();

        calculatedSidebarWidth = (int) (this.width * SIDEBAR_WIDTH_PERCENT);

        int sidebarAreaX = (int) (width * LEFT_GAP_PERCENT);

        int sidebarY = getContentY();
        int buttonMargin = (int) (width * 0.005);
        int buttonX = sidebarAreaX + buttonMargin;
        int buttonWidth = calculatedSidebarWidth - (buttonMargin * 2);
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing() + (int) (this.height * 0.005);

        if (pillarItemsButton != null) {
            pillarItemsButton.x = buttonX;
            pillarItemsButton.y = sidebarY;
            pillarItemsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarItemsButton, buttonHeight);
            } catch (Exception e) {
            }
        }

        if (pillarParticlesButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarParticlesButton.x = buttonX;
            pillarParticlesButton.y = sidebarY;
            pillarParticlesButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarParticlesButton, buttonHeight);
            } catch (Exception e) {
            }
        }

        if (pillarIdsButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarIdsButton.x = buttonX;
            pillarIdsButton.y = sidebarY;
            pillarIdsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarIdsButton, buttonHeight);
            } catch (Exception e) {
            }
        }

        if (worldSettingsButton != null) {
            sidebarY += buttonHeight + spacing;
            worldSettingsButton.x = buttonX;
            worldSettingsButton.y = sidebarY;
            worldSettingsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(worldSettingsButton, buttonHeight);
            } catch (Exception e) {
            }
        }

        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int bottomPadding = scaleSize(10);
        int kofiY = this.height - frameHeight - bottomPadding;

        if (editGuiButton != null) {
            editGuiButton.x = buttonX;
            editGuiButton.y = this.height - scaleSize(60);
            editGuiButton.setWidth(buttonWidth);
        }
        if (kofiButton != null) {
            kofiButton.x = buttonX;
            kofiButton.y = kofiY;
            kofiButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(kofiButton, frameHeight);
            } catch (Exception e) {
            }
        }
        if (reportButton != null) {
            int reportY = kofiY - buttonHeight - spacing - scaleSize(6);
            reportButton.x = buttonX;
            reportButton.y = reportY;
            reportButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class
                        .getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(reportButton, buttonHeight);
            } catch (Exception e) {
            }
        }

        super.resize(mc, this.width, this.height);
        updateCategoryButtonScales();
    }

    private void updateCategoryButtonScales() {
        // Calculate max text width to ensure all buttons use the same scale
        float maxTextWidth = 0.0f;
        int maxAvailableWidth = 0;

        java.util.List<ConfigCategoryButton> buttons = java.util.Arrays.asList(
                pillarItemsButton, pillarParticlesButton, pillarIdsButton, worldSettingsButton, reportButton
        );

        for (ConfigCategoryButton btn : buttons) {
            if (btn != null) {
                int width = btn.getWidth() - scaleSize(12); // Padding
                maxAvailableWidth = Math.max(maxAvailableWidth, width);
                maxTextWidth = Math.max(maxTextWidth, font.width(btn.getMessage()));
            }
        }

        float commonScale = 1.0f;
        if (maxAvailableWidth > 0 && maxTextWidth > 0) {
            // Apply a 0.90 multiplier as a safety margin to ensure text never touches the edge or triggers truncation
            commonScale = Math.min(1.0f, ((float) maxAvailableWidth / maxTextWidth) * 0.90f);
        }

        // Apply common scale to all category buttons
        for (ConfigCategoryButton btn : buttons) {
            if (btn != null) {
                btn.setTextScale(commonScale);
            }
        }
    }

    private void renderGradientTitle(com.mojang.blaze3d.vertex.PoseStack poseStack, int x, int y, String text, float scale, boolean drawShadow) {
        poseStack.pushPose();
        poseStack.translate(x, y, 0);
        poseStack.scale(scale, scale, 1.0f);

        int textWidth = font.width(text);
        int startX = -textWidth / 2;

        // Define gradient colors: Cyan -> Blue -> Purple -> Magenta -> Orange
        int[] colors = new int[]{0xFF00FFFF, 0xFF0088FF, 0xFF8800FF, 0xFFFF00FF, 0xFFFF8800};

        // Draw border/shadow first
        if (drawShadow) {
            for (int ox = -1; ox <= 1; ox++) {
                for (int oy = -1; oy <= 1; oy++) {
                    if (ox == 0 && oy == 0) continue;
                    font.draw(poseStack, text, startX + ox, oy, 0xFF000000);
                }
            }
        } else {
            font.draw(poseStack, text, startX + 1f, 1f, 0xFF000000); // Standard text drop shadow equivalent
        }

        // Draw gradient text character by character
        float colorStep = (float) (colors.length - 1) / (float) text.length();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);
            
            // Calculate the exact exact start x position using substring to preserve font kerning alignments
            String prefix = text.substring(0, i);
            int preciseX = startX + font.width(prefix);

            float colorPos = i * colorStep / (float) colors.length * (float) (colors.length - 1);
            int colorIndex = (int) colorPos;
            float progress = colorPos - colorIndex;

            int c1 = colors[Math.min(colorIndex, colors.length - 1)];
            int c2 = colors[Math.min(colorIndex + 1, colors.length - 1)];

            int r1 = (c1 >> 16) & 0xFF;
            int g1 = (c1 >> 8) & 0xFF;
            int b1 = c1 & 0xFF;

            int r2 = (c2 >> 16) & 0xFF;
            int g2 = (c2 >> 8) & 0xFF;
            int b2 = c2 & 0xFF;

            int r = (int) (r1 + (r2 - r1) * progress);
            int g = (int) (g1 + (g2 - g1) * progress);
            int b = (int) (b1 + (b2 - b1) * progress);

            int color = 0xFF000000 | (r << 16) | (g << 8) | b;

            font.draw(poseStack, charStr, preciseX, 0, color);
        }

        poseStack.popPose();
    }

    private void drawTexture(com.mojang.blaze3d.vertex.PoseStack poseStack, int x, int y, int w, int h, String texturePath) {
        drawCroppedTexture(poseStack, x, y, w, h, w, h, texturePath); // Default passthrough without cropping UV bounds
    }

    private void drawCroppedTexture(com.mojang.blaze3d.vertex.PoseStack poseStack, int x, int y, int w, int h, int texW, int texH, String texturePath) {
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, new net.minecraft.resources.ResourceLocation("buildscape:" + "textures/gui/" + texturePath));
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        com.mojang.blaze3d.vertex.Tesselator tesselator = com.mojang.blaze3d.vertex.Tesselator.getInstance();
        com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(poseStack.last().pose(), x, y + h, 0).uv(u0, v1).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), x + w, y + h, 0).uv(u1, v1).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), x + w, y, 0).uv(u1, v0).endVertex();
        bufferbuilder.vertex(poseStack.last().pose(), x, y, 0).uv(u0, v0).endVertex();
        tesselator.end();
        com.mojang.blaze3d.systems.RenderSystem.disableBlend();
    }

    private void renderCustomFrame(com.mojang.blaze3d.vertex.PoseStack poseStack, int x, int y, int width, int height) {
        int cx = 6;
        int cy = 6;
        int btX = 1;
        int btY = 1;

        // Top edge - cropped from 178x1 native width
        drawCroppedTexture(poseStack, x + cx, y, width - cx * 2, btY, 178, 1, "frame/green_vertical.png");
        // Bottom edge - cropped from 178x1 native width
        drawCroppedTexture(poseStack, x + cx, y + height - btY, width - cx * 2, btY, 178, 1, "frame/blue_vertical.png");
        // Left edge - cropped from 1x22 native height
        drawCroppedTexture(poseStack, x, y + cy, btX, height - cy * 2, 1, 22, "frame/middleside_horizontal.png");
        // Right edge - cropped from 1x22 native height using the secondary right-edge asset
        drawCroppedTexture(poseStack, x + width - btX, y + cy, btX, height - cy * 2, 1, 22, "frame/middleside_horizontal-1.png");

        // Corners (no cropping needed, they are exactly 6x6)
        drawCroppedTexture(poseStack, x, y, cx, cy, cx, cy, "frame/topleft_corner.png");
        drawCroppedTexture(poseStack, x + width - cx, y, cx, cy, cx, cy, "frame/topright_corner.png");
        drawCroppedTexture(poseStack, x, y + height - cy, cx, cy, cx, cy, "frame/bottomleft_corner.png");
        drawCroppedTexture(poseStack, x + width - cx, y + height - cy, cx, cy, cx, cy, "frame/bottomright_corner.png");
    }

    public void setActiveTab(AbstractConfigTab tab) {
        if (activeTab != null) {
            activeTab.onClose();
        }

        if (pillarItemsButton != null)
            pillarItemsButton.setActive(false);
        if (pillarParticlesButton != null)
            pillarParticlesButton.setActive(false);
        if (pillarIdsButton != null)
            pillarIdsButton.setActive(false);
        if (worldSettingsButton != null)
            worldSettingsButton.setActive(false);
        if (reportButton != null)
            reportButton.setActive(false);

        activeTab = tab;
        if (activeTab != null) {
            activeTab.init();
        }

        updateButtonStates();
    }

    public AbstractConfigTab getActiveTab() {
        return activeTab;
    }

    /**
     * Refreshes the currently active tab. Called when data is received from server.
     */
    public void refreshCurrentTab() {
        if (activeTab != null && activeTab instanceof PillarIdsConfigTab) {
            ((PillarIdsConfigTab) activeTab).refreshFromManager();
        }
    }

    private void updateButtonStates() {
        if (activeTab == null) {
            if (pillarItemsButton != null)
                pillarItemsButton.setActive(false);
            if (pillarParticlesButton != null)
                pillarParticlesButton.setActive(false);
            if (pillarIdsButton != null)
                pillarIdsButton.setActive(false);
            if (worldSettingsButton != null)
                worldSettingsButton.setActive(false);
            if (reportButton != null)
                reportButton.setActive(false);
            return;
        }

        boolean isItems = activeTab instanceof PillarItemsConfigTab;
        boolean isParticles = activeTab instanceof PillarParticlesConfigTab;
        boolean isIds = activeTab instanceof PillarIdsConfigTab || activeTab instanceof PillarIdDetailConfigTab;
        boolean isWorldSettings = activeTab instanceof WorldSettingsConfigTab;

        if (pillarItemsButton != null)
            pillarItemsButton.setActive(isItems);
        if (pillarParticlesButton != null)
            pillarParticlesButton.setActive(isParticles);
        if (pillarIdsButton != null)
            pillarIdsButton.setActive(isIds);
        if (worldSettingsButton != null)
            worldSettingsButton.setActive(isWorldSettings);
        if (reportButton != null)
            reportButton.setActive(false);
    }

    private void openKofiLink() {
        String kofiUrl = "https://ko-fi.com/itzmedga";
        try {
            net.minecraft.Util.getPlatform().openUri(new java.net.URI(kofiUrl));
        } catch (Exception e) {
        }
    }

    private void openReportLink() {
        String reportUrl = "https://buildscape.online/report";
        try {
            net.minecraft.Util.getPlatform().openUri(new java.net.URI(reportUrl));
        } catch (Exception e) {
        }
    }

    private void openGuiEditor() {
        if (activeTab != null) {
            String tabName = activeTab.getTabName();
            Minecraft.getInstance()
                    .setScreen(new com.kingodogo.buildscape.client.screen.GuiEditorScreen(this, tabName, activeTab));
        }
    }

    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        recalculateDimensions();

        this.renderBackground(poseStack);

        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);
        int sidebarStartX = (int) (width * LEFT_GAP_PERCENT);

        // Sidebar background: Draw from StartX to StartX + Width
        fill(poseStack, sidebarStartX, 0, sidebarStartX + calculatedSidebarWidth, height, 0xC0101010);

        int buttonMargin = (int) (width * 0.005);
        int frameX = sidebarStartX + buttonMargin;
        int frameWidth = calculatedSidebarWidth - (buttonMargin * 2);
        
        // Use the native scaling function instead of hardcoded 20 so the frame actively shrinks naturally to match the Category buttons on high UI scales
        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4); 
        int frameY = scaleSize(10); 

        int titlePadding = 10;
        int maxTitleWidth = frameWidth - titlePadding * 2;
        int titleTextWidth = font.width(title);

        float titleScale = 1.0f;
        if (titleTextWidth > maxTitleWidth) {
            titleScale = Math.max(0.5f, (float) maxTitleWidth / titleTextWidth);
        }

        // Aligning exact center using half frame height and half scaled font height.
        int titleY = (int) (frameY + (frameHeight / 2.0f) - ((8.0f * titleScale) / 2.0f)); 

        int titleX = frameX + frameWidth / 2;

        renderCustomFrame(poseStack, frameX, frameY, frameWidth, frameHeight);

        poseStack.pushPose();
        // Use renderGradientTitle instead of drawCenteredString
        renderGradientTitle(poseStack, titleX, titleY, title.getString(), titleScale, true);
        poseStack.popPose();

        if (activeTab != null) {
            activeTab.render(poseStack, mouseX, mouseY, partialTick);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);

        com.kingodogo.buildscape.client.ClientEvents.renderOverlay(poseStack, width, height);

        if (activeTab != null) {
            // Disable any scissor tests that might clip tooltips
            com.mojang.blaze3d.systems.RenderSystem.disableScissor();
            // Render tooltips last so they appear on top of everything
            activeTab.renderTooltips(poseStack, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (activeTab != null && activeTab.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (activeTab != null && activeTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (activeTab != null && activeTab.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (activeTab != null && activeTab.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (activeTab != null && activeTab.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if (activeTab != null) {
            activeTab.onClose();
        }
        Minecraft.getInstance().setScreen(parentScreen);
    }

    public int getContentX() {
        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);
        int leftGap = (int) (width * LEFT_GAP_PERCENT);
        int gapAfterSidebar = (int) (width * GAP_SIDEBAR_PANEL_PERCENT);
        return leftGap + calculatedSidebarWidth + gapAfterSidebar;
    }

    public int getContentY() {
        // Content panels MUST start at exactly the same Y as the first sidebar button.
        // The title frame sits at scaleSize(10) with frameHeight = getScaledCategoryButtonHeight() + scaleSize(4).
        // First sidebar button Y = titleBottom + spacing (scaleSize(8)).
        int dynamicFrameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int titleBottom = scaleSize(10) + dynamicFrameHeight;
        return titleBottom + scaleSize(8);
    }

    public int getContentWidth() {
        return (int) (width * LEFT_CONTENT_WIDTH_PERCENT);
    }

    public int getRightPanelX() {
        int contentX = getContentX();
        int leftPanelW = getContentWidth();
        int centerGap = (int) (width * PANEL_GAP_PERCENT);
        return contentX + leftPanelW + centerGap;
    }

    public int getRightPanelWidth() {
        return (int) (width * RIGHT_CONTENT_WIDTH_PERCENT);
    }

    public int getSidebarWidth() {
        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);
        return calculatedSidebarWidth;
    }


    public int getContentHeight() {
        // Total panel height: from content top down to 0.5% from bottom
        int topGap = getContentY();
        int bottomGap = (int) (height * 0.005); // 0.5% bottom gap
        return height - topGap - bottomGap;
    }

    /**
     * Returns the consistent 0.5% vertical gap between stacked panels.
     * Use this everywhere instead of hard-coding (int)(height * 0.005).
     */
    public int getVerticalPanelGap() {
        return Math.max(1, (int) (height * 0.005));
    }

    /**
     * Returns the single standard text scale for ALL label/header text across every tab.
     * Keeps all panel text the same visual size regardless of GUI scale.
     */
    public static float getStandardTextScale() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return 1.0f;
        double guiScale = mc.getWindow().getGuiScale();
        if (guiScale >= 3.0) return 0.75f;
        if (guiScale >= 2.5) return 0.85f;
        return 1.0f;
    }

    public void addTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        if (widget instanceof net.minecraft.client.gui.components.AbstractWidget abstractWidget) {
            this.addRenderableWidget(abstractWidget);
        } else {
            BuildScape.getLogger().error("Widget type not supported for direct addition (must extend AbstractWidget): " + widget.getClass().getName());
        }
    }

    public void removeTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        if (widget instanceof net.minecraft.client.gui.components.events.GuiEventListener) {
            this.removeWidget(widget);
        }
    }
}
