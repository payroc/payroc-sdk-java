package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.processingterminals.requests.RetrieveHostConfigurationProcessingTerminalsRequest;
import com.payroc.api.resources.boarding.processingterminals.requests.RetrieveProcessingTerminalsRequest;
import com.payroc.api.types.HostConfiguration;
import com.payroc.api.types.ProcessingTerminal;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingProcessingTerminalsWireTest {
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingProcessingTerminalsWireTest_testRetrieve_response.json")));
        ProcessingTerminal response = client.boarding()
                .processingTerminals()
                .retrieve(
                        "1234001", RetrieveProcessingTerminalsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingTerminalsWireTest_testRetrieve_response.json");
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

    @Test
    public void testRetrieveHostConfiguration() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingTerminalId\":\"0123451001\",\"processingAccountId\":\"23451\",\"configuration\":{\"merchant\":{\"posMid\":\"123456789101\",\"chainNumber\":\"222222\",\"settlementAgent\":\"0001\",\"abaNumber\":\"967854108\",\"binNumber\":\"888888\",\"agentBankNumber\":\"000001\",\"reimbursementAttribute\":\"Z\",\"locationNumber\":\"000001\"},\"terminal\":{\"terminalId\":\"V500000\",\"terminalNumber\":\"1111\",\"authenticationCode\":\"A1B2C3\",\"sharingGroups\":\"3E7HULY8NQWZG\",\"motoAllowed\":true,\"internetAllowed\":true,\"cardPresentAllowed\":true},\"processor\":\"tsys\"}}"));
        HostConfiguration response = client.boarding()
                .processingTerminals()
                .retrieveHostConfiguration(
                        "1234001",
                        RetrieveHostConfigurationProcessingTerminalsRequest.builder()
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"processingTerminalId\": \"0123451001\",\n"
                + "  \"processingAccountId\": \"23451\",\n"
                + "  \"configuration\": {\n"
                + "    \"merchant\": {\n"
                + "      \"posMid\": \"123456789101\",\n"
                + "      \"chainNumber\": \"222222\",\n"
                + "      \"settlementAgent\": \"0001\",\n"
                + "      \"abaNumber\": \"967854108\",\n"
                + "      \"binNumber\": \"888888\",\n"
                + "      \"agentBankNumber\": \"000001\",\n"
                + "      \"reimbursementAttribute\": \"Z\",\n"
                + "      \"locationNumber\": \"000001\"\n"
                + "    },\n"
                + "    \"terminal\": {\n"
                + "      \"terminalId\": \"V500000\",\n"
                + "      \"terminalNumber\": \"1111\",\n"
                + "      \"authenticationCode\": \"A1B2C3\",\n"
                + "      \"sharingGroups\": \"3E7HULY8NQWZG\",\n"
                + "      \"motoAllowed\": true,\n"
                + "      \"internetAllowed\": true,\n"
                + "      \"cardPresentAllowed\": true\n"
                + "    },\n"
                + "    \"processor\": \"tsys\"\n"
                + "  }\n"
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
