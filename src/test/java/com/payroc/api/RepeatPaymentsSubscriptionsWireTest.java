package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.DeactivateSubscriptionsRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.PartiallyUpdateSubscriptionsRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.ReactivateSubscriptionsRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.RetrieveSubscriptionsRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.SubscriptionPaymentRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.requests.SubscriptionRequest;
import com.payroc.api.resources.repeatpayments.subscriptions.types.SubscriptionRequestPaymentMethod;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.SecureTokenPayload;
import com.payroc.api.types.Subscription;
import com.payroc.api.types.SubscriptionOrderBreakdownRequest;
import com.payroc.api.types.SubscriptionPayment;
import com.payroc.api.types.SubscriptionPaymentOrder;
import com.payroc.api.types.SubscriptionPaymentOrderRequest;
import com.payroc.api.types.SubscriptionRecurringOrderRequest;
import com.payroc.api.types.TaxRate;
import com.payroc.api.types.TaxRateType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepeatPaymentsSubscriptionsWireTest {
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
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"paymentPlan\":{\"paymentPlanId\":\"PlanRef8765\",\"name\":\"Monthly Premium Club subscription\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"name\":\"Premium Club\",\"description\":\"Premium Club subscription\",\"currency\":\"USD\",\"setupOrder\":{\"orderId\":\"OrderRef6543\",\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"currentState\":{\"status\":\"active\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":0,\"outstandingInvoices\":3},\"startDate\":\"2024-07-02\",\"endDate\":\"2025-07-01\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"pauseCollectionFor\":0,\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Subscription response = client.repeatPayments()
                .subscriptions()
                .create(
                        "1234001",
                        SubscriptionRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .subscriptionId("SubRef7654")
                                .paymentPlanId("PlanRef8765")
                                .paymentMethod(SubscriptionRequestPaymentMethod.secureToken(SecureTokenPayload.builder()
                                        .token("1234567890123456789")
                                        .build()))
                                .startDate(LocalDate.parse("2024-07-02"))
                                .name("Premium Club")
                                .description("Premium Club subscription")
                                .setupOrder(SubscriptionPaymentOrderRequest.builder()
                                        .orderId("OrderRef6543")
                                        .amount(4999L)
                                        .description("Initial setup fee for Premium Club subscription")
                                        .build())
                                .recurringOrder(SubscriptionRecurringOrderRequest.builder()
                                        .amount(4999L)
                                        .description("Monthly Premium Club subscription")
                                        .breakdown(SubscriptionOrderBreakdownRequest.builder()
                                                .subtotal(4347L)
                                                .taxes(Optional.of(Arrays.asList(TaxRate.builder()
                                                        .type(TaxRateType.RATE)
                                                        .rate(5.0)
                                                        .name("Sales Tax")
                                                        .build())))
                                                .build())
                                        .build())
                                .endDate(LocalDate.parse("2025-07-01"))
                                .length(12)
                                .pauseCollectionFor(0)
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
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"paymentPlanId\": \"PlanRef8765\",\n"
                + "  \"paymentMethod\": {\n"
                + "    \"token\": \"1234567890123456789\",\n"
                + "    \"type\": \"secureToken\"\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Premium Club subscription\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\"\n"
                + "  },\n"
                + "  \"recurringOrder\": {\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"type\": \"rate\",\n"
                + "          \"rate\": 5,\n"
                + "          \"name\": \"Sales Tax\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"pauseCollectionFor\": 0\n"
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
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"paymentPlan\": {\n"
                + "    \"paymentPlanId\": \"PlanRef8765\",\n"
                + "    \"name\": \"Monthly Premium Club subscription\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
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
                + "    \"description\": \"Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"active\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 0,\n"
                + "    \"outstandingInvoices\": 3\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"pauseCollectionFor\": 0,\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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

    @Test
    public void testRetrieve() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"paymentPlan\":{\"paymentPlanId\":\"PlanRef8765\",\"name\":\"Monthly Premium Club subscription\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"name\":\"Premium Club\",\"description\":\"Premium Club subscription\",\"currency\":\"USD\",\"setupOrder\":{\"orderId\":\"OrderRef6543\",\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"currentState\":{\"status\":\"active\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":0,\"outstandingInvoices\":3},\"startDate\":\"2024-07-02\",\"endDate\":\"2025-07-01\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"pauseCollectionFor\":0,\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Subscription response = client.repeatPayments()
                .subscriptions()
                .retrieve(
                        "1234001",
                        "SubRef7654",
                        RetrieveSubscriptionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"paymentPlan\": {\n"
                + "    \"paymentPlanId\": \"PlanRef8765\",\n"
                + "    \"name\": \"Monthly Premium Club subscription\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
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
                + "    \"description\": \"Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"active\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 0,\n"
                + "    \"outstandingInvoices\": 3\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"pauseCollectionFor\": 0,\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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

    @Test
    public void testPartiallyUpdate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"paymentPlan\":{\"paymentPlanId\":\"PlanRef8765\",\"name\":\"Monthly Premium Club subscription\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"name\":\"Premium Club\",\"description\":\"Premium Club subscription\",\"currency\":\"USD\",\"setupOrder\":{\"orderId\":\"OrderRef6543\",\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"currentState\":{\"status\":\"active\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":0,\"outstandingInvoices\":3},\"startDate\":\"2024-07-02\",\"endDate\":\"2025-07-01\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"pauseCollectionFor\":0,\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Subscription response = client.repeatPayments()
                .subscriptions()
                .partiallyUpdate(
                        "1234001",
                        "SubRef7654",
                        PartiallyUpdateSubscriptionsRequest.builder()
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
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"paymentPlan\": {\n"
                + "    \"paymentPlanId\": \"PlanRef8765\",\n"
                + "    \"name\": \"Monthly Premium Club subscription\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
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
                + "    \"description\": \"Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"active\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 0,\n"
                + "    \"outstandingInvoices\": 3\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"pauseCollectionFor\": 0,\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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

    @Test
    public void testDeactivate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"paymentPlan\":{\"paymentPlanId\":\"PlanRef8765\",\"name\":\"Monthly Premium Club subscription\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"name\":\"Premium Club\",\"description\":\"Monthly Premium Club subscription\",\"currency\":\"USD\",\"setupOrder\":{\"orderId\":\"OrderRef6543\",\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Monthly Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"currentState\":{\"status\":\"cancelled\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":0,\"outstandingInvoices\":3},\"startDate\":\"2024-07-02\",\"endDate\":\"2025-07-01\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"pauseCollectionFor\":0,\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Subscription response = client.repeatPayments()
                .subscriptions()
                .deactivate(
                        "1234001",
                        "SubRef7654",
                        DeactivateSubscriptionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"paymentPlan\": {\n"
                + "    \"paymentPlanId\": \"PlanRef8765\",\n"
                + "    \"name\": \"Monthly Premium Club subscription\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Monthly Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
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
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"cancelled\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 0,\n"
                + "    \"outstandingInvoices\": 3\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"pauseCollectionFor\": 0,\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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

    @Test
    public void testReactivate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"paymentPlan\":{\"paymentPlanId\":\"PlanRef8765\",\"name\":\"Monthly Premium Club subscription\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"name\":\"Premium Club\",\"description\":\"Premium Club subscription\",\"currency\":\"USD\",\"setupOrder\":{\"orderId\":\"OrderRef6543\",\"amount\":4999,\"description\":\"Initial setup fee for Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"recurringOrder\":{\"amount\":4999,\"description\":\"Premium Club subscription\",\"breakdown\":{\"subtotal\":4347,\"convenienceFee\":{\"amount\":25},\"surcharge\":{\"bypass\":false,\"amount\":217,\"percentage\":5},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":190}]}},\"currentState\":{\"status\":\"active\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":0,\"outstandingInvoices\":3},\"startDate\":\"2024-07-02\",\"endDate\":\"2025-07-01\",\"length\":12,\"type\":\"automatic\",\"frequency\":\"monthly\",\"pauseCollectionFor\":0,\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Subscription response = client.repeatPayments()
                .subscriptions()
                .reactivate(
                        "1234001",
                        "SubRef7654",
                        ReactivateSubscriptionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"paymentPlan\": {\n"
                + "    \"paymentPlanId\": \"PlanRef8765\",\n"
                + "    \"name\": \"Monthly Premium Club subscription\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/payment-plans/PlanRef8765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"name\": \"Premium Club\",\n"
                + "  \"description\": \"Premium Club subscription\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"setupOrder\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Initial setup fee for Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
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
                + "    \"description\": \"Premium Club subscription\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 217,\n"
                + "        \"percentage\": 5\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"active\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 0,\n"
                + "    \"outstandingInvoices\": 3\n"
                + "  },\n"
                + "  \"startDate\": \"2024-07-02\",\n"
                + "  \"endDate\": \"2025-07-01\",\n"
                + "  \"length\": 12,\n"
                + "  \"type\": \"automatic\",\n"
                + "  \"frequency\": \"monthly\",\n"
                + "  \"pauseCollectionFor\": 0,\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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

    @Test
    public void testPay() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"subscriptionId\":\"SubRef7654\",\"processingTerminalId\":\"1234001\",\"payment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"USD\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/bank-transfer-payments/M2MJOG6O2Y\"}},\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"self\",\"method\":\"GET\",\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"}},\"currentState\":{\"status\":\"active\",\"nextDueDate\":\"2024-08-02\",\"paidInvoices\":1,\"outstandingInvoices\":2},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        SubscriptionPayment response = client.repeatPayments()
                .subscriptions()
                .pay(
                        "1234001",
                        "SubRef7654",
                        SubscriptionPaymentRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .order(SubscriptionPaymentOrder.builder()
                                        .orderId("OrderRef6543")
                                        .amount(4999L)
                                        .description("Monthly Premium Club subscription")
                                        .build())
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
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"description\": \"Monthly Premium Club subscription\"\n"
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
                + "  \"subscriptionId\": \"SubRef7654\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"payment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/bank-transfer-payments/M2MJOG6O2Y\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"secureToken\": {\n"
                + "    \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "    \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "    \"token\": \"296753123456\",\n"
                + "    \"status\": \"notValidated\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"self\",\n"
                + "      \"method\": \"GET\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001/secure-tokens/MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"currentState\": {\n"
                + "    \"status\": \"active\",\n"
                + "    \"nextDueDate\": \"2024-08-02\",\n"
                + "    \"paidInvoices\": 1,\n"
                + "    \"outstandingInvoices\": 2\n"
                + "  },\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
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
