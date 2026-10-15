package com.kingodogo.buildscape.client.screen;

import java.util.HashMap;
import java.util.Map;

/**
 * High-performance UI Tab Cache Manager.
 * Lazily caches tab view models, scroll viewports, and heavy query lists so tabs only load
 * when accessed and can be switched instantaneously without dropping frames or re-allocating.
 */
public class TabCacheManager {

    public static class TabViewState {
        public final String tabId;
        public boolean isLoaded = false;
        public double scrollOffset = 0.0;
        public double targetScrollOffset = 0.0;
        public double maxScroll = 0.0;
        public int totalContentHeight = 0;
        public boolean isDirty = true;
        private final Map<String, Object> customData = new HashMap<>();

        public TabViewState(String tabId) {
            this.tabId = tabId;
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String key) {
            return (T) customData.get(key);
        }

        public void put(String key, Object value) {
            customData.put(key, value);
        }

        public boolean has(String key) {
            return customData.containsKey(key);
        }

        public void clearData() {
            customData.clear();
            isDirty = true;
        }
    }

    private final Map<String, TabViewState> tabs = new HashMap<>();

    public TabViewState getOrCreate(String tabId) {
        return tabs.computeIfAbsent(tabId, TabViewState::new);
    }

    public boolean isLoaded(String tabId) {
        TabViewState state = tabs.get(tabId);
        return state != null && state.isLoaded;
    }

    public void markDirty(String tabId) {
        TabViewState state = tabs.get(tabId);
        if (state != null) {
            state.isDirty = true;
        }
    }

    public void markAllDirty() {
        for (TabViewState state : tabs.values()) {
            state.isDirty = true;
        }
    }

    public void clear() {
        tabs.clear();
    }
}
