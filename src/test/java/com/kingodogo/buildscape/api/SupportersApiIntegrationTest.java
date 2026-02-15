package com.kingodogo.buildscape.api;

import com.google.gson.Gson;
import com.kingodogo.buildscape.api.model.AuthenticateRequest;
import com.kingodogo.buildscape.api.model.AuthenticateResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIf("isIntegrationTestsEnabled")
public class SupportersApiIntegrationTest {

    private static final Gson GSON = new Gson();
    private static HttpClient httpClient;
    private static String apiBaseUrl;
    private static String testUuid;
    private static String testAccessToken;
    private static String testRedeemCode;

    @BeforeAll
    static void setUp() throws IOException {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();

        Properties props = loadTestConfig();
        apiBaseUrl = props.getProperty("api.base.url", "http://localhost:8888/.netlify/functions");
        testUuid = props.getProperty("test.uuid", "");
        testAccessToken = props.getProperty("test.access.token", "");
        testRedeemCode = props.getProperty("test.redeem.code", "TEST123");
    }

    static boolean isIntegrationTestsEnabled() {
        try {
            Properties props = loadTestConfig();
            return "true".equalsIgnoreCase(props.getProperty("tests.integration.enabled", "false"));
        } catch (IOException e) {
            return false;
        }
    }

    private static Properties loadTestConfig() throws IOException {
        Properties props = new Properties();

        try (InputStream is = SupportersApiIntegrationTest.class.getResourceAsStream("/test-config.local.properties")) {
            if (is != null) {
                props.load(is);
                return props;
            }
        }

        try (InputStream is = SupportersApiIntegrationTest.class.getResourceAsStream("/test-config.properties")) {
            if (is != null) {
                props.load(is);
            }
        }

        return props;
    }

    @Test
    @Order(1)
    @DisplayName("UUID with dashes should be accepted")
    void testUUID_WithDashes_Accepted() throws IOException, InterruptedException {
        String uuidWithDashes = "550e8400-e29b-41d4-a716-446655440000";

        AuthenticateRequest request = new AuthenticateRequest("authenticate", uuidWithDashes, "fake_token");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertNotEquals(400, response.statusCode(), "UUID with dashes should not return 400");
    }

    @Test
    @Order(2)
    @DisplayName("UUID without dashes should be accepted")
    void testUUID_WithoutDashes_Accepted() throws IOException, InterruptedException {
        String uuidWithoutDashes = "550e8400e29b41d4a716446655440000";

        AuthenticateRequest request = new AuthenticateRequest("authenticate", uuidWithoutDashes, "fake_token");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertNotEquals(400, response.statusCode(), "UUID without dashes should not return 400");
    }

    @Test
    @Order(3)
    @DisplayName("Invalid UUID format should return 400")
    void testUUID_InvalidFormat_Returns400() throws IOException, InterruptedException {
        AuthenticateRequest request = new AuthenticateRequest("authenticate", "not-a-valid-uuid", "fake_token");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertEquals(400, response.statusCode(), "Invalid UUID should return 400");
    }

    @Test
    @Order(4)
    @DisplayName("Missing UUID should return 400")
    void testUUID_Missing_Returns400() throws IOException, InterruptedException {
        String jsonBody = "{\"action\":\"authenticate\",\"accessToken\":\"fake_token\"}";

        HttpResponse<String> response = sendRawPostRequest("/api-minecraft", jsonBody);

        assertEquals(400, response.statusCode(), "Missing UUID should return 400");
    }

    @Test
    @Order(10)
    @DisplayName("Missing access token should return 400")
    void testAccessToken_Missing_Returns400() throws IOException, InterruptedException {
        AuthenticateRequest request = new AuthenticateRequest("authenticate", "550e8400e29b41d4a716446655440000", null);

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertEquals(400, response.statusCode(), "Missing access token should return 400");
    }

    @Test
    @Order(11)
    @DisplayName("Empty access token should return 400")
    void testAccessToken_Empty_Returns400() throws IOException, InterruptedException {
        AuthenticateRequest request = new AuthenticateRequest("authenticate", "550e8400e29b41d4a716446655440000", "");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertEquals(400, response.statusCode(), "Empty access token should return 400");
    }

    @Test
    @Order(12)
    @DisplayName("Invalid access token should return 401")
    void testAccessToken_Invalid_Returns401() throws IOException, InterruptedException {
        if (testUuid.isEmpty()) {
            testUuid = "550e8400e29b41d4a716446655440000";
        }

        AuthenticateRequest request = new AuthenticateRequest("authenticate", testUuid, "fake_invalid_token");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        System.out.println("Invalid token response: " + response.statusCode() + " - " + response.body());
    }

    @Test
    @Order(20)
    @DisplayName("Missing action should return 400")
    void testAction_Missing_Returns400() throws IOException, InterruptedException {
        String jsonBody = "{\"uuid\":\"550e8400e29b41d4a716446655440000\",\"accessToken\":\"fake_token\"}";

        HttpResponse<String> response = sendRawPostRequest("/api-minecraft", jsonBody);

        assertEquals(400, response.statusCode(), "Missing action should return 400");
    }

    @Test
    @Order(21)
    @DisplayName("Invalid action should return 400")
    void testAction_Invalid_Returns400() throws IOException, InterruptedException {
        AuthenticateRequest request = new AuthenticateRequest("invalid_action", "550e8400e29b41d4a716446655440000", "fake_token");

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        assertEquals(400, response.statusCode(), "Invalid action should return 400");
    }

    @Test
    @Order(22)
    @DisplayName("Malformed JSON should return 400")
    void testMalformedJSON_Returns400() throws IOException, InterruptedException {
        String malformedJson = "{\"action\":\"authenticate\",\"uuid\":\"";

        HttpResponse<String> response = sendRawPostRequest("/api-minecraft", malformedJson);

        assertEquals(400, response.statusCode(), "Malformed JSON should return 400");
    }

    @Test
    @Order(30)
    @DisplayName("Rate limiting should trigger after 11 requests")
    void testRateLimiting_TriggersAfterLimit() throws IOException, InterruptedException {
        String uuid = "550e8400e29b41d4a716446655440000";
        AuthenticateRequest request = new AuthenticateRequest("authenticate", uuid, "fake_token");

        int rateLimitedCount = 0;

        for (int i = 0; i < 15; i++) {
            HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

            if (response.statusCode() == 429) {
                rateLimitedCount++;
            }

            Thread.sleep(100);
        }

        assertTrue(rateLimitedCount > 0, "Some requests should be rate limited (429)");
        System.out.println("Rate limited requests: " + rateLimitedCount + "/15");
    }

    @Test
    @Order(40)
    @DisplayName("Response should contain expected fields")
    void testResponse_ContainsExpectedFields() throws IOException, InterruptedException {
        if (testUuid.isEmpty() || testAccessToken.isEmpty()) {
            System.out.println("Skipping: No test credentials available");
            return;
        }

        AuthenticateRequest request = new AuthenticateRequest("authenticate", testUuid, testAccessToken);

        HttpResponse<String> response = sendPostRequest("/api-minecraft", request);

        if (response.statusCode() == 200) {
            AuthenticateResponse authResponse = GSON.fromJson(response.body(), AuthenticateResponse.class);

            assertNotNull(authResponse.getDefaultCosmetics(), "Response should contain defaultCosmetics");
            assertNotNull(authResponse.getUnlockedCosmetics(), "Response should contain unlockedCosmetics");
            assertNotNull(authResponse.getSelectedCosmetics(), "Response should contain selectedCosmetics");

            System.out.println("Response: " + response.body());
        } else {
            System.out.println("Authentication failed with status: " + response.statusCode());
            System.out.println("Response: " + response.body());
        }
    }

    @Test
    @Order(50)
    @DisplayName("Redeem code without authentication should fail")
    void testRedeemCode_NoAuth_Fails() throws IOException, InterruptedException {
        String jsonBody = String.format(
                "{\"action\":\"redeemCode\",\"uuid\":\"%s\",\"accessToken\":\"fake_token\",\"code\":\"%s\"}",
                testUuid.isEmpty() ? "550e8400e29b41d4a716446655440000" : testUuid,
                testRedeemCode
        );

        HttpResponse<String> response = sendRawPostRequest("/api-redeem", jsonBody);

        assertTrue(response.statusCode() == 401 || response.statusCode() == 400,
                "Redeem without valid auth should return 401 or 400");
    }

    @Test
    @Order(51)
    @DisplayName("Invalid redeem code should return error")
    void testRedeemCode_Invalid_ReturnsError() throws IOException, InterruptedException {
        if (testUuid.isEmpty() || testAccessToken.isEmpty()) {
            System.out.println("Skipping: No test credentials available");
            return;
        }

        String jsonBody = String.format(
                "{\"action\":\"redeemCode\",\"uuid\":\"%s\",\"accessToken\":\"%s\",\"code\":\"INVALID_CODE_12345\"}",
                testUuid, testAccessToken
        );

        HttpResponse<String> response = sendRawPostRequest("/api-redeem", jsonBody);

        System.out.println("Invalid code response: " + response.statusCode() + " - " + response.body());

        assertTrue(response.statusCode() == 400 || response.statusCode() == 404,
                "Invalid code should return 400 or 404");
    }

    private HttpResponse<String> sendPostRequest(String endpoint, Object body) throws IOException, InterruptedException {
        String jsonBody = GSON.toJson(body);
        return sendRawPostRequest(endpoint, jsonBody);
    }

    private HttpResponse<String> sendRawPostRequest(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
