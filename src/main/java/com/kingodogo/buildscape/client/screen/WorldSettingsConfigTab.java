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
        boolean treeBreaker = com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(mc.player.getUUID());
        creativeTreeBreakerToggle = new ScalableToggle(0, 0, 100, 20,
                new TranslatableComponent("buildscape.config.world.tree_breaker"), treeBreaker, (btn) -> {
            com.kingodogo.buildscape.config.CosmeticsConfig.get().setCreativeTreeBreaker(mc.player.getUUID(), ((ScalableToggle) btn).isToggled());
        });
        addTabWidget(creativeTreeBreakerToggle);

        boolean leafDecay = mc.level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY);
        fastLeafDecayToggle = new ScalableToggle(0, 0, 100, 20,
                new TranslatableComponent("buildscape.config.world.leaf_decay"), leafDecay, (btn) -> {
            ModMessages.INSTANCE.sendToServer(new UpdateGameRulePacket("fastLeafDecay", ((ScalableToggle) btn).isToggled()));
        });
        fastLeafDecayToggle.active = parent.hasOpAccess();
        addTabWidget(fastLeafDecayToggle);

        relayout(contentX, contentY, contentWidth, contentHeight);

        lastContentWidth = contentWidth;
        lastContentHeight = contentHeight;
    }

    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int screenWidth = parent.width;
        int screenHeight = parent.height;

        int fullContentHeight = parent.getContentHeight();
        int padding = BuildScapeConfigScreen.scaleSize(10);

        // leftPanel
        leftBoxX = parent.getContentX();
        leftBoxY = parent.getContentY();
        leftBoxWidth = parent.getContentWidth();
        leftBoxHeight = fullContentHeight;

        // rightPanel
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
        creativeTreeBreakerToggle.setHeight(buttonHeight);

        fastLeafDecayToggle.x = rightBoxX + padding;
        fastLeafDecayToggle.y = rightBoxY + padding + titleHeight + BuildScapeConfigScreen.scaleSize(5);
        fastLeafDecayToggle.setWidth(rightBoxWidth - padding * 2);
        fastLeafDecayToggle.setHeight(buttonHeight);

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
        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int titleYOffset = 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - (int)(mc.font.lineHeight * textScale) / 2 + 1;

        poseStack.pushPose();
        poseStack.translate(leftBoxX + 2, leftBoxY + titleYOffset, 0);
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.world.player_rules"), 0, 0, 0xFFFFFF);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(rightBoxX + 2, rightBoxY + titleYOffset, 0);
        poseStack.scale(textScale, textScale, 1.0f);
        mc.font.draw(poseStack, new TranslatableComponent("buildscape.config.world.update_rules"), 0, 0, 0xFFFFFF);
        poseStack.popPose();

        // Update button states from mc.level.getGameRules() to ensure they reflect sync packets
        if (mc.level != null) {
            creativeTreeBreakerToggle.toggled = com.kingodogo.buildscape.config.CosmeticsConfig.get().getCreativeTreeBreaker(mc.player.getUUID());
            fastLeafDecayToggle.toggled = mc.level.getGameRules().getBoolean(ModGameRules.FAST_LEAF_DECAY);
        }
    }

    private static class ScalableMenuButton extends net.minecraft.client.gui.components.Button {
        private final net.minecraft.network.chat.Component baseMessage;

        public ScalableMenuButton(int x, int y, int width, int height, net.minecraft.network.chat.Component message, OnPress onPress) {
            super(x, y, width, height, TextComponent.EMPTY, onPress);
            this.baseMessage = message;
        }

        public void setHeight(int h) {
            this.height = h;
        }

        @Override
        public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
            Minecraft mc = Minecraft.getInstance();
            int borderColor = (active && isHoveredOrFocused()) ? 0xFFFFFFFF : 0xFF666666;

            // Draw custom button background
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + width, y + height, 0x80000000); // 50% dark

            // Draw border
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + width, y + 1, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y + height - 1, x + width, y + height, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x, y, x + 1, y + height, borderColor);
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x + width - 1, y, x + width, y + height, borderColor);

            // Draw left aligned text matching toggle box pattern
            float textScale = BuildScapeConfigScreen.getStandardTextScale();
            int textX = x + BuildScapeConfigScreen.scaleSize(6);
            int textY = y + (height - (int)(mc.font.lineHeight * textScale)) / 2;
            
            poseStack.pushPose();
            poseStack.translate(textX, textY, 0);
            poseStack.scale(textScale, textScale, 1.0f);
            mc.font.draw(poseStack, baseMessage, 0, 0, active ? 0xFFFFFF : 0x888888);
            poseStack.popPose();
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

        public void setHeight(int h) {
            this.height = h;
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
            int borderColor = (active && isHoveredOrFocused()) ? 0xFFFFFFFF : 0xFF666666;

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
            if (!active) barColor = 0xFF555555;
            net.minecraft.client.gui.GuiComponent.fill(poseStack, x + 2, y + height - 2 - barHeight, x + width - 2, y + height - 2, barColor);

            // Draw text
            float textScale = BuildScapeConfigScreen.getStandardTextScale();
            int textY = y + (height - (int)(mc.font.lineHeight * textScale)) / 2;

            poseStack.pushPose();
            poseStack.translate(x + BuildScapeConfigScreen.scaleSize(6), textY, 0);
            poseStack.scale(textScale, textScale, 1.0f);
            mc.font.draw(poseStack, baseMessage, 0, 0, active ? 0xFFFFFF : 0x888888);
            poseStack.popPose();

            String status = toggled ? "ON" : "OFF";
            int statusWidth = (int)(mc.font.width(status) * textScale);

            poseStack.pushPose();
            poseStack.translate(x + width - statusWidth - BuildScapeConfigScreen.scaleSize(6), textY, 0);
            poseStack.scale(textScale, textScale, 1.0f);
            mc.font.draw(poseStack, status, 0, 0, active ? barColor : 0x888888);
            poseStack.popPose();
            
            // Render lock icon if inactive
            if (!active) {
                com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, new net.minecraft.resources.ResourceLocation(com.kingodogo.buildscape.BuildScape.MODID, "textures/gui/lock.png"));
                com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
                int lockSize = Math.max(8, BuildScapeConfigScreen.scaleSize(10));
                int lockX = x + width - statusWidth - BuildScapeConfigScreen.scaleSize(22);
                net.minecraft.client.gui.GuiComponent.blit(poseStack, lockX, y + (height - lockSize) / 2, lockSize, lockSize, 0, 0, 16, 16, 16, 16);
                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            }
        }
    }
}
