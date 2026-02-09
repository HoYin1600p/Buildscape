package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.client.screen.widget.ConfigCategoryButton;
import com.kingodogo.buildscape.client.screen.widget.ScaledTextButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.client.Minecraft;
import java.net.URI;

public class BuildScapeConfigScreen extends Screen {
    // Percentage-based layout: 11% sidebar, 44% left content, 1% gap, 44% right content
    private static final double SIDEBAR_WIDTH_PERCENT = 0.11; // 11% of full screen width
    private static final double LEFT_CONTENT_WIDTH_PERCENT = 0.44; // 44% of full screen width
    private static final double GAP_PERCENT = 0.01; // 1% of full screen width (gap between left and right)
    private static final double RIGHT_CONTENT_WIDTH_PERCENT = 0.44; // 44% of full screen width
    // Legacy constants for backwards compatibility
    private static final double CONTENT_WIDTH_PERCENT = LEFT_CONTENT_WIDTH_PERCENT; // 44% of full screen width
    private static final double RIGHT_PANEL_WIDTH_PERCENT = RIGHT_CONTENT_WIDTH_PERCENT; // 44% of full screen width
    
    // Reference resolution: 1920x1080 at GUI scale 2 (standard baseline)
    private static final int REFERENCE_WIDTH = 1920;
    private static final int REFERENCE_HEIGHT = 1080;
    private static final double REFERENCE_GUI_SCALE = 2.0;
    
    // Base sizes at reference resolution and GUI scale 2
    private static final int BASE_SPACING = 10;
    private static final int BASE_CATEGORY_BUTTON_HEIGHT = 20;
    private static final int BASE_CATEGORY_BUTTON_SPACING = 2;
    private static final int BASE_BUTTON_HEIGHT = 20;
    private static final int BASE_EDITBOX_HEIGHT = 20;
    
    // Minimum and maximum sizes to prevent UI from being too small or too large
    private static final int MIN_BUTTON_HEIGHT = 16;
    private static final int MAX_BUTTON_HEIGHT = 32;
    private static final int MIN_SPACING = 6;
    private static final int MAX_SPACING = 20;
    
    // Legacy fixed width for backwards compatibility (will be calculated from percentage)
    private int calculatedSidebarWidth = 200;
    
    /**
     * Calculate a comprehensive scale factor based on both display resolution and GUI scale.
     * This ensures UI elements are appropriately sized for any monitor (21", 42", etc.) and GUI scale (1x-4x).
     * 
     * Algorithm:
     * 1. Gets actual window resolution (physical pixels) and GUI scale
     * 2. Calculates effective resolution (what user sees after GUI scaling)
     * 3. Compares to reference resolution (1920x1080 at GUI scale 2)
     * 4. Calculates resolution-based scale factor
     * 5. Calculates GUI scale factor
     * 6. Combines both with weighted average (resolution 70%, GUI scale 30%)
     * 7. Applies constraints to prevent extreme scaling
     * 
     * Examples:
     * - 4K monitor (3840x2160) at GUI scale 2: effective = 1920x1080, scale ≈ 1.0 (perfect match)
     * - 1080p monitor (1920x1080) at GUI scale 1: effective = 1920x1080, scale ≈ 0.85 (slightly smaller)
     * - 1440p monitor (2560x1440) at GUI scale 3: effective = 853x480, scale ≈ 0.7 (smaller, high DPI)
     * - Large 4K monitor at GUI scale 4: effective = 960x540, scale ≈ 0.6 (very small, needs scaling up)
     * 
     * @return Scale factor that accounts for both resolution and GUI scale (typically 0.5 to 2.0)
     */
    public static double calculateScaleFactor() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) {
            return 1.0; // Fallback if window not initialized
        }
        
        // Get actual window resolution (physical pixels)
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        
        // Get current GUI scale
        double currentGuiScale = mc.getWindow().getGuiScale();
        
        // Calculate effective resolution (what the user sees after GUI scaling)
        // This is the "logical" resolution that Minecraft uses for rendering
        int effectiveWidth = (int)(windowWidth / currentGuiScale);
        int effectiveHeight = (int)(windowHeight / currentGuiScale);
        
        // Calculate scale factor based on resolution difference from reference
        // Use the smaller dimension to maintain aspect ratio and prevent over-scaling
        double widthScale = (double)effectiveWidth / REFERENCE_WIDTH;
        double heightScale = (double)effectiveHeight / REFERENCE_HEIGHT;
        double resolutionScale = Math.min(widthScale, heightScale);
        
        // Also account for GUI scale difference from reference
        // Higher GUI scale = smaller effective resolution = elements should be larger
        // Lower GUI scale = larger effective resolution = elements can be smaller
        double guiScaleFactor = currentGuiScale / REFERENCE_GUI_SCALE;
        
        // Combine both factors with weighted average
        // Resolution has more weight (70%) because it's the primary factor
        // GUI scale has less weight (30%) but still matters for fine-tuning
        double combinedScale = (resolutionScale * 0.7) + (guiScaleFactor * 0.3);
        
        // Clamp to reasonable bounds to prevent UI from being unusable
        // Minimum 0.5x ensures elements aren't too small to click
        // Maximum 2.0x prevents elements from being comically large
        combinedScale = Math.max(0.5, Math.min(2.0, combinedScale));
        
        return combinedScale;
    }
    
    /**
     * Scale a base size intelligently based on both display resolution and GUI scale.
     * This ensures professional appearance across all monitor sizes and GUI scales.
     * 
     * @param baseSize Base size at reference resolution (1920x1080) and GUI scale 2
     * @return Appropriately scaled size
     */
    public static int scaleSize(int baseSize) {
        double scaleFactor = calculateScaleFactor();
        int scaled = (int)(baseSize * scaleFactor);
        
        // Round to nearest even number for cleaner rendering
        return (scaled / 2) * 2;
    }
    
    /**
     * Scale a size with minimum and maximum constraints.
     * Useful for buttons and other interactive elements that need to remain usable.
     * 
     * @param baseSize Base size at reference resolution
     * @param minSize Minimum allowed size
     * @param maxSize Maximum allowed size
     * @return Constrained scaled size
     */
    public static int scaleSizeConstrained(int baseSize, int minSize, int maxSize) {
        int scaled = scaleSize(baseSize);
        return Math.max(minSize, Math.min(maxSize, scaled));
    }
    
    /**
     * Get scaled spacing value with constraints.
     */
    public static int getScaledSpacing() {
        return scaleSizeConstrained(BASE_SPACING, MIN_SPACING, MAX_SPACING);
    }
    
    /**
     * Get scaled category button height with constraints.
     */
    public static int getScaledCategoryButtonHeight() {
        return scaleSizeConstrained(BASE_CATEGORY_BUTTON_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }
    
    /**
     * Get scaled category button spacing.
     */
    public static int getScaledCategoryButtonSpacing() {
        return scaleSize(BASE_CATEGORY_BUTTON_SPACING);
    }
    
    /**
     * Get scaled button height with constraints.
     */
    public static int getScaledButtonHeight() {
        return scaleSizeConstrained(BASE_BUTTON_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }
    
    /**
     * Get scaled EditBox height with constraints.
     */
    public static int getScaledEditBoxHeight() {
        return scaleSizeConstrained(BASE_EDITBOX_HEIGHT, MIN_BUTTON_HEIGHT, MAX_BUTTON_HEIGHT);
    }
    
    /**
     * Get the current effective resolution (after GUI scaling).
     * Useful for debugging and layout calculations.
     */
    public static int getEffectiveWidth() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return REFERENCE_WIDTH;
        int windowWidth = mc.getWindow().getWidth();
        double guiScale = mc.getWindow().getGuiScale();
        return (int)(windowWidth / guiScale);
    }
    
    /**
     * Get the current effective height (after GUI scaling).
     */
    public static int getEffectiveHeight() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) return REFERENCE_HEIGHT;
        int windowHeight = mc.getWindow().getHeight();
        double guiScale = mc.getWindow().getGuiScale();
        return (int)(windowHeight / guiScale);
    }
    
    private final Screen parentScreen;
    private ConfigCategoryButton pillarItemsButton;
    private ConfigCategoryButton pillarParticlesButton;
    private ConfigCategoryButton pillarIdsButton;
    private ConfigCategoryButton supportersButton;
    private AbstractConfigTab activeTab;
    private Button kofiButton;
    private Button editGuiButton;
    
    // Track last window dimensions to avoid unnecessary recalculations
    private int lastWindowWidth = -1;
    private int lastWindowHeight = -1;
    
    public BuildScapeConfigScreen(Screen parent) {
        super(new TranslatableComponent("buildscape.config.title"));
        this.parentScreen = parent;
    }
    
    /**
     * Recalculate screen dimensions - use Minecraft's native GUI scaled dimensions.
     * This ensures the layout works correctly at any GUI scale setting.
     */
    private void recalculateDimensions() {
        Minecraft mc = Minecraft.getInstance();
        int windowWidth = mc.getWindow().getWidth();
        int windowHeight = mc.getWindow().getHeight();
        
        // Only recalculate if window size actually changed (prevents flickering)
        if (windowWidth == lastWindowWidth && windowHeight == lastWindowHeight) {
            return;
        }
        
        lastWindowWidth = windowWidth;
        lastWindowHeight = windowHeight;
        
        // Use Minecraft's native GUI scaled dimensions - no modifications needed
        // Minecraft handles all scaling automatically based on user's GUI scale setting
        try {
            java.lang.reflect.Field widthField = Screen.class.getDeclaredField("width");
            java.lang.reflect.Field heightField = Screen.class.getDeclaredField("height");
            widthField.setAccessible(true);
            heightField.setAccessible(true);
            
            // Get the actual GUI scaled dimensions from Minecraft
            int guiScaledWidth = mc.getWindow().getGuiScaledWidth();
            int guiScaledHeight = mc.getWindow().getGuiScaledHeight();
            
            widthField.setInt(this, guiScaledWidth);
            heightField.setInt(this, guiScaledHeight);
            
            // Calculate sidebar width based on percentage
            calculatedSidebarWidth = (int)(guiScaledWidth * SIDEBAR_WIDTH_PERCENT);
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to set screen dimensions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void init() {
        // Override width and height to use fixed GUI scale before calling super.init()
        // This ensures the config screen always uses the same scale regardless of user's Minecraft GUI scale setting
        recalculateDimensions();
        
        super.init();
        
        // Sidebar buttons - use percentage-based sizing relative to sidebar width
        // Calculate sidebar width based on percentage
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        
        // Default positions (percentage-based, relative to sidebar)
        int scaledSpacing = getScaledSpacing();
        int sidebarX = scaledSpacing;
        int sidebarY = scaleSize(30);
        // Button width is percentage of sidebar width (90% to leave padding)
        int buttonWidth = (int)(calculatedSidebarWidth * 0.90);
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing();
        
        // Create sidebar buttons with percentage-based positioning
        // They will auto-scale with sidebar width
        pillarItemsButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.items"),
            (button) -> setActiveTab(new PillarItemsConfigTab(this))
        );
        addRenderableWidget(pillarItemsButton);
        
        // Position next button below previous one
        sidebarY += buttonHeight + spacing;
        pillarParticlesButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.particles"),
            (button) -> setActiveTab(new PillarParticlesConfigTab(this))
        );
        addRenderableWidget(pillarParticlesButton);
        
        // Position next button below previous one
        sidebarY += buttonHeight + spacing;
        pillarIdsButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.ids"),
            (button) -> setActiveTab(new PillarIdsConfigTab(this))
        );
        addRenderableWidget(pillarIdsButton);
        
        // Position next button below previous one
        sidebarY += buttonHeight + spacing;
        supportersButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.supporters"),
            (button) -> setActiveTab(new com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersOnlyTab(this))
        );
        addRenderableWidget(supportersButton);
        
        // Edit GUI button - DISABLED (hidden from users)
        // Edit GUI button is intentionally not added to screen

        // Ko-fi button in sidebar (at bottom)
        int kofiY = height - scaleSize(30);
        kofiButton = new ScaledTextButton(
            sidebarX, kofiY,
            buttonWidth, getScaledButtonHeight(),
            new TranslatableComponent("buildscape.config.kofi"),
            (button) -> openKofiLink()
        );
        addRenderableWidget(kofiButton);
        
        // Set default tab
        if (activeTab == null) {
            setActiveTab(new PillarItemsConfigTab(this));
        }
    }
    
    @Override
    public void resize(Minecraft mc, int width, int height) {
        // Force recalculation by resetting tracked dimensions
        lastWindowWidth = -1;
        lastWindowHeight = -1;
        
        // Recalculate dimensions with fixed scale when window is resized
        recalculateDimensions();
        
        // Recalculate sidebar width and update all sidebar button positions/sizes
        calculatedSidebarWidth = (int)(this.width * SIDEBAR_WIDTH_PERCENT);
        int scaledSpacing = getScaledSpacing();
        int sidebarX = scaledSpacing;
        int sidebarY = scaleSize(30);
        int buttonWidth = (int)(calculatedSidebarWidth * 0.90);
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing();
        
        // Update category buttons
        if (pillarItemsButton != null) {
            pillarItemsButton.x = sidebarX;
            pillarItemsButton.y = sidebarY;
            pillarItemsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarItemsButton, buttonHeight);
            } catch (Exception e) {
                // Fallback
            }
        }
        
        if (pillarParticlesButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarParticlesButton.x = sidebarX;
            pillarParticlesButton.y = sidebarY;
            pillarParticlesButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarParticlesButton, buttonHeight);
            } catch (Exception e) {
                // Fallback
            }
        }
        
        if (pillarIdsButton != null) {
            sidebarY += buttonHeight + spacing;
            pillarIdsButton.x = sidebarX;
            pillarIdsButton.y = sidebarY;
            pillarIdsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarIdsButton, buttonHeight);
            } catch (Exception e) {
                // Fallback
            }
        }
        
        if (supportersButton != null) {
            sidebarY += buttonHeight + spacing;
            supportersButton.x = sidebarX;
            supportersButton.y = sidebarY;
            supportersButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(supportersButton, buttonHeight);
            } catch (Exception e) {
                // Fallback
            }
        }
        
        // Update bottom buttons
        if (editGuiButton != null) {
            editGuiButton.x = sidebarX;
            editGuiButton.y = this.height - scaleSize(60);
            editGuiButton.setWidth(buttonWidth);
        }
        if (kofiButton != null) {
            kofiButton.x = sidebarX;
            kofiButton.y = this.height - scaleSize(30);
            kofiButton.setWidth(buttonWidth);
        }
        
        // Call super with our fixed-scale dimensions, not the passed parameters
        super.resize(mc, this.width, this.height);
    }
    
    public void setActiveTab(AbstractConfigTab tab) {
        // Remove old tab widgets and clear button states first
        if (activeTab != null) {
            activeTab.onClose();
        }
        
        // Clear all button states before setting new one
        if (pillarItemsButton != null) pillarItemsButton.setActive(false);
        if (pillarParticlesButton != null) pillarParticlesButton.setActive(false);
        if (pillarIdsButton != null) pillarIdsButton.setActive(false);
        if (supportersButton != null) supportersButton.setActive(false);
        
        activeTab = tab;
        if (activeTab != null) {
            activeTab.init();
        }
        
        // Update button states
        updateButtonStates();
    }
    
    /**
     * Get the currently active tab
     */
    public AbstractConfigTab getActiveTab() {
        return activeTab;
    }
    
    private void updateButtonStates() {
        if (activeTab == null) {
            // Clear all if no active tab
            if (pillarItemsButton != null) pillarItemsButton.setActive(false);
            if (pillarParticlesButton != null) pillarParticlesButton.setActive(false);
            if (pillarIdsButton != null) pillarIdsButton.setActive(false);
            if (supportersButton != null) supportersButton.setActive(false);
            return;
        }
        
        boolean isItems = activeTab instanceof PillarItemsConfigTab;
        boolean isParticles = activeTab instanceof PillarParticlesConfigTab;
        boolean isIds = activeTab instanceof PillarIdsConfigTab;
        boolean isSupporters = activeTab instanceof com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersOnlyTab;
        
        if (pillarItemsButton != null) pillarItemsButton.setActive(isItems);
        if (pillarParticlesButton != null) pillarParticlesButton.setActive(isParticles);
        if (pillarIdsButton != null) pillarIdsButton.setActive(isIds);
        if (supportersButton != null) supportersButton.setActive(isSupporters);
    }
    
    private void openKofiLink() {
        String kofiUrl = "https://ko-fi.com/itzmedga";

        // Send clickable link in chat
        if (Minecraft.getInstance().player != null) {
            // Create clickable chat component
            net.minecraft.network.chat.MutableComponent linkComponent = new net.minecraft.network.chat.TextComponent("Ko-fi: ");
            net.minecraft.network.chat.MutableComponent urlComponent = new net.minecraft.network.chat.TextComponent(kofiUrl)
                .withStyle(style -> style
                    .withColor(net.minecraft.ChatFormatting.AQUA)
                    .withUnderlined(true)
                    .withClickEvent(new net.minecraft.network.chat.ClickEvent(
                        net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,
                        kofiUrl
                    ))
                    .withHoverEvent(new net.minecraft.network.chat.HoverEvent(
                        net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                        new net.minecraft.network.chat.TextComponent("Click to open Ko-fi link")
                    ))
                );

            linkComponent.append(urlComponent);
            Minecraft.getInstance().player.sendMessage(linkComponent, java.util.UUID.randomUUID());
        }

        // Also try to open in browser
        try {
            URI uri = new URI(kofiUrl);
            java.awt.Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
            // Silently fail if browser opening doesn't work - user can still click the chat link
        }
    }
    
    private void openGuiEditor() {
        if (activeTab != null) {
            String tabName = activeTab.getTabName();
            Minecraft.getInstance().setScreen(new com.kingodogo.buildscape.client.screen.GuiEditorScreen(this, tabName, activeTab));
        }
    }
    
    @Override
    public void render(com.mojang.blaze3d.vertex.PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Check if window was resized (only recalculates if size actually changed)
        recalculateDimensions();
        
        // Render background
        this.renderBackground(poseStack);
        
        // Calculate sidebar width based on percentage (recalculate in case width changed)
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        
        // Render sidebar background
        fill(poseStack, 0, 0, calculatedSidebarWidth, height, 0xC0101010);
        
        // Render title (styled like buttons - smaller and auto-aligned within sidebar)
        int titleY = scaleSize(10);
        // Calculate title width and center it within sidebar (with padding)
        int titlePadding = scaleSize(10);
        int maxTitleWidth = calculatedSidebarWidth - titlePadding * 2;
        int titleTextWidth = font.width(title);
        
        // Scale down title text if it doesn't fit, or use smaller scale for styling
        float titleScale = 0.85f; // Make title smaller
        if (titleTextWidth > maxTitleWidth) {
            titleScale = Math.max(0.5f, (float)maxTitleWidth / titleTextWidth * 0.85f);
        }
        
        // Center title horizontally within sidebar
        int titleX = titlePadding + (calculatedSidebarWidth - titlePadding * 2) / 2;
        
        poseStack.pushPose();
        poseStack.translate(titleX, titleY, 0);
        poseStack.scale(titleScale, titleScale, 1.0f);
        // Center the text after scaling
        drawCenteredString(poseStack, font, title, 0, 0, 0xFFFFFF);
        poseStack.popPose();
        
        // Render active tab - no coordinate conversion needed, Minecraft handles it
        if (activeTab != null) {
            activeTab.render(poseStack, mouseX, mouseY, partialTick);
        }
        
        // Render widgets - no coordinate conversion needed
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // No coordinate conversion needed - Minecraft handles GUI scaling automatically
        if (activeTab != null && activeTab.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // No coordinate conversion needed - Minecraft handles GUI scaling automatically
        if (activeTab != null && activeTab.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // No coordinate conversion needed - Minecraft handles GUI scaling automatically
        if (activeTab != null && activeTab.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // No coordinate conversion needed - Minecraft handles GUI scaling automatically
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
        if (keyCode == 256) { // ESC
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
    
    /**
     * Get the X position of the content area (calculated from percentage).
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getContentX() {
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        return calculatedSidebarWidth;
    }
    
    /**
     * Get the Y position of the content area.
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getContentY() {
        return scaleSize(30);
    }
    
    /**
     * Get the width of the left content area (calculated from percentage: 44% of full screen width).
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getContentWidth() {
        return (int)(width * LEFT_CONTENT_WIDTH_PERCENT);
    }
    
    /**
     * Get the width of the sidebar (calculated from percentage: 11% of screen width).
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getSidebarWidth() {
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        return calculatedSidebarWidth;
    }
    
    /**
     * Get the width of the right panel (calculated from percentage: 44% of full screen width).
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getRightPanelWidth() {
        return (int)(width * RIGHT_CONTENT_WIDTH_PERCENT);
    }
    
    /**
     * Get the X position of the right panel.
     * Uses percentage-based layout: 11% sidebar + 44% left content + 1% gap = right panel start
     */
    public int getRightPanelX() {
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        int leftContentWidth = (int)(width * LEFT_CONTENT_WIDTH_PERCENT);
        int gap = (int)(width * GAP_PERCENT);
        return calculatedSidebarWidth + leftContentWidth + gap;
    }
    
    /**
     * Get the height of the content area.
     * Uses percentage-based layout that automatically adjusts to any display size and GUI scale.
     */
    public int getContentHeight() {
        return height - scaleSize(30); // Ko-fi button is now in sidebar
    }
    
    // Public method to allow tabs to add widgets
    public void addTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        // Try to find addRenderableWidget method by signature (works in both dev and production)
        try {
            // First try the GuiEventListener signature (most common in 1.18.2)
            java.lang.reflect.Method addMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod(
                "addRenderableWidget",
                net.minecraft.client.gui.components.events.GuiEventListener.class
            );
            addMethod.setAccessible(true);
            addMethod.invoke(this, widget);
        } catch (NoSuchMethodException e) {
            // If that doesn't work, try to find by signature matching
            try {
                for (java.lang.reflect.Method method : net.minecraft.client.gui.screens.Screen.class.getDeclaredMethods()) {
                    if (method.getName().equals("addRenderableWidget") && 
                        method.getParameterCount() == 1 && 
                        method.getParameterTypes()[0].isAssignableFrom(net.minecraft.client.gui.components.events.GuiEventListener.class)) {
                        method.setAccessible(true);
                        method.invoke(this, widget);
                        return;
                    }
                }
                BuildScape.getLogger().error("Could not find addRenderableWidget method");
            } catch (Exception ex) {
                BuildScape.getLogger().error("Failed to add widget: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to add widget: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Public method to allow tabs to remove widgets
    public void removeTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        try {
            // Try to find removeWidget method
            java.lang.reflect.Method removeMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod(
                "removeWidget",
                net.minecraft.client.gui.components.events.GuiEventListener.class
            );
            removeMethod.setAccessible(true);
            removeMethod.invoke(this, widget);
        } catch (NoSuchMethodException e) {
            // Try alternative method names
            try {
                for (java.lang.reflect.Method method : net.minecraft.client.gui.screens.Screen.class.getDeclaredMethods()) {
                    if ((method.getName().equals("removeWidget") || method.getName().equals("remove")) && 
                        method.getParameterCount() == 1 && 
                        method.getParameterTypes()[0].isAssignableFrom(net.minecraft.client.gui.components.events.GuiEventListener.class)) {
                        method.setAccessible(true);
                        method.invoke(this, widget);
                        return;
                    }
                }
                // If no remove method found, try to access the children list and remove directly
                try {
                    java.lang.reflect.Field childrenField = net.minecraft.client.gui.screens.Screen.class.getDeclaredField("children");
                    childrenField.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    java.util.List<net.minecraft.client.gui.components.events.GuiEventListener> children = 
                        (java.util.List<net.minecraft.client.gui.components.events.GuiEventListener>) childrenField.get(this);
                    if (children != null) {
                        children.remove(widget);
                    }
                } catch (Exception ex) {
                    BuildScape.getLogger().error("Failed to remove widget: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } catch (Exception ex) {
                BuildScape.getLogger().error("Failed to remove widget: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to remove widget: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

