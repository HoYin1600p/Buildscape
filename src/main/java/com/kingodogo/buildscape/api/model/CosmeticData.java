package com.kingodogo.buildscape.api.model;

import java.util.List;

/**
 * Response model for cosmetics API endpoint.
 * GET /api/v1/supporters/cosmetics/{uuid}
 * 
 * Arrays contain cosmetic IDs in format:
 * - "item:namespace:item_id" (e.g., "item:minecraft:diamond_sword")
 * - "block:namespace:block_id" (e.g., "block:minecraft:gold_block")
 * - "nbt:custom_data" - NBT-based cosmetic
 * - "type:armor_set_1" - Type-based cosmetic
 */
public class CosmeticData {
    private List<String> unlocked;
    private List<String> locked;
    private List<String> equipped;
    
    public CosmeticData() {
    }
    
    public CosmeticData(List<String> unlocked, List<String> locked, List<String> equipped) {
        this.unlocked = unlocked;
        this.locked = locked;
        this.equipped = equipped;
    }
    
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
}

