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
    private CompletableFuture<CosmeticData> currentAuthFuture = null;

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
            return CompletableFuture.completedFuture(cachedCosmetics);
        }

        synchronized (this) {
            // Return existing future if authentication is already in progress
            if (currentAuthFuture != null) {
                return currentAuthFuture;
            }

            // Double-check after acquiring lock
            if (authenticated && cachedCosmetics != null) {
                return CompletableFuture.completedFuture(cachedCosmetics);
            }

            // Get UUID and access token from Minecraft
            Minecraft mc = Minecraft.getInstance();
            if (mc.getUser() == null) {
                BuildScape.getLogger().warn("CosmeticAuthManager: No user available, cannot authenticate");
                return CompletableFuture.completedFuture(null);
            }

            String uuid = mc.getUser().getUuid();
            String accessToken = mc.getUser().getAccessToken();

            if (uuid == null || uuid.isEmpty()) {
                BuildScape.getLogger().warn("CosmeticAuthManager: UUID is null or empty");
                return CompletableFuture.completedFuture(null);
            }

            if (accessToken == null || accessToken.isEmpty()) {
                BuildScape.getLogger().warn("CosmeticAuthManager: Access token is null or empty");
                return CompletableFuture.completedFuture(null);
            }

            // Call the secure API and share the future
            currentAuthFuture = SupportersApiClient.getInstance()
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

                        return cosmeticData;
                    })
                    .exceptionally(throwable -> {
                        BuildScape.getLogger().error("CosmeticAuthManager: Authentication failed with exception", throwable);
                        return null;
                    })
                    .whenComplete((result, throwable) -> {
                        synchronized (this) {
                            currentAuthFuture = null;
                        }
                    });

            return currentAuthFuture;
        }
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
            this.currentAuthFuture = null;
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
