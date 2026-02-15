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

            BuildScape.getLogger().info("GameLaunchHandler: Triggering cosmetic authentication on game launch");

            // Run authentication asynchronously to not block game startup
            CosmeticAuthManager.getInstance().authenticateOnLaunch()
                    .thenAccept(cosmeticData -> {
                        if (cosmeticData != null) {
                            // Update SupportersTabState with cosmetic data
                            updateSupportersTabState(cosmeticData);
                            BuildScape.getLogger().info("GameLaunchHandler: Cosmetic data loaded successfully");
                        } else {
                            BuildScape.getLogger().warn("GameLaunchHandler: Failed to load cosmetic data, using defaults");
                            // Use empty/default cosmetics
                            SupportersTabState.getInstance().setUnlockedCosmetics(new HashSet<>());
                        }
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

            // Set unlocked cosmetics
            if (cosmeticData.getUnlocked() != null) {
                state.setUnlockedCosmetics(new HashSet<>(cosmeticData.getUnlocked()));
            } else {
                state.setUnlockedCosmetics(new HashSet<>());
            }

            // Set equipped cosmetics from selectedCosmetics map
            if (cosmeticData.getSelectedCosmetics() != null && !cosmeticData.getSelectedCosmetics().isEmpty()) {
                state.setEquippedCosmetics(new HashSet<>(cosmeticData.getSelectedCosmetics().values()));
            }

            BuildScape.getLogger().debug("GameLaunchHandler: SupportersTabState updated with " +
                    state.getUnlockedCosmetics().size() + " unlocked cosmetics");

        } catch (Exception e) {
            BuildScape.getLogger().error("GameLaunchHandler: Failed to update SupportersTabState", e);
        }
    }
}
