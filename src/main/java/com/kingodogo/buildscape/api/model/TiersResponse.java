package com.kingodogo.buildscape.api.model;

import java.util.List;

/**
 * Response model for tiers API endpoint.
 * GET /api/v1/supporters/tiers
 */
public class TiersResponse {
    private List<MembershipTier> tiers;
    
    public TiersResponse() {
    }
    
    public TiersResponse(List<MembershipTier> tiers) {
        this.tiers = tiers;
    }
    
    public List<MembershipTier> getTiers() {
        return tiers;
    }
    
    public void setTiers(List<MembershipTier> tiers) {
        this.tiers = tiers;
    }
}

