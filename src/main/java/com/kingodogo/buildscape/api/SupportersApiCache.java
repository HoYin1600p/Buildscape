package com.kingodogo.buildscape.api;

import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.api.model.SupporterStatus;
import com.kingodogo.buildscape.api.model.TiersResponse;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SupportersApiCache {
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;

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
    
    public void cacheStatus(UUID uuid, SupporterStatus status) {
        if (uuid != null && status != null) {
            statusCache.put(uuid, new CachedData<>(status));
        }
    }
    
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
    
    public void cacheCosmetics(UUID uuid, CosmeticData cosmetics) {
        if (uuid != null && cosmetics != null) {
            cosmeticsCache.put(uuid, new CachedData<>(cosmetics));
        }
    }
    
    public TiersResponse getCachedTiers() {
        if (tiersCache == null || tiersCache.isExpired()) {
            tiersCache = null;
            return null;
        }
        return tiersCache.getData();
    }
    
    public void cacheTiers(TiersResponse tiers) {
        if (tiers != null) {
            tiersCache = new CachedData<>(tiers);
        }
    }
    
    public void invalidate(UUID uuid) {
        statusCache.remove(uuid);
        cosmeticsCache.remove(uuid);
    }
    
    public void invalidateAll() {
        statusCache.clear();
        cosmeticsCache.clear();
        tiersCache = null;
    }
    
    public void clearExpired() {
        statusCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        cosmeticsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        if (tiersCache != null && tiersCache.isExpired()) {
            tiersCache = null;
        }
    }
}

