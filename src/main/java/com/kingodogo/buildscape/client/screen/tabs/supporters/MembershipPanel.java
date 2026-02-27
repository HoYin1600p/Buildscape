package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.SupportersApiCache;
import com.kingodogo.buildscape.api.model.MembershipTier;
import com.kingodogo.buildscape.api.model.SupporterStatus;
import com.kingodogo.buildscape.api.model.TiersResponse;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class MembershipPanel extends BasePanel {
    private static final int PADDING = 10;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int TIER_SPACING = 30;
    
    private final SupportersApiCache apiCache = SupportersApiCache.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    
    private Button connectButton;
    private List<MembershipTier> tiers;
    private SupporterStatus currentStatus;
    private String statusMessage = "";
    private boolean isLoading = false;
    
    @Override
    public void init() {
        int buttonX = startX + PADDING;
        int buttonY = startY + PADDING;
        int buttonWidth = width - PADDING * 2;

        String connectText = "Connect Account";
        try {
            connectText = new TranslatableComponent("buildscape.supporters.connect").getString();
            if (connectText.startsWith("buildscape.")) {
                connectText = "Connect Account";
            }
        } catch (Exception e) {
            connectText = "Connect Account";
        }
        
        connectButton = new Button(
            buttonX, buttonY, buttonWidth, BUTTON_HEIGHT,
            new TextComponent(connectText),
            (button) -> openConnectPage()
        );

        loadTiers();

        UUID playerUuid = getPlayerUuid();
        if (playerUuid != null) {
            loadStatus(playerUuid);
        } else {
            statusMessage = new TranslatableComponent("buildscape.supporters.no_player").getString();
        }
    }
    
    private UUID getPlayerUuid() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            return mc.player.getUUID();
        }
        return null;
    }
    
    private void loadTiers() {
        TiersResponse cached = apiCache.getCachedTiers();
        if (cached != null && cached.getTiers() != null) {
            this.tiers = cached.getTiers();
            return;
        }
        // No API call - only use cached data from game launch
    }
    
    private void loadStatus(UUID uuid) {
        SupporterStatus cached = apiCache.getCachedStatus(uuid);
        if (cached != null) {
            this.currentStatus = cached;
            updateStatusDisplay();
            return;
        }
        // No API call - only use cached data from game launch
        isLoading = false;
        updateStatusDisplay();
    }
    
    private void updateStatusDisplay() {
        if (currentStatus == null) {
            statusMessage = "Not connected. Click 'Connect Account' to link your account.";
            return;
        }
        
        if (currentStatus.isConnected()) {
            String username = currentStatus.getUsername();
            String tier = currentStatus.getTier();
            if (username != null && !username.isEmpty()) {
                statusMessage = "Connected as: " + username;
            } else {
                statusMessage = "Connected (username not available)";
            }
            if (tier != null && !tier.isEmpty()) {
                statusMessage += " - Tier: " + tier;
            }
        } else {
            statusMessage = "Not connected. Click 'Connect Account' to link your account.";
        }
    }
    
    private void openConnectPage() {
        UUID playerUuid = getPlayerUuid();
        if (playerUuid == null) {
            statusMessage = "Player not found. Please join a world first.";
            return;
        }
        
        try {
            String url = "https://buildscape.online/connect?uuid=" + playerUuid.toString();
            java.awt.Desktop.getDesktop().browse(new URI(url));
            statusMessage = "Opening browser... Please complete the connection on the website.";

            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    loadStatus(playerUuid);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } catch (Exception e) {
            BuildScape.getLogger().error("Failed to open browser: " + e.getMessage());
            statusMessage = "Failed to open browser. Please visit: https://buildscape.online/connect";
        }
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);
        
        Minecraft mc = Minecraft.getInstance();
        int y = startY + PADDING;

        String title = "Membership";
        try {
            String translated = new TranslatableComponent("buildscape.supporters.membership").getString();
            if (!translated.startsWith("buildscape.")) {
                title = translated;
            }
        } catch (Exception e) {
        }
        mc.font.draw(poseStack, title, startX + PADDING, y, 0xFFFFFF);
        y += 20;

        if (connectButton != null) {
            connectButton.render(poseStack, mouseX, mouseY, partialTick);
            y += BUTTON_HEIGHT + BUTTON_SPACING;
        }

        if (!statusMessage.isEmpty()) {
            String displayMessage = statusMessage;
            if (statusMessage.startsWith("buildscape.supporters.")) {
                try {
                    String translated = new TranslatableComponent(statusMessage).getString();
                    if (!translated.startsWith("buildscape.")) {
                        displayMessage = translated;
                    } else {
                        if (statusMessage.contains("error")) {
                            displayMessage = "Unable to connect to server. Check your internet connection.";
                        } else if (statusMessage.contains("no_tiers")) {
                            displayMessage = "No membership tiers available.";
                        } else if (statusMessage.contains("loading")) {
                            displayMessage = "Loading...";
                        } else if (statusMessage.contains("not_connected")) {
                            displayMessage = "Not connected. Click 'Connect Account' to link your account.";
                        } else if (statusMessage.contains("no_player")) {
                            displayMessage = "Player not found.";
                        } else {
                            displayMessage = statusMessage.replace("buildscape.supporters.", "");
                        }
                    }
                } catch (Exception e) {
                    if (statusMessage.contains("error")) {
                        displayMessage = "Unable to connect to server.";
                    } else if (statusMessage.contains("no_tiers")) {
                        displayMessage = "No tiers available.";
                    } else {
                        displayMessage = statusMessage.replace("buildscape.supporters.", "");
                    }
                }
            }
            mc.font.draw(poseStack, displayMessage, startX + PADDING, y, isLoading ? 0xFFFF00 : 0xCCCCCC);
            y += 20;
        }

        if (tiers != null && !tiers.isEmpty()) {
            y += 10;

            String tiersTitle = "Membership Tiers";
            try {
                String translated = new TranslatableComponent("buildscape.supporters.tiers").getString();
                if (!translated.startsWith("buildscape.")) {
                    tiersTitle = translated;
                }
            } catch (Exception e) {
            }
            mc.font.draw(poseStack, tiersTitle, startX + PADDING, y, 0xFFFFFF);
            y += 20;
            
            for (MembershipTier tier : tiers) {
                String tierText = tier.getName() + " (Level " + tier.getLevel() + ")";
                mc.font.draw(poseStack, tierText, startX + PADDING, y, 0xCCCCCC);
                y += 12;

                if (tier.getDescription() != null && !tier.getDescription().isEmpty()) {
                    List<net.minecraft.util.FormattedCharSequence> wrapped = mc.font.split(
                        new net.minecraft.network.chat.TextComponent(tier.getDescription()), 
                        width - PADDING * 2
                    );
                    for (net.minecraft.util.FormattedCharSequence line : wrapped) {
                        mc.font.draw(poseStack, line, startX + PADDING + 5, y, 0xAAAAAA);
                        y += 10;
                    }
                }
                y += TIER_SPACING;
            }
        } else if (!isLoading) {
            String noTiersMsg = "No membership tiers available.";
            try {
                String translated = new TranslatableComponent("buildscape.supporters.no_tiers").getString();
                if (!translated.startsWith("buildscape.")) {
                    noTiersMsg = translated;
                }
            } catch (Exception e) {
            }
            mc.font.draw(poseStack, noTiersMsg, startX + PADDING, y, 0xAAAAAA);
        }
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        if (connectButton != null && connectButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return false;
    }
}

