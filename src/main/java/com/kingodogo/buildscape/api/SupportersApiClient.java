package com.kingodogo.buildscape.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.model.ApiResponse;
import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.api.model.SupporterStatus;
import com.kingodogo.buildscape.api.model.TiersResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Secure API client for communicating with buildscape.online.
 * 
 * Security features:
 * - HTTPS only (rejects HTTP connections)
 * - No credentials in code (all authentication server-side)
 * - UUID validation before API calls
 * - Rate limiting (max 1 request per 2 seconds per UUID)
 * - Input sanitization
 * - Certificate validation (uses default Java trust store)
 * 
 * Base URL: https://buildscape.online/api/v1
 */
public class SupportersApiClient {
    private static final String BASE_URL = "https://buildscape.online/api/v1";
    private static final int TIMEOUT_SECONDS = 10;
    private static final long RATE_LIMIT_MS = 2000; // 2 seconds between requests per UUID
    
    private final HttpClient httpClient;
    private final Gson gson;
    
    // Rate limiting: track last request time per UUID
    private final ConcurrentHashMap<UUID, Long> lastRequestTime = new ConcurrentHashMap<>();
    
    private SupportersApiClient() {
        // Create HTTP client with HTTPS-only configuration
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();
        
        // Gson for JSON parsing
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    }
    
    private static final SupportersApiClient INSTANCE = new SupportersApiClient();
    
    public static SupportersApiClient getInstance() {
        return INSTANCE;
    }
    
    /**
     * Validate UUID format.
     * 
     * @param uuidString UUID string to validate
     * @return true if valid UUID format
     */
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
    
    /**
     * Check rate limit for a UUID.
     * 
     * @param uuid UUID to check
     * @return true if request is allowed (not rate limited)
     */
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
    
    /**
     * Sanitize input string to prevent injection attacks.
     * 
     * @param input Input string
     * @return Sanitized string
     */
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove any potentially dangerous characters
        return input.replaceAll("[^a-zA-Z0-9_\\-.:]", "");
    }
    
    /**
     * Make an HTTP GET request.
     * 
     * @param endpoint Endpoint path (e.g., "/supporters/status/")
     * @param responseClass Response class for JSON deserialization
     * @return CompletableFuture with response
     */
    private <T> CompletableFuture<T> getRequest(String endpoint, Class<T> responseClass) {
        String url = BASE_URL + endpoint;
        
        // Ensure HTTPS
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
    
    /**
     * Make an HTTP POST request.
     * 
     * @param endpoint Endpoint path
     * @param body Request body object (will be serialized to JSON)
     * @param responseClass Response class for JSON deserialization
     * @return CompletableFuture with response
     */
    private <T> CompletableFuture<T> postRequest(String endpoint, Object body, Class<T> responseClass) {
        String url = BASE_URL + endpoint;
        
        // Ensure HTTPS
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
    
    /**
     * Get supporter status for a UUID.
     * GET /api/v1/supporters/status/{uuid}
     * 
     * @param uuid Player UUID
     * @return CompletableFuture with SupporterStatus
     */
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
    
    /**
     * Get cosmetics data for a UUID.
     * GET /api/v1/supporters/cosmetics/{uuid}
     * 
     * @param uuid Player UUID
     * @return CompletableFuture with CosmeticData
     */
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
    
    /**
     * Connect account (initiate connection process).
     * POST /api/v1/supporters/connect/{uuid}
     * 
     * @param uuid Player UUID
     * @param verificationCode Optional verification code (can be null)
     * @return CompletableFuture with ApiResponse
     */
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
    
    /**
     * Get all membership tiers.
     * GET /api/v1/supporters/tiers
     * 
     * @return CompletableFuture with TiersResponse
     */
    public CompletableFuture<TiersResponse> getTiers() {
        // Tiers endpoint doesn't require UUID, so no rate limiting needed
        return getRequest("/supporters/tiers", TiersResponse.class);
    }
    
    /**
     * Request body for connect endpoint.
     */
    private static class ConnectRequest {
        private String verificationCode;
        
        public ConnectRequest(String verificationCode) {
            this.verificationCode = verificationCode;
        }
        
        public String getVerificationCode() {
            return verificationCode;
        }
    }
    
    /**
     * Custom exception for API errors.
     */
    public static class ApiException extends RuntimeException {
        public ApiException(String message) {
            super(message);
        }
        
        public ApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Exception for rate limit violations.
     */
    public static class RateLimitException extends RuntimeException {
        public RateLimitException(String message) {
            super(message);
        }
    }
}

