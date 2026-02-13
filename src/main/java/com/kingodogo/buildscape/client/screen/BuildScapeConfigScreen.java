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
    private static final double SIDEBAR_WIDTH_PERCENT = 0.11;
    private static final double LEFT_CONTENT_WIDTH_PERCENT = 0.44;
    private static final double GAP_PERCENT = 0.01;
    private static final double RIGHT_CONTENT_WIDTH_PERCENT = 0.44;
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

        int effectiveWidth = (int)(windowWidth / currentGuiScale);
        int effectiveHeight = (int)(windowHeight / currentGuiScale);

        double widthScale = (double)effectiveWidth / REFERENCE_WIDTH;
        double heightScale = (double)effectiveHeight / REFERENCE_HEIGHT;
        double resolutionScale = Math.min(widthScale, heightScale);

        double guiScaleFactor = currentGuiScale / REFERENCE_GUI_SCALE;

        double combinedScale = (resolutionScale * 0.7) + (guiScaleFactor * 0.3);

        combinedScale = Math.max(0.5, Math.min(2.0, combinedScale));
        
        return combinedScale;
    }
    
    public static int scaleSize(int baseSize) {
        double scaleFactor = calculateScaleFactor();
        int scaled = (int)(baseSize * scaleFactor);

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
        if (mc.getWindow() == null) return REFERENCE_WIDTH;
        int windowWidth = mc.getWindow().getWidth();
        double guiScale = mc.getWindow().getGuiScale();
        return (int)(windowWidth / guiScale);
    }
    
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

            calculatedSidebarWidth = (int)(guiScaledWidth * SIDEBAR_WIDTH_PERCENT);
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to set screen dimensions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void init() {
        recalculateDimensions();
        
        super.init();

        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);

        int scaledSpacing = getScaledSpacing();
        int sidebarX = scaledSpacing;
        int sidebarY = scaleSize(30);
        int buttonWidth = (int)(calculatedSidebarWidth * 0.90);
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing();

        pillarItemsButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.items"),
            (button) -> setActiveTab(new PillarItemsConfigTab(this))
        );
        addRenderableWidget(pillarItemsButton);

        sidebarY += buttonHeight + spacing;
        pillarParticlesButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.particles"),
            (button) -> setActiveTab(new PillarParticlesConfigTab(this))
        );
        addRenderableWidget(pillarParticlesButton);

        sidebarY += buttonHeight + spacing;
        pillarIdsButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.ids"),
            (button) -> setActiveTab(new PillarIdsConfigTab(this))
        );
        addRenderableWidget(pillarIdsButton);

        sidebarY += buttonHeight + spacing;
        supportersButton = new ConfigCategoryButton(
            sidebarX, sidebarY,
            buttonWidth, buttonHeight,
            new TranslatableComponent("buildscape.config.category.supporters"),
            (button) -> setActiveTab(new com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersOnlyTab(this))
        );
        addRenderableWidget(supportersButton);

        int kofiY = height - scaleSize(30);
        kofiButton = new ScaledTextButton(
            sidebarX, kofiY,
            buttonWidth, getScaledButtonHeight(),
            new TranslatableComponent("buildscape.config.kofi"),
            (button) -> openKofiLink()
        );
        addRenderableWidget(kofiButton);

        if (activeTab == null) {
            setActiveTab(new PillarItemsConfigTab(this));
        }
    }
    
    @Override
    public void resize(Minecraft mc, int width, int height) {
        lastWindowWidth = -1;
        lastWindowHeight = -1;

        recalculateDimensions();

        calculatedSidebarWidth = (int)(this.width * SIDEBAR_WIDTH_PERCENT);
        int scaledSpacing = getScaledSpacing();
        int sidebarX = scaledSpacing;
        int sidebarY = scaleSize(30);
        int buttonWidth = (int)(calculatedSidebarWidth * 0.90);
        int buttonHeight = getScaledCategoryButtonHeight();
        int spacing = getScaledCategoryButtonSpacing();

        if (pillarItemsButton != null) {
            pillarItemsButton.x = sidebarX;
            pillarItemsButton.y = sidebarY;
            pillarItemsButton.setWidth(buttonWidth);
            try {
                java.lang.reflect.Field heightField = net.minecraft.client.gui.components.AbstractWidget.class.getDeclaredField("height");
                heightField.setAccessible(true);
                heightField.setInt(pillarItemsButton, buttonHeight);
            } catch (Exception e) {
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
            }
        }

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

        super.resize(mc, this.width, this.height);
    }
    
    public void setActiveTab(AbstractConfigTab tab) {
        if (activeTab != null) {
            activeTab.onClose();
        }

        if (pillarItemsButton != null) pillarItemsButton.setActive(false);
        if (pillarParticlesButton != null) pillarParticlesButton.setActive(false);
        if (pillarIdsButton != null) pillarIdsButton.setActive(false);
        if (supportersButton != null) supportersButton.setActive(false);
        
        activeTab = tab;
        if (activeTab != null) {
            activeTab.init();
        }

        updateButtonStates();
    }
    
    public AbstractConfigTab getActiveTab() {
        return activeTab;
    }
    
    private void updateButtonStates() {
        if (activeTab == null) {
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

        if (Minecraft.getInstance().player != null) {
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

        try {
            URI uri = new URI(kofiUrl);
            java.awt.Desktop.getDesktop().browse(uri);
        } catch (Exception e) {
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
        recalculateDimensions();

        this.renderBackground(poseStack);

        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);

        fill(poseStack, 0, 0, calculatedSidebarWidth, height, 0xC0101010);

        int titleY = scaleSize(10);
        int titlePadding = scaleSize(10);
        int maxTitleWidth = calculatedSidebarWidth - titlePadding * 2;
        int titleTextWidth = font.width(title);

        float titleScale = 0.85f;
        if (titleTextWidth > maxTitleWidth) {
            titleScale = Math.max(0.5f, (float)maxTitleWidth / titleTextWidth * 0.85f);
        }

        int titleX = titlePadding + (calculatedSidebarWidth - titlePadding * 2) / 2;
        
        poseStack.pushPose();
        poseStack.translate(titleX, titleY, 0);
        poseStack.scale(titleScale, titleScale, 1.0f);
        drawCenteredString(poseStack, font, title, 0, 0, 0xFFFFFF);
        poseStack.popPose();

        if (activeTab != null) {
            activeTab.render(poseStack, mouseX, mouseY, partialTick);
        }

        super.render(poseStack, mouseX, mouseY, partialTick);
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
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        return calculatedSidebarWidth;
    }
    
    public int getContentY() {
        return scaleSize(30);
    }
    
    public int getContentWidth() {
        return (int)(width * LEFT_CONTENT_WIDTH_PERCENT);
    }
    
    public int getSidebarWidth() {
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        return calculatedSidebarWidth;
    }
    
    public int getRightPanelWidth() {
        return (int)(width * RIGHT_CONTENT_WIDTH_PERCENT);
    }
    
    public int getRightPanelX() {
        calculatedSidebarWidth = (int)(width * SIDEBAR_WIDTH_PERCENT);
        int leftContentWidth = (int)(width * LEFT_CONTENT_WIDTH_PERCENT);
        int gap = (int)(width * GAP_PERCENT);
        return calculatedSidebarWidth + leftContentWidth + gap;
    }
    
    public int getContentHeight() {
        return height - scaleSize(30);
    }
    
    public void addTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        try {
            java.lang.reflect.Method addMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod(
                "addRenderableWidget",
                net.minecraft.client.gui.components.events.GuiEventListener.class
            );
            addMethod.setAccessible(true);
            addMethod.invoke(this, widget);
        } catch (NoSuchMethodException e) {
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
    
    public void removeTabWidget(net.minecraft.client.gui.components.events.GuiEventListener widget) {
        try {
            java.lang.reflect.Method removeMethod = net.minecraft.client.gui.screens.Screen.class.getDeclaredMethod(
                "removeWidget",
                net.minecraft.client.gui.components.events.GuiEventListener.class
            );
            removeMethod.setAccessible(true);
            removeMethod.invoke(this, widget);
        } catch (NoSuchMethodException e) {
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

