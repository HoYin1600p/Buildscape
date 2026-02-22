package com.kingodogo.buildscape.api.model;

import java.util.List;
import java.util.Map;

/**
 * Response from the secure authenticate endpoint.
 * POST /api/minecraft
 */
public class AuthenticateResponse {
    private List<String> defaultCosmetics;
    private List<String> unlockedCosmetics;
    private Map<String, String> selectedCosmetics;
    private boolean isAdmin;

    // Error fields (when authentication fails)
    private String error;
    private String code;

    public AuthenticateResponse() {
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
        isAdmin = admin;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Check if the response indicates an error.
     */
    public boolean isError() {
        return error != null && !error.isEmpty();
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * Convert this response to CosmeticData format.
     */
    public CosmeticData toCosmeticData() {
        CosmeticData data = new CosmeticData();
        data.setDefaultCosmetics(this.defaultCosmetics);
        data.setUnlockedCosmetics(this.unlockedCosmetics);
        data.setSelectedCosmetics(this.selectedCosmetics);
        data.setAdmin(this.isAdmin);
        data.adaptFromSecureResponse();
        return data;
    }
}
