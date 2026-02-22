package com.kingodogo.buildscape.api.model;

/**
 * Request body for the secure select cosmetic endpoint.
 * POST /api/cosmetics
 */
public class SelectCosmeticRequest {
    private String action;
    private String uuid;
    private String accessToken;
    private String cosmeticId;
    private String cosmeticType;

    public SelectCosmeticRequest() {
    }

    public SelectCosmeticRequest(String action, String uuid, String accessToken, String cosmeticId, String cosmeticType) {
        this.action = action;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.cosmeticId = cosmeticId;
        this.cosmeticType = cosmeticType;
    }

    public static SelectCosmeticRequest createSelect(String uuid, String accessToken, String cosmeticId, String cosmeticType) {
        return new SelectCosmeticRequest("selectCosmetic", uuid, accessToken, cosmeticId, cosmeticType);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getCosmeticId() {
        return cosmeticId;
    }

    public void setCosmeticId(String cosmeticId) {
        this.cosmeticId = cosmeticId;
    }

    public String getCosmeticType() {
        return cosmeticType;
    }

    public void setCosmeticType(String cosmeticType) {
        this.cosmeticType = cosmeticType;
    }
}
