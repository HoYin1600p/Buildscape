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

public class SupportersOnlyTab extends AbstractConfigTab {
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

        int parentWidth = parent.width;
        int parentHeight = parent.height;

        int sidebarWidth = parent.getSidebarWidth();
        int contentY = parent.getContentY();
        int contentHeight = parentHeight - contentY - 10;

        double gapPercent = 0.01;
        double panel1Percent = 0.55;
        double panel5Percent = 0.32;

        int panel1Width = (int)(parentWidth * panel1Percent);
        int panel5Width = (int)(parentWidth * panel5Percent);
        int gapWidth = (int)(parentWidth * gapPercent);

        int panel1X = sidebarWidth + gapWidth;
        int panel1Y = contentY;
        int panel1Height = contentHeight;

        int panel5X = panel1X + panel1Width + gapWidth;
        int panel5Y = contentY;
        int panel5Height = contentHeight;

        panel1X = Math.max(0, Math.min(panel1X, parentWidth - 10));
        panel1Y = Math.max(0, Math.min(panel1Y, parentHeight - 10));
        panel1Width = Math.max(1, Math.min(panel1Width, parentWidth - panel1X));
        panel1Height = Math.max(1, Math.min(panel1Height, parentHeight - panel1Y));
        
        panel5X = Math.max(0, Math.min(panel5X, parentWidth - 10));
        panel5Y = Math.max(0, Math.min(panel5Y, parentHeight - 10));
        panel5Width = Math.max(1, Math.min(panel5Width, parentWidth - panel5X));
        panel5Height = Math.max(1, Math.min(panel5Height, parentHeight - panel5Y));

        panel1 = new CosmeticsDisplayPanel();
        panel1.setBounds(panel1X, panel1Y, panel1Width, panel1Height);
        panel1.init();
        
        panel5 = new PlayerAvatarPanel();
        panel5.setBounds(panel5X, panel5Y, panel5Width, panel5Height);
        panel5.init();

        loadApiData();
    }
    
    private void loadApiData() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            loadDefaultCosmetics();
            return;
        }
        
        UUID playerUuid = mc.player.getUUID();
        if (state.getPlayerUuid() == null || !state.getPlayerUuid().equals(playerUuid) || state.getEquippedCosmetics().isEmpty()) {
            state.setPlayerUuid(playerUuid);
        }
        
        SupportersApiClient apiClient = SupportersApiClient.getInstance();
        SupportersApiCache apiCache = SupportersApiCache.getInstance();

        CosmeticData cachedCosmetics = apiCache.getCachedCosmetics(playerUuid);
        if (cachedCosmetics != null) {
            updateCosmeticsData(cachedCosmetics);
        } else {
            loadDefaultCosmetics();
        }
        
        apiClient.getCosmetics(playerUuid)
            .thenAccept(cosmetics -> {
                if (cosmetics != null) {
                    apiCache.cacheCosmetics(playerUuid, cosmetics);
                    updateCosmeticsData(cosmetics);
                } else {
                    loadDefaultCosmetics();
                }
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("Failed to load cosmetics: " + throwable.getMessage());
                loadDefaultCosmetics();
                return null;
            });
    }
    
    private void loadDefaultCosmetics() {
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager =
            com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();
        Set<String> allRegisteredCosmetics = cosmeticManager.getAllCosmetics();

        List<String> defaultCosmetics = new ArrayList<>(allRegisteredCosmetics);

        String playerUsername = null;
        UUID currentPlayerUuid = null;
        if (mc.player != null) {
            playerUsername = mc.player.getName().getString();
            currentPlayerUuid = mc.player.getUUID();
        }

        Set<String> defaultUnlocked;
        if (hasFullAccess(currentPlayerUuid)) {
            defaultUnlocked = new java.util.HashSet<>(allRegisteredCosmetics);
            BuildScape.getLogger().info("Full-access UUID detected - unlocking all " + allRegisteredCosmetics.size() + " cosmetics");
        } else if (playerUsername != null && playerUsername.equalsIgnoreCase("Dev")) {
            defaultUnlocked = new java.util.HashSet<>(allRegisteredCosmetics);
            BuildScape.getLogger().info("Dev access granted - unlocking all " + allRegisteredCosmetics.size() + " cosmetics");
        } else {
            defaultUnlocked = cosmeticManager.getUnlockedCosmetics(playerUsername);
        }
        
        state.setUnlockedCosmetics(defaultUnlocked);

        if (panel1 != null) {
            panel1.setAllCosmeticIds(defaultCosmetics);
        }
    }
    
    private void updateCosmeticsData(CosmeticData cosmetics) {
        com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager =
            com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();
        Set<String> allRegisteredCosmetics = cosmeticManager.getAllCosmetics();

        String playerUsername = null;
        UUID currentPlayerUuid = null;
        if (mc.player != null) {
            playerUsername = mc.player.getName().getString();
            currentPlayerUuid = mc.player.getUUID();
        }

        Set<String> unlocked = new java.util.HashSet<>(cosmetics.getUnlocked() != null ? cosmetics.getUnlocked() : new ArrayList<>());

        if (hasFullAccess(currentPlayerUuid)) {
            unlocked.addAll(allRegisteredCosmetics);
            BuildScape.getLogger().debug("Full-access UUID detected - unlocking all cosmetics");
        } else if (playerUsername != null && playerUsername.equalsIgnoreCase("Dev")) {
            unlocked.addAll(allRegisteredCosmetics);
            BuildScape.getLogger().debug("Dev access - unlocking all cosmetics");
        }
        
        state.setUnlockedCosmetics(unlocked);

        List<String> allCosmetics = new ArrayList<>(allRegisteredCosmetics);

        if (cosmetics.getLocked() != null) {
            for (String lockedId : cosmetics.getLocked()) {
                if (!allCosmetics.contains(lockedId)) {
                    allCosmetics.add(lockedId);
                }
            }
        }

        if (panel1 != null) {
            panel1.setAllCosmeticIds(allCosmetics);
        }

        if (cosmetics.getEquipped() != null && !cosmetics.getEquipped().isEmpty()) {
            state.setEquippedCosmetics(new java.util.HashSet<>(cosmetics.getEquipped()));
        } else {
            UUID storedUuid = state.getPlayerUuid();
            if (storedUuid != null) {
                state.setPlayerUuid(storedUuid);
            }
        }
    }
    
    private boolean hasFullAccess(UUID uuid) {
        return uuid != null && FULL_ACCESS_UUIDS.contains(uuid);
    }
    
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (panel1 != null) panel1.render(poseStack, mouseX, mouseY, partialTick);
        if (panel5 != null) panel5.render(poseStack, mouseX, mouseY, partialTick);

        com.mojang.blaze3d.systems.RenderSystem.disableScissor();

        boolean mouseOverPanel1 = false;
        if (panel1 != null) {
            int panel1X = panel1.getStartX();
            int panel1Y = panel1.getStartY();
            int panel1Width = panel1.getWidth();
            int panel1Height = panel1.getHeight();

            if (mouseX >= panel1X && mouseX < panel1X + panel1Width &&
                mouseY >= panel1Y && mouseY < panel1Y + panel1Height) {
                mouseOverPanel1 = true;
                panel1.renderTooltips(poseStack, mouseX, mouseY);
            }
        }

        if (!mouseOverPanel1 && panel5 != null) {
            panel5.renderTooltips(poseStack, mouseX, mouseY);
        }

        if (panel1 != null) {
            panel1.renderColorPickerOverlay(poseStack, mouseX, mouseY, partialTick);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (panel1 != null && panel1.mouseClicked(mouseX, mouseY, button)) return true;
        return panel5 != null && panel5.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (panel1 != null && panel1.mouseScrolled(mouseX, mouseY, delta)) return true;
        return panel5 != null && panel5.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panel1 != null && panel1.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;

        return panel5 != null && panel5.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (panel5 != null && panel5 instanceof PlayerAvatarPanel) {
            panel5.mouseReleased(mouseX, mouseY, button);
        }

        if (panel1 != null && panel1.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public void onClose() {
        SupportersTabState state = SupportersTabState.getInstance();
        UUID playerUuid = state.getPlayerUuid();
        if (playerUuid != null) {
            com.kingodogo.buildscape.config.CosmeticsConfig.get().setEquippedCosmetics(
                playerUuid, 
                state.getEquippedCosmeticsBySlot()
            );
        }

        state.setSelectedCosmeticId(null);
        super.onClose();
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (panel1 != null && panel1.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (panel1 != null && panel1.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}