package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.funding.fundinginstructions.requests.CreateFundingInstructionsRequest;
import com.payroc.api.resources.funding.fundinginstructions.requests.DeleteFundingInstructionsRequest;
import com.payroc.api.resources.funding.fundinginstructions.requests.RetrieveFundingInstructionsRequest;
import com.payroc.api.resources.funding.fundinginstructions.requests.UpdateFundingInstructionsRequest;
import com.payroc.api.types.Instruction;
import com.payroc.api.types.InstructionMerchantsItem;
import com.payroc.api.types.InstructionMerchantsItemRecipientsItem;
import com.payroc.api.types.InstructionMerchantsItemRecipientsItemAmount;
import com.payroc.api.types.InstructionMerchantsItemRecipientsItemAmountCurrency;
import com.payroc.api.types.InstructionMerchantsItemRecipientsItemPaymentMethod;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FundingFundingInstructionsWireTest {
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
                                "{\"instructionId\":64643131,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"accepted\",\"merchants\":[{\"merchantId\":\"4525644354\",\"recipients\":[{\"fundingAccountId\":123,\"paymentMethod\":\"ACH\",\"amount\":{\"value\":120000,\"currency\":\"USD\"},\"status\":\"accepted\",\"metadata\":{\"yourCustomField\":\"abc123\"},\"link\":{\"rel\":\"fundingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\"}}],\"link\":{\"rel\":\"merchant\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/4525644354\"}}],\"metadata\":{\"instructionRef\":\"abc123\"}}"));
        Instruction response = client.funding()
                .fundingInstructions()
                .create(CreateFundingInstructionsRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .body(Instruction.builder()
                                .merchants(Optional.of(Arrays.asList(InstructionMerchantsItem.builder()
                                        .merchantId("4525644354")
                                        .recipients(Arrays.asList(InstructionMerchantsItemRecipientsItem.builder()
                                                .fundingAccountId(123)
                                                .paymentMethod(InstructionMerchantsItemRecipientsItemPaymentMethod.ACH)
                                                .amount(InstructionMerchantsItemRecipientsItemAmount.builder()
                                                        .value(120000)
                                                        .currency(
                                                                InstructionMerchantsItemRecipientsItemAmountCurrency
                                                                        .USD)
                                                        .build())
                                                .metadata(new HashMap<String, String>() {
                                                    {
                                                        put("yourCustomField", "abc123");
                                                    }
                                                })
                                                .build()))
                                        .build())))
                                .metadata(new HashMap<String, String>() {
                                    {
                                        put("yourCustomField", "abc123");
                                    }
                                })
                                .build())
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
                + "  \"merchants\": [\n"
                + "    {\n"
                + "      \"merchantId\": \"4525644354\",\n"
                + "      \"recipients\": [\n"
                + "        {\n"
                + "          \"fundingAccountId\": 123,\n"
                + "          \"paymentMethod\": \"ACH\",\n"
                + "          \"amount\": {\n"
                + "            \"value\": 120000,\n"
                + "            \"currency\": \"USD\"\n"
                + "          },\n"
                + "          \"metadata\": {\n"
                + "            \"yourCustomField\": \"abc123\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
                + "  \"instructionId\": 64643131,\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"status\": \"accepted\",\n"
                + "  \"merchants\": [\n"
                + "    {\n"
                + "      \"merchantId\": \"4525644354\",\n"
                + "      \"recipients\": [\n"
                + "        {\n"
                + "          \"fundingAccountId\": 123,\n"
                + "          \"paymentMethod\": \"ACH\",\n"
                + "          \"amount\": {\n"
                + "            \"value\": 120000,\n"
                + "            \"currency\": \"USD\"\n"
                + "          },\n"
                + "          \"status\": \"accepted\",\n"
                + "          \"metadata\": {\n"
                + "            \"yourCustomField\": \"abc123\"\n"
                + "          },\n"
                + "          \"link\": {\n"
                + "            \"rel\": \"fundingAccount\",\n"
                + "            \"method\": \"get\",\n"
                + "            \"href\": \"https://api.payroc.com/v1/funding-accounts/123\"\n"
                + "          }\n"
                + "        }\n"
                + "      ],\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"merchant\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/4525644354\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"instructionRef\": \"abc123\"\n"
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

    @Test
    public void testRetrieve() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"instructionId\":64643131,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"accepted\",\"merchants\":[{\"merchantId\":\"4525644354\",\"recipients\":[{\"fundingAccountId\":123,\"paymentMethod\":\"ACH\",\"amount\":{\"value\":120000},\"metadata\":{\"yourCustomField\":\"abc123\"},\"link\":{\"rel\":\"fundingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\"}}],\"link\":{\"rel\":\"merchant\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/4525644354\"}}],\"metadata\":{\"yourCustomField\":\"abc123\"}}"));
        Instruction response = client.funding()
                .fundingInstructions()
                .retrieve(1, RetrieveFundingInstructionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"instructionId\": 64643131,\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"status\": \"accepted\",\n"
                + "  \"merchants\": [\n"
                + "    {\n"
                + "      \"merchantId\": \"4525644354\",\n"
                + "      \"recipients\": [\n"
                + "        {\n"
                + "          \"fundingAccountId\": 123,\n"
                + "          \"paymentMethod\": \"ACH\",\n"
                + "          \"amount\": {\n"
                + "            \"value\": 120000\n"
                + "          },\n"
                + "          \"metadata\": {\n"
                + "            \"yourCustomField\": \"abc123\"\n"
                + "          },\n"
                + "          \"link\": {\n"
                + "            \"rel\": \"fundingAccount\",\n"
                + "            \"method\": \"get\",\n"
                + "            \"href\": \"https://api.payroc.com/v1/funding-accounts/123\"\n"
                + "          }\n"
                + "        }\n"
                + "      ],\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"merchant\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/4525644354\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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

    @Test
    public void testUpdate() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.funding()
                .fundingInstructions()
                .update(
                        1,
                        UpdateFundingInstructionsRequest.builder()
                                .body(Instruction.builder()
                                        .merchants(Optional.of(Arrays.asList(InstructionMerchantsItem.builder()
                                                .merchantId("9876543219")
                                                .recipients(
                                                        Arrays.asList(InstructionMerchantsItemRecipientsItem.builder()
                                                                .fundingAccountId(124)
                                                                .paymentMethod(
                                                                        InstructionMerchantsItemRecipientsItemPaymentMethod
                                                                                .ACH)
                                                                .amount(
                                                                        InstructionMerchantsItemRecipientsItemAmount
                                                                                .builder()
                                                                                .value(69950)
                                                                                .currency(
                                                                                        InstructionMerchantsItemRecipientsItemAmountCurrency
                                                                                                .USD)
                                                                                .build())
                                                                .metadata(new HashMap<String, String>() {
                                                                    {
                                                                        put("supplier", "IT Support Services");
                                                                    }
                                                                })
                                                                .build()))
                                                .build())))
                                        .metadata(new HashMap<String, String>() {
                                            {
                                                put("instructionCreatedBy", "Jane Doe");
                                            }
                                        })
                                        .build())
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PUT", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"merchants\": [\n"
                + "    {\n"
                + "      \"merchantId\": \"9876543219\",\n"
                + "      \"recipients\": [\n"
                + "        {\n"
                + "          \"fundingAccountId\": 124,\n"
                + "          \"paymentMethod\": \"ACH\",\n"
                + "          \"amount\": {\n"
                + "            \"value\": 69950,\n"
                + "            \"currency\": \"USD\"\n"
                + "          },\n"
                + "          \"metadata\": {\n"
                + "            \"supplier\": \"IT Support Services\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"instructionCreatedBy\": \"Jane Doe\"\n"
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
    }

    @Test
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.funding()
                .fundingInstructions()
                .delete(1, DeleteFundingInstructionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
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
