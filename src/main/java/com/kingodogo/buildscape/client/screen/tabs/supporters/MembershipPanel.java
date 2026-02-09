package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.SupportersApiCache;
import com.kingodogo.buildscape.api.SupportersApiClient;
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
import java.util.concurrent.CompletableFuture;

/**
 * Membership Panel (Panel 3)
 * 
 * Displays membership tiers from API.
 * "Connect Account" button → opens browser to https://buildscape.online/connect
 * Shows connected username when authenticated.
 * Filters Panel 1 based on tier.
 * 
 * Dimensions: 33% width × 100% height
 * Position: (67%, 0%)
 */
public class MembershipPanel extends BasePanel {
    private static final int PADDING = 10;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 5;
    private static final int TIER_SPACING = 30;
    
    private final SupportersApiClient apiClient = SupportersApiClient.getInstance();
    private final SupportersApiCache apiCache = SupportersApiCache.getInstance();
    private final SupportersTabState state = SupportersTabState.getInstance();
    
    private Button connectButton;
    private List<MembershipTier> tiers;
    private SupporterStatus currentStatus;
    private String statusMessage = "";
    private boolean isLoading = false;
    
    @Override
    public void init() {
        // Create connect button
        int buttonX = startX + PADDING;
        int buttonY = startY + PADDING;
        int buttonWidth = width - PADDING * 2;
        
        // Use fallback text if translation key doesn't exist
        String connectText = "Connect Account";
        try {
            connectText = new TranslatableComponent("buildscape.supporters.connect").getString();
            if (connectText.startsWith("buildscape.")) {
                connectText = "Connect Account"; // Fallback if translation missing
            }
        } catch (Exception e) {
            connectText = "Connect Account";
        }
        
        connectButton = new Button(
            buttonX, buttonY, buttonWidth, BUTTON_HEIGHT,
            new TextComponent(connectText),
            (button) -> openConnectPage()
        );
        
        // Load tiers
        loadTiers();
        
        // Load status if player UUID is available
        UUID playerUuid = getPlayerUuid();
        if (playerUuid != null) {
            loadStatus(playerUuid);
        } else {
            statusMessage = new TranslatableComponent("buildscape.supporters.no_player").getString();
        }
    }
    
    /**
     * Get the current player's UUID.
     */
    private UUID getPlayerUuid() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            return mc.player.getUUID();
        }
        return null;
    }
    
    /**
     * Load membership tiers from API.
     */
    private void loadTiers() {
        // Check cache first
        TiersResponse cached = apiCache.getCachedTiers();
        if (cached != null && cached.getTiers() != null) {
            this.tiers = cached.getTiers();
            return;
        }
        
        // Load from API
        apiClient.getTiers()
            .thenAccept(response -> {
                if (response != null && response.getTiers() != null) {
                    this.tiers = response.getTiers();
                    apiCache.cacheTiers(response);
                }
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("Failed to load tiers: " + throwable.getMessage());
                return null;
            });
    }
    
    /**
     * Load supporter status from API.
     */
    private void loadStatus(UUID uuid) {
        isLoading = true;
        statusMessage = "Loading...";
        
        // Check cache first
        SupporterStatus cached = apiCache.getCachedStatus(uuid);
        if (cached != null) {
            this.currentStatus = cached;
            updateStatusDisplay();
            isLoading = false;
            return;
        }
        
        // Load from API
        apiClient.getSupporterStatus(uuid)
            .thenAccept(status -> {
                if (status != null) {
                    this.currentStatus = status;
                    apiCache.cacheStatus(uuid, status);
                    updateStatusDisplay();
                } else {
                    statusMessage = "Unable to load status. Check your internet connection.";
                }
                isLoading = false;
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("Failed to load status: " + throwable.getMessage());
                statusMessage = "Unable to connect to server. Check your internet connection.";
                isLoading = false;
                return null;
            });
    }
    
    /**
     * Update status display based on current status.
     */
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
    
    /**
     * Open connect page in browser.
     */
    private void openConnectPage() {
        UUID playerUuid = getPlayerUuid();
        if (playerUuid == null) {
            statusMessage = "Player not found. Please join a world first.";
            return;
        }
        
        try {
            String url = "https://buildscape.online/connect?uuid=" + playerUuid;
            java.awt.Desktop.getDesktop().browse(new URI(url));
            statusMessage = "Opening browser... Please complete the connection on the website.";
            
            // Refresh status after a delay (user might connect)
            new Thread(() -> {
                try {
                    Thread.sleep(5000); // Wait 5 seconds
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
        // Render background
        GuiComponent.fill(poseStack, startX, startY, endX, endY, 0x80000000);
        
        Minecraft mc = Minecraft.getInstance();
        int y = startY + PADDING;
        
        // Render title
        String title = "Membership";
        try {
            String translated = new TranslatableComponent("buildscape.supporters.membership").getString();
            if (!translated.startsWith("buildscape.")) {
                title = translated;
            }
        } catch (Exception e) {
            // Use default
        }
        mc.font.draw(poseStack, title, startX + PADDING, y, 0xFFFFFF);
        y += 20;
        
        // Render connect button
        if (connectButton != null) {
            connectButton.render(poseStack, mouseX, mouseY, partialTick);
            y += BUTTON_HEIGHT + BUTTON_SPACING;
        }
        
        // Render status message
        if (!statusMessage.isEmpty()) {
            // Fix translation keys
            String displayMessage = statusMessage;
            if (statusMessage.startsWith("buildscape.supporters.")) {
                // Try to translate, otherwise use fallback
                try {
                    String translated = new TranslatableComponent(statusMessage).getString();
                    if (!translated.startsWith("buildscape.")) {
                        displayMessage = translated;
                    } else {
                        // Use fallback messages
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
                    // Use fallback
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
        
        // Render tiers
        if (tiers != null && !tiers.isEmpty()) {
            y += 10; // Spacing
            
            String tiersTitle = "Membership Tiers";
            try {
                String translated = new TranslatableComponent("buildscape.supporters.tiers").getString();
                if (!translated.startsWith("buildscape.")) {
                    tiersTitle = translated;
                }
            } catch (Exception e) {
                // Use default
            }
            mc.font.draw(poseStack, tiersTitle, startX + PADDING, y, 0xFFFFFF);
            y += 20;
            
            for (MembershipTier tier : tiers) {
                // Render tier name
                String tierText = tier.getName() + " (Level " + tier.getLevel() + ")";
                mc.font.draw(poseStack, tierText, startX + PADDING, y, 0xCCCCCC);
                y += 12;
                
                // Render tier description if available
                if (tier.getDescription() != null && !tier.getDescription().isEmpty()) {
                    // Word wrap description
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
                // Use default
            }
            mc.font.draw(poseStack, noTiersMsg, startX + PADDING, y, 0xAAAAAA);
        }
    }
    
    @Override
    protected boolean handleMouseClicked(double mouseX, double mouseY, int button) {
        return connectButton != null && connectButton.mouseClicked(mouseX, mouseY, button);
    }
}

