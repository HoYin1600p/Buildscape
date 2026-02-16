package com.kingodogo.buildscape.api.model;

/**
 * Request body for the secure authenticate endpoint.
 * POST /api/minecraft
 */
public class AuthenticateRequest {
    private String action;
    private String uuid;
    private String accessToken;

    public AuthenticateRequest() {
    }

    public AuthenticateRequest(String action, String uuid, String accessToken) {
        this.action = action;
        this.uuid = uuid;
        this.accessToken = accessToken;
    }

    /**
     * Create a standard authenticate request.
     */
    public static AuthenticateRequest createAuthenticate(String uuid, String accessToken) {
        return new AuthenticateRequest("authenticate", uuid, accessToken);
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
}
