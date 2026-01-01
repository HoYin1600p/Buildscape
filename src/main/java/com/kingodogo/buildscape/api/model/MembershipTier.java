package com.kingodogo.buildscape.api.model;

/**
 * Model for membership tier information.
 * Part of GET /api/v1/supporters/tiers response.
 */
public class MembershipTier {
    private String id;
    private String name;
    private int level;
    private String description;
    
    public MembershipTier() {
    }
    
    public MembershipTier(String id, String name, int level, String description) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.description = description;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

