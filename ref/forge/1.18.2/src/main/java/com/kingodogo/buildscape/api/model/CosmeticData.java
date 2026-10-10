package com.kingodogo.buildscape.api.model;

import java.util.List;
import java.util.Map;

public class CosmeticData {
    private List<String> unlocked;
    private List<String> locked;
    private List<String> equipped;

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

    public void adaptFromSecureResponse() {
        if (this.unlockedCosmetics != null) {
            this.unlocked = this.unlockedCosmetics;
        }

        if (this.isAdmin) {
        }

        if (this.selectedCosmetics != null && !this.selectedCosmetics.isEmpty()) {
            this.equipped = this.selectedCosmetics.values()
                    .stream()
                    .filter(id -> id != null && !id.isEmpty())
                    .toList();
        }

        this.locked = List.of();
    }

    public boolean isSecureFormat() {
        return this.unlockedCosmetics != null || this.selectedCosmetics != null;
    }
}

