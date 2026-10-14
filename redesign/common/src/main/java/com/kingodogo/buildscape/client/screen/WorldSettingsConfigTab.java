package com.kingodogo.buildscape.client.screen;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.config.CosmeticsConfig;
import com.kingodogo.buildscape.network.PacketFactory;
import com.kingodogo.buildscape.network.UpdateGameRulePacket;
import com.kingodogo.buildscape.world.ModGameRules;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import com.kingodogo.buildscape.platform.Services;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import com.kingodogo.buildscape.util.CommonId;
import com.kingodogo.buildscape.util.ComponentHelper;
import net.minecraft.world.level.Level;

public class WorldSettingsConfigTab extends AbstractConfigTab {
    private ScalableToggle creativeTreeBreakerToggle;
    private ScalableToggle shulkerPreviewToggle;
    private ScalableToggle fastLeafDecayToggle;
    private ScalableToggle disableEndermanGriefingToggle;
    private ScalableToggle disableCreeperGriefingToggle;
    private ScalableToggle disableGhastGriefingToggle;
    private ScalableToggle cakeStackingToggle;
    private ScalableToggle waterBottleStackingToggle;
    private int leftBoxX, leftBoxY, leftBoxWidth, leftBoxHeight;
    private int rightBoxX, rightBoxY, rightBoxWidth, rightBoxHeight;
    private int lastContentWidth = -1;
    private int lastContentHeight = -1;

    public WorldSettingsConfigTab(BuildScapeConfigScreen parent) {
        super(parent);
    }

    private boolean getRuleBoolean(Level level, Object ruleKey, boolean fallback) {
        return Services.PLATFORM.getGameRuleBoolean(level, ruleKey, fallback);
    }

    @Override
    public void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        int contentX = parent.getContentX();
        int contentY = parent.getContentY();
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        boolean treeBreaker = CosmeticsConfig.get().getCreativeTreeBreaker(mc.player != null ? mc.player.getUUID() : null);
        creativeTreeBreakerToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.tree_breaker"), treeBreaker, (toggle) -> {
            CosmeticsConfig.get().setCreativeTreeBreaker(
                    mc.player != null ? mc.player.getUUID() : null,
                    toggle.isToggled()
            );
        });
        addTabWidget(creativeTreeBreakerToggle);

        boolean shulkerPreview = CosmeticsConfig.get().getShulkerPreview(mc.player != null ? mc.player.getUUID() : null);
        shulkerPreviewToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.shulker_preview"), shulkerPreview, (toggle) -> {
            CosmeticsConfig.get().setShulkerPreview(
                    mc.player != null ? mc.player.getUUID() : null,
                    toggle.isToggled()
            );
        });
        addTabWidget(shulkerPreviewToggle);

        boolean leafDecay = getRuleBoolean(mc.level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay);
        fastLeafDecayToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.leaf_decay"), leafDecay, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("fastLeafDecay", toggle.isToggled()));
        });
        fastLeafDecayToggle.active = parent.hasOpAccess();
        addTabWidget(fastLeafDecayToggle);

        boolean endermanGriefing = getRuleBoolean(mc.level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing);
        disableEndermanGriefingToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.disable_enderman_griefing"), endermanGriefing, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("disableEndermanGriefing", toggle.isToggled()));
        });
        disableEndermanGriefingToggle.active = parent.hasOpAccess();
        addTabWidget(disableEndermanGriefingToggle);

        boolean creeperGriefing = getRuleBoolean(mc.level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing);
        disableCreeperGriefingToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.disable_creeper_griefing"), creeperGriefing, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("disableCreeperGriefing", toggle.isToggled()));
        });
        disableCreeperGriefingToggle.active = parent.hasOpAccess();
        addTabWidget(disableCreeperGriefingToggle);

        boolean ghastGriefing = getRuleBoolean(mc.level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing);
        disableGhastGriefingToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.disable_ghast_griefing"), ghastGriefing, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("disableGhastGriefing", toggle.isToggled()));
        });
        disableGhastGriefingToggle.active = parent.hasOpAccess();
        addTabWidget(disableGhastGriefingToggle);

        boolean cakeStacking = getRuleBoolean(mc.level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking);
        cakeStackingToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.cake_stacking"), cakeStacking, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("isCakeStack", toggle.isToggled()));
        });
        cakeStackingToggle.active = parent.hasOpAccess();
        addTabWidget(cakeStackingToggle);

        boolean waterBottleStacking = getRuleBoolean(mc.level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking);
        waterBottleStackingToggle = new ScalableToggle(0, 0, 100, 20,
                ComponentHelper.translatable("buildscape.config.world.water_bottle_stacking"), waterBottleStacking, (toggle) -> {
            PacketFactory.sendToServer(new UpdateGameRulePacket("isWaterbottleStack", toggle.isToggled()));
        });
        waterBottleStackingToggle.active = parent.hasOpAccess();
        addTabWidget(waterBottleStackingToggle);

        relayout(contentX, contentY, contentWidth, contentHeight);

        lastContentWidth = contentWidth;
        lastContentHeight = contentHeight;
    }

    private void relayout(int contentX, int contentY, int contentWidth, int contentHeight) {
        int fullContentHeight = parent.getContentHeight();
        int padding = BuildScapeConfigScreen.scaleSize(10);

        leftBoxX = parent.getContentX();
        leftBoxY = parent.getContentY();
        leftBoxWidth = parent.getContentWidth();
        leftBoxHeight = fullContentHeight;

        rightBoxX = parent.getRightPanelX();
        rightBoxY = parent.getContentY();
        rightBoxWidth = parent.getRightPanelWidth();
        rightBoxHeight = fullContentHeight;

        int buttonWidth = leftBoxWidth - padding * 2;
        int buttonHeight = BuildScapeConfigScreen.getScaledButtonHeight();
        int titleHeight = BuildScapeConfigScreen.scaleSize(20);
        int spacing = BuildScapeConfigScreen.scaleSize(5);

        int leftY = leftBoxY + padding + titleHeight + BuildScapeConfigScreen.scaleSize(5);

        creativeTreeBreakerToggle.x = leftBoxX + padding;
        creativeTreeBreakerToggle.y = leftY;
        creativeTreeBreakerToggle.setWidth(buttonWidth);
        creativeTreeBreakerToggle.setHeight(buttonHeight);

        shulkerPreviewToggle.x = leftBoxX + padding;
        shulkerPreviewToggle.y = leftY + buttonHeight + spacing;
        shulkerPreviewToggle.setWidth(buttonWidth);
        shulkerPreviewToggle.setHeight(buttonHeight);

        int rightY = rightBoxY + padding + titleHeight + BuildScapeConfigScreen.scaleSize(5);

        fastLeafDecayToggle.x = rightBoxX + padding;
        fastLeafDecayToggle.y = rightY;
        fastLeafDecayToggle.setWidth(rightBoxWidth - padding * 2);
        fastLeafDecayToggle.setHeight(buttonHeight);

        disableEndermanGriefingToggle.x = rightBoxX + padding;
        disableEndermanGriefingToggle.y = rightY + buttonHeight + spacing;
        disableEndermanGriefingToggle.setWidth(rightBoxWidth - padding * 2);
        disableEndermanGriefingToggle.setHeight(buttonHeight);

        disableCreeperGriefingToggle.x = rightBoxX + padding;
        disableCreeperGriefingToggle.y = rightY + (buttonHeight + spacing) * 2;
        disableCreeperGriefingToggle.setWidth(rightBoxWidth - padding * 2);
        disableCreeperGriefingToggle.setHeight(buttonHeight);

        disableGhastGriefingToggle.x = rightBoxX + padding;
        disableGhastGriefingToggle.y = rightY + (buttonHeight + spacing) * 3;
        disableGhastGriefingToggle.setWidth(rightBoxWidth - padding * 2);
        disableGhastGriefingToggle.setHeight(buttonHeight);

        cakeStackingToggle.x = rightBoxX + padding;
        cakeStackingToggle.y = rightY + (buttonHeight + spacing) * 4;
        cakeStackingToggle.setWidth(rightBoxWidth - padding * 2);
        cakeStackingToggle.setHeight(buttonHeight);

        waterBottleStackingToggle.x = rightBoxX + padding;
        waterBottleStackingToggle.y = rightY + (buttonHeight + spacing) * 5;
        waterBottleStackingToggle.setWidth(rightBoxWidth - padding * 2);
        waterBottleStackingToggle.setHeight(buttonHeight);
    }

    @Override
    public void render(Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
        int contentWidth = parent.getContentWidth();
        int contentHeight = parent.getContentHeight();

        if (contentWidth != lastContentWidth || contentHeight != lastContentHeight) {
            relayout(parent.getContentX(), parent.getContentY(), contentWidth, contentHeight);
            lastContentWidth = contentWidth;
            lastContentHeight = contentHeight;
        }

        int borderColor = 0xFF666666;

        Services.PLATFORM.fill(poseStackOrGraphics, leftBoxX, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, leftBoxX, leftBoxY + leftBoxHeight - 1, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, leftBoxX, leftBoxY, leftBoxX + 1, leftBoxY + leftBoxHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, leftBoxX + leftBoxWidth - 1, leftBoxY, leftBoxX + leftBoxWidth, leftBoxY + leftBoxHeight, borderColor);

        Services.PLATFORM.fill(poseStackOrGraphics, rightBoxX, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + 1, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, rightBoxX, rightBoxY + rightBoxHeight - 1, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, rightBoxX, rightBoxY, rightBoxX + 1, rightBoxY + rightBoxHeight, borderColor);
        Services.PLATFORM.fill(poseStackOrGraphics, rightBoxX + rightBoxWidth - 1, rightBoxY, rightBoxX + rightBoxWidth, rightBoxY + rightBoxHeight, borderColor);

        Minecraft mc = Minecraft.getInstance();
        float textScale = BuildScapeConfigScreen.getStandardTextScale();
        int titleYOffset = 2 + BuildScapeConfigScreen.getScaledButtonHeight() / 2 - (int)(mc.font.lineHeight * textScale) / 2 + 1;

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, leftBoxX + 2, leftBoxY + titleYOffset, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, ComponentHelper.translatable("buildscape.config.world.player_rules").getString(), 0, 0, 0xFFFFFF);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);

        Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
        Services.PLATFORM.translateGuiPose(poseStackOrGraphics, rightBoxX + 2, rightBoxY + titleYOffset, 0);
        Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
        Services.PLATFORM.draw(poseStackOrGraphics, mc.font, ComponentHelper.translatable("buildscape.config.world.update_rules").getString(), 0, 0, 0xFFFFFF);
        Services.PLATFORM.popGuiPose(poseStackOrGraphics);

        if (mc.level != null && mc.player != null) {
            creativeTreeBreakerToggle.toggled = CosmeticsConfig.get().getCreativeTreeBreaker(mc.player.getUUID());
            shulkerPreviewToggle.toggled = CosmeticsConfig.get().getShulkerPreview(mc.player.getUUID());
            fastLeafDecayToggle.toggled = getRuleBoolean(mc.level, ModGameRules.FAST_LEAF_DECAY, ModGameRules.clientFastLeafDecay);
            disableEndermanGriefingToggle.toggled = getRuleBoolean(mc.level, ModGameRules.DISABLE_ENDERMAN_GRIEFING, ModGameRules.clientDisableEndermanGriefing);
            disableCreeperGriefingToggle.toggled = getRuleBoolean(mc.level, ModGameRules.DISABLE_CREEPER_GRIEFING, ModGameRules.clientDisableCreeperGriefing);
            disableGhastGriefingToggle.toggled = getRuleBoolean(mc.level, ModGameRules.DISABLE_GHAST_GRIEFING, ModGameRules.clientDisableGhastGriefing);
            cakeStackingToggle.toggled = getRuleBoolean(mc.level, ModGameRules.IS_CAKE_STACK, ModGameRules.clientCakeStacking);
            waterBottleStackingToggle.toggled = getRuleBoolean(mc.level, ModGameRules.IS_WATER_BOTTLE_STACK, ModGameRules.clientWaterBottleStacking);
        }
    }

    private void addTabWidget(ScalableToggle toggle) {
        addTabWidget(toggle.getButton());
    }

    @FunctionalInterface
    public interface OnToggle {
        void onToggle(ScalableToggle toggle);
    }

    private static class ScalableToggle {
        public int x;
        public int y;
        public int width;
        public int height;
        public boolean active = true;
        private final Component baseMessage;
        public boolean toggled;
        private final Button button;

        public ScalableToggle(int x, int y, int width, int height, Component message, boolean initialValue, OnToggle onToggle) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.baseMessage = message;
            this.toggled = initialValue;
            this.button = Services.PLATFORM.createCustomButton(x, y, width, height, ComponentHelper.empty(), (btn) -> {
                this.toggled = !this.toggled;
                if (onToggle != null) {
                    onToggle.onToggle(this);
                }
            }, this::render);
        }

        public Button getButton() {
            syncPosition();
            return button;
        }

        public void syncPosition() {
            com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper.setPosition(this.button, this.x, this.y);
            this.button.setWidth(this.width);
            com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper.setWidgetHeight(this.button, this.height);
            this.button.active = this.active;
        }

        public boolean isToggled() {
            return toggled;
        }

        public void setHeight(int h) {
            this.height = h;
            com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper.setWidgetHeight(this.button, h);
        }

        public void setWidth(int w) {
            this.width = w;
            this.button.setWidth(w);
        }

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
            com.kingodogo.buildscape.client.screen.widget.WidgetLayoutHelper.setPosition(this.button, x, y);
        }

        public void render(Button btn, Object poseStackOrGraphics, int mouseX, int mouseY, float partialTick) {
            syncPosition();
            Minecraft mc = Minecraft.getInstance();
            int curX = this.x;
            int curY = this.y;
            int curWidth = this.width;
            int curHeight = this.height;
            boolean hovered = (this.active && btn.isHoveredOrFocused());
            int borderColor = hovered ? 0xFFFFFFFF : 0xFF666666;

            Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + curHeight, 0x80000000);

            Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + curWidth, curY + 1, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, curX, curY + curHeight - 1, curX + curWidth, curY + curHeight, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, curX, curY, curX + 1, curY + curHeight, borderColor);
            Services.PLATFORM.fill(poseStackOrGraphics, curX + curWidth - 1, curY, curX + curWidth, curY + curHeight, borderColor);

            int barHeight = Math.max(2, BuildScapeConfigScreen.scaleSize(2));
            int barColor = toggled ? 0xFF55FF55 : 0xFFFF5555;
            if (!this.active) barColor = 0xFF555555;
            Services.PLATFORM.fill(poseStackOrGraphics, curX + 2, curY + curHeight - 2 - barHeight, curX + curWidth - 2, curY + curHeight - 2, barColor);

            float textScale = BuildScapeConfigScreen.getStandardTextScale();
            int textY = curY + (curHeight - (int)(mc.font.lineHeight * textScale)) / 2;

            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, curX + BuildScapeConfigScreen.scaleSize(6), textY, 0);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, baseMessage.getString(), 0, 0, this.active ? 0xFFFFFF : 0x888888);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);

            String status = toggled ? "ON" : "OFF";
            int statusWidth = (int)(mc.font.width(status) * textScale);

            Services.PLATFORM.pushGuiPose(poseStackOrGraphics);
            Services.PLATFORM.translateGuiPose(poseStackOrGraphics, curX + curWidth - statusWidth - BuildScapeConfigScreen.scaleSize(6), textY, 0);
            Services.PLATFORM.scaleGuiPose(poseStackOrGraphics, textScale, textScale);
            Services.PLATFORM.draw(poseStackOrGraphics, mc.font, status, 0, 0, this.active ? barColor : 0x888888);
            Services.PLATFORM.popGuiPose(poseStackOrGraphics);

            if (!this.active) {
                int lockSize = Math.max(8, BuildScapeConfigScreen.scaleSize(10));
                int lockX = curX + curWidth - statusWidth - BuildScapeConfigScreen.scaleSize(22);
                Services.PLATFORM.blit(poseStackOrGraphics, CommonId.of(BuildScape.MODID, "textures/gui/lock.png"), lockX, curY + (curHeight - lockSize) / 2, 0, 0, lockSize, lockSize, 16, 16);
            }
        }
    }
}
