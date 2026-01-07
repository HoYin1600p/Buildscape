package com.kingodogo.buildscape.client.screen.tabs.supporters;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.SupportersApiCache;
import com.kingodogo.buildscape.api.SupportersApiClient;
import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.client.screen.AbstractConfigTab;
import com.kingodogo.buildscape.client.screen.BuildScapeConfigScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Supporters-Only configuration tab with custom panel-based layout.
 * 
 * This tab intentionally deviates from the standard Buildscape tab layout
 * while still respecting all core UI constraints.
 * 
 * Layout (from full screen width/height):
 * - Panel 0 (Navigation): 11% × 100% at (0%, 0%) - handled by parent
 * - Gap: 1% width
 * - Panel 1: 54% × 100% at (12%, 0%) - Available Cosmetics (includes preview feature)
 * - Gap: 1% width
 * - Panel 5: 24% × 100% at (67%, 0%) - Player Avatar/Model Showcase
 * 
 * Total width: 11% + 1% + 54% + 1% + 24% = 91% (9% for rounding/margin)
 * Total height: 100% (all panels fit within screen)
 * 
 * Panel 1 item layout:
 * - 4 items per row
 * - Each item: 12% of panel width
 * - Gap between items: 1.4% of panel width
 */
public class SupportersOnlyTab extends AbstractConfigTab {
    // UUIDs with full cosmetic access regardless of API response
    private static final Set<UUID> FULL_ACCESS_UUIDS = Set.of(
        UUID.fromString("3f97920a-de17-4e52-9770-a4183ddf2267"),
        UUID.fromString("7145ec43-712f-45ef-83aa-b259b8a8f184")
    );
    
    private CosmeticsDisplayPanel panel1;
    private PlayerAvatarPanel panel5;
    private final SupportersTabState state = SupportersTabState.getInstance();
    private final Minecraft mc = Minecraft.getInstance();
    
    public SupportersOnlyTab(BuildScapeConfigScreen parent) {
        super(parent);
    }
    
    @Override
    public void init() {
        // LOCKED TO FIXED PERCENTAGES: Calculate everything in GUI-scaled coordinates
        // This ensures panels align properly with the sidebar which is also in GUI-scaled coordinates
        // The panels will apply scale transformation in render() to render at scale 2.0
        
        // Get parent's GUI-scaled dimensions (matches sidebar coordinate system)
        int parentWidth = parent.width;  // GUI-scaled width
        int parentHeight = parent.height; // GUI-scaled height
        
        // Get sidebar width and content Y from parent (already in GUI-scaled coordinates)
        int sidebarWidth = parent.getSidebarWidth();
        int contentY = parent.getContentY();
        int contentHeight = parentHeight - contentY - 10;
        
        // Calculate panel sizes as FIXED PERCENTAGES of GUI-scaled screen width
        // Layout: 11% sidebar + 1% gap + 55% panel1 (Available Cosmetics) + 32% panel5 (Player Avatar)
        double gapPercent = 0.01;      // 1% gap
        double panel1Percent = 0.55;  // 55% Available Cosmetics panel
        double panel5Percent = 0.32;  // 32% Player Avatar panel (remaining space)
        
        // Calculate panel widths as percentages of GUI-scaled screen width
        int panel1Width = (int)(parentWidth * panel1Percent);
        int panel5Width = (int)(parentWidth * panel5Percent);
        int gapWidth = (int)(parentWidth * gapPercent);
        
        // Calculate panel positions in GUI-scaled coordinates (to match sidebar)
        // Panel 1: 55% width starting after sidebar + gap
        int panel1X = sidebarWidth + gapWidth;
        int panel1Y = contentY;
        int panel1Height = contentHeight;
        
        // Panel 5: 32% width starting after panel1 + gap
        int panel5X = panel1X + panel1Width + gapWidth;
        int panel5Y = contentY;
        int panel5Height = contentHeight;
        
        // Ensure panels fit within GUI-scaled screen bounds
        panel1X = Math.max(0, Math.min(panel1X, parentWidth - 10));
        panel1Y = Math.max(0, Math.min(panel1Y, parentHeight - 10));
        panel1Width = Math.max(1, Math.min(panel1Width, parentWidth - panel1X));
        panel1Height = Math.max(1, Math.min(panel1Height, parentHeight - panel1Y));
        
        panel5X = Math.max(0, Math.min(panel5X, parentWidth - 10));
        panel5Y = Math.max(0, Math.min(panel5Y, parentHeight - 10));
        panel5Width = Math.max(1, Math.min(panel5Width, parentWidth - panel5X));
        panel5Height = Math.max(1, Math.min(panel5Height, parentHeight - panel5Y));
        
        // Create and initialize panels
        panel1 = new CosmeticsDisplayPanel();
        panel1.setBounds(panel1X, panel1Y, panel1Width, panel1Height);
        panel1.init();
        
        panel5 = new PlayerAvatarPanel();
        panel5.setBounds(panel5X, panel5Y, panel5Width, panel5Height);
        panel5.init();
        
        // Load API data
        loadApiData();
    }
    
    /**
     * Load data from API and update panels.
     */
    private void loadApiData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            // Load default cosmetics for testing
            loadDefaultCosmetics();
            return;
        }
        
        UUID playerUuid = mc.player.getUUID();
        // Only set and reload if the UUID changed or if we have no equipped data yet
        if (state.getPlayerUuid() == null || !state.getPlayerUuid().equals(playerUuid) || state.getEquippedCosmetics().isEmpty()) {
            state.setPlayerUuid(playerUuid);
        }
        
        SupportersApiClient apiClient = SupportersApiClient.getInstance();
        SupportersApiCache apiCache = SupportersApiCache.getInstance();
        
        // Load cosmetics data
        CosmeticData cachedCosmetics = apiCache.getCachedCosmetics(playerUuid);
        if (cachedCosmetics != null) {
            updateCosmeticsData(cachedCosmetics);
        } else {
            // Load default cosmetics while API loads
            loadDefaultCosmetics();
        }
        
        apiClient.getCosmetics(playerUuid)
            .thenAccept(cosmetics -> {
                if (cosmetics != null) {
                    apiCache.cacheCosmetics(playerUuid, cosmetics);
                    updateCosmeticsData(cosmetics);
                } else {
                    // If API fails, keep default cosmetics
                    loadDefaultCosmetics();
                }
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("Failed to load cosmetics: " + throwable.getMessage());
                // Keep default cosmetics on error
                loadDefaultCosmetics();
                return null;
            });
    }
    
    /**
     * Load default cosmetic IDs for testing/display when API is unavailable.
     * Uses CosmeticManager to get all registered cosmetics.
     */
    private void loadDefaultCosmetics() {
        // Get all registered cosmetics from CosmeticManager
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager = 
            com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();
        Set<String> allRegisteredCosmetics = cosmeticManager.getAllCosmetics();
        
        // Convert to list for display
        List<String> defaultCosmetics = new ArrayList<>(allRegisteredCosmetics);
        
        // Get player info for dev/full-access checks
        String playerUsername = null;
        UUID currentPlayerUuid = null;
        if (mc.player != null) {
            playerUsername = mc.player.getName().getString();
            currentPlayerUuid = mc.player.getUUID();
        }
        
        // Check if player has full access (UUID) or is Dev - unlock everything
        Set<String> defaultUnlocked;
        if (hasFullAccess(currentPlayerUuid)) {
            defaultUnlocked = new java.util.HashSet<>(allRegisteredCosmetics);
            BuildScape.getLogger().info("Full-access UUID detected - unlocking all " + allRegisteredCosmetics.size() + " cosmetics");
        } else if (playerUsername != null && playerUsername.equalsIgnoreCase("Dev")) {
            // Dev gets access to everything
            defaultUnlocked = new java.util.HashSet<>(allRegisteredCosmetics);
            BuildScape.getLogger().info("Dev access granted - unlocking all " + allRegisteredCosmetics.size() + " cosmetics");
        } else {
            // For non-dev players, get unlocked cosmetics from manager
            // (API will override this when it loads)
            defaultUnlocked = cosmeticManager.getUnlockedCosmetics(playerUsername);
        }
        
        state.setUnlockedCosmetics(defaultUnlocked);
        
        // Set all cosmetics in display panel
        if (panel1 != null) {
            panel1.setAllCosmeticIds(defaultCosmetics);
        }
    }
    
    /**
     * Update panels with cosmetics data.
     */
    private void updateCosmeticsData(CosmeticData cosmetics) {
        // Get all registered cosmetics from CosmeticManager
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager = 
            com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();
        Set<String> allRegisteredCosmetics = cosmeticManager.getAllCosmetics();
        
        // Get player info for dev/full-access checks
        String playerUsername = null;
        UUID currentPlayerUuid = null;
        if (mc.player != null) {
            playerUsername = mc.player.getName().getString();
            currentPlayerUuid = mc.player.getUUID();
        }
        
        // Update shared state with unlocked cosmetics from API
        Set<String> unlocked = new java.util.HashSet<>(cosmetics.getUnlocked() != null ? cosmetics.getUnlocked() : new ArrayList<>());
        
        // If player has full access (UUID) or is Dev, grant access to everything
        if (hasFullAccess(currentPlayerUuid)) {
            unlocked.addAll(allRegisteredCosmetics);
            BuildScape.getLogger().debug("Full-access UUID detected - unlocking all cosmetics");
        } else if (playerUsername != null && playerUsername.equalsIgnoreCase("Dev")) {
            unlocked.addAll(allRegisteredCosmetics);
            BuildScape.getLogger().debug("Dev access - unlocking all cosmetics");
        }
        
        state.setUnlockedCosmetics(unlocked);
        
        // Combine unlocked and locked for display
        // Use all registered cosmetics as the base list
        List<String> allCosmetics = new ArrayList<>(allRegisteredCosmetics);
        
        // Add any API-provided cosmetics that aren't in the registered list
        if (cosmetics.getLocked() != null) {
            for (String lockedId : cosmetics.getLocked()) {
                if (!allCosmetics.contains(lockedId)) {
                    allCosmetics.add(lockedId);
                }
            }
        }
        
        // Update cosmetics display panel
        if (panel1 != null) {
            panel1.setAllCosmeticIds(allCosmetics);
        }
        
        // Update equipped cosmetics - only if the API provides something
        // This avoids overwriting local config with empty API data on join
        if (cosmetics.getEquipped() != null && !cosmetics.getEquipped().isEmpty()) {
            state.setEquippedCosmetics(new java.util.HashSet<>(cosmetics.getEquipped()));
        } else {
            // If API returns no equipped items, but we have local ones, 
            // re-trigger loading from local config just in case
            UUID storedUuid = state.getPlayerUuid();
            if (storedUuid != null) {
                state.setPlayerUuid(storedUuid);
            }
        }
    }
    
    /**
     * Check if a UUID should receive full cosmetic access.
     */
    private boolean hasFullAccess(UUID uuid) {
        return uuid != null && FULL_ACCESS_UUIDS.contains(uuid);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // Delegate all rendering to panels
        // Tab contains zero rendering logic itself
        if (panel1 != null) panel1.render(poseStack, mouseX, mouseY, partialTick);
        if (panel5 != null) panel5.render(poseStack, mouseX, mouseY, partialTick);
        
        // Render tooltips at tab level AFTER all panels to ensure they're on top
        // CRITICAL: Only render tooltip from ONE panel at a time to prevent double tooltips
        // Check which panel the mouse is actually over and only render that panel's tooltip
        com.mojang.blaze3d.systems.RenderSystem.disableScissor();
        
        // Check if mouse is over panel1 (Available Cosmetics) first
        boolean mouseOverPanel1 = false;
        if (panel1 != null) {
            // Use panel's actual bounds
            int panel1X = panel1.getStartX();
            int panel1Y = panel1.getStartY();
            int panel1Width = panel1.getWidth();
            int panel1Height = panel1.getHeight();
            
            // Check if mouse is within panel bounds
            if (mouseX >= panel1X && mouseX < panel1X + panel1Width && 
                mouseY >= panel1Y && mouseY < panel1Y + panel1Height) {
                mouseOverPanel1 = true;
                panel1.renderTooltips(poseStack, mouseX, mouseY);
            }
        }
        
        // Only render panel5 tooltips if mouse is NOT over panel1
        if (!mouseOverPanel1 && panel5 != null) {
            panel5.renderTooltips(poseStack, mouseX, mouseY);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Delegate to panels (they check bounds internally)
        if (panel1 != null && panel1.mouseClicked(mouseX, mouseY, button)) return true;
        if (panel5 != null && panel5.mouseClicked(mouseX, mouseY, button)) return true;
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        // Delegate to panels (they check bounds internally)
        if (panel1 != null && panel1.mouseScrolled(mouseX, mouseY, delta)) return true;
        if (panel5 != null && panel5.mouseScrolled(mouseX, mouseY, delta)) return true;
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // Delegate to panels (they check bounds internally)
        // No drag functionality for cosmetics anymore - single click equip/unequip
        if (panel5 != null && panel5.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Delegate to panels for mouse release (e.g., to stop dragging)
        if (panel5 != null && panel5 instanceof PlayerAvatarPanel) {
            ((PlayerAvatarPanel) panel5).mouseReleased(mouseX, mouseY, button);
        }
        
        // Also handle panel1's mouse release
        if (panel1 != null && panel1.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void onClose() {
        // Save equipped cosmetics before closing (they're already saved on equip, but ensure they're persisted)
        SupportersTabState state = SupportersTabState.getInstance();
        UUID playerUuid = state.getPlayerUuid();
        if (playerUuid != null) {
            com.kingodogo.buildscape.config.CosmeticsConfig.get().setEquippedCosmetics(
                playerUuid, 
                state.getEquippedCosmeticsBySlot()
            );
        }
        
        // Don't reset equipped cosmetics - they should persist
        // Only reset selection and other temporary state
        state.setSelectedCosmeticId(null);
        super.onClose();
    }
}