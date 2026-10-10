package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.ClientEvents;
import com.kingodogo.buildscape.client.screen.widget.ConfigCategoryButton;
import com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper;
import com.kingodogo.buildscape.client.screen.widget.ICustomWidget;
import com.kingodogo.buildscape.config.PillarParticleConfig;
import com.kingodogo.buildscape.platform.Services;
import com.kingodogo.buildscape.util.ComponentHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class BuildScapeConfigScreen implements IScreenDelegate {
    private static final double SIDEBAR_WIDTH_PERCENT = 0.11;
    private static final double LEFT_GAP_PERCENT = 0.005;
    private static final double GAP_SIDEBAR_PANEL_PERCENT = 0.005;
    private static final double PANEL_GAP_PERCENT = 0.005;
    private static final double RIGHT_GAP_PERCENT = 0.005;
    private static final double PANEL_HEIGHT_GAP_PERCENT = 0.005;

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
    private final Component title = ComponentHelper.translatable("buildscape.config.title");
    public int width;
    public int height;
    private Consumer<AbstractWidget> addWidgetConsumer;
    private Consumer<GuiEventListener> removeWidgetConsumer;

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
    private Screen wrapperScreen;

    public BuildScapeConfigScreen(Screen parent) {
        this.parentScreen = parent;
    }

    @Override
    public void setWrapperScreen(Object screen) {
        if (screen instanceof Screen s) {
            this.wrapperScreen = s;
        }
    }

    @Override
    public Screen getWrapperScreen() {
        return this.wrapperScreen;
    }

    private void recalculateDimensions() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return;
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();

        if (windowWidth == lastWindowWidth && windowHeight == lastWindowHeight) {
            return;
        }

        lastWindowWidth = windowWidth;
        lastWindowHeight = windowHeight;

        int guiScaledWidth = mc.getWindow().getGuiScaledWidth();
        int guiScaledHeight = mc.getWindow().getGuiScaledHeight();

        this.width = guiScaledWidth;
        this.height = guiScaledHeight;

        calculatedSidebarWidth = (int) (guiScaledWidth * SIDEBAR_WIDTH_PERCENT);
    }

    @Override
    public void init(Minecraft mc, int width, int height, Consumer<AbstractWidget> addWidget, Consumer<GuiEventListener> removeWidget) {
        this.width = width;
        this.height = height;
        this.addWidgetConsumer = addWidget;
        this.removeWidgetConsumer = removeWidget;

        recalculateDimensions();

        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);

        int sidebarAreaX = (int) (width * LEFT_GAP_PERCENT);
        int sidebarAreaWidth = calculatedSidebarWidth;

        int buttonMargin = (int) (width * 0.005);
        int buttonX = sidebarAreaX + buttonMargin;
        int buttonWidth = sidebarAreaWidth - (buttonMargin * 2);

        int sidebarY = getContentY();
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing() + (int) (height * 0.005);

        pillarItemsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.category.items"),
                (button) -> {
                    if (checkConfigAccessAndNotify()) setActiveTab(new PillarItemsConfigTab(this));
                });
        addWidget.accept(pillarItemsButton.getButton());

        sidebarY += buttonHeight + spacing;
        pillarParticlesButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.category.particles"),
                (button) -> {
                    if (checkConfigAccessAndNotify()) setActiveTab(new PillarParticlesConfigTab(this));
                });
        addWidget.accept(pillarParticlesButton.getButton());

        sidebarY += buttonHeight + spacing;
        pillarIdsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.category.ids"),
                (button) -> {
                    if (checkOpAccessAndNotify()) setActiveTab(new PillarIdsConfigTab(this));
                });
        addWidget.accept(pillarIdsButton.getButton());

        sidebarY += buttonHeight + spacing;
        worldSettingsButton = new ConfigCategoryButton(
                buttonX, sidebarY,
                buttonWidth, buttonHeight,
                ComponentHelper.translatable("buildscape.config.category.others"),
                (button) -> {
                    setActiveTab(new WorldSettingsConfigTab(this));
                });
        addWidget.accept(worldSettingsButton.getButton());

        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int bottomPadding = scaleSize(10);
        int kofiY = height - frameHeight - bottomPadding;

        kofiButton = Services.PLATFORM.createButton(
                buttonX, kofiY,
                buttonWidth, frameHeight,
                ComponentHelper.literal("Ko-fi"),
                (button) -> openKofiLink());
        addWidget.accept(kofiButton);

        int reportY = kofiY - getScaledCategoryButtonHeight() - spacing - scaleSize(6);
        reportButton = new ConfigCategoryButton(
                buttonX, reportY,
                buttonWidth, getScaledCategoryButtonHeight(),
                ComponentHelper.literal("Report"),
                (button) -> openReportLink());
        addWidget.accept(reportButton.getButton());

        if (activeTab == null) {
            if (hasConfigAccess()) {
                setActiveTab(new PillarItemsConfigTab(this));
            } else {
                setActiveTab(new WorldSettingsConfigTab(this));
            }
        } else {
            activeTab.init();
            updateButtonStates();
        }

        updateCategoryButtonScales();
    }

    public boolean hasOpAccess() {
        if (Minecraft.getInstance().player == null) {
            return false;
        }
        return Services.PLATFORM.hasPlayerPermissions(Minecraft.getInstance().player, 2);
    }

    public boolean hasConfigAccess() {
        return Minecraft.getInstance().player != null
                && Services.PLATFORM.hasPlayerPermissions(Minecraft.getInstance().player, PillarParticleConfig.CONFIG_PERMISSION_LEVEL);
    }

    private boolean checkConfigAccessAndNotify() {
        if (hasConfigAccess()) return true;

        ClientEvents.setOverlayMessage(
                ComponentHelper.translatable("buildscape.config.server_config_only")
        );
        return false;
    }

    private boolean checkOpAccessAndNotify() {
        if (hasOpAccess()) return true;

        ClientEvents.setOverlayMessage(
                ComponentHelper.translatable("buildscape.config.op_only")
        );
        return false;
    }

    @Override
    public void resize(Minecraft mc, int width, int height) {
        this.width = width;
        this.height = height;
        lastWindowWidth = -1;
        lastWindowHeight = -1;

        recalculateDimensions();

        calculatedSidebarWidth = (int) (this.width * SIDEBAR_WIDTH_PERCENT);

        int sidebarAreaX = (int) (this.width * LEFT_GAP_PERCENT);
        int buttonMargin = (int) (this.width * 0.005);
        int buttonX = sidebarAreaX + buttonMargin;
        int buttonWidth = calculatedSidebarWidth - (buttonMargin * 2);

        int sidebarY = getContentY();
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing() + (int) (this.height * 0.005);

        if (pillarItemsButton != null) {
            pillarItemsButton.setPosition(buttonX, sidebarY);
            pillarItemsButton.setWidth(buttonWidth);
            pillarItemsButton.setHeight(buttonHeight);
        }

        if (pillarParticlesButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarParticlesButton.setPosition(buttonX, sidebarY);
            pillarParticlesButton.setWidth(buttonWidth);
            pillarParticlesButton.setHeight(buttonHeight);
        }

        if (pillarIdsButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarIdsButton.setPosition(buttonX, sidebarY);
            pillarIdsButton.setWidth(buttonWidth);
            pillarIdsButton.setHeight(buttonHeight);
        }

        if (worldSettingsButton != null) {
            sidebarY += buttonHeight + spacing;
            worldSettingsButton.setPosition(buttonX, sidebarY);
            worldSettingsButton.setWidth(buttonWidth);
            worldSettingsButton.setHeight(buttonHeight);
        }

        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int bottomPadding = scaleSize(10);
        int kofiY = this.height - frameHeight - bottomPadding;

        if (editGuiButton != null) {
            WidgetLayoutHelper.setPosition(editGuiButton, buttonX, this.height - scaleSize(60));
            editGuiButton.setWidth(buttonWidth);
        }
        if (kofiButton != null) {
            WidgetLayoutHelper.setPosition(kofiButton, buttonX, kofiY);
            kofiButton.setWidth(buttonWidth);
            WidgetLayoutHelper.setWidgetHeight(kofiButton, frameHeight);
        }
        if (reportButton != null) {
            int reportY = kofiY - buttonHeight - spacing - scaleSize(6);
            reportButton.setPosition(buttonX, reportY);
            reportButton.setWidth(buttonWidth);
            reportButton.setHeight(buttonHeight);
        }

        updateCategoryButtonScales();
    }

    private void updateCategoryButtonScales() {
        Font font = Minecraft.getInstance().font;
        float maxTextWidth = 0.0f;
        int maxAvailableWidth = 0;

        List<ConfigCategoryButton> buttons = Arrays.asList(
                pillarItemsButton, pillarParticlesButton, pillarIdsButton, worldSettingsButton, reportButton
        );

        for (ConfigCategoryButton btn : buttons) {
            if (btn != null) {
                maxTextWidth = Math.max(maxTextWidth, font.width(btn.getMessage()));
                int borderPadding = scaleSize(6);
                int availableWidth = btn.getWidth() - borderPadding * 2;
                if (maxAvailableWidth == 0 || availableWidth < maxAvailableWidth) {
                    maxAvailableWidth = availableWidth;
                }
            }
        }

        float commonScale = 1.0f;
        if (maxAvailableWidth > 0 && maxTextWidth > 0) {
            commonScale = Math.min(1.0f, ((float) maxAvailableWidth / maxTextWidth) * 0.90f);
        }

        for (ConfigCategoryButton btn : buttons) {
            if (btn != null) {
                btn.setTextScale(commonScale);
            }
        }
    }

    private void renderGradientTitle(Object poseStackOrGraphics, int x, int y, String text, float scale, boolean drawShadow) {
        Font font = Minecraft.getInstance().font;
        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, x, y, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, scale, scale);

        int textWidth = font.width(text);
        int startX = -textWidth / 2;

        int[] colors = new int[]{0xFF00FFFF, 0xFF0088FF, 0xFF8800FF, 0xFFFF00FF, 0xFFFF8800};

        if (drawShadow) {
            for (int ox = -1; ox <= 1; ox++) {
                for (int oy = -1; oy <= 1; oy++) {
                    if (ox == 0 && oy == 0) continue;
                    Services.PLATFORM.draw(poseStackOrGraphics, font, text, startX + ox, oy, 0xFF000000);
                }
            }
        } else {
            Services.PLATFORM.draw(poseStackOrGraphics, font, text, startX + 1f, 1f, 0xFF000000);
        }

        float colorStep = (float) (colors.length - 1) / (float) text.length();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String charStr = String.valueOf(c);

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

            Services.PLATFORM.draw(poseStackOrGraphics, font, charStr, preciseX, 0, color);
        }

        Services.PLATFORM.popGuiPose(poseStackOrGraphics);
    }

    private void drawTexture(Object poseStackOrGraphics, int x, int y, int w, int h, String texturePath) {
        drawCroppedTexture(poseStackOrGraphics, x, y, w, h, w, h, texturePath);
    }

    private void drawCroppedTexture(Object poseStackOrGraphics, int x, int y, int w, int h, int texW, int texH, String texturePath) {
        Services.PLATFORM.blit(poseStackOrGraphics, com.kingodogo.buildscape.util.CommonId.of("buildscape", "textures/gui/" + texturePath), x, y, 0, 0, w, h, texW, texH);
    }

    private void renderCustomFrame(Object poseStackOrGraphics, int x, int y, int width, int height) {
        int cx = 6;
        int cy = 6;
        int btX = 1;
        int btY = 1;

        drawCroppedTexture(poseStackOrGraphics, x + cx, y, width - cx * 2, btY, 178, 1, "frame/green_vertical.png");
        drawCroppedTexture(poseStackOrGraphics, x + cx, y + height - btY, width - cx * 2, btY, 178, 1, "frame/blue_vertical.png");
        drawCroppedTexture(poseStackOrGraphics, x, y + cy, btX, height - cy * 2, 1, 22, "frame/middleside_horizontal.png");
        drawCroppedTexture(poseStackOrGraphics, x + width - btX, y + cy, btX, height - cy * 2, 1, 22, "frame/middleside_horizontal-1.png");

        drawCroppedTexture(poseStackOrGraphics, x, y, cx, cy, cx, cy, "frame/topleft_corner.png");
        drawCroppedTexture(poseStackOrGraphics, x + width - cx, y, cx, cy, cx, cy, "frame/topright_corner.png");
        drawCroppedTexture(poseStackOrGraphics, x, y + height - cy, cx, cy, cx, cy, "frame/bottomleft_corner.png");
        drawCroppedTexture(poseStackOrGraphics, x + width - cx, y + height - cy, cx, cy, cx, cy, "frame/bottomright_corner.png");
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
            Services.PLATFORM.openUri(new URI(kofiUrl));
        } catch (Exception e) {
        }
    }

    private void openReportLink() {
        String reportUrl = "https://buildscape.online/report";
        try {
            Services.PLATFORM.openUri(new URI(reportUrl));
        } catch (Exception e) {
        }
    }

    private void openGuiEditor() {
        if (activeTab != null) {
            String tabName = activeTab.getTabName();
            Services.PLATFORM.openScreen(Services.PLATFORM.createGuiEditorScreen(this.getWrapperScreen(), tabName, activeTab));
        }
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        recalculateDimensions();

        Services.PLATFORM.fill(poseStackOrGraphics, 0, 0, width, height, 0x80000000);

        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);
        int sidebarStartX = (int) (width * LEFT_GAP_PERCENT);

        Services.PLATFORM.fill(poseStackOrGraphics, sidebarStartX, 0, sidebarStartX + calculatedSidebarWidth, height, 0xC0101010);

        int buttonMargin = (int) (width * 0.005);
        int frameX = sidebarStartX + buttonMargin;
        int frameWidth = calculatedSidebarWidth - (buttonMargin * 2);

        int frameHeight = getScaledCategoryButtonHeight() + scaleSize(4);
        int frameY = scaleSize(10);

        int titlePadding = 10;
        int maxTitleWidth = frameWidth - titlePadding * 2;
        Font font = Minecraft.getInstance().font;
        int titleTextWidth = font.width(title);

        float titleScale = 1.0f;
        if (titleTextWidth > maxTitleWidth) {
            titleScale = Math.max(0.5f, (float) maxTitleWidth / titleTextWidth);
        }

        int titleY = (int) (frameY + (frameHeight / 2.0f) - ((8.0f * titleScale) / 2.0f));
        int titleX = frameX + frameWidth / 2;

        renderCustomFrame(poseStackOrGraphics, frameX, frameY, frameWidth, frameHeight);

        renderGradientTitle(poseStackOrGraphics, titleX, titleY, title.getString(), titleScale, true);

        if (activeTab != null) {
            activeTab.render(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderAfterWidgets(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        Services.PLATFORM.renderClientOverlay(poseStackOrGraphics, width, height);

        if (activeTab != null) {
            activeTab.renderTooltips(poseStackOrGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (activeTab != null && activeTab.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (activeTab != null && activeTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (activeTab != null && activeTab.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return false;
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
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (activeTab != null && activeTab.charTyped(codePoint, modifiers)) {
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        if (activeTab != null) {
            activeTab.onClose();
        }
        Services.PLATFORM.openScreen(parentScreen);
    }

    public int getContentX() {
        calculatedSidebarWidth = (int) (width * SIDEBAR_WIDTH_PERCENT);
        int leftGap = (int) (width * LEFT_GAP_PERCENT);
        int gapAfterSidebar = (int) (width * GAP_SIDEBAR_PANEL_PERCENT);
        return leftGap + calculatedSidebarWidth + gapAfterSidebar;
    }

    public int getContentY() {
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
        int topGap = getContentY();
        int bottomGap = (int) (height * 0.005);
        return height - topGap - bottomGap;
    }

    public int getVerticalPanelGap() {
        return Math.max(1, (int) (height * 0.005));
    }

    public static float getStandardTextScale() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return 1.0f;
        double guiScale = mc.getWindow().getGuiScale();
        if (guiScale >= 3.0) return 0.75f;
        if (guiScale >= 2.5) return 0.85f;
        return 1.0f;
    }

    public void addTabWidget(GuiEventListener widget) {
        if (widget instanceof AbstractWidget abstractWidget && addWidgetConsumer != null) {
            this.addWidgetConsumer.accept(abstractWidget);
        } else if (widget != null) {
            BuildScape.getLogger().warn("Widget cannot be directly added to screen: " + widget.getClass().getName());
        }
    }

    public GuiEventListener addCustomWidget(ICustomWidget widget) {
        if (widget == null || addWidgetConsumer == null) return null;
        AbstractWidget nativeWidget = Services.PLATFORM.wrapCustomWidget(
                widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight(),
                ComponentHelper.literal(""), widget);
        addWidgetConsumer.accept(nativeWidget);
        return nativeWidget;
    }

    private GuiEventListener focused;

    public void setFocused(GuiEventListener focused) {
        this.focused = focused;
    }

    public void setFocused(ICustomWidget focused) {
        this.focused = null;
        if (focused != null) {
            focused.setFocused(true);
        }
    }

    public GuiEventListener getFocused() {
        return this.focused;
    }

    public void removeTabWidget(GuiEventListener widget) {
        if (widget != null && removeWidgetConsumer != null) {
            this.removeWidgetConsumer.accept(widget);
        }
    }
}
