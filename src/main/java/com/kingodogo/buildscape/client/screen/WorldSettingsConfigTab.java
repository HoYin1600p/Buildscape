package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.network.ModMessages;
import com.kingodogo.buildscape.network.UpdateGameRulePacket;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class WorldSettingsConfigTab extends AbstractConfigTab {
    private ScalableToggle creativeTreeBreakerToggle;
    private ScalableToggle fastLeafDecayToggle;
    private int leftBoxX, leftBoxY, leftBoxWidth, leftBoxHeight;
    private int rightBoxX, rightBoxY, rightBoxWidth, rightBoxHeight;
    private int lastContentWidth = -1;
    private int lastContentHeight = -1;

    public WorldSettingsConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    @Override
    public void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        // One-time widget creation
        boolean treeBreaker = mc.level.getGameRules().getBoolean(ModGameRules.CREATIVE_TREE_BREAKER);
        creativeTreeBreakerToggle = new ScalableToggle(0, 0, 100, 20,
                new TranslatableComponent("buildscape.config.world.tree_breaker"), treeBreaker, (btn) -> {
            ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("creativeTreeBreaker", ((ScalableToggle) btn).isToggled()));
        });
        addTabWidget(creativeTreeBreakerToggle);

        boolean leafDecay = mc.level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY);
        fastLeafDecayToggle = new ScalableToggle(0, 0, 100, 20,
                new TranslatableComponent("buildscape.config.world.leaf_decay"), leafDecay, (btn) -> {
            ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("fastLeafDecay", ((ScalableToggle) btn).isToggled()));
        });
        addTabWidget(fastLeafDecayToggle);

        relayout(contentX, contentY, contentWidth, contentHeight);

        lastContentWidth = contentWidth;
        lastContentHeight = contentHeight;
    }

    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int padding = BuildScapeConfigScreen.scaleSize(10);
        int middleGap = (int) (parent.height * 0.005);
        int topGap = parent.getContentY();
        int fullContentHeight = parent.height - topGap - (int) (parent.height * 0.005);

        // Define two panels side-by-side matching the middle and right panels of other tabs
        // Left Panel (Player settings)
        leftBoxX = parent.getContentX();
        leftBoxY = parent.getContentY();
        leftBoxWidth = parent.getContentWidth();
        leftBoxHeight = fullContentHeight;

        // Right Panel (Update settings)
        rightBoxX = parent.getRightPanelX();
        rightBoxY = parent.getContentY();
        rightBoxWidth = parent.getRightPanelWidth();
        rightBoxHeight = fullContentHeight;

        int buttonWidth = leftBoxWidth - padding * 2;
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        int titleHeight = BuildScapeConfigScreen.scaleSize(20);

        creativeTreeBreakerToggle.x = leftBoxX + padding;
        creativeTreeBreakerToggle.y = leftBoxY + padding + titleHeight + BuildScapeConfigScreen.scaleSize(5);
        creativeTreeBreakerToggle.setWidth(buttonWidth);

        fastLeafDecayToggle.x = rightBoxX + padding;
        fastLeafDecayToggle.y = rightBoxY + padding + titleHeight + BuildScapeConfigScreen.scaleSize(5);
        fastLeafDecayToggle.setWidth(rightBoxWidth - padding * 2);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        if (contentWidth != lastContentWidth || contentHeight != lastContentHeight) {
            relayout(parent.getContentX(), parent.getContentY(), contentWidth, contentHeight);
            lastContentWidth = contentWidth;
            lastContentHeight = contentHeight;
        }

        int borderColor = 0xFF666666;

        // Draw Left Box (Player Rules)
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY + leftBoxHeight - 1, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX, leftBoxY, leftBoxX + 1, leftBoxY + leftBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, leftBoxX + leftBoxWidth - 1, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);

        // Draw Right Box (World Rules)
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + 1, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY + rightBoxHeight - 1, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX, rightBoxY, rightBoxX + 1, rightBoxY + rightBoxHeight, borderColor);
        net.minecraft.client.gui.GuiComponent.fill(poseStack, rightBoxX + rightBoxWidth - 1, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);

        Minecraft mc = Minecraft.getInstance();
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.world.player_rules"), leftBoxX + 10, leftBoxY + 5, 0xFFFFFF);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.world.update_rules"), rightBoxX + 10, rightBoxY + 5, 0xFFFFFF);

        // Update button states from mc.level.getGameRules() to ensure they reflect sync packets
        if (mc.level != null) {
            creativeTreeBreakerToggle.toggled = mc.level.getGameRules().getBoolean(ModGameRules.CREATIVE_TREE_BREAKER);
            fastLeafDecayToggle.toggled = mc.level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY);
        }
    }

    // A simple inner class for a toggle button with BuildScape visuals
    private static class ScalableToggle extends net.minecraft.client.gui.components.Button {
        private final net.minecraft.network.chat.Component baseMessage;
        private boolean toggled;

        public ScalableToggle(int x, int y, int width, int height, net.minecraft.network.chat.Component message, boolean initialValue, OnPress onPress) {
            super(x, y, width, height, TextComponent.EMPTY, onPress);
            this.baseMessage = message;
            this.toggled = initialValue;
            updateMessage();
        }

        public boolean isToggled() {
            return toggled;
        }

        private void updateMessage() {
            // Message is now handled manually in renderButton for cleaner look
            setMessage(TextComponent.EMPTY);
        }

        @Override
        public void onPress() {
            this.toggled = !this.toggled;
            updateMessage();
            super.onPress();
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            Minecraft mc = Minecraft.getInstance();
            int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFF666666;

            // Draw custom button background
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + width, y + height, 0x80000000); // 50% dark

            // Draw border
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + width, y + 1, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + 1, y + height, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

            // Draw toggle status bar
            int barHeight = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
            int barColor = toggled ? 0xFF55FF55 : 0xFFFF5555;
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x + 2, y + height - 2 - barHeight, x + width - 2, y + height - 2, barColor);

            // Draw text
            int textY = y + (height - mc.font.lineHeight) / 2;
            mc.font.draw(poseStack, baseMessage, x + BuildScapeConfigScreen.scaleSize(6), textY, 0xFFFFFF);

            String status = toggled ? "ON" : "OFF";
            int statusWidth = mc.font.width(status);
            mc.font.draw(poseStack, status, x + width - statusWidth - BuildScapeConfigScreen.scaleSize(6), textY, barColor);
        }
    }
}
