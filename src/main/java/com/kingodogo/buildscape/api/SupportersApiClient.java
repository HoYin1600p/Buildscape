package com.kingodogo.buildscape.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SupportersApiClient {
    private static final String BASE_URL = "https://buildscape.online/api/v1";
    private static final String SECURE_API_URL = "https://buildscape.online/.netlify/functions";
    private static final int TIMEOUT_SECONDS = 10;
    private static final long RATE_LIMIT_MS = 2000;

    private final HttpClient httpClient;
    private final Gson gson;
    
    private final ConcurrentHashMap<UUID, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    private SupportersApiClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }
    
    private static final SupportersApiClient INSTANCE = new SupportersApiClient();
    
    public static SupportersApiClient getInstance() {
        return INSTANCE;
    }
    
    private boolean isValidUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return false;
        }
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    private boolean checkRateLimit(UUID uuid) {
        long now = System.currentTimeMillis();
        Long lastTime = lastRequestTime.get(uuid);
        
        if (lastTime == null) {
            lastRequestTime.put(uuid, now);
            return true;
        }
        
        long timeSinceLastRequest = now - lastTime;
        if (timeSinceLastRequest < RATE_LIMIT_MS) {
            BuildScape.getLogger().debug("Rate limit: Request for UUID " + uuid + " too soon");
            return false;
        }
        
        lastRequestTime.put(uuid, now);
        return true;
    }
    
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[^a-zA-Z0-9_\\-.:]", "");
    }
    
    private <T> CompletableFuture<T> getRequest(String endpoint, Class<T> responseClass) {
        String url = BASE_URL + endpoint;

        if (!url.startsWith("https://")) {
            BuildScape.getLogger().error("Rejected non-HTTPS URL: " + url);
            return CompletableFuture.failedFuture(new SecurityException("HTTPS required"));
        }
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .header("Accept", "application/json")
            .header("User-Agent", "BuildScape-Mod/1.0")
            .GET()
            .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new ApiException("API returned status code: " + response.statusCode());
                }
                
                String body = response.body();
                if (body == null || body.isEmpty()) {
                    throw new ApiException("Empty response body");
                }
                
                try {
                    return gson.fromJson(body, responseClass);
                } catch (Exception e) {
                    BuildScape.getLogger().error("Failed to parse JSON response: " + e.getMessage());
                    throw new ApiException("Failed to parse response: " + e.getMessage(), e);
                }
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("API request failed: " + throwable.getMessage());
                if (throwable.getCause() != null) {
                    BuildScape.getLogger().error("Cause: " + throwable.getCause().getMessage());
                }
                return null;
            });
    }
    
    private <T> CompletableFuture<T> postRequest(String endpoint, Object body, Class<T> responseClass) {
        String url = BASE_URL + endpoint;

        if (!url.startsWith("https://")) {
            BuildScape.getLogger().error("Rejected non-HTTPS URL: " + url);
            return CompletableFuture.failedFuture(new SecurityException("HTTPS required"));
        }
        
        String jsonBody = gson.toJson(body);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "BuildScape-Mod/1.0")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    throw new ApiException("API returned status code: " + response.statusCode());
                }
                
                String bodyStr = response.body();
                if (bodyStr == null || bodyStr.isEmpty()) {
                    throw new ApiException("Empty response body");
                }
                
                try {
                    return gson.fromJson(bodyStr, responseClass);
                } catch (Exception e) {
                    BuildScape.getLogger().error("Failed to parse JSON response: " + e.getMessage());
                    throw new ApiException("Failed to parse response: " + e.getMessage(), e);
                }
            })
            .exceptionally(throwable -> {
                BuildScape.getLogger().error("API POST request failed: " + throwable.getMessage());
                if (throwable.getCause() != null) {
                    BuildScape.getLogger().error("Cause: " + throwable.getCause().getMessage());
                }
                return null;
            });
    }
    
    public CompletableFuture<SupporterStatus> getSupporterStatus(UUID uuid) {
        if (uuid == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null"));
        }
        
        if (!checkRateLimit(uuid)) {
            return CompletableFuture.failedFuture(new RateLimitException("Rate limit exceeded"));
        }
        
        String sanitizedUuid = sanitizeInput(uuid.toString());
        String endpoint = "/supporters/status/" + sanitizedUuid;
        
        return getRequest(endpoint, SupporterStatus.class);
    }
    
    public CompletableFuture<CosmeticData> getCosmetics(UUID uuid) {
        if (uuid == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null"));
        }
        
        if (!checkRateLimit(uuid)) {
            return CompletableFuture.failedFuture(new RateLimitException("Rate limit exceeded"));
        }
        
        String sanitizedUuid = sanitizeInput(uuid.toString());
        String endpoint = "/supporters/cosmetics/" + sanitizedUuid;
        
        return getRequest(endpoint, CosmeticData.class);
    }
    
    public CompletableFuture<ApiResponse> connectAccount(UUID uuid, String verificationCode) {
        if (uuid == null) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null"));
        }
        
        if (!checkRateLimit(uuid)) {
            return CompletableFuture.failedFuture(new RateLimitException("Rate limit exceeded"));
        }
        
        String sanitizedUuid = sanitizeInput(uuid.toString());
        String endpoint = "/supporters/connect/" + sanitizedUuid;
        
        ConnectRequest request = new ConnectRequest(verificationCode);
        return postRequest(endpoint, request, ApiResponse.class);
    }
    
    public CompletableFuture<TiersResponse> getTiers() {
        return getRequest("/supporters/tiers", TiersResponse.class);
    }

    /**
     * Authenticate with the secure API using Minecraft session credentials.
     * This is the new secure authentication method that verifies the access token with Mojang.
     *
     * @param uuid        The player's UUID
     * @param accessToken The player's Minecraft access token
     * @return CompletableFuture with AuthenticateResponse containing cosmetic data
     */
    public CompletableFuture<AuthenticateResponse> authenticate(String uuid, String accessToken) {
        if (uuid == null || uuid.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null or empty"));
        }

        if (accessToken == null || accessToken.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Access token cannot be null or empty"));
        }

        // Sanitize inputs
        String sanitizedUuid = sanitizeInput(uuid);
        String sanitizedToken = sanitizeInput(accessToken);

        // Create request body
        AuthenticateRequest requestBody = AuthenticateRequest.createAuthenticate(sanitizedUuid, sanitizedToken);

        // Make POST request to secure API
        String url = SECURE_API_URL + "/api-minecraft";

        if (!url.startsWith("https://")) {
            BuildScape.getLogger().error("Rejected non-HTTPS URL: " + url);
            return CompletableFuture.failedFuture(new SecurityException("HTTPS required"));
        }

        String jsonBody = gson.toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "BuildScape-Mod/1.0")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();



        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String bodyStr = response.body();

                    if (bodyStr == null || bodyStr.isEmpty()) {
                        BuildScape.getLogger().error("Empty response body from authentication");
                        AuthenticateResponse errorResponse = new AuthenticateResponse();
                        errorResponse.setError("Empty response from server");
                        errorResponse.setCode("EMPTY_RESPONSE");
                        return errorResponse;
                    }

                    try {
                        AuthenticateResponse authResponse = gson.fromJson(bodyStr, AuthenticateResponse.class);

                        // Check for HTTP error status
                        if (response.statusCode() < 200 || response.statusCode() >= 300) {
                            BuildScape.getLogger().error("Authentication failed with status " + response.statusCode() +
                                    ": " + authResponse.getError());
                            // Error details should be in the response body
                            return authResponse;
                        }

                        return authResponse;
                    } catch (Exception e) {
                        BuildScape.getLogger().error("Failed to parse authentication response: " + e.getMessage());
                        AuthenticateResponse errorResponse = new AuthenticateResponse();
                        errorResponse.setError("Failed to parse response: " + e.getMessage());
                        errorResponse.setCode("PARSE_ERROR");
                        return errorResponse;
                    }
                })
                .exceptionally(throwable -> {
                    BuildScape.getLogger().error("Authentication request failed: " + throwable.getMessage());
                    AuthenticateResponse errorResponse = new AuthenticateResponse();
                    errorResponse.setError("Request failed: " + throwable.getMessage());
                    errorResponse.setCode("REQUEST_FAILED");
                    return errorResponse;
                });
    }

    /**
     * Redeem a code for the player.
     * This uses the secure API and requires authentication appropriately.
     *
     * @param uuid        The player's UUID
     * @param accessToken The player's Minecraft access token for verification
     * @param code        The code to redeem
     * @return CompletableFuture with ApiResponse indicating success or failure
     */
    public CompletableFuture<ApiResponse> redeemCode(String uuid, String accessToken, String code) {
        if (uuid == null || uuid.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null or empty"));
        }

        if (accessToken == null || accessToken.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Access token cannot be null or empty"));
        }
        
        if (code == null || code.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Code cannot be null or empty"));
        }

        // Sanitize inputs
        String sanitizedUuid = sanitizeInput(uuid);
        String sanitizedToken = sanitizeInput(accessToken);
        // Simple alphanumeric check for code
        String sanitizedCode = sanitizeInput(code);

        // Create request body
        RedeemCodeRequest requestBody = RedeemCodeRequest.createRedeemCode(sanitizedUuid, sanitizedToken, sanitizedCode);

        // Make POST request to secure API
        String url = SECURE_API_URL + "/api-redeem";

        if (!url.startsWith("https://")) {
            BuildScape.getLogger().error("Rejected non-HTTPS URL: " + url);
            return CompletableFuture.failedFuture(new SecurityException("HTTPS required"));
        }

        String jsonBody = gson.toJson(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "BuildScape-Mod/1.0")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();



        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    String bodyStr = response.body();

                    if (bodyStr == null || bodyStr.isEmpty()) {
                        BuildScape.getLogger().error("Empty response body from redeem");
                        ApiResponse errorResponse = new ApiResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setError("Empty response from server");
                        errorResponse.setMessage("Server returned empty response");
                        return errorResponse;
                    }

                    try {
                        ApiResponse apiResponse = gson.fromJson(bodyStr, ApiResponse.class);
                        
                        // Check for HTTP error status
                        // Even if status is 4xx, the body might contain useful error message
                        if (response.statusCode() < 200 || response.statusCode() >= 300) {
                            BuildScape.getLogger().error("Redeem failed with status " + response.statusCode());
                            if (apiResponse.getError() == null || apiResponse.getError().isEmpty()) {
                                apiResponse.setError("HTTP " + response.statusCode());
                            }
                            // Ensure success is false if http error
                            if (apiResponse.isSuccess()) {
                                apiResponse.setSuccess(false); 
                            }
                        }

                        return apiResponse;
                    } catch (Exception e) {
                        BuildScape.getLogger().error("Failed to parse redeem response: " + e.getMessage());
                        ApiResponse errorResponse = new ApiResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setError("Failed to parse response: " + e.getMessage());
                        return errorResponse;
                    }
                })
                .exceptionally(throwable -> {
                    BuildScape.getLogger().error("Redeem request failed: " + throwable.getMessage());
                    ApiResponse errorResponse = new ApiResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setError("Request failed: " + throwable.getMessage());
                    return errorResponse;
                });
    }

    /**
     * Select a cosmetic for a player.
     */
    public CompletableFuture<ApiResponse> selectCosmetic(String uuid, String accessToken, String cosmeticId, String cosmeticType) {
        if (uuid == null || uuid.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("UUID cannot be null or empty"));
        }

        if (accessToken == null || accessToken.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Access token cannot be null or empty"));
        }

        if (cosmeticId == null || cosmeticId.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Cosmetic ID cannot be null or empty"));
        }

        if (cosmeticType == null || cosmeticType.isEmpty()) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Cosmetic type cannot be null or empty"));
        }

        // Convert string UUID to UUID object and check rate limit
        // Handle both formats: with dashes and without dashes
        UUID uuidObj = null;
        try {
            String normalizedUuid = uuid.replace("-", "");
            if (normalizedUuid.length() == 32) {
                // Insert dashes to make valid UUID format
                String formattedUuid = normalizedUuid.substring(0, 8) + "-" +
                        normalizedUuid.substring(8, 12) + "-" +
                        normalizedUuid.substring(12, 16) + "-" +
                        normalizedUuid.substring(16, 20) + "-" +
                        normalizedUuid.substring(20);
                uuidObj = UUID.fromString(formattedUuid);
            } else {
                uuidObj = UUID.fromString(uuid);
            }
        } catch (IllegalArgumentException e) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid UUID format: " + uuid));
        }

        if (!checkRateLimit(uuidObj)) {
            return CompletableFuture.failedFuture(new RateLimitException("Rate limit exceeded"));
        }

        String sanitizedUuid = uuid.replace("-", "");
        String sanitizedToken = accessToken.trim();
        
        SelectCosmeticRequest requestBody = SelectCosmeticRequest.createSelect(sanitizedUuid, sanitizedToken, cosmeticId, cosmeticType);
        
        // Use the secure API
        String url = SECURE_API_URL + "/api-cosmetics";
        
        String jsonBody = gson.toJson(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "BuildScape-Mod/1.0")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    try {
                        String body = response.body();
                        ApiResponse apiResponse = gson.fromJson(body, ApiResponse.class);
                        
                        if (response.statusCode() != 200) {
                            if (apiResponse == null) {
                                apiResponse = new ApiResponse();
                                apiResponse.setError("HTTP " + response.statusCode());
                            }
                            apiResponse.setSuccess(false);
                        }
                        
                        return apiResponse;
                    } catch (Exception e) {
                        ApiResponse errorResponse = new ApiResponse();
                        errorResponse.setSuccess(false);
                        errorResponse.setError("Failed to parse response");
                        return errorResponse;
                    }
                })
                .exceptionally(throwable -> {
                    ApiResponse errorResponse = new ApiResponse();
                    errorResponse.setSuccess(false);
                    errorResponse.setError("Request failed: " + throwable.getMessage());
                    return errorResponse;
                });
    }

    private static class ConnectRequest {
        private final String verificationCode;
        
        public ConnectRequest(String verificationCode) {
            this.verificationCode = verificationCode;
        }
        
        public String getVerificationCode() {
            return verificationCode;
        }
    }
    
    public static class ApiException extends RuntimeException {
        public ApiException(String message) {
            super(message);
        }
        
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) {
            super(message);
        }
    }
}

