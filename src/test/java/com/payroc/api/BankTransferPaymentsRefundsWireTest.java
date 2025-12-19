package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.banktransferpayments.refunds.requests.BankTransferReferencedRefund;
import com.payroc.api.resources.banktransferpayments.refunds.requests.BankTransferUnreferencedRefund;
import com.payroc.api.resources.banktransferpayments.refunds.requests.RetrieveRefundsRequest;
import com.payroc.api.resources.banktransferpayments.refunds.requests.ReversePaymentRefundsRequest;
import com.payroc.api.resources.banktransferpayments.refunds.requests.ReverseRefundRefundsRequest;
import com.payroc.api.resources.banktransferpayments.refunds.types.BankTransferUnreferencedRefundRefundMethod;
import com.payroc.api.types.AchPayload;
import com.payroc.api.types.BankTransferCustomer;
import com.payroc.api.types.BankTransferCustomerNotificationLanguage;
import com.payroc.api.types.BankTransferPayment;
import com.payroc.api.types.BankTransferRefund;
import com.payroc.api.types.BankTransferRefundOrder;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.Currency;
import com.payroc.api.types.CustomField;
import java.util.Arrays;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTransferPaymentsRefundsWireTest {
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
    public void testReversePayment() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Large Pepperoni Pizza\",\"amount\":4999,\"currency\":\"USD\",\"breakdown\":{\"subtotal\":4347,\"tip\":{\"type\":\"percentage\",\"amount\":500,\"percentage\":10},\"taxes\":[{\"name\":\"VAT\",\"rate\":5,\"amount\":217}]}},\"customer\":{\"notificationLanguage\":\"en\",\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"bankAccount\":{\"nameOnAccount\":\"Sarah Hazel Hopper\",\"accountNumber\":\"1234567890\",\"transitNumber\":\"76543\",\"institutionNumber\":\"543\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"type\":\"pad\"},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"returns\":[{\"paymentId\":\"M2MJOG6O2Y\",\"date\":\"2024-07-02\",\"returnCode\":\"R11\",\"returnReason\":\"Customer advises not authorized\",\"represented\":false,\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"representment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"transactionResult\":{\"type\":\"payment\",\"status\":\"reversal\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"Payment Approved\",\"processorResponseCode\":\"A\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        BankTransferPayment response = client.bankTransferPayments()
                .refunds()
                .reversePayment(
                        "M2MJOG6O2Y",
                        ReversePaymentRefundsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate headers
        Assertions.assertEquals(
                "8e03978e-40d5-43e8-bc93-6894a57f9324",
                request.getHeader("Idempotency-Key"),
                "Header 'Idempotency-Key' should match expected value");

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 500,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"VAT\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 217\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"bankAccount\": {\n"
                + "    \"nameOnAccount\": \"Sarah Hazel Hopper\",\n"
                + "    \"accountNumber\": \"1234567890\",\n"
                + "    \"transitNumber\": \"76543\",\n"
                + "    \"institutionNumber\": \"543\",\n"
                + "    \"secureToken\": {\n"
                + "      \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "      \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "      \"token\": \"296753123456\",\n"
                + "      \"status\": \"notValidated\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"type\": \"pad\"\n"
                + "  },\n"
                + "  \"refunds\": [\n"
                + "    {\n"
                + "      \"refundId\": \"CD3HN88U9F\",\n"
                + "      \"dateTime\": \"2024-07-14T12:25:00Z\",\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"amount\": 4999,\n"
                + "      \"status\": \"ready\",\n"
                + "      \"responseCode\": \"A\",\n"
                + "      \"responseMessage\": \"Transaction refunded\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"returns\": [\n"
                + "    {\n"
                + "      \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "      \"date\": \"2024-07-02\",\n"
                + "      \"returnCode\": \"R11\",\n"
                + "      \"returnReason\": \"Customer advises not authorized\",\n"
                + "      \"represented\": false,\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"representment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"payment\",\n"
                + "    \"status\": \"reversal\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Payment Approved\",\n"
                + "    \"processorResponseCode\": \"A\"\n"
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

    @Test
    public void testRefund() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Refund for order OrderRef6543\",\"amount\":4999,\"currency\":\"USD\",\"breakdown\":{\"subtotal\":4347,\"tip\":{\"type\":\"percentage\",\"amount\":435,\"percentage\":10},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":5,\"amount\":217}]}},\"customer\":{\"notificationLanguage\":\"en\",\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"bankAccount\":{\"nameOnAccount\":\"Sarah Hazel Hopper\",\"accountNumber\":\"1234567890\",\"transitNumber\":\"76543\",\"institutionNumber\":\"543\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"type\":\"pad\"},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"returns\":[{\"paymentId\":\"M2MJOG6O2Y\",\"date\":\"2024-07-02\",\"returnCode\":\"R11\",\"returnReason\":\"Customer advises not authorized\",\"represented\":false,\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"representment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"transactionResult\":{\"type\":\"payment\",\"status\":\"reversal\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"Payment Approved\",\"processorResponseCode\":\"A\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        BankTransferPayment response = client.bankTransferPayments()
                .refunds()
                .refund(
                        "M2MJOG6O2Y",
                        BankTransferReferencedRefund.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .amount(4999L)
                                .description("amount to refund")
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
        String expectedRequestBody =
                "" + "{\n" + "  \"amount\": 4999,\n" + "  \"description\": \"amount to refund\"\n" + "}";
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
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Refund for order OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 435,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 5,\n"
                + "          \"amount\": 217\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"bankAccount\": {\n"
                + "    \"nameOnAccount\": \"Sarah Hazel Hopper\",\n"
                + "    \"accountNumber\": \"1234567890\",\n"
                + "    \"transitNumber\": \"76543\",\n"
                + "    \"institutionNumber\": \"543\",\n"
                + "    \"secureToken\": {\n"
                + "      \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "      \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "      \"token\": \"296753123456\",\n"
                + "      \"status\": \"notValidated\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"type\": \"pad\"\n"
                + "  },\n"
                + "  \"refunds\": [\n"
                + "    {\n"
                + "      \"refundId\": \"CD3HN88U9F\",\n"
                + "      \"dateTime\": \"2024-07-14T12:25:00Z\",\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"amount\": 4999,\n"
                + "      \"status\": \"ready\",\n"
                + "      \"responseCode\": \"A\",\n"
                + "      \"responseMessage\": \"Transaction refunded\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"returns\": [\n"
                + "    {\n"
                + "      \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "      \"date\": \"2024-07-02\",\n"
                + "      \"returnCode\": \"R11\",\n"
                + "      \"returnReason\": \"Customer advises not authorized\",\n"
                + "      \"represented\": false,\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"representment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"payment\",\n"
                + "    \"status\": \"reversal\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Payment Approved\",\n"
                + "    \"processorResponseCode\": \"A\"\n"
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

    @Test
    public void testCreate() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"refundId\":\"CD3HN88U9F\",\"processingTerminalId\":\"1234001\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Refund for order OrderRef6543\",\"amount\":4999,\"currency\":\"USD\"},\"customer\":{\"notificationLanguage\":\"en\",\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"bankAccount\":{\"secCode\":\"web\",\"nameOnAccount\":\"Sarah Hazel Hopper\",\"accountNumber\":\"123456789\",\"routingNumber\":\"123456789\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"type\":\"ach\"},\"payment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"transactionResult\":{\"type\":\"unreferencedRefund\",\"status\":\"ready\",\"authorizedAmount\":-4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"NoError\",\"processorResponseCode\":\"0\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        BankTransferRefund response = client.bankTransferPayments()
                .refunds()
                .create(BankTransferUnreferencedRefund.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .processingTerminalId("1234001")
                        .order(BankTransferRefundOrder.builder()
                                .orderId("OrderRef6543")
                                .description("Refund for order OrderRef6543")
                                .amount(4999L)
                                .currency(Currency.USD)
                                .build())
                        .refundMethod(BankTransferUnreferencedRefundRefundMethod.ach(AchPayload.builder()
                                .nameOnAccount("Shara Hazel Hopper")
                                .accountNumber("1234567890")
                                .routingNumber("123456789")
                                .build()))
                        .customer(BankTransferCustomer.builder()
                                .notificationLanguage(BankTransferCustomerNotificationLanguage.EN)
                                .contactMethods(
                                        Optional.of(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                .value("jane.doe@example.com")
                                                .build()))))
                                .build())
                        .customFields(Optional.of(Arrays.asList(CustomField.builder()
                                .name("yourCustomField")
                                .value("abc123")
                                .build())))
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
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"description\": \"Refund for order OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\"\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"refundMethod\": {\n"
                + "    \"nameOnAccount\": \"Shara Hazel Hopper\",\n"
                + "    \"accountNumber\": \"1234567890\",\n"
                + "    \"routingNumber\": \"123456789\",\n"
                + "    \"type\": \"ach\"\n"
                + "  },\n"
                + "  \"customFields\": [\n"
                + "    {\n"
                + "      \"name\": \"yourCustomField\",\n"
                + "      \"value\": \"abc123\"\n"
                + "    }\n"
                + "  ]\n"
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
                + "  \"refundId\": \"CD3HN88U9F\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Refund for order OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\"\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"bankAccount\": {\n"
                + "    \"secCode\": \"web\",\n"
                + "    \"nameOnAccount\": \"Sarah Hazel Hopper\",\n"
                + "    \"accountNumber\": \"123456789\",\n"
                + "    \"routingNumber\": \"123456789\",\n"
                + "    \"secureToken\": {\n"
                + "      \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "      \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "      \"token\": \"296753123456\",\n"
                + "      \"status\": \"notValidated\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"type\": \"ach\"\n"
                + "  },\n"
                + "  \"payment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"unreferencedRefund\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"authorizedAmount\": -4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"NoError\",\n"
                + "    \"processorResponseCode\": \"0\"\n"
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

    @Test
    public void testRetrieve() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"refundId\":\"CD3HN88U9F\",\"processingTerminalId\":\"1234001\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Refund for order OrderRef6543\",\"amount\":4999,\"currency\":\"USD\"},\"customer\":{\"notificationLanguage\":\"en\",\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"bankAccount\":{\"secCode\":\"web\",\"nameOnAccount\":\"Sarah Hazel Hopper\",\"accountNumber\":\"123456789\",\"routingNumber\":\"123456789\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"type\":\"ach\"},\"payment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"transactionResult\":{\"type\":\"unreferencedRefund\",\"status\":\"ready\",\"authorizedAmount\":-4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"NoError\",\"processorResponseCode\":\"0\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        BankTransferRefund response = client.bankTransferPayments()
                .refunds()
                .retrieve("CD3HN88U9F", RetrieveRefundsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"refundId\": \"CD3HN88U9F\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Refund for order OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\"\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"bankAccount\": {\n"
                + "    \"secCode\": \"web\",\n"
                + "    \"nameOnAccount\": \"Sarah Hazel Hopper\",\n"
                + "    \"accountNumber\": \"123456789\",\n"
                + "    \"routingNumber\": \"123456789\",\n"
                + "    \"secureToken\": {\n"
                + "      \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "      \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "      \"token\": \"296753123456\",\n"
                + "      \"status\": \"notValidated\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"type\": \"ach\"\n"
                + "  },\n"
                + "  \"payment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"unreferencedRefund\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"authorizedAmount\": -4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"NoError\",\n"
                + "    \"processorResponseCode\": \"0\"\n"
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

    @Test
    public void testReverseRefund() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"refundId\":\"CD3HN88U9F\",\"processingTerminalId\":\"1234001\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Refund for order OrderRef6543\",\"amount\":4999,\"currency\":\"USD\"},\"customer\":{\"notificationLanguage\":\"en\",\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"bankAccount\":{\"secCode\":\"web\",\"nameOnAccount\":\"Sarah Hazel Hopper\",\"accountNumber\":\"123456789\",\"routingNumber\":\"123456789\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"type\":\"ach\"},\"payment\":{\"paymentId\":\"M2MJOG6O2Y\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction approved\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"transactionResult\":{\"type\":\"unreferencedRefund\",\"status\":\"reversal\",\"authorizedAmount\":-4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"NoError\",\"processorResponseCode\":\"0\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        BankTransferRefund response = client.bankTransferPayments()
                .refunds()
                .reverseRefund(
                        "CD3HN88U9F",
                        ReverseRefundRefundsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate headers
        Assertions.assertEquals(
                "8e03978e-40d5-43e8-bc93-6894a57f9324",
                request.getHeader("Idempotency-Key"),
                "Header 'Idempotency-Key' should match expected value");

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"refundId\": \"CD3HN88U9F\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Refund for order OrderRef6543\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\"\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"notificationLanguage\": \"en\",\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"bankAccount\": {\n"
                + "    \"secCode\": \"web\",\n"
                + "    \"nameOnAccount\": \"Sarah Hazel Hopper\",\n"
                + "    \"accountNumber\": \"123456789\",\n"
                + "    \"routingNumber\": \"123456789\",\n"
                + "    \"secureToken\": {\n"
                + "      \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "      \"customerName\": \"Sarah Hazel Hopper\",\n"
                + "      \"token\": \"296753123456\",\n"
                + "      \"status\": \"notValidated\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"previous\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"<uri>\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"type\": \"ach\"\n"
                + "  },\n"
                + "  \"payment\": {\n"
                + "    \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"status\": \"ready\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"Transaction approved\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"unreferencedRefund\",\n"
                + "    \"status\": \"reversal\",\n"
                + "    \"authorizedAmount\": -4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"NoError\",\n"
                + "    \"processorResponseCode\": \"0\"\n"
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
