package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.banktransferpayments.payments.requests.BankTransferPaymentRequest;
import com.payroc.api.resources.banktransferpayments.payments.requests.Representment;
import com.payroc.api.resources.banktransferpayments.payments.requests.RetrievePaymentsRequest;
import com.payroc.api.resources.banktransferpayments.payments.types.BankTransferPaymentRequestPaymentMethod;
import com.payroc.api.resources.banktransferpayments.payments.types.RepresentmentPaymentMethod;
import com.payroc.api.types.AchPayload;
import com.payroc.api.types.BankTransferCustomer;
import com.payroc.api.types.BankTransferCustomerNotificationLanguage;
import com.payroc.api.types.BankTransferPayment;
import com.payroc.api.types.BankTransferPaymentRequestOrder;
import com.payroc.api.types.BankTransferRequestBreakdown;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.Currency;
import com.payroc.api.types.CustomField;
import com.payroc.api.types.SchemasCredentialOnFile;
import com.payroc.api.types.TaxRate;
import com.payroc.api.types.TaxRateType;
import com.payroc.api.types.Tip;
import com.payroc.api.types.TipType;
import java.util.Arrays;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BankTransferPaymentsPaymentsWireTest {
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BankTransferPaymentsPaymentsWireTest_testCreate_response.json")));
        BankTransferPayment response = client.bankTransferPayments()
                .payments()
                .create(BankTransferPaymentRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .processingTerminalId("1234001")
                        .order(BankTransferPaymentRequestOrder.builder()
                                .orderId("OrderRef6543")
                                .description("Large Pepperoni Pizza")
                                .amount(4999L)
                                .currency(Currency.USD)
                                .breakdown(BankTransferRequestBreakdown.builder()
                                        .subtotal(4347L)
                                        .tip(Tip.builder()
                                                .type(TipType.PERCENTAGE)
                                                .percentage(10.0)
                                                .build())
                                        .taxes(Optional.of(Arrays.asList(TaxRate.builder()
                                                .type(TaxRateType.RATE)
                                                .rate(5.0)
                                                .name("Sales Tax")
                                                .build())))
                                        .build())
                                .build())
                        .paymentMethod(BankTransferPaymentRequestPaymentMethod.ach(AchPayload.builder()
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
                        .credentialOnFile(
                                SchemasCredentialOnFile.builder().tokenize(true).build())
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
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 4347,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"type\": \"rate\",\n"
                + "          \"rate\": 5,\n"
                + "          \"name\": \"Sales Tax\"\n"
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
                + "  \"credentialOnFile\": {\n"
                + "    \"tokenize\": true\n"
                + "  },\n"
                + "  \"paymentMethod\": {\n"
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
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/BankTransferPaymentsPaymentsWireTest_testCreate_response.json");
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BankTransferPaymentsPaymentsWireTest_testRetrieve_response.json")));
        BankTransferPayment response = client.bankTransferPayments()
                .payments()
                .retrieve("M2MJOG6O2Y", RetrievePaymentsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BankTransferPaymentsPaymentsWireTest_testRetrieve_response.json");
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
    public void testRepresent() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BankTransferPaymentsPaymentsWireTest_testRepresent_response.json")));
        BankTransferPayment response = client.bankTransferPayments()
                .payments()
                .represent(
                        "M2MJOG6O2Y",
                        Representment.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .paymentMethod(RepresentmentPaymentMethod.ach(AchPayload.builder()
                                        .nameOnAccount("Shara Hazel Hopper")
                                        .accountNumber("1234567890")
                                        .routingNumber("123456789")
                                        .build()))
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
                + "  \"paymentMethod\": {\n"
                + "    \"nameOnAccount\": \"Shara Hazel Hopper\",\n"
                + "    \"accountNumber\": \"1234567890\",\n"
                + "    \"routingNumber\": \"123456789\",\n"
                + "    \"type\": \"ach\"\n"
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
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BankTransferPaymentsPaymentsWireTest_testRepresent_response.json");
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
