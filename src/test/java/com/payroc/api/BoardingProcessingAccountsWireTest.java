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
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingAccountId\":\"38765\",\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"status\":\"approved\",\"doingBusinessAs\":\"Pizza Doe\",\"owners\":[{\"ownerId\":4564,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"link\":{\"rel\":\"owner\",\"href\":\"https://api.payroc.com/v1/owners/1543\",\"method\":\"get\"}}],\"website\":\"www.example.com\",\"businessType\":\"restaurant\",\"categoryCode\":5999,\"merchandiseOrServiceSold\":\"Pizza\",\"businessStartDate\":\"2020-01-01\",\"timezone\":\"America/Chicago\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"processing\":{\"merchantId\":\"444412365478965\",\"transactionAmounts\":{\"average\":5000,\"highest\":10000},\"monthlyAmounts\":{\"average\":50000,\"highest\":100000},\"volumeBreakdown\":{\"cardPresent\":77,\"mailOrTelephone\":3,\"ecommerce\":20},\"isSeasonal\":true,\"monthsOfOperation\":[\"jan\",\"feb\"],\"ach\":{\"naics\":\"5812\",\"previouslyTerminatedForAch\":false,\"refunds\":{\"writtenRefundPolicy\":true,\"refundPolicyUrl\":\"www.example.com/refund-poilcy-url\"},\"estimatedMonthlyTransactions\":3000,\"limits\":{\"singleTransaction\":10000,\"dailyDeposit\":200000,\"monthlyDeposit\":6000000},\"transactionTypes\":[\"prearrangedPayment\",\"other\"],\"transactionTypesOther\":\"anotherTransactionType\"},\"cardAcceptance\":{\"debitOnly\":false,\"hsaFsa\":false,\"cardsAccepted\":[\"visa\",\"mastercard\"],\"specialityCards\":{\"americanExpressDirect\":{\"enabled\":true,\"merchantNumber\":\"abc1234567\"},\"electronicBenefitsTransfer\":{\"enabled\":true,\"fnsNumber\":\"6789012\"},\"other\":{\"wexMerchantNumber\":\"abc1234567\",\"voyagerMerchantId\":\"abc1234567\",\"fleetMerchantId\":\"abc1234567\"}}}},\"funding\":{\"status\":\"enabled\",\"fundingSchedule\":\"nextday\",\"acceleratedFundingFee\":1999,\"dailyDiscount\":false,\"fundingAccounts\":[{\"fundingAccountId\":123,\"status\":\"pending\",\"link\":{\"rel\":\"fundingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\"}}]},\"pricing\":{\"link\":{\"rel\":\"pricing\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765/pricing\",\"method\":\"get\"}},\"contacts\":[{\"contactId\":1543,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"link\":{\"rel\":\"contact\",\"href\":\"https://api.payroc.com/v1/contacts/1543\",\"method\":\"get\"}}],\"signature\":{\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"},\"type\":\"requestedViaDirectLink\"},\"metadata\":{\"customerId\":\"2345\"},\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}]}"));
        ProcessingAccount response = client.boarding()
                .processingAccounts()
                .retrieve("38765", RetrieveProcessingAccountsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"processingAccountId\": \"38765\",\n"
                + "  \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"status\": \"approved\",\n"
                + "  \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"ownerId\": 4564,\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"owner\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/owners/1543\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"website\": \"www.example.com\",\n"
                + "  \"businessType\": \"restaurant\",\n"
                + "  \"categoryCode\": 5999,\n"
                + "  \"merchandiseOrServiceSold\": \"Pizza\",\n"
                + "  \"businessStartDate\": \"2020-01-01\",\n"
                + "  \"timezone\": \"America/Chicago\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
                + "    \"address2\": \"Example Address Line 2\",\n"
                + "    \"address3\": \"Example Address Line 3\",\n"
                + "    \"city\": \"Chicago\",\n"
                + "    \"state\": \"Illinois\",\n"
                + "    \"country\": \"US\",\n"
                + "    \"postalCode\": \"60056\"\n"
                + "  },\n"
                + "  \"contactMethods\": [\n"
                + "    {\n"
                + "      \"value\": \"jane.doe@example.com\",\n"
                + "      \"type\": \"email\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"processing\": {\n"
                + "    \"merchantId\": \"444412365478965\",\n"
                + "    \"transactionAmounts\": {\n"
                + "      \"average\": 5000,\n"
                + "      \"highest\": 10000\n"
                + "    },\n"
                + "    \"monthlyAmounts\": {\n"
                + "      \"average\": 50000,\n"
                + "      \"highest\": 100000\n"
                + "    },\n"
                + "    \"volumeBreakdown\": {\n"
                + "      \"cardPresent\": 77,\n"
                + "      \"mailOrTelephone\": 3,\n"
                + "      \"ecommerce\": 20\n"
                + "    },\n"
                + "    \"isSeasonal\": true,\n"
                + "    \"monthsOfOperation\": [\n"
                + "      \"jan\",\n"
                + "      \"feb\"\n"
                + "    ],\n"
                + "    \"ach\": {\n"
                + "      \"naics\": \"5812\",\n"
                + "      \"previouslyTerminatedForAch\": false,\n"
                + "      \"refunds\": {\n"
                + "        \"writtenRefundPolicy\": true,\n"
                + "        \"refundPolicyUrl\": \"www.example.com/refund-poilcy-url\"\n"
                + "      },\n"
                + "      \"estimatedMonthlyTransactions\": 3000,\n"
                + "      \"limits\": {\n"
                + "        \"singleTransaction\": 10000,\n"
                + "        \"dailyDeposit\": 200000,\n"
                + "        \"monthlyDeposit\": 6000000\n"
                + "      },\n"
                + "      \"transactionTypes\": [\n"
                + "        \"prearrangedPayment\",\n"
                + "        \"other\"\n"
                + "      ],\n"
                + "      \"transactionTypesOther\": \"anotherTransactionType\"\n"
                + "    },\n"
                + "    \"cardAcceptance\": {\n"
                + "      \"debitOnly\": false,\n"
                + "      \"hsaFsa\": false,\n"
                + "      \"cardsAccepted\": [\n"
                + "        \"visa\",\n"
                + "        \"mastercard\"\n"
                + "      ],\n"
                + "      \"specialityCards\": {\n"
                + "        \"americanExpressDirect\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"merchantNumber\": \"abc1234567\"\n"
                + "        },\n"
                + "        \"electronicBenefitsTransfer\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"fnsNumber\": \"6789012\"\n"
                + "        },\n"
                + "        \"other\": {\n"
                + "          \"wexMerchantNumber\": \"abc1234567\",\n"
                + "          \"voyagerMerchantId\": \"abc1234567\",\n"
                + "          \"fleetMerchantId\": \"abc1234567\"\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"funding\": {\n"
                + "    \"status\": \"enabled\",\n"
                + "    \"fundingSchedule\": \"nextday\",\n"
                + "    \"acceleratedFundingFee\": 1999,\n"
                + "    \"dailyDiscount\": false,\n"
                + "    \"fundingAccounts\": [\n"
                + "      {\n"
                + "        \"fundingAccountId\": 123,\n"
                + "        \"status\": \"pending\",\n"
                + "        \"link\": {\n"
                + "          \"rel\": \"fundingAccount\",\n"
                + "          \"method\": \"get\",\n"
                + "          \"href\": \"https://api.payroc.com/v1/funding-accounts/123\"\n"
                + "        }\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"pricing\": {\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"pricing\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765/pricing\",\n"
                + "      \"method\": \"get\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"contacts\": [\n"
                + "    {\n"
                + "      \"contactId\": 1543,\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"contact\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/contacts/1543\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"signature\": {\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    },\n"
                + "    \"type\": \"requestedViaDirectLink\"\n"
                + "  },\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
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
    public void testListProcessingAccountFundingAccounts() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "[{\"fundingAccountId\":123,\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"status\":\"approved\",\"type\":\"checking\",\"use\":\"creditAndDebit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\"}]},{\"fundingAccountId\":124,\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"status\":\"pending\",\"type\":\"checking\",\"use\":\"creditAndDebit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\"}]}]"));
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
        String expectedResponseBody = ""
                + "[\n"
                + "  {\n"
                + "    \"fundingAccountId\": 123,\n"
                + "    \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "    \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "    \"status\": \"approved\",\n"
                + "    \"type\": \"checking\",\n"
                + "    \"use\": \"creditAndDebit\",\n"
                + "    \"nameOnAccount\": \"Jane Doe\",\n"
                + "    \"paymentMethods\": [\n"
                + "      {\n"
                + "        \"value\": {\n"
                + "          \"routingNumber\": \"123456789\",\n"
                + "          \"accountNumber\": \"1234567890\"\n"
                + "        },\n"
                + "        \"type\": \"ach\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"metadata\": {\n"
                + "      \"yourCustomField\": \"abc123\"\n"
                + "    },\n"
                + "    \"links\": [\n"
                + "      {\n"
                + "        \"rel\": \"parent\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  {\n"
                + "    \"fundingAccountId\": 124,\n"
                + "    \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "    \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "    \"status\": \"pending\",\n"
                + "    \"type\": \"checking\",\n"
                + "    \"use\": \"creditAndDebit\",\n"
                + "    \"nameOnAccount\": \"Jane Doe\",\n"
                + "    \"paymentMethods\": [\n"
                + "      {\n"
                + "        \"value\": {\n"
                + "          \"routingNumber\": \"123456789\",\n"
                + "          \"accountNumber\": \"1234567890\"\n"
                + "        },\n"
                + "        \"type\": \"ach\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"metadata\": {\n"
                + "      \"yourCustomField\": \"abc123\"\n"
                + "    },\n"
                + "    \"links\": [\n"
                + "      {\n"
                + "        \"rel\": \"parent\",\n"
                + "        \"method\": \"get\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\"\n"
                + "      }\n"
                + "    ]\n"
                + "  }\n"
                + "]";
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
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"limit\":2,\"count\":2,\"hasMore\":true,\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765/contacts?before=1543&limit=2\"},{\"rel\":\"next\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765/contacts?after=87926&limit=2\"}],\"data\":[{\"contactId\":1543,\"type\":\"manager\",\"firstName\":\"Jane\",\"middleName\":\"Helen\",\"lastName\":\"Doe\",\"identifiers\":[{\"type\":\"nationalId\",\"value\":\"xxxxx4320\"}],\"contactMethods\":[{\"value\":\"2025550164\",\"type\":\"phone\"},{\"value\":\"8445557624\",\"type\":\"mobile\"},{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},{\"contactId\":87926,\"type\":\"representative\",\"firstName\":\"Fred\",\"middleName\":\"Jim\",\"lastName\":\"Nerk\",\"identifiers\":[{\"type\":\"nationalId\",\"value\":\"xxxxx9876\"}],\"contactMethods\":[{\"value\":\"2025550164\",\"type\":\"phone\"},{\"value\":\"8445557624\",\"type\":\"mobile\"},{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]}]}"));
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
        String expectedResponseBody = ""
                + "{\n"
                + "  \"limit\": 2,\n"
                + "  \"count\": 2,\n"
                + "  \"hasMore\": true,\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765/contacts?before=1543&limit=2\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"rel\": \"next\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765/contacts?after=87926&limit=2\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"data\": [\n"
                + "    {\n"
                + "      \"contactId\": 1543,\n"
                + "      \"type\": \"manager\",\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"middleName\": \"Helen\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"identifiers\": [\n"
                + "        {\n"
                + "          \"type\": \"nationalId\",\n"
                + "          \"value\": \"xxxxx4320\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"2025550164\",\n"
                + "          \"type\": \"phone\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"value\": \"8445557624\",\n"
                + "          \"type\": \"mobile\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ]\n"
                + "    },\n"
                + "    {\n"
                + "      \"contactId\": 87926,\n"
                + "      \"type\": \"representative\",\n"
                + "      \"firstName\": \"Fred\",\n"
                + "      \"middleName\": \"Jim\",\n"
                + "      \"lastName\": \"Nerk\",\n"
                + "      \"identifiers\": [\n"
                + "        {\n"
                + "          \"type\": \"nationalId\",\n"
                + "          \"value\": \"xxxxx9876\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"2025550164\",\n"
                + "          \"type\": \"phone\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"value\": \"8445557624\",\n"
                + "          \"type\": \"mobile\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ]\n"
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
    public void testGetProcessingAccountPricingAgreement() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"country\":\"US\",\"version\":\"5.0\",\"base\":{\"addressVerification\":5,\"annualFee\":{\"billInMonth\":\"december\",\"amount\":9900},\"regulatoryAssistanceProgram\":15,\"pciNonCompliance\":4995,\"merchantAdvantage\":10,\"platinumSecurity\":{\"amount\":1295,\"billingFrequency\":\"monthly\"},\"maintenance\":500,\"minimum\":100,\"voiceAuthorization\":95,\"chargeback\":2500,\"retrieval\":1500,\"batch\":5,\"earlyTermination\":57500},\"processor\":{\"card\":{\"fees\":{\"mastercardVisaDiscover\":{\"volume\":1.25},\"amex\":{\"volume\":1.25,\"transaction\":1,\"type\":\"optBlue\"},\"pinDebit\":{\"additionalDiscount\":1.25,\"transaction\":1,\"monthlyAccess\":1},\"enhancedInterchange\":{\"enrollment\":1,\"creditToMerchant\":1.25}},\"planType\":\"interchangePlus\"},\"ach\":{\"fees\":{\"transaction\":50,\"batch\":1000,\"returns\":400,\"unauthorizedReturn\":1999,\"statement\":800,\"monthlyMinimum\":20000,\"accountVerification\":100,\"discountRateUnder10000\":1.25,\"discountRateAbove10000\":1.25}}},\"gateway\":{\"fees\":{\"monthly\":0,\"setup\":0,\"perTransaction\":0,\"perDeviceMonthly\":0}},\"services\":[{\"enabled\":true,\"name\":\"hardwareAdvantagePlan\"}]}"));
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
        String expectedResponseBody = ""
                + "{\n"
                + "  \"country\": \"US\",\n"
                + "  \"version\": \"5.0\",\n"
                + "  \"base\": {\n"
                + "    \"addressVerification\": 5,\n"
                + "    \"annualFee\": {\n"
                + "      \"billInMonth\": \"december\",\n"
                + "      \"amount\": 9900\n"
                + "    },\n"
                + "    \"regulatoryAssistanceProgram\": 15,\n"
                + "    \"pciNonCompliance\": 4995,\n"
                + "    \"merchantAdvantage\": 10,\n"
                + "    \"platinumSecurity\": {\n"
                + "      \"amount\": 1295,\n"
                + "      \"billingFrequency\": \"monthly\"\n"
                + "    },\n"
                + "    \"maintenance\": 500,\n"
                + "    \"minimum\": 100,\n"
                + "    \"voiceAuthorization\": 95,\n"
                + "    \"chargeback\": 2500,\n"
                + "    \"retrieval\": 1500,\n"
                + "    \"batch\": 5,\n"
                + "    \"earlyTermination\": 57500\n"
                + "  },\n"
                + "  \"processor\": {\n"
                + "    \"card\": {\n"
                + "      \"fees\": {\n"
                + "        \"mastercardVisaDiscover\": {\n"
                + "          \"volume\": 1.25\n"
                + "        },\n"
                + "        \"amex\": {\n"
                + "          \"volume\": 1.25,\n"
                + "          \"transaction\": 1,\n"
                + "          \"type\": \"optBlue\"\n"
                + "        },\n"
                + "        \"pinDebit\": {\n"
                + "          \"additionalDiscount\": 1.25,\n"
                + "          \"transaction\": 1,\n"
                + "          \"monthlyAccess\": 1\n"
                + "        },\n"
                + "        \"enhancedInterchange\": {\n"
                + "          \"enrollment\": 1,\n"
                + "          \"creditToMerchant\": 1.25\n"
                + "        }\n"
                + "      },\n"
                + "      \"planType\": \"interchangePlus\"\n"
                + "    },\n"
                + "    \"ach\": {\n"
                + "      \"fees\": {\n"
                + "        \"transaction\": 50,\n"
                + "        \"batch\": 1000,\n"
                + "        \"returns\": 400,\n"
                + "        \"unauthorizedReturn\": 1999,\n"
                + "        \"statement\": 800,\n"
                + "        \"monthlyMinimum\": 20000,\n"
                + "        \"accountVerification\": 100,\n"
                + "        \"discountRateUnder10000\": 1.25,\n"
                + "        \"discountRateAbove10000\": 1.25\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"gateway\": {\n"
                + "    \"fees\": {\n"
                + "      \"monthly\": 0,\n"
                + "      \"setup\": 0,\n"
                + "      \"perTransaction\": 0,\n"
                + "      \"perDeviceMonthly\": 0\n"
                + "    }\n"
                + "  },\n"
                + "  \"services\": [\n"
                + "    {\n"
                + "      \"enabled\": true,\n"
                + "      \"name\": \"hardwareAdvantagePlan\"\n"
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
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "[{\"terminalOrderId\":\"12345\",\"status\":\"open\",\"trainingProvider\":\"payroc\",\"shipping\":{\"preferences\":{\"method\":\"nextDay\",\"saturdayDelivery\":true},\"address\":{\"recipientName\":\"Recipient Name\",\"businessName\":\"Company Ltd\",\"addressLine1\":\"1 Example Ave.\",\"addressLine2\":\"Example Address Line 2\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"postalCode\":\"60056\",\"email\":\"example@mail.com\",\"phone\":\"2025550164\"}},\"orderItems\":[{\"links\":[{\"processingTerminalId\":\"1234001\",\"link\":{\"href\":\"https://api.payroc.com/v1/processing-terminals/1234001\",\"rel\":\"processingTerminal\",\"method\":\"get\"}}],\"type\":\"solution\",\"solutionTemplateId\":\"Roc Services_DX8000\",\"solutionQuantity\":1,\"deviceCondition\":\"new\",\"solutionSetup\":{\"timezone\":\"America/Chicago\",\"industryTemplateId\":\"Retail\",\"gatewaySettings\":{\"merchantPortfolioId\":\"Company Ltd\",\"merchantTemplateId\":\"Company Ltd Merchant Template\",\"userTemplateId\":\"Company Ltd User Template\",\"terminalTemplateId\":\"Company Ltd Terminal Template\"},\"applicationSettings\":{\"clerkPrompt\":false,\"security\":{\"refundPassword\":true,\"keyedSalePassword\":false,\"reversalPassword\":true}},\"deviceSettings\":{\"numberOfMobileUsers\":2,\"communicationType\":\"wifi\"},\"batchClosure\":{\"batchCloseTime\":\"23:40\",\"batchCloseType\":\"automatic\"},\"receiptNotifications\":{\"emailReceipt\":true,\"smsReceipt\":false},\"taxes\":[{\"taxRate\":6,\"taxLabel\":\"Sales Tax\"}],\"tips\":{\"enabled\":false},\"tokenization\":true}}],\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\"}]"));
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
        String expectedResponseBody = ""
                + "[\n"
                + "  {\n"
                + "    \"terminalOrderId\": \"12345\",\n"
                + "    \"status\": \"open\",\n"
                + "    \"trainingProvider\": \"payroc\",\n"
                + "    \"shipping\": {\n"
                + "      \"preferences\": {\n"
                + "        \"method\": \"nextDay\",\n"
                + "        \"saturdayDelivery\": true\n"
                + "      },\n"
                + "      \"address\": {\n"
                + "        \"recipientName\": \"Recipient Name\",\n"
                + "        \"businessName\": \"Company Ltd\",\n"
                + "        \"addressLine1\": \"1 Example Ave.\",\n"
                + "        \"addressLine2\": \"Example Address Line 2\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"postalCode\": \"60056\",\n"
                + "        \"email\": \"example@mail.com\",\n"
                + "        \"phone\": \"2025550164\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"orderItems\": [\n"
                + "      {\n"
                + "        \"links\": [\n"
                + "          {\n"
                + "            \"processingTerminalId\": \"1234001\",\n"
                + "            \"link\": {\n"
                + "              \"href\": \"https://api.payroc.com/v1/processing-terminals/1234001\",\n"
                + "              \"rel\": \"processingTerminal\",\n"
                + "              \"method\": \"get\"\n"
                + "            }\n"
                + "          }\n"
                + "        ],\n"
                + "        \"type\": \"solution\",\n"
                + "        \"solutionTemplateId\": \"Roc Services_DX8000\",\n"
                + "        \"solutionQuantity\": 1,\n"
                + "        \"deviceCondition\": \"new\",\n"
                + "        \"solutionSetup\": {\n"
                + "          \"timezone\": \"America/Chicago\",\n"
                + "          \"industryTemplateId\": \"Retail\",\n"
                + "          \"gatewaySettings\": {\n"
                + "            \"merchantPortfolioId\": \"Company Ltd\",\n"
                + "            \"merchantTemplateId\": \"Company Ltd Merchant Template\",\n"
                + "            \"userTemplateId\": \"Company Ltd User Template\",\n"
                + "            \"terminalTemplateId\": \"Company Ltd Terminal Template\"\n"
                + "          },\n"
                + "          \"applicationSettings\": {\n"
                + "            \"clerkPrompt\": false,\n"
                + "            \"security\": {\n"
                + "              \"refundPassword\": true,\n"
                + "              \"keyedSalePassword\": false,\n"
                + "              \"reversalPassword\": true\n"
                + "            }\n"
                + "          },\n"
                + "          \"deviceSettings\": {\n"
                + "            \"numberOfMobileUsers\": 2,\n"
                + "            \"communicationType\": \"wifi\"\n"
                + "          },\n"
                + "          \"batchClosure\": {\n"
                + "            \"batchCloseTime\": \"23:40\",\n"
                + "            \"batchCloseType\": \"automatic\"\n"
                + "          },\n"
                + "          \"receiptNotifications\": {\n"
                + "            \"emailReceipt\": true,\n"
                + "            \"smsReceipt\": false\n"
                + "          },\n"
                + "          \"taxes\": [\n"
                + "            {\n"
                + "              \"taxRate\": 6,\n"
                + "              \"taxLabel\": \"Sales Tax\"\n"
                + "            }\n"
                + "          ],\n"
                + "          \"tips\": {\n"
                + "            \"enabled\": false\n"
                + "          },\n"
                + "          \"tokenization\": true\n"
                + "        }\n"
                + "      }\n"
                + "    ],\n"
                + "    \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "    \"lastModifiedDate\": \"2024-07-02T12:00:00Z\"\n"
                + "  }\n"
                + "]";
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
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"terminalOrderId\":\"12345\",\"status\":\"open\",\"trainingProvider\":\"payroc\",\"shipping\":{\"preferences\":{\"method\":\"nextDay\",\"saturdayDelivery\":true},\"address\":{\"recipientName\":\"Recipient Name\",\"businessName\":\"Company Ltd\",\"addressLine1\":\"1 Example Ave.\",\"addressLine2\":\"Example Address Line 2\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"postalCode\":\"60056\",\"email\":\"example@mail.com\",\"phone\":\"2025550164\"}},\"orderItems\":[{\"links\":[{\"processingTerminalId\":\"processingTerminalId\",\"link\":{\"href\":\"href\",\"rel\":\"rel\",\"method\":\"method\"}}],\"type\":\"solution\",\"solutionTemplateId\":\"Roc Services_DX8000\",\"solutionQuantity\":1,\"deviceCondition\":\"new\",\"solutionSetup\":{\"timezone\":\"America/Chicago\",\"industryTemplateId\":\"Retail\",\"gatewaySettings\":{\"merchantPortfolioId\":\"Company Ltd\",\"merchantTemplateId\":\"Company Ltd Merchant Template\",\"userTemplateId\":\"Company Ltd User Template\",\"terminalTemplateId\":\"Company Ltd Terminal Template\"},\"applicationSettings\":{\"clerkPrompt\":false,\"security\":{\"refundPassword\":true,\"keyedSalePassword\":false,\"reversalPassword\":true}},\"deviceSettings\":{\"numberOfMobileUsers\":2,\"communicationType\":\"wifi\"},\"batchClosure\":{\"batchCloseTime\":\"23:40\",\"batchCloseType\":\"automatic\"},\"receiptNotifications\":{\"emailReceipt\":true,\"smsReceipt\":false},\"taxes\":[{\"taxRate\":6,\"taxLabel\":\"Sales Tax\"}],\"tips\":{\"enabled\":false},\"tokenization\":true}}],\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\"}"));
        TerminalOrder response = client.boarding()
                .processingAccounts()
                .createTerminalOrder(
                        "38765",
                        CreateTerminalOrder.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .orderItems(Arrays.asList(OrderItem.builder()
                                        .type(OrderItemType.SOLUTION)
                                        .solutionTemplateId("Roc Services_DX8000")
                                        .solutionQuantity(1f)
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
                                                        .numberOfMobileUsers(2f)
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
        String expectedRequestBody = ""
                + "{\n"
                + "  \"trainingProvider\": \"payroc\",\n"
                + "  \"shipping\": {\n"
                + "    \"preferences\": {\n"
                + "      \"method\": \"nextDay\",\n"
                + "      \"saturdayDelivery\": true\n"
                + "    },\n"
                + "    \"address\": {\n"
                + "      \"recipientName\": \"Recipient Name\",\n"
                + "      \"businessName\": \"Company Ltd\",\n"
                + "      \"addressLine1\": \"1 Example Ave.\",\n"
                + "      \"addressLine2\": \"Example Address Line 2\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"postalCode\": \"60056\",\n"
                + "      \"email\": \"example@mail.com\",\n"
                + "      \"phone\": \"2025550164\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"orderItems\": [\n"
                + "    {\n"
                + "      \"type\": \"solution\",\n"
                + "      \"solutionTemplateId\": \"Roc Services_DX8000\",\n"
                + "      \"solutionQuantity\": 1,\n"
                + "      \"deviceCondition\": \"new\",\n"
                + "      \"solutionSetup\": {\n"
                + "        \"timezone\": \"America/Chicago\",\n"
                + "        \"industryTemplateId\": \"Retail\",\n"
                + "        \"gatewaySettings\": {\n"
                + "          \"merchantPortfolioId\": \"Company Ltd\",\n"
                + "          \"merchantTemplateId\": \"Company Ltd Merchant Template\",\n"
                + "          \"userTemplateId\": \"Company Ltd User Template\",\n"
                + "          \"terminalTemplateId\": \"Company Ltd Terminal Template\"\n"
                + "        },\n"
                + "        \"applicationSettings\": {\n"
                + "          \"clerkPrompt\": false,\n"
                + "          \"security\": {\n"
                + "            \"refundPassword\": true,\n"
                + "            \"keyedSalePassword\": false,\n"
                + "            \"reversalPassword\": true\n"
                + "          }\n"
                + "        },\n"
                + "        \"deviceSettings\": {\n"
                + "          \"numberOfMobileUsers\": 2,\n"
                + "          \"communicationType\": \"wifi\"\n"
                + "        },\n"
                + "        \"batchClosure\": {\n"
                + "          \"batchCloseType\": \"automatic\"\n"
                + "        },\n"
                + "        \"receiptNotifications\": {\n"
                + "          \"emailReceipt\": true,\n"
                + "          \"smsReceipt\": false\n"
                + "        },\n"
                + "        \"taxes\": [\n"
                + "          {\n"
                + "            \"taxRate\": 6,\n"
                + "            \"taxLabel\": \"Sales Tax\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"tips\": {\n"
                + "          \"enabled\": false\n"
                + "        },\n"
                + "        \"tokenization\": true\n"
                + "      }\n"
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
                + "  \"terminalOrderId\": \"12345\",\n"
                + "  \"status\": \"open\",\n"
                + "  \"trainingProvider\": \"payroc\",\n"
                + "  \"shipping\": {\n"
                + "    \"preferences\": {\n"
                + "      \"method\": \"nextDay\",\n"
                + "      \"saturdayDelivery\": true\n"
                + "    },\n"
                + "    \"address\": {\n"
                + "      \"recipientName\": \"Recipient Name\",\n"
                + "      \"businessName\": \"Company Ltd\",\n"
                + "      \"addressLine1\": \"1 Example Ave.\",\n"
                + "      \"addressLine2\": \"Example Address Line 2\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"postalCode\": \"60056\",\n"
                + "      \"email\": \"example@mail.com\",\n"
                + "      \"phone\": \"2025550164\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"orderItems\": [\n"
                + "    {\n"
                + "      \"links\": [\n"
                + "        {\n"
                + "          \"processingTerminalId\": \"processingTerminalId\",\n"
                + "          \"link\": {\n"
                + "            \"href\": \"href\",\n"
                + "            \"rel\": \"rel\",\n"
                + "            \"method\": \"method\"\n"
                + "          }\n"
                + "        }\n"
                + "      ],\n"
                + "      \"type\": \"solution\",\n"
                + "      \"solutionTemplateId\": \"Roc Services_DX8000\",\n"
                + "      \"solutionQuantity\": 1,\n"
                + "      \"deviceCondition\": \"new\",\n"
                + "      \"solutionSetup\": {\n"
                + "        \"timezone\": \"America/Chicago\",\n"
                + "        \"industryTemplateId\": \"Retail\",\n"
                + "        \"gatewaySettings\": {\n"
                + "          \"merchantPortfolioId\": \"Company Ltd\",\n"
                + "          \"merchantTemplateId\": \"Company Ltd Merchant Template\",\n"
                + "          \"userTemplateId\": \"Company Ltd User Template\",\n"
                + "          \"terminalTemplateId\": \"Company Ltd Terminal Template\"\n"
                + "        },\n"
                + "        \"applicationSettings\": {\n"
                + "          \"clerkPrompt\": false,\n"
                + "          \"security\": {\n"
                + "            \"refundPassword\": true,\n"
                + "            \"keyedSalePassword\": false,\n"
                + "            \"reversalPassword\": true\n"
                + "          }\n"
                + "        },\n"
                + "        \"deviceSettings\": {\n"
                + "          \"numberOfMobileUsers\": 2,\n"
                + "          \"communicationType\": \"wifi\"\n"
                + "        },\n"
                + "        \"batchClosure\": {\n"
                + "          \"batchCloseTime\": \"23:40\",\n"
                + "          \"batchCloseType\": \"automatic\"\n"
                + "        },\n"
                + "        \"receiptNotifications\": {\n"
                + "          \"emailReceipt\": true,\n"
                + "          \"smsReceipt\": false\n"
                + "        },\n"
                + "        \"taxes\": [\n"
                + "          {\n"
                + "            \"taxRate\": 6,\n"
                + "            \"taxLabel\": \"Sales Tax\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"tips\": {\n"
                + "          \"enabled\": false\n"
                + "        },\n"
                + "        \"tokenization\": true\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T12:00:00Z\"\n"
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
