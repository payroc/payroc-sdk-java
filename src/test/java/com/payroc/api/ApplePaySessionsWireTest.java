package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.applepaysessions.requests.ApplePaySessions;
import com.payroc.api.types.ApplePayResponseSession;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ApplePaySessionsWireTest {
    private MockWebServer server;
    private PayrocApiClient client;
    private ObjectMapper objectMapper = ObjectMappers.JSON_MAPPER;

    @BeforeEach
    public void setup() throws Exception {
        server = new MockWebServer();
        server.start();
        client = PayrocApiClient.builder()
                .environment(Environment.custom()
                        .api(server.url("/").toString())
                        .identity(server.url("/").toString())
                        .build())
                .build();
    }

    @AfterEach
    public void teardown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testCreate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"startSessionResponse\":\"{\\n  \\\"epochTimestamp\\\": 1736264582447,\\n  \\\"expiresAt\\\": 1736268182447,\\n  \\\"merchantSessionIdentifier\\\": \\\"SSHE464E2B91B714F18BFD19D46D0F582BF_916523AAED1343F5BC5815E12BEE9250AFFDC1A17C46B0DE5A943F0F94927C24\\\",\\n  \\\"nonce\\\": \\\"e5775127\\\",\\n  \\\"merchantIdentifier\\\": \\\"BFB110EE83BE2AF4AA7468926C926CCFC57F4A541CCE6E7F3BEFD05002ECE503\\\",\\n  \\\"domainName\\\": \\\"store.com\\\",\\n  \\\"displayName\\\": \\\"Store One\\\",\\n  \\\"signature\\\": \\\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\\\",\\n  \\\"operationalAnalyticsIdentifier\\\": \\\"Store One:BFB110EE83BE2AF4AA7468926C926CCFC57F4A541CCE6E7F3BEFD05002ECE503\\\",\\n  \\\"retries\\\": 0,\\n  \\\"pspId\\\": \\\"17D4AAA8D9357D26D771ABA0DAA0B9D3BB462AD1585492E1FE688AF8BB9558E5\\\"\\n}\\n\"}"));
        ApplePayResponseSession response = client.applePaySessions()
                .create(
                        "1234001",
                        ApplePaySessions.builder()
                                .appleDomainId("DUHDZJHGYY")
                                .appleValidationUrl("https://apple-pay-gateway.apple.com/paymentservices/startSession")
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"appleDomainId\": \"DUHDZJHGYY\",\n"
                + "  \"appleValidationUrl\": \"https://apple-pay-gateway.apple.com/paymentservices/startSession\"\n"
                + "}";
        JsonNode actualJson = objectMapper.readTree(actualRequestBody);
        JsonNode expectedJson = objectMapper.readTree(expectedRequestBody);
        Assertions.assertTrue(jsonEquals(expectedJson, actualJson), "Request body structure does not match expected");
        if (actualJson.has("type") || actualJson.has("_type") || actualJson.has("kind")) {
            String discriminator = null;
            if (actualJson.has("type")) discriminator = actualJson.get("type").asText();
            else if (actualJson.has("_type"))
                discriminator = actualJson.get("_type").asText();
            else if (actualJson.has("kind"))
                discriminator = actualJson.get("kind").asText();
            Assertions.assertNotNull(discriminator, "Union type should have a discriminator field");
            Assertions.assertFalse(discriminator.isEmpty(), "Union discriminator should not be empty");
        }

        if (!actualJson.isNull()) {
            Assertions.assertTrue(
                    actualJson.isObject() || actualJson.isArray() || actualJson.isValueNode(),
                    "request should be a valid JSON value");
        }

        if (actualJson.isArray()) {
            Assertions.assertTrue(actualJson.size() >= 0, "Array should have valid size");
        }
        if (actualJson.isObject()) {
            Assertions.assertTrue(actualJson.size() >= 0, "Object should have valid field count");
        }

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"startSessionResponse\": \"{\\n  \\\"epochTimestamp\\\": 1736264582447,\\n  \\\"expiresAt\\\": 1736268182447,\\n  \\\"merchantSessionIdentifier\\\": \\\"SSHE464E2B91B714F18BFD19D46D0F582BF_916523AAED1343F5BC5815E12BEE9250AFFDC1A17C46B0DE5A943F0F94927C24\\\",\\n  \\\"nonce\\\": \\\"e5775127\\\",\\n  \\\"merchantIdentifier\\\": \\\"BFB110EE83BE2AF4AA7468926C926CCFC57F4A541CCE6E7F3BEFD05002ECE503\\\",\\n  \\\"domainName\\\": \\\"store.com\\\",\\n  \\\"displayName\\\": \\\"Store One\\\",\\n  \\\"signature\\\": \\\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\\\",\\n  \\\"operationalAnalyticsIdentifier\\\": \\\"Store One:BFB110EE83BE2AF4AA7468926C926CCFC57F4A541CCE6E7F3BEFD05002ECE503\\\",\\n  \\\"retries\\\": 0,\\n  \\\"pspId\\\": \\\"17D4AAA8D9357D26D771ABA0DAA0B9D3BB462AD1585492E1FE688AF8BB9558E5\\\"\\n}\\n\"\n"
                + "}";
        JsonNode actualResponseNode = objectMapper.readTree(actualResponseJson);
        JsonNode expectedResponseNode = objectMapper.readTree(expectedResponseBody);
        Assertions.assertTrue(
                jsonEquals(expectedResponseNode, actualResponseNode),
                "Response body structure does not match expected");
        if (actualResponseNode.has("type") || actualResponseNode.has("_type") || actualResponseNode.has("kind")) {
            String discriminator = null;
            if (actualResponseNode.has("type"))
                discriminator = actualResponseNode.get("type").asText();
            else if (actualResponseNode.has("_type"))
                discriminator = actualResponseNode.get("_type").asText();
            else if (actualResponseNode.has("kind"))
                discriminator = actualResponseNode.get("kind").asText();
            Assertions.assertNotNull(discriminator, "Union type should have a discriminator field");
            Assertions.assertFalse(discriminator.isEmpty(), "Union discriminator should not be empty");
        }

        if (!actualResponseNode.isNull()) {
            Assertions.assertTrue(
                    actualResponseNode.isObject() || actualResponseNode.isArray() || actualResponseNode.isValueNode(),
                    "response should be a valid JSON value");
        }

        if (actualResponseNode.isArray()) {
            Assertions.assertTrue(actualResponseNode.size() >= 0, "Array should have valid size");
        }
        if (actualResponseNode.isObject()) {
            Assertions.assertTrue(actualResponseNode.size() >= 0, "Object should have valid field count");
        }
    }

    /**
     * Compares two JsonNodes with numeric equivalence and null safety.
     * For objects, checks that all fields in 'expected' exist in 'actual' with matching values.
     * Allows 'actual' to have extra fields (e.g., default values added during serialization).
     */
    private boolean jsonEquals(JsonNode expected, JsonNode actual) {
        if (expected == null && actual == null) return true;
        if (expected == null || actual == null) return false;
        if (expected.equals(actual)) return true;
        if (expected.isNumber() && actual.isNumber())
            return Math.abs(expected.doubleValue() - actual.doubleValue()) < 1e-10;
        if (expected.isObject() && actual.isObject()) {
            java.util.Iterator<java.util.Map.Entry<String, JsonNode>> iter = expected.fields();
            while (iter.hasNext()) {
                java.util.Map.Entry<String, JsonNode> entry = iter.next();
                JsonNode actualValue = actual.get(entry.getKey());
                if (actualValue == null || !jsonEquals(entry.getValue(), actualValue)) return false;
            }
            return true;
        }
        if (expected.isArray() && actual.isArray()) {
            if (expected.size() != actual.size()) return false;
            for (int i = 0; i < expected.size(); i++) {
                if (!jsonEquals(expected.get(i), actual.get(i))) return false;
            }
            return true;
        }
        return false;
    }
}
