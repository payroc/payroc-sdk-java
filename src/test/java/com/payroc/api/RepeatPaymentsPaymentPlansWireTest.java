package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.repeatpayments.paymentplans.requests.CreatePaymentPlansRequest;
import com.payroc.api.resources.repeatpayments.paymentplans.requests.DeletePaymentPlansRequest;
import com.payroc.api.resources.repeatpayments.paymentplans.requests.PartiallyUpdatePaymentPlansRequest;
import com.payroc.api.resources.repeatpayments.paymentplans.requests.RetrievePaymentPlansRequest;
import com.payroc.api.types.Currency;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.PaymentPlan;
import com.payroc.api.types.PaymentPlanBaseFrequency;
import com.payroc.api.types.PaymentPlanBaseOnDelete;
import com.payroc.api.types.PaymentPlanBaseOnUpdate;
import com.payroc.api.types.PaymentPlanBaseType;
import com.payroc.api.types.PaymentPlanOrderBreakdown;
import com.payroc.api.types.PaymentPlanRecurringOrder;
import com.payroc.api.types.PaymentPlanSetupOrder;
import com.payroc.api.types.RetrievedTax;
import java.util.Arrays;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepeatPaymentsPaymentPlansWireTest {
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
                                "{\"paymentPlanId\":\"PlanRef8765\",\"processingTerminalId\":\"1234001\",\"name\":\"Premium Club\",\"description\":\"Monthly Premium Club subscription\",\"currency\":\"USD\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"onUpdate\":\"continue\",\"onDelete\":\"complete\",\"customFieldNames\":[\"yourCustomField\"],\"setupOrder\":{\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Monthly Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}}}"));
        PaymentPlan response = client.repeatPayments()
                .paymentPlans()
                .create(
                        "1234001",
                        CreatePaymentPlansRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(PaymentPlan.builder()
                                        .paymentPlanId("PlanRef8765")
                                        .name("Premium Club")
                                        .currency(Currency.USD)
                                        .type(PaymentPlanBaseType.AUTOMATIC)
                                        .frequency(PaymentPlanBaseFrequency.MONTHLY)
                                        .onUpdate(PaymentPlanBaseOnUpdate.CONTINUE)
                                        .onDelete(PaymentPlanBaseOnDelete.COMPLETE)
                                        .description("Monthly Premium Club subscription")
                                        .length(12)
                                        .customFieldNames(Optional.of(Arrays.asList("yourCustomField")))
                                        .setupOrder(PaymentPlanSetupOrder.builder()
                                                .amount(4999L)
                                                .description("Initial setup fee for Premium Club subscription")
                                                .breakdown(PaymentPlanOrderBreakdown.builder()
                                                        .subtotal(4347L)
                                                        .taxes(Optional.of(Arrays.asList(RetrievedTax.builder()
                                                                .name("Sales Tax")
                                                                .rate(5.0)
                                                                .build())))
                                                        .build())
                                                .build())
                                        .recurringOrder(PaymentPlanRecurringOrder.builder()
                                                .amount(4999L)
                                                .description("Monthly Premium Club subscription")
                                                .breakdown(PaymentPlanOrderBreakdown.builder()
                                                        .subtotal(4347L)
                                                        .taxes(Optional.of(Arrays.asList(RetrievedTax.builder()
                                                                .name("Sales Tax")
                                                                .rate(5.0)
                                                                .build())))
                                                        .build())
                                                .build())
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
                + "  \"paymentPlanId\": \"PlanRef8765\",\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Monthly Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"onUpdate\": \"continue\",\n"
                + "  \"onDelete\": \"complete\",\n"
                + "  \"customFieldNames\": [\n"
                + "    \"yourCustomField\"\n"
                + "  ],\n"
                + "  \"setupOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"recurringOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
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
                + "  \"paymentPlanId\": \"PlanRef8765\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Monthly Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"onUpdate\": \"continue\",\n"
                + "  \"onDelete\": \"complete\",\n"
                + "  \"customFieldNames\": [\n"
                + "    \"yourCustomField\"\n"
                + "  ],\n"
                + "  \"setupOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"recurringOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
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
                                "{\"paymentPlanId\":\"PlanRef8765\",\"processingTerminalId\":\"1234001\",\"name\":\"Premium Club\",\"description\":\"Monthly Premium Club subscription\",\"currency\":\"USD\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"onUpdate\":\"continue\",\"onDelete\":\"complete\",\"customFieldNames\":[\"yourCustomField\"],\"setupOrder\":{\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Monthly Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}}}"));
        PaymentPlan response = client.repeatPayments()
                .paymentPlans()
                .retrieve(
                        "1234001",
                        "PlanRef8765",
                        RetrievePaymentPlansRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"paymentPlanId\": \"PlanRef8765\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Monthly Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"onUpdate\": \"continue\",\n"
                + "  \"onDelete\": \"complete\",\n"
                + "  \"customFieldNames\": [\n"
                + "    \"yourCustomField\"\n"
                + "  ],\n"
                + "  \"setupOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"recurringOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
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
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.repeatPayments()
                .paymentPlans()
                .delete(
                        "1234001",
                        "PlanRef8765",
                        DeletePaymentPlansRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testPartiallyUpdate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"paymentPlanId\":\"PlanRef8765\",\"processingTerminalId\":\"1234001\",\"name\":\"Premium Club\",\"description\":\"Monthly Premium Club subscription\",\"currency\":\"USD\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"onUpdate\":\"continue\",\"onDelete\":\"complete\",\"customFieldNames\":[\"yourCustomField\"],\"setupOrder\":{\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Monthly Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}}}"));
        PaymentPlan response = client.repeatPayments()
                .paymentPlans()
                .partiallyUpdate(
                        "1234001",
                        "PlanRef8765",
                        PartiallyUpdatePaymentPlansRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(Arrays.asList(
                                        PatchDocument.remove(PatchRemove.builder()
                                                .path("path")
                                                .build()),
                                        PatchDocument.remove(PatchRemove.builder()
                                                .path("path")
                                                .build()),
                                        PatchDocument.remove(PatchRemove.builder()
                                                .path("path")
                                                .build())))
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PATCH", request.getMethod());

        // Validate headers
        Assertions.assertEquals(
                "8e03978e-40d5-43e8-bc93-6894a57f9324",
                request.getHeader("Idempotency-Key"),
                "Header 'Idempotency-Key' should match expected value");
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "[\n"
                + "  {\n"
                + "    \"path\": \"path\",\n"
                + "    \"op\": \"remove\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"path\": \"path\",\n"
                + "    \"op\": \"remove\"\n"
                + "  },\n"
                + "  {\n"
                + "    \"path\": \"path\",\n"
                + "    \"op\": \"remove\"\n"
                + "  }\n"
                + "]";
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
                + "  \"paymentPlanId\": \"PlanRef8765\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Monthly Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"onUpdate\": \"continue\",\n"
                + "  \"onDelete\": \"complete\",\n"
                + "  \"customFieldNames\": [\n"
                + "    \"yourCustomField\"\n"
                + "  ],\n"
                + "  \"setupOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"recurringOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
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
