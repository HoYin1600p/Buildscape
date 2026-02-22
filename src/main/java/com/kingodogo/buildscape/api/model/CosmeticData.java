package com.kingodogo.buildscape.api.model;

import java.util.List;
import java.util.Map;

/**
 * Response model for cosmetics API endpoint.
 * Supports both legacy format (GET /api/v1/supporters/cosmetics/{uuid})
 * and secure format (POST /api/minecraft with authentication).
 *
 * Arrays contain cosmetic IDs in format:
 * - "item:namespace:item_id" (e.g., "item:minecraft:diamond_sword")
 * - "block:namespace:block_id" (e.g., "block:minecraft:gold_block")
 * - "nbt:custom_data" - NBT-based cosmetic
 * - "type:armor_set_1" - Type-based cosmetic
 */
public class CosmeticData {
    // Legacy fields (backward compatibility)
    private List<String> unlocked;
    private List<String> locked;
    private List<String> equipped;

    // New secure API fields
    private List<String> defaultCosmetics;
    private List<String> unlockedCosmetics;
    private Map<String, String> selectedCosmetics;
    private boolean isAdmin;

    public CosmeticData() {
    }

    public CosmeticData(List<String> unlocked, List<String> locked, List<String> equipped) {
        this.unlocked = unlocked;
        this.locked = locked;
        this.equipped = equipped;
    }

    // Legacy getters/setters
    public List<String> getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(List<String> unlocked) {
        this.unlocked = unlocked;
    }

    public List<String> getLocked() {
        return locked;
    }

    public void setLocked(List<String> locked) {
        this.locked = locked;
    }

    public List<String> getEquipped() {
        return equipped;
    }

    public void setEquipped(List<String> equipped) {
        this.equipped = equipped;
    }

    // New secure API getters/setters
    public List<String> getDefaultCosmetics() {
        return defaultCosmetics;
    }

    public void setDefaultCosmetics(List<String> defaultCosmetics) {
        this.defaultCosmetics = defaultCosmetics;
    }

    public List<String> getUnlockedCosmetics() {
        return unlockedCosmetics;
    }

    public void setUnlockedCosmetics(List<String> unlockedCosmetics) {
        this.unlockedCosmetics = unlockedCosmetics;
    }

    public Map<String, String> getSelectedCosmetics() {
        return selectedCosmetics;
    }

    public void setSelectedCosmetics(Map<String, String> selectedCosmetics) {
        this.selectedCosmetics = selectedCosmetics;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    /**
     * Adapter method to populate legacy fields from new secure API response.
     * Call this after receiving data from the new API.
     */
    public void adaptFromSecureResponse() {
        // Populate unlocked from unlockedCosmetics (combines defaults + unlocked)
        if (this.unlockedCosmetics != null) {
            this.unlocked = this.unlockedCosmetics;
        }

        if (this.isAdmin) {
            // If admin, we can't easily list everything here without CosmeticManager
            // But we can flag it for the UI
        }

        // Populate equipped from selectedCosmetics values
        if (this.selectedCosmetics != null && !this.selectedCosmetics.isEmpty()) {
            this.equipped = this.selectedCosmetics.values()
                    .stream()
                    .filter(id -> id != null && !id.isEmpty())
                    .toList();
        }

        // locked is not provided by new API, leave as null/empty
        this.locked = List.of();
    }

    /**
     * Check if this data came from the secure API (new format).
     */
    public boolean isSecureFormat() {
        return this.unlockedCosmetics != null || this.selectedCosmetics != null;
    }
}

