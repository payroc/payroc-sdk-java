package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.tokenization.securetokens.requests.DeleteSecureTokensRequest;
import com.payroc.api.resources.tokenization.securetokens.requests.PartiallyUpdateSecureTokensRequest;
import com.payroc.api.resources.tokenization.securetokens.requests.RetrieveSecureTokensRequest;
import com.payroc.api.resources.tokenization.securetokens.requests.TokenizationRequest;
import com.payroc.api.resources.tokenization.securetokens.requests.UpdateAccountSecureTokensRequest;
import com.payroc.api.resources.tokenization.securetokens.types.TokenizationRequestMitAgreement;
import com.payroc.api.resources.tokenization.securetokens.types.TokenizationRequestSource;
import com.payroc.api.types.AccountUpdate;
import com.payroc.api.types.Address;
import com.payroc.api.types.CardPayload;
import com.payroc.api.types.CardPayloadCardDetails;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.CustomField;
import com.payroc.api.types.Customer;
import com.payroc.api.types.CustomerNotificationLanguage;
import com.payroc.api.types.Device;
import com.payroc.api.types.DeviceModel;
import com.payroc.api.types.IpAddress;
import com.payroc.api.types.IpAddressType;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.RawCardDetails;
import com.payroc.api.types.SecureToken;
import com.payroc.api.types.SecureTokenWithAccountType;
import com.payroc.api.types.Shipping;
import com.payroc.api.types.SingleUseTokenAccountUpdate;
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

public class TokenizationSecureTokensWireTest {
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
                                "{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"processingTerminalId\":\"1234001\",\"mitAgreement\":\"unscheduled\",\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"Customer-12\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"source\":{\"cardholderName\":\"Sarah Hazel Hopper\",\"cardNumber\":\"4539858876047062\",\"expiryDate\":\"1225\",\"cardType\":\"cardType\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"},\"type\":\"card\"},\"token\":\"296753123456\",\"status\":\"notValidated\",\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        SecureToken response = client.tokenization()
                .secureTokens()
                .create(
                        "1234001",
                        TokenizationRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .source(TokenizationRequestSource.card(CardPayload.builder()
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
                                .mitAgreement(TokenizationRequestMitAgreement.UNSCHEDULED)
                                .customer(Customer.builder()
                                        .firstName("Sarah")
                                        .lastName("Hopper")
                                        .dateOfBirth(LocalDate.parse("1990-07-15"))
                                        .referenceNumber("Customer-12")
                                        .billingAddress(Address.builder()
                                                .address1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .country("US")
                                                .postalCode("60056")
                                                .address2("Example Address Line 2")
                                                .address3("Example Address Line 3")
                                                .build())
                                        .shippingAddress(Shipping.builder()
                                                .recipientName("Sarah Hopper")
                                                .address(Address.builder()
                                                        .address1("1 Example Ave.")
                                                        .city("Chicago")
                                                        .state("Illinois")
                                                        .country("US")
                                                        .postalCode("60056")
                                                        .address2("Example Address Line 2")
                                                        .address3("Example Address Line 3")
                                                        .build())
                                                .build())
                                        .contactMethods(Optional.of(
                                                Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                        .value("jane.doe@example.com")
                                                        .build()))))
                                        .notificationLanguage(CustomerNotificationLanguage.EN)
                                        .build())
                                .ipAddress(IpAddress.builder()
                                        .type(IpAddressType.IPV_4)
                                        .value("104.18.24.203")
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
                + "  \"operator\": \"Jane\",\n"
                + "  \"mitAgreement\": \"unscheduled\",\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"Customer-12\",\n"
                + "    \"billingAddress\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"shippingAddress\": {\n"
                + "      \"recipientName\": \"Sarah Hopper\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"notificationLanguage\": \"en\"\n"
                + "  },\n"
                + "  \"ipAddress\": {\n"
                + "    \"type\": \"ipv4\",\n"
                + "    \"value\": \"104.18.24.203\"\n"
                + "  },\n"
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
                + "  \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"mitAgreement\": \"unscheduled\",\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"Customer-12\",\n"
                + "    \"billingAddress\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"shippingAddress\": {\n"
                + "      \"recipientName\": \"Sarah Hopper\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"notificationLanguage\": \"en\"\n"
                + "  },\n"
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
                + "  },\n"
                + "  \"token\": \"296753123456\",\n"
                + "  \"status\": \"notValidated\",\n"
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
                                "{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"processingTerminalId\":\"1234001\",\"mitAgreement\":\"unscheduled\",\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"Customer-12\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"source\":{\"cardholderName\":\"Sarah Hazel Hopper\",\"cardNumber\":\"4539858876047062\",\"expiryDate\":\"1225\",\"cardType\":\"cardType\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"},\"type\":\"card\"},\"token\":\"296753123456\",\"status\":\"notValidated\",\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        SecureTokenWithAccountType response = client.tokenization()
                .secureTokens()
                .retrieve(
                        "1234001",
                        "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
                        RetrieveSecureTokensRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"mitAgreement\": \"unscheduled\",\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"Customer-12\",\n"
                + "    \"billingAddress\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"shippingAddress\": {\n"
                + "      \"recipientName\": \"Sarah Hopper\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"notificationLanguage\": \"en\"\n"
                + "  },\n"
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
                + "  },\n"
                + "  \"token\": \"296753123456\",\n"
                + "  \"status\": \"notValidated\",\n"
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
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.tokenization()
                .secureTokens()
                .delete(
                        "1234001",
                        "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
                        DeleteSecureTokensRequest.builder().build());
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
                                "{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"processingTerminalId\":\"1234001\",\"mitAgreement\":\"unscheduled\",\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"Customer-12\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"source\":{\"cardholderName\":\"Sarah Hazel Hopper\",\"cardNumber\":\"4539858876047062\",\"expiryDate\":\"1225\",\"cardType\":\"cardType\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"},\"type\":\"card\"},\"token\":\"296753123456\",\"status\":\"notValidated\",\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        SecureToken response = client.tokenization()
                .secureTokens()
                .partiallyUpdate(
                        "1234001",
                        "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
                        PartiallyUpdateSecureTokensRequest.builder()
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
                + "  \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"mitAgreement\": \"unscheduled\",\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"Customer-12\",\n"
                + "    \"billingAddress\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"shippingAddress\": {\n"
                + "      \"recipientName\": \"Sarah Hopper\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"notificationLanguage\": \"en\"\n"
                + "  },\n"
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
                + "  },\n"
                + "  \"token\": \"296753123456\",\n"
                + "  \"status\": \"notValidated\",\n"
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
    public void testUpdateAccount() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"processingTerminalId\":\"1234001\",\"mitAgreement\":\"unscheduled\",\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"Customer-12\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"source\":{\"cardholderName\":\"Sarah Hazel Hopper\",\"cardNumber\":\"4539858876047062\",\"expiryDate\":\"1225\",\"cardType\":\"cardType\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"},\"type\":\"card\"},\"token\":\"296753123456\",\"status\":\"notValidated\",\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        SecureToken response = client.tokenization()
                .secureTokens()
                .updateAccount(
                        "1234001",
                        "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
                        UpdateAccountSecureTokensRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(AccountUpdate.singleUseToken(SingleUseTokenAccountUpdate.builder()
                                        .token(
                                                "abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
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
                + "  \"token\": \"abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890\",\n"
                + "  \"type\": \"singleUseToken\"\n"
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
                + "  \"secureTokenId\": \"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"mitAgreement\": \"unscheduled\",\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"Customer-12\",\n"
                + "    \"billingAddress\": {\n"
                + "      \"address1\": \"1 Example Ave.\",\n"
                + "      \"address2\": \"Example Address Line 2\",\n"
                + "      \"address3\": \"Example Address Line 3\",\n"
                + "      \"city\": \"Chicago\",\n"
                + "      \"state\": \"Illinois\",\n"
                + "      \"country\": \"US\",\n"
                + "      \"postalCode\": \"60056\"\n"
                + "    },\n"
                + "    \"shippingAddress\": {\n"
                + "      \"recipientName\": \"Sarah Hopper\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"notificationLanguage\": \"en\"\n"
                + "  },\n"
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
                + "  },\n"
                + "  \"token\": \"296753123456\",\n"
                + "  \"status\": \"notValidated\",\n"
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
