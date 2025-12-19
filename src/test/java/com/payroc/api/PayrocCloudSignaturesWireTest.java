package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.payroccloud.signatures.requests.RetrieveSignaturesRequest;
import com.payroc.api.resources.payroccloud.signatures.types.RetrieveSignaturesResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PayrocCloudSignaturesWireTest {
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
    public void testRetrieve() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"signatureId\":\"JDN4ILZB0T\",\"processingTerminalId\":\"1024\",\"createdOn\":\"2024-07-02\",\"contentType\":\"image/png\",\"signature\":\"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\"}"));
        RetrieveSignaturesResponse response = client.payrocCloud()
                .signatures()
                .retrieve("JDN4ILZB0T", RetrieveSignaturesRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"signatureId\": \"JDN4ILZB0T\",\n"
                + "  \"processingTerminalId\": \"1024\",\n"
                + "  \"createdOn\": \"2024-07-02\",\n"
                + "  \"contentType\": \"image/png\",\n"
                + "  \"signature\": \"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\"\n"
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
