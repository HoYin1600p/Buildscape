package com.kingodogo.buildscape.api.model;

import java.util.List;

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

