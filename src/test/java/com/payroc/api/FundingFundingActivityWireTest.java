package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.funding.fundingactivity.requests.RetrieveBalanceFundingActivityRequest;
import com.payroc.api.resources.funding.fundingactivity.types.RetrieveBalanceFundingActivityResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FundingFundingActivityWireTest {
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
    public void testRetrieveBalance() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"limit\":2,\"count\":2,\"hasMore\":true,\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-balance?before=4525644354&limit=2\"},{\"rel\":\"next\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-balance?after=9876543219&limit=2\"}],\"data\":[{\"merchantId\":\"4525644354\",\"funds\":120000,\"pending\":50050,\"available\":69950,\"currency\":\"USD\"},{\"merchantId\":\"9876543219\",\"funds\":50000,\"pending\":0,\"available\":50000,\"currency\":\"USD\"}]}"));
        RetrieveBalanceFundingActivityResponse response = client.funding()
                .fundingActivity()
                .retrieveBalance(RetrieveBalanceFundingActivityRequest.builder()
                        .before("2571")
                        .after("8516")
                        .limit(1)
                        .merchantId("4525644354")
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"limit\": 2,\n"
                + "  \"count\": 2,\n"
                + "  \"hasMore\": true,\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/funding-balance?before=4525644354&limit=2\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"rel\": \"next\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/funding-balance?after=9876543219&limit=2\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"data\": [\n"
                + "    {\n"
                + "      \"merchantId\": \"4525644354\",\n"
                + "      \"funds\": 120000,\n"
                + "      \"pending\": 50050,\n"
                + "      \"available\": 69950,\n"
                + "      \"currency\": \"USD\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"merchantId\": \"9876543219\",\n"
                + "      \"funds\": 50000,\n"
                + "      \"pending\": 0,\n"
                + "      \"available\": 50000,\n"
                + "      \"currency\": \"USD\"\n"
                + "    }\n"
                + "  ]\n"
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
