package com.kingodogo.buildscape.api.model;

import java.util.List;

/**
 * Response model for supporter status API endpoint.
 * GET /api/v1/supporters/status/{uuid}
 */
public class SupporterStatus {
    private boolean connected;
    private String username;
    private String tier;
    private Integer tierLevel;
    private List<String> cosmetics;
    
    public SupporterStatus() {
    }
    
    public SupporterStatus(boolean connected, String username, String tier, Integer tierLevel, List<String> cosmetics) {
        this.connected = connected;
        this.username = username;
        this.tier = tier;
        this.tierLevel = tierLevel;
        this.cosmetics = cosmetics;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getTier() {
        return tier;
    }
    
    public void setTier(String tier) {
        this.tier = tier;
    }
    
    public Integer getTierLevel() {
        return tierLevel;
    }
    
    public void setTierLevel(Integer tierLevel) {
        this.tierLevel = tierLevel;
    }
    
    public List<String> getCosmetics() {
        return cosmetics;
    }
    
    public void setCosmetics(List<String> cosmetics) {
        this.cosmetics = cosmetics;
    }
}

