package com.kingodogo.buildscape.event;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.CosmeticAuthManager;
import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.client.screen.tabs.supporters.SupportersTabState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashSet;
import java.util.Map;

/**
 * Handles game launch events to trigger one-time cosmetic authentication.
 * Authentication happens during game startup, before the main menu appears.
 */
@Mod.EventBusSubscriber(modid = BuildScape.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GameLaunchHandler {
    private static boolean authenticationTriggered = false;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Trigger one-time authentication on game launch
        if (!authenticationTriggered) {
            authenticationTriggered = true;



            // Run authentication asynchronously to not block game startup
            CosmeticAuthManager.getInstance().authenticateOnLaunch()
                    .thenAccept(cosmeticData -> {
                        net.minecraft.client.Minecraft.getInstance().execute(() -> {
                            if (cosmeticData != null) {
                                // Update SupportersTabState with cosmetic data
                                updateSupportersTabState(cosmeticData);
                            } else {
                                BuildScape.getLogger().warn("GameLaunchHandler: API returned NULL for cosmetic data.");
                                // Ensure default cosmetics are still available (particle trails, etc)
                                updateSupportersTabState(null);
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        BuildScape.getLogger().error("GameLaunchHandler: Exception during authentication", throwable);
                        return null;
                    });
        }
    }

    /**
     * Update SupportersTabState with the authenticated cosmetic data.
     */
    private static void updateSupportersTabState(CosmeticData cosmeticData) {
        try {
            SupportersTabState state = SupportersTabState.getInstance();
            com.kingodogo.buildscape.cosmetics.CosmeticManager cosmeticManager =
                com.kingodogo.buildscape.cosmetics.CosmeticManager.getInstance();

            java.util.Set<String> unlocked = new HashSet<>();
            
            if (cosmeticData != null) {
                // Add API-provided unlocked cosmetics
                if (cosmeticData.getUnlocked() != null) {
                    unlocked.addAll(cosmeticData.getUnlocked());
                }
                
                // If admin, unlock ALL registered cosmetics
                if (cosmeticData.isAdmin()) {
                    unlocked.addAll(cosmeticManager.getAllCosmetics());

                }
            }
            
            // ALWAYS add default cosmetics (particle trails, builder's hat, etc.)
            unlocked.addAll(cosmeticManager.getDefaultCosmetics());
            state.setUnlockedCosmetics(unlocked);

            if (cosmeticData != null && cosmeticData.getSelectedCosmetics() != null && !cosmeticData.getSelectedCosmetics().isEmpty()) {
                state.setEquippedCosmetics(new HashSet<>(cosmeticData.getSelectedCosmetics().values()));
                
                for (Map.Entry<String, String> entry : cosmeticData.getSelectedCosmetics().entrySet()) {
                    String type = entry.getKey();
                    String cosmeticId = entry.getValue();
                    int slot = getSlotFromType(type);
                    if (slot != -100 && cosmeticId != null && !cosmeticId.isEmpty()) {
                        state.equipCosmeticToSlot(slot, cosmeticId);
                    }
                }
            }



        } catch (Exception e) {
            BuildScape.getLogger().error("GameLaunchHandler: Failed to update SupportersTabState", e);
        }
    }

    /**
     * Map string type from API to internal slot ID.
     */
    private static int getSlotFromType(String type) {
        if (type == null) return -100;
        return switch (type.toLowerCase()) {
            case "head" -> SupportersTabState.SLOT_HEAD;
            case "chest" -> SupportersTabState.SLOT_CHEST;
            case "legs" -> SupportersTabState.SLOT_LEGS;
            case "feet" -> SupportersTabState.SLOT_FEET;
            case "trail" -> SupportersTabState.SLOT_TRAIL;
            case "wings" -> SupportersTabState.SLOT_WINGS;
            case "back" -> SupportersTabState.SLOT_BACK;
            case "shoulder" -> SupportersTabState.SLOT_SHOULDER;
            default -> -100;
        };
    }
}
