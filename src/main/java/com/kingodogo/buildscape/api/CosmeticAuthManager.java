package com.kingodogo.buildscape.api;

import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.model.CosmeticData;
import net.minecraft.client.Minecraft;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    public CompletableFuture<CosmeticData> authenticateOnLaunch() {
        if (authenticated && cachedCosmetics != null) {
            return CompletableFuture.completedFuture(cachedCosmetics);
        }

        synchronized (this) {
            if (currentAuthFuture != null) {
                return currentAuthFuture;
            }

            if (authenticated && cachedCosmetics != null) {
                return CompletableFuture.completedFuture(cachedCosmetics);
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.getUser() == null) {
                BuildScape.getLogger().warn("CosmeticAuthManager: No user available, cannot authenticate");
                return CompletableFuture.completedFuture(null);
            }

            String uuid = mc.getUser().getUuid();

            if (uuid == null || uuid.isEmpty()) {
                BuildScape.getLogger().warn("CosmeticAuthManager: UUID is null or empty");
                return CompletableFuture.completedFuture(null);
            }

            UUID playerId = parseUuid(uuid);
            if (playerId == null) {
                BuildScape.getLogger().warn("CosmeticAuthManager: UUID is invalid");
                return CompletableFuture.completedFuture(null);
            }

            currentAuthFuture = SupportersApiClient.getInstance()
                    .getCosmetics(playerId)
                    .thenApply(cosmeticData -> {
                        if (cosmeticData == null) {
                            BuildScape.getLogger().error("CosmeticAuthManager: Cosmetic lookup returned null");
                            return null;
                        }

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

    private static UUID parseUuid(String value) {
        try {
            String normalized = value.replace("-", "");
            if (normalized.length() != 32) {
                return null;
            }
            return UUID.fromString(normalized.substring(0, 8) + "-"
                    + normalized.substring(8, 12) + "-"
                    + normalized.substring(12, 16) + "-"
                    + normalized.substring(16, 20) + "-"
                    + normalized.substring(20));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public CosmeticData getCachedCosmetics() {
        return cachedCosmetics;
    }

    public long getAuthTimestamp() {
        return authTimestamp;
    }

    public void clearCache() {
        synchronized (this) {
            this.authenticated = false;
            this.cachedCosmetics = null;
            this.authTimestamp = 0;
            this.currentAuthFuture = null;
        }
    }

    public void forceReauthentication() {
        synchronized (this) {
            this.authenticated = false;
            this.cachedCosmetics = null;
            this.authTimestamp = 0;
        }
    }
}
