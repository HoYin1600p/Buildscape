package com.kingodogo.buildscape.api;

import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.api.model.SupporterStatus;
import com.kingodogo.buildscape.api.model.TiersResponse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for API responses to reduce API calls and improve performance.
 * 
 * Cache TTL: 5 minutes
 * UUID-based cache keys
 * Cache invalidation on manual refresh
 */
public class SupportersApiCache {
    private static final long CACHE_TTL_MS = 5 * 60 * 1000; // 5 minutes
    
    private static final SupportersApiCache INSTANCE = new SupportersApiCache();
    
    private static class CachedData<T> {
        private final T data;
        private final long timestamp;
        
        public CachedData(T data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }
        
        public T getData() {
            return data;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL_MS;
        }
    }
    
    private final ConcurrentHashMap<UUID, CachedData<SupporterStatus>> statusCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, CachedData<CosmeticData>> cosmeticsCache = new ConcurrentHashMap<>();
    private CachedData<TiersResponse> tiersCache;
    
    private SupportersApiCache() {
    }
    
    public static SupportersApiCache getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get cached supporter status for a UUID.
     * 
     * @param uuid Player UUID
     * @return Cached status if available and not expired, null otherwise
     */
    public SupporterStatus getCachedStatus(UUID uuid) {
        CachedData<SupporterStatus> cached = statusCache.get(uuid);
        if (cached == null || cached.isExpired()) {
            if (cached != null && cached.isExpired()) {
                statusCache.remove(uuid);
            }
            return null;
        }
        return cached.getData();
    }
    
    /**
     * Cache supporter status for a UUID.
     * 
     * @param uuid Player UUID
     * @param status Status to cache
     */
    public void cacheStatus(UUID uuid, SupporterStatus status) {
        if (uuid != null && status != null) {
            statusCache.put(uuid, new CachedData<>(status));
        }
    }
    
    /**
     * Get cached cosmetics data for a UUID.
     * 
     * @param uuid Player UUID
     * @return Cached cosmetics if available and not expired, null otherwise
     */
    public CosmeticData getCachedCosmetics(UUID uuid) {
        CachedData<CosmeticData> cached = cosmeticsCache.get(uuid);
        if (cached == null || cached.isExpired()) {
            if (cached != null && cached.isExpired()) {
                cosmeticsCache.remove(uuid);
            }
            return null;
        }
        return cached.getData();
    }
    
    /**
     * Cache cosmetics data for a UUID.
     * 
     * @param uuid Player UUID
     * @param cosmetics Cosmetics data to cache
     */
    public void cacheCosmetics(UUID uuid, CosmeticData cosmetics) {
        if (uuid != null && cosmetics != null) {
            cosmeticsCache.put(uuid, new CachedData<>(cosmetics));
        }
    }
    
    /**
     * Get cached tiers data.
     * 
     * @return Cached tiers if available and not expired, null otherwise
     */
    public TiersResponse getCachedTiers() {
        if (tiersCache == null || tiersCache.isExpired()) {
            tiersCache = null;
            return null;
        }
        return tiersCache.getData();
    }
    
    /**
     * Cache tiers data.
     * 
     * @param tiers Tiers data to cache
     */
    public void cacheTiers(TiersResponse tiers) {
        if (tiers != null) {
            tiersCache = new CachedData<>(tiers);
        }
    }
    
    /**
     * Invalidate cache for a specific UUID.
     * 
     * @param uuid Player UUID
     */
    public void invalidate(UUID uuid) {
        statusCache.remove(uuid);
        cosmeticsCache.remove(uuid);
    }
    
    /**
     * Invalidate all caches.
     */
    public void invalidateAll() {
        statusCache.clear();
        cosmeticsCache.clear();
        tiersCache = null;
    }
    
    /**
     * Clear expired entries from cache.
     * Should be called periodically to prevent memory leaks.
     */
    public void clearExpired() {
        statusCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        cosmeticsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        if (tiersCache != null && tiersCache.isExpired()) {
            tiersCache = null;
        }
    }
}

