package com.kingodogo.buildscape.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kingodogo.buildscape.BuildScape;
import com.kingodogo.buildscape.api.model.ApiResponse;
import com.kingodogo.buildscape.api.model.CosmeticData;
import com.kingodogo.buildscape.api.model.SupporterStatus;
import com.kingodogo.buildscape.api.model.TiersResponse;

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
    
    private static class ConnectRequest {
        private String verificationCode;
        
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

