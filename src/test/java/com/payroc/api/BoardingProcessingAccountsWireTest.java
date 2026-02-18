package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.processingaccounts.requests.CreateReminderProcessingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.requests.CreateTerminalOrder;
import com.payroc.api.resources.boarding.processingaccounts.requests.GetProcessingAccountPricingAgreementProcessingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.requests.ListContactsProcessingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.requests.ListProcessingAccountFundingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.requests.ListTerminalOrdersProcessingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.requests.RetrieveProcessingAccountsRequest;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateReminderProcessingAccountsRequestBody;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateReminderProcessingAccountsResponse;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateTerminalOrderShipping;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateTerminalOrderShippingAddress;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateTerminalOrderShippingPreferences;
import com.payroc.api.resources.boarding.processingaccounts.types.CreateTerminalOrderShippingPreferencesMethod;
import com.payroc.api.resources.boarding.processingaccounts.types.GetProcessingAccountPricingAgreementProcessingAccountsResponse;
import com.payroc.api.resources.boarding.processingaccounts.types.ListTerminalOrdersProcessingAccountsRequestStatus;
import com.payroc.api.types.AutomaticBatchClose;
import com.payroc.api.types.FundingAccount;
import com.payroc.api.types.OrderItem;
import com.payroc.api.types.OrderItemDeviceCondition;
import com.payroc.api.types.OrderItemSolutionSetup;
import com.payroc.api.types.OrderItemSolutionSetupApplicationSettings;
import com.payroc.api.types.OrderItemSolutionSetupApplicationSettingsSecurity;
import com.payroc.api.types.OrderItemSolutionSetupBatchClosure;
import com.payroc.api.types.OrderItemSolutionSetupDeviceSettings;
import com.payroc.api.types.OrderItemSolutionSetupDeviceSettingsCommunicationType;
import com.payroc.api.types.OrderItemSolutionSetupGatewaySettings;
import com.payroc.api.types.OrderItemSolutionSetupReceiptNotifications;
import com.payroc.api.types.OrderItemSolutionSetupTaxesItem;
import com.payroc.api.types.OrderItemSolutionSetupTips;
import com.payroc.api.types.OrderItemType;
import com.payroc.api.types.PaginatedContacts;
import com.payroc.api.types.PricingAgreementReminder;
import com.payroc.api.types.ProcessingAccount;
import com.payroc.api.types.SchemasTimezone;
import com.payroc.api.types.TerminalOrder;
import com.payroc.api.types.TrainingProvider;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingProcessingAccountsWireTest {
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
                        "/wire-tests/BoardingProcessingAccountsWireTest_testRetrieve_response.json")));
        ProcessingAccount response = client.boarding()
                .processingAccounts()
                .retrieve("38765", RetrieveProcessingAccountsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/BoardingProcessingAccountsWireTest_testRetrieve_response.json");
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
    public void testListProcessingAccountFundingAccounts() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                TestResources.loadResource(
                                        "/wire-tests/BoardingProcessingAccountsWireTest_testListProcessingAccountFundingAccounts_response.json")));
        List<FundingAccount> response = client.boarding()
                .processingAccounts()
                .listProcessingAccountFundingAccounts(
                        "38765",
                        ListProcessingAccountFundingAccountsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingAccountsWireTest_testListProcessingAccountFundingAccounts_response.json");
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
    public void testListContacts() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingProcessingAccountsWireTest_testListContacts_response.json")));
        PaginatedContacts response = client.boarding()
                .processingAccounts()
                .listContacts(
                        "38765",
                        ListContactsProcessingAccountsRequest.builder()
                                .before("2571")
                                .after("8516")
                                .limit(1)
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingAccountsWireTest_testListContacts_response.json");
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
    public void testGetProcessingAccountPricingAgreement() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                TestResources.loadResource(
                                        "/wire-tests/BoardingProcessingAccountsWireTest_testGetProcessingAccountPricingAgreement_response.json")));
        GetProcessingAccountPricingAgreementProcessingAccountsResponse response = client.boarding()
                .processingAccounts()
                .getProcessingAccountPricingAgreement(
                        "38765",
                        GetProcessingAccountPricingAgreementProcessingAccountsRequest.builder()
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingAccountsWireTest_testGetProcessingAccountPricingAgreement_response.json");
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
    public void testCreateReminder() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"reminderId\":\"1234567\",\"type\":\"pricingAgreement\"}"));
        CreateReminderProcessingAccountsResponse response = client.boarding()
                .processingAccounts()
                .createReminder(
                        "38765",
                        CreateReminderProcessingAccountsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(CreateReminderProcessingAccountsRequestBody.pricingAgreement(
                                        PricingAgreementReminder.builder().build()))
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
        String expectedRequestBody = "" + "{\n" + "  \"type\": \"pricingAgreement\"\n" + "}";
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
                "" + "{\n" + "  \"reminderId\": \"1234567\",\n" + "  \"type\": \"pricingAgreement\"\n" + "}";
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
    public void testListTerminalOrders() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingProcessingAccountsWireTest_testListTerminalOrders_response.json")));
        List<TerminalOrder> response = client.boarding()
                .processingAccounts()
                .listTerminalOrders(
                        "38765",
                        ListTerminalOrdersProcessingAccountsRequest.builder()
                                .status(ListTerminalOrdersProcessingAccountsRequestStatus.OPEN)
                                .fromDateTime(OffsetDateTime.parse("2024-09-08T12:00:00Z"))
                                .toDateTime(OffsetDateTime.parse("2024-12-08T11:00:00Z"))
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingAccountsWireTest_testListTerminalOrders_response.json");
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
    public void testCreateTerminalOrder() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingProcessingAccountsWireTest_testCreateTerminalOrder_response.json")));
        TerminalOrder response = client.boarding()
                .processingAccounts()
                .createTerminalOrder(
                        "38765",
                        CreateTerminalOrder.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .orderItems(Arrays.asList(OrderItem.builder()
                                        .type(OrderItemType.SOLUTION)
                                        .solutionTemplateId("Roc Services_DX8000")
                                        .solutionQuantity(1)
                                        .deviceCondition(OrderItemDeviceCondition.NEW)
                                        .solutionSetup(OrderItemSolutionSetup.builder()
                                                .timezone(SchemasTimezone.AMERICA_CHICAGO)
                                                .industryTemplateId("Retail")
                                                .gatewaySettings(OrderItemSolutionSetupGatewaySettings.builder()
                                                        .merchantPortfolioId("Company Ltd")
                                                        .merchantTemplateId("Company Ltd Merchant Template")
                                                        .userTemplateId("Company Ltd User Template")
                                                        .terminalTemplateId("Company Ltd Terminal Template")
                                                        .build())
                                                .applicationSettings(OrderItemSolutionSetupApplicationSettings.builder()
                                                        .clerkPrompt(false)
                                                        .security(
                                                                OrderItemSolutionSetupApplicationSettingsSecurity
                                                                        .builder()
                                                                        .refundPassword(true)
                                                                        .keyedSalePassword(false)
                                                                        .reversalPassword(true)
                                                                        .build())
                                                        .build())
                                                .deviceSettings(OrderItemSolutionSetupDeviceSettings.builder()
                                                        .numberOfMobileUsers(2)
                                                        .communicationType(
                                                                OrderItemSolutionSetupDeviceSettingsCommunicationType
                                                                        .WIFI)
                                                        .build())
                                                .batchClosure(OrderItemSolutionSetupBatchClosure.automatic(
                                                        AutomaticBatchClose.builder()
                                                                .build()))
                                                .receiptNotifications(
                                                        OrderItemSolutionSetupReceiptNotifications.builder()
                                                                .emailReceipt(true)
                                                                .smsReceipt(false)
                                                                .build())
                                                .taxes(Optional.of(Arrays.asList(
                                                        OrderItemSolutionSetupTaxesItem.builder()
                                                                .taxRate(6f)
                                                                .taxLabel("Sales Tax")
                                                                .build())))
                                                .tips(OrderItemSolutionSetupTips.builder()
                                                        .enabled(false)
                                                        .build())
                                                .tokenization(true)
                                                .build())
                                        .build()))
                                .trainingProvider(TrainingProvider.PAYROC)
                                .shipping(CreateTerminalOrderShipping.builder()
                                        .preferences(CreateTerminalOrderShippingPreferences.builder()
                                                .method(CreateTerminalOrderShippingPreferencesMethod.NEXT_DAY)
                                                .saturdayDelivery(true)
                                                .build())
                                        .address(CreateTerminalOrderShippingAddress.builder()
                                                .recipientName("Recipient Name")
                                                .addressLine1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .postalCode("60056")
                                                .email("example@mail.com")
                                                .businessName("Company Ltd")
                                                .addressLine2("Example Address Line 2")
                                                .phone("2025550164")
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
        String expectedRequestBody = TestResources.loadResource(
                "/wire-tests/BoardingProcessingAccountsWireTest_testCreateTerminalOrder_request.json");
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
                "/wire-tests/BoardingProcessingAccountsWireTest_testCreateTerminalOrder_response.json");
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
