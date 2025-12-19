package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.tokenization.singleusetokens.requests.SingleUseTokenRequest;
import com.payroc.api.resources.tokenization.singleusetokens.types.SingleUseTokenRequestChannel;
import com.payroc.api.resources.tokenization.singleusetokens.types.SingleUseTokenRequestSource;
import com.payroc.api.types.CardPayload;
import com.payroc.api.types.CardPayloadCardDetails;
import com.payroc.api.types.Device;
import com.payroc.api.types.DeviceModel;
import com.payroc.api.types.RawCardDetails;
import com.payroc.api.types.SingleUseToken;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TokenizationSingleUseTokensWireTest {
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
                                "{\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"paymentMethod\":{\"accountType\":\"checking\",\"cardDetails\":{\"downgradeTo\":\"keyed\",\"device\":{\"model\":\"bbposChp\",\"serialNumber\":\"1850010868\",\"firmwareVersion\":\"v1.2.3\",\"config\":{\"quickChip\":false}},\"rawData\":\"A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"entryMethod\":\"raw\"},\"type\":\"card\"},\"token\":\"fa2e9e51bc5265a33a5ca41449524d53d1def596ffd8c0904f222183a71a65cdb58835120a65196a48a6375abc4deafe2b7e948689ab9d6aba919e860f32e247\",\"expiresAt\":\"2024-08-05T17:50:05Z\",\"source\":{\"cardholderName\":\"Sarah Hazel Hopper\",\"cardNumber\":\"4539858876047062\",\"expiryDate\":\"1225\",\"cardType\":\"cardType\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"},\"type\":\"card\"}}"));
        SingleUseToken response = client.tokenization()
                .singleUseTokens()
                .create(
                        "1234001",
                        SingleUseTokenRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .channel(SingleUseTokenRequestChannel.WEB)
                                .source(SingleUseTokenRequestSource.card(CardPayload.builder()
                                        .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                                .device(Device.builder()
                                                        .model(DeviceModel.BBPOS_CHP)
                                                        .serialNumber("1850010868")
                                                        .build())
                                                .rawData(
                                                        "A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                                .build()))
                                        .build()))
                                .operator("Jane")
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate headers
        Assertions.assertEquals(
                "8e03978e-40d5-43e8-bc93-6894a57f9324",
                request.getHeader("Idempotency-Key"),
                "Header 'Idempotency-Key' should match expected value");
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"channel\": \"web\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"source\": {\n"
                + "    \"cardDetails\": {\n"
                + "      \"device\": {\n"
                + "        \"model\": \"bbposChp\",\n"
                + "        \"serialNumber\": \"1850010868\"\n"
                + "      },\n"
                + "      \"rawData\": \"A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF\",\n"
                + "      \"entryMethod\": \"raw\"\n"
                + "    },\n"
                + "    \"type\": \"card\"\n"
                + "  }\n"
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
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"paymentMethod\": {\n"
                + "    \"accountType\": \"checking\",\n"
                + "    \"cardDetails\": {\n"
                + "      \"downgradeTo\": \"keyed\",\n"
                + "      \"device\": {\n"
                + "        \"model\": \"bbposChp\",\n"
                + "        \"serialNumber\": \"1850010868\",\n"
                + "        \"firmwareVersion\": \"v1.2.3\",\n"
                + "        \"config\": {\n"
                + "          \"quickChip\": false\n"
                + "        }\n"
                + "      },\n"
                + "      \"rawData\": \"A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF\",\n"
                + "      \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "      \"entryMethod\": \"raw\"\n"
                + "    },\n"
                + "    \"type\": \"card\"\n"
                + "  },\n"
                + "  \"token\": \"fa2e9e51bc5265a33a5ca41449524d53d1def596ffd8c0904f222183a71a65cdb58835120a65196a48a6375abc4deafe2b7e948689ab9d6aba919e860f32e247\",\n"
                + "  \"expiresAt\": \"2024-08-05T17:50:05Z\",\n"
                + "  \"source\": {\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardNumber\": \"4539858876047062\",\n"
                + "    \"expiryDate\": \"1225\",\n"
                + "    \"cardType\": \"cardType\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"debit\": true,\n"
                + "    \"surcharging\": {\n"
                + "      \"allowed\": true,\n"
                + "      \"amount\": 87,\n"
                + "      \"percentage\": 3,\n"
                + "      \"disclosure\": \"A 3% surcharge is applied to cover processing fees.\"\n"
                + "    },\n"
                + "    \"type\": \"card\"\n"
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
