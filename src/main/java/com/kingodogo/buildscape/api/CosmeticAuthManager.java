package com.kingodogo.buildscape.api;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.model.CosmeticData;
import net.minecraft.client.Minecraft;

import java.util.concurrent.CompletableFuture;

/**
 * Manages one-time authentication at game launch for cosmetic data.
 * Authentication happens once when the game starts, and the data is cached
 * for the entire game session.
 */
public class CosmeticAuthManager {
    private static final CosmeticAuthManager INSTANCE = new CosmeticAuthManager();

    private volatile boolean authenticated = false;
    private volatile CosmeticData cachedCosmetics = null;
    private volatile long authTimestamp = 0;
    private volatile boolean authenticationInProgress = false;

    private CosmeticAuthManager() {
    }

    public static CosmeticAuthManager getInstance() {
        return INSTANCE;
    }

    /**
     * Authenticate once at game launch and cache the cosmetic data.
     * This method is thread-safe and ensures authentication only happens once.
     *
     * @return CompletableFuture with CosmeticData, or null if authentication fails
     */
    public CompletableFuture<CosmeticData> authenticateOnLaunch() {
        // Return cached data if already authenticated
        if (authenticated && cachedCosmetics != null) {
            BuildScape.getLogger().debug("CosmeticAuthManager: Returning cached cosmetic data");
            return CompletableFuture.completedFuture(cachedCosmetics);
        }

        // Prevent concurrent authentication attempts
        if (authenticationInProgress) {
            BuildScape.getLogger().debug("CosmeticAuthManager: Authentication already in progress, waiting...");
            return waitForAuthentication();
        }

        synchronized (this) {
            // Double-check after acquiring lock
            if (authenticated && cachedCosmetics != null) {
                return CompletableFuture.completedFuture(cachedCosmetics);
            }

            if (authenticationInProgress) {
                return waitForAuthentication();
            }

            authenticationInProgress = true;

            // Get UUID and access token from Minecraft
            Minecraft mc = Minecraft.getInstance();
            if (mc.getUser() == null) {
                BuildScape.getLogger().warn("CosmeticAuthManager: No user available, cannot authenticate");
                authenticationInProgress = false;
                return CompletableFuture.completedFuture(null);
            }

            String uuid = mc.getUser().getUuid();
            String accessToken = mc.getUser().getAccessToken();

            if (uuid == null || uuid.isEmpty()) {
                BuildScape.getLogger().warn("CosmeticAuthManager: UUID is null or empty");
                authenticationInProgress = false;
                return CompletableFuture.completedFuture(null);
            }

            if (accessToken == null || accessToken.isEmpty()) {
                BuildScape.getLogger().warn("CosmeticAuthManager: Access token is null or empty");
                authenticationInProgress = false;
                return CompletableFuture.completedFuture(null);
            }

            BuildScape.getLogger().info("CosmeticAuthManager: Starting authentication...");

            // Call the secure API
            return SupportersApiClient.getInstance()
                    .authenticate(uuid, accessToken)
                    .thenApply(response -> {
                        if (response == null) {
                            BuildScape.getLogger().error("CosmeticAuthManager: Authentication returned null");
                            return null;
                        }

                        if (response.isError()) {
                            BuildScape.getLogger().error("CosmeticAuthManager: Authentication failed - " +
                                    response.getCode() + ": " + response.getError());
                            return null;
                        }

                        // Convert response to CosmeticData
                        CosmeticData cosmeticData = response.toCosmeticData();

                        // Cache the result
                        this.cachedCosmetics = cosmeticData;
                        this.authenticated = true;
                        this.authTimestamp = System.currentTimeMillis();

                        BuildScape.getLogger().info("CosmeticAuthManager: Authentication successful, " +
                                "unlocked " + (cosmeticData.getUnlocked() != null ? cosmeticData.getUnlocked().size() : 0) + " cosmetics");

                        return cosmeticData;
                    })
                    .exceptionally(throwable -> {
                        BuildScape.getLogger().error("CosmeticAuthManager: Authentication failed with exception", throwable);
                        return null;
                    })
                    .whenComplete((result, throwable) -> {
                        authenticationInProgress = false;
                    });
        }
    }

    /**
     * Wait for an ongoing authentication to complete.
     */
    private CompletableFuture<CosmeticData> waitForAuthentication() {
        return CompletableFuture.supplyAsync(() -> {
            int attempts = 0;
            while (authenticationInProgress && attempts < 100) {
                try {
                    Thread.sleep(100);
                    attempts++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
            return cachedCosmetics;
        });
    }

    /**
     * Check if authentication has been completed successfully.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Get the cached cosmetic data.
     * Returns null if authentication hasn't completed yet.
     */
    public CosmeticData getCachedCosmetics() {
        return cachedCosmetics;
    }

    /**
     * Get the timestamp of successful authentication.
     */
    public long getAuthTimestamp() {
        return authTimestamp;
    }

    /**
     * Clear the authentication cache.
     * This should only be called when the game is shutting down.
     */
    public void clearCache() {
        synchronized (this) {
            this.authenticated = false;
            this.cachedCosmetics = null;
            this.authTimestamp = 0;
            this.authenticationInProgress = false;
        }
    }

    /**
     * Force re-authentication on next call to authenticateOnLaunch().
     * Use with caution - this will trigger a new API call.
     */
    public void forceReauthentication() {
        synchronized (this) {
            this.authenticated = false;
            this.cachedCosmetics = null;
            this.authTimestamp = 0;
        }
    }
}
