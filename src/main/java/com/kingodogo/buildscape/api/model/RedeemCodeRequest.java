package com.kingodogo.buildscape.api.model;

/**
 * Request body for the secure redeem code endpoint.
 * POST /api/redeem
 */
public class RedeemCodeRequest {
    private String action;
    private String uuid;
    private String accessToken;
    private String code;

    public RedeemCodeRequest() {
    }

    public RedeemCodeRequest(String action, String uuid, String accessToken, String code) {
        this.action = action;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.code = code;
    }

    /**
     * Create a standard redeem code request.
     */
    public static RedeemCodeRequest createRedeemCode(String uuid, String accessToken, String code) {
        return new RedeemCodeRequest("redeemCode", uuid, accessToken, code);
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
    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
