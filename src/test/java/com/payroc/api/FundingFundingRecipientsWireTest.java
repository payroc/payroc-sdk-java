package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.funding.fundingrecipients.requests.CreateAccountFundingRecipientsRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.CreateFundingRecipient;
import com.payroc.api.resources.funding.fundingrecipients.requests.CreateOwnerFundingRecipientsRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.DeleteFundingRecipientsRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.ListFundingRecipientFundingAccountsRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.ListFundingRecipientOwnersRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.RetrieveFundingRecipientsRequest;
import com.payroc.api.resources.funding.fundingrecipients.requests.UpdateFundingRecipientsRequest;
import com.payroc.api.resources.funding.fundingrecipients.types.CreateFundingRecipientRecipientType;
import com.payroc.api.types.Address;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.FundingAccount;
import com.payroc.api.types.FundingAccountType;
import com.payroc.api.types.FundingAccountUse;
import com.payroc.api.types.FundingRecipient;
import com.payroc.api.types.FundingRecipientRecipientType;
import com.payroc.api.types.Identifier;
import com.payroc.api.types.IdentifierType;
import com.payroc.api.types.Owner;
import com.payroc.api.types.OwnerRelationship;
import com.payroc.api.types.PaymentMethodAch;
import com.payroc.api.types.PaymentMethodsItem;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FundingFundingRecipientsWireTest {
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
                                "{\"recipientId\":234,\"status\":\"approved\",\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"recipientType\":\"privateCorporation\",\"taxId\":\"123456789\",\"charityId\":\"charityId\",\"doingBusinessAs\":\"doingBusinessAs\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"owners\":[{\"ownerId\":4564,\"link\":{\"rel\":\"owner\",\"href\":\"https://api.payroc.com/v1/owners/4564\",\"method\":\"get\"}}],\"fundingAccounts\":[{\"fundingAccountId\":123,\"status\":\"approved\",\"link\":{\"rel\":\"fundingAccount\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\",\"method\":\"get\"}},{\"fundingAccountId\":124,\"status\":\"rejected\",\"link\":{\"rel\":\"fundingAccount\",\"href\":\"https://api.payroc.com/v1/funding-accounts/124\",\"method\":\"get\"}}]}"));
        FundingRecipient response = client.funding()
                .fundingRecipients()
                .create(CreateFundingRecipient.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .recipientType(CreateFundingRecipientRecipientType.PRIVATE_CORPORATION)
                        .taxId("123456789")
                        .doingBusinessAs("doingBusinessAs")
                        .address(Address.builder()
                                .address1("1 Example Ave.")
                                .city("Chicago")
                                .state("Illinois")
                                .country("US")
                                .postalCode("60056")
                                .build())
                        .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                .value("jane.doe@example.com")
                                .build())))
                        .owners(Arrays.asList(Owner.builder()
                                .firstName("Jane")
                                .lastName("Doe")
                                .dateOfBirth(LocalDate.parse("1964-03-22"))
                                .address(Address.builder()
                                        .address1("1 Example Ave.")
                                        .city("Chicago")
                                        .state("Illinois")
                                        .country("US")
                                        .postalCode("60056")
                                        .build())
                                .relationship(OwnerRelationship.builder()
                                        .isControlProng(true)
                                        .build())
                                .identifiers(Arrays.asList(Identifier.builder()
                                        .type(IdentifierType.NATIONAL_ID)
                                        .value("xxxxx4320")
                                        .build()))
                                .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                        .value("jane.doe@example.com")
                                        .build())))
                                .build()))
                        .fundingAccounts(Arrays.asList(FundingAccount.builder()
                                .type(FundingAccountType.CHECKING)
                                .use(FundingAccountUse.CREDIT)
                                .nameOnAccount("Jane Doe")
                                .paymentMethods(Arrays.asList(PaymentMethodsItem.ach(
                                        PaymentMethodAch.builder().build())))
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
                + "  \"recipientType\": \"privateCorporation\",\n"
                + "  \"taxId\": \"123456789\",\n"
                + "  \"doingBusinessAs\": \"doingBusinessAs\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
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
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"dateOfBirth\": \"1964-03-22\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      },\n"
                + "      \"identifiers\": [\n"
                + "        {\n"
                + "          \"type\": \"nationalId\",\n"
                + "          \"value\": \"xxxxx4320\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"relationship\": {\n"
                + "        \"isControlProng\": true\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"fundingAccounts\": [\n"
                + "    {\n"
                + "      \"type\": \"checking\",\n"
                + "      \"use\": \"credit\",\n"
                + "      \"nameOnAccount\": \"Jane Doe\",\n"
                + "      \"paymentMethods\": [\n"
                + "        {\n"
                + "          \"type\": \"ach\"\n"
                + "        }\n"
                + "      ]\n"
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
                + "  \"recipientId\": 234,\n"
                + "  \"status\": \"approved\",\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"recipientType\": \"privateCorporation\",\n"
                + "  \"taxId\": \"123456789\",\n"
                + "  \"charityId\": \"charityId\",\n"
                + "  \"doingBusinessAs\": \"doingBusinessAs\",\n"
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
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
                + "  },\n"
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"ownerId\": 4564,\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"owner\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/owners/4564\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"fundingAccounts\": [\n"
                + "    {\n"
                + "      \"fundingAccountId\": 123,\n"
                + "      \"status\": \"approved\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"fundingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/funding-accounts/123\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    },\n"
                + "    {\n"
                + "      \"fundingAccountId\": 124,\n"
                + "      \"status\": \"rejected\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"fundingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/funding-accounts/124\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
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
                                "{\"recipientId\":234,\"status\":\"approved\",\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"recipientType\":\"privateCorporation\",\"taxId\":\"123456789\",\"charityId\":\"charityId\",\"doingBusinessAs\":\"Pizza Doe\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"contactMethods\":[{\"value\":\"2025550164\",\"type\":\"phone\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"owners\":[{\"ownerId\":4564,\"link\":{\"rel\":\"owner\",\"href\":\"https://api.payroc.com/v1/owners/4564\",\"method\":\"get\"}}],\"fundingAccounts\":[{\"fundingAccountId\":123,\"status\":\"approved\",\"link\":{\"rel\":\"fundingAccount\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\",\"method\":\"get\"}},{\"fundingAccountId\":124,\"status\":\"hold\",\"link\":{\"rel\":\"fundingAccount\",\"href\":\"https://api.payroc.com/v1/funding-accounts/124\",\"method\":\"get\"}}]}"));
        FundingRecipient response = client.funding()
                .fundingRecipients()
                .retrieve(1, RetrieveFundingRecipientsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"recipientId\": 234,\n"
                + "  \"status\": \"approved\",\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"recipientType\": \"privateCorporation\",\n"
                + "  \"taxId\": \"123456789\",\n"
                + "  \"charityId\": \"charityId\",\n"
                + "  \"doingBusinessAs\": \"Pizza Doe\",\n"
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
                + "      \"value\": \"2025550164\",\n"
                + "      \"type\": \"phone\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
                + "  },\n"
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"ownerId\": 4564,\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"owner\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/owners/4564\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"fundingAccounts\": [\n"
                + "    {\n"
                + "      \"fundingAccountId\": 123,\n"
                + "      \"status\": \"approved\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"fundingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/funding-accounts/123\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    },\n"
                + "    {\n"
                + "      \"fundingAccountId\": 124,\n"
                + "      \"status\": \"hold\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"fundingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/funding-accounts/124\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
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
    public void testUpdate() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.funding()
                .fundingRecipients()
                .update(
                        1,
                        UpdateFundingRecipientsRequest.builder()
                                .body(FundingRecipient.builder()
                                        .recipientType(FundingRecipientRecipientType.PRIVATE_CORPORATION)
                                        .taxId("123456789")
                                        .doingBusinessAs("doingBusinessAs")
                                        .address(Address.builder()
                                                .address1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .country("US")
                                                .postalCode("60056")
                                                .build())
                                        .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                .value("jane.doe@example.com")
                                                .build())))
                                        .build())
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PUT", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"recipientType\": \"privateCorporation\",\n"
                + "  \"taxId\": \"123456789\",\n"
                + "  \"doingBusinessAs\": \"doingBusinessAs\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
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
    }

    @Test
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.funding()
                .fundingRecipients()
                .delete(1, DeleteFundingRecipientsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testListAccounts() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "[{\"fundingAccountId\":123,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"approved\",\"type\":\"checking\",\"use\":\"credit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-recipients/234\"}]},{\"fundingAccountId\":124,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"pending\",\"type\":\"checking\",\"use\":\"debit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-recipients/235\"}]}]"));
        List<FundingAccount> response = client.funding()
                .fundingRecipients()
                .listAccounts(
                        1, ListFundingRecipientFundingAccountsRequest.builder().build());
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
                + "    \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"status\": \"approved\",\n"
                + "    \"type\": \"checking\",\n"
                + "    \"use\": \"credit\",\n"
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
                + "        \"href\": \"https://api.payroc.com/v1/funding-recipients/234\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  {\n"
                + "    \"fundingAccountId\": 124,\n"
                + "    \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"status\": \"pending\",\n"
                + "    \"type\": \"checking\",\n"
                + "    \"use\": \"debit\",\n"
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
                + "        \"href\": \"https://api.payroc.com/v1/funding-recipients/235\"\n"
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
    public void testCreateAccount() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"fundingAccountId\":123,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"approved\",\"type\":\"checking\",\"use\":\"credit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-recipients/234\"}]}"));
        FundingAccount response = client.funding()
                .fundingRecipients()
                .createAccount(
                        1,
                        CreateAccountFundingRecipientsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(FundingAccount.builder()
                                        .type(FundingAccountType.CHECKING)
                                        .use(FundingAccountUse.CREDIT)
                                        .nameOnAccount("Jane Doe")
                                        .paymentMethods(Arrays.asList(PaymentMethodsItem.ach(
                                                PaymentMethodAch.builder().build())))
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
                + "  \"type\": \"checking\",\n"
                + "  \"use\": \"credit\",\n"
                + "  \"nameOnAccount\": \"Jane Doe\",\n"
                + "  \"paymentMethods\": [\n"
                + "    {\n"
                + "      \"type\": \"ach\"\n"
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
                + "  \"fundingAccountId\": 123,\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"status\": \"approved\",\n"
                + "  \"type\": \"checking\",\n"
                + "  \"use\": \"credit\",\n"
                + "  \"nameOnAccount\": \"Jane Doe\",\n"
                + "  \"paymentMethods\": [\n"
                + "    {\n"
                + "      \"value\": {\n"
                + "        \"routingNumber\": \"123456789\",\n"
                + "        \"accountNumber\": \"1234567890\"\n"
                + "      },\n"
                + "      \"type\": \"ach\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"parent\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/funding-recipients/234\"\n"
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
    public void testListOwners() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "[{\"ownerId\":4564,\"firstName\":\"Jane\",\"middleName\":\"Helen\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1964-03-22\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"identifiers\":[{\"type\":\"nationalId\",\"value\":\"xxxxx4320\"}],\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"relationship\":{\"equityPercentage\":48.5,\"title\":\"CFO\",\"isControlProng\":true,\"isAuthorizedSignatory\":false}}]"));
        List<Owner> response = client.funding()
                .fundingRecipients()
                .listOwners(1, ListFundingRecipientOwnersRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "[\n"
                + "  {\n"
                + "    \"ownerId\": 4564,\n"
                + "    \"firstName\": \"Jane\",\n"
                + "    \"middleName\": \"Helen\",\n"
                + "    \"lastName\": \"Doe\",\n"
                + "    \"dateOfBirth\": \"1964-03-22\",\n"
                + "    \"address\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"identifiers\": [\n"
                + "      {\n"
                + "        \"type\": \"nationalId\",\n"
                + "        \"value\": \"xxxxx4320\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"relationship\": {\n"
                + "      \"equityPercentage\": 48.5,\n"
                + "      \"title\": \"CFO\",\n"
                + "      \"isControlProng\": true,\n"
                + "      \"isAuthorizedSignatory\": false\n"
                + "    }\n"
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
    public void testCreateOwner() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"ownerId\":4564,\"firstName\":\"Jane\",\"middleName\":\"Helen\",\"lastName\":\"Doe\",\"dateOfBirth\":\"1964-03-22\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"identifiers\":[{\"type\":\"nationalId\",\"value\":\"xxxxx4320\"}],\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"relationship\":{\"equityPercentage\":48.5,\"title\":\"CFO\",\"isControlProng\":true,\"isAuthorizedSignatory\":false}}"));
        Owner response = client.funding()
                .fundingRecipients()
                .createOwner(
                        1,
                        CreateOwnerFundingRecipientsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(Owner.builder()
                                        .firstName("Jane")
                                        .lastName("Doe")
                                        .dateOfBirth(LocalDate.parse("1964-03-22"))
                                        .address(Address.builder()
                                                .address1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .country("US")
                                                .postalCode("60056")
                                                .build())
                                        .relationship(OwnerRelationship.builder()
                                                .isControlProng(true)
                                                .build())
                                        .identifiers(Arrays.asList(Identifier.builder()
                                                .type(IdentifierType.NATIONAL_ID)
                                                .value("xxxxx4320")
                                                .build()))
                                        .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                .value("jane.doe@example.com")
                                                .build())))
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
                + "  \"firstName\": \"Jane\",\n"
                + "  \"lastName\": \"Doe\",\n"
                + "  \"dateOfBirth\": \"1964-03-22\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
                + "    \"city\": \"Chicago\",\n"
                + "    \"state\": \"Illinois\",\n"
                + "    \"country\": \"US\",\n"
                + "    \"postalCode\": \"60056\"\n"
                + "  },\n"
                + "  \"identifiers\": [\n"
                + "    {\n"
                + "      \"type\": \"nationalId\",\n"
                + "      \"value\": \"xxxxx4320\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"contactMethods\": [\n"
                + "    {\n"
                + "      \"value\": \"jane.doe@example.com\",\n"
                + "      \"type\": \"email\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"relationship\": {\n"
                + "    \"isControlProng\": true\n"
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
                + "  \"ownerId\": 4564,\n"
                + "  \"firstName\": \"Jane\",\n"
                + "  \"middleName\": \"Helen\",\n"
                + "  \"lastName\": \"Doe\",\n"
                + "  \"dateOfBirth\": \"1964-03-22\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
                + "    \"address2\": \"Example Address Line 2\",\n"
                + "    \"address3\": \"Example Address Line 3\",\n"
                + "    \"city\": \"Chicago\",\n"
                + "    \"state\": \"Illinois\",\n"
                + "    \"country\": \"US\",\n"
                + "    \"postalCode\": \"60056\"\n"
                + "  },\n"
                + "  \"identifiers\": [\n"
                + "    {\n"
                + "      \"type\": \"nationalId\",\n"
                + "      \"value\": \"xxxxx4320\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"contactMethods\": [\n"
                + "    {\n"
                + "      \"value\": \"jane.doe@example.com\",\n"
                + "      \"type\": \"email\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"relationship\": {\n"
                + "    \"equityPercentage\": 48.5,\n"
                + "    \"title\": \"CFO\",\n"
                + "    \"isControlProng\": true,\n"
                + "    \"isAuthorizedSignatory\": false\n"
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
