package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.paymentfeatures.cards.requests.BalanceInquiry;
import com.payroc.api.resources.paymentfeatures.cards.requests.BinLookup;
import com.payroc.api.resources.paymentfeatures.cards.requests.CardVerificationRequest;
import com.payroc.api.resources.paymentfeatures.cards.requests.FxRateInquiry;
import com.payroc.api.resources.paymentfeatures.cards.types.BalanceInquiryCard;
import com.payroc.api.resources.paymentfeatures.cards.types.BinLookupCard;
import com.payroc.api.resources.paymentfeatures.cards.types.CardVerificationRequestCard;
import com.payroc.api.resources.paymentfeatures.cards.types.FxRateInquiryChannel;
import com.payroc.api.resources.paymentfeatures.cards.types.FxRateInquiryPaymentMethod;
import com.payroc.api.types.Balance;
import com.payroc.api.types.CardInfo;
import com.payroc.api.types.CardPayload;
import com.payroc.api.types.CardPayloadCardDetails;
import com.payroc.api.types.CardVerificationResult;
import com.payroc.api.types.Currency;
import com.payroc.api.types.Device;
import com.payroc.api.types.DeviceModel;
import com.payroc.api.types.FxRate;
import com.payroc.api.types.RawCardDetails;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentFeaturesCardsWireTest {
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
    public void testVerifyCard() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/PaymentFeaturesCardsWireTest_testVerifyCard_response.json")));
        CardVerificationResult response = client.paymentFeatures()
                .cards()
                .verifyCard(CardVerificationRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .processingTerminalId("1234001")
                        .card(CardVerificationRequestCard.card(CardPayload.builder()
                                .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                        .device(Device.builder()
                                                .model(DeviceModel.BBPOS_CHP)
                                                .serialNumber("1850010868")
                                                .build())
                                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
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
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"card\": {\n"
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
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/PaymentFeaturesCardsWireTest_testVerifyCard_response.json");
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
    public void testViewEbtBalance() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"card\":{\"type\":\"Common Benefit Identification Card\",\"entryMethod\":\"keyed\",\"cardholderName\":\"Sarah Hazel Hopper\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"cardNumber\":\"453985******7062\",\"expiryDate\":\"1230\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"securityChecks\":{\"cvvResult\":\"M\",\"avsResult\":\"Y\"},\"emvTags\":[{\"hex\":\"9F36\",\"value\":\"001234\"},{\"hex\":\"5F2A\",\"value\":\"0840\"}],\"balances\":[{\"benefitCategory\":\"cash\",\"amount\":10000,\"currency\":\"USD\"}]},\"responseCode\":\"A\",\"responseMessage\":\"Approved\"}"));
        Balance response = client.paymentFeatures()
                .cards()
                .viewEbtBalance(BalanceInquiry.builder()
                        .processingTerminalId("1234001")
                        .currency(Currency.USD)
                        .card(BalanceInquiryCard.card(CardPayload.builder()
                                .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                        .device(Device.builder()
                                                .model(DeviceModel.BBPOS_CHP)
                                                .serialNumber("1850010868")
                                                .build())
                                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                        .build()))
                                .build()))
                        .operator("Jane")
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"card\": {\n"
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
                + "  \"card\": {\n"
                + "    \"type\": \"Common Benefit Identification Card\",\n"
                + "    \"entryMethod\": \"keyed\",\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"expiryDate\": \"1230\",\n"
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
                + "    \"securityChecks\": {\n"
                + "      \"cvvResult\": \"M\",\n"
                + "      \"avsResult\": \"Y\"\n"
                + "    },\n"
                + "    \"emvTags\": [\n"
                + "      {\n"
                + "        \"hex\": \"9F36\",\n"
                + "        \"value\": \"001234\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"hex\": \"5F2A\",\n"
                + "        \"value\": \"0840\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"balances\": [\n"
                + "      {\n"
                + "        \"benefitCategory\": \"cash\",\n"
                + "        \"amount\": 10000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"responseCode\": \"A\",\n"
                + "  \"responseMessage\": \"Approved\"\n"
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
    public void testLookupBin() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"type\":\"MASTERCARD\",\"cardNumber\":\"453985******7062\",\"country\":\"US\",\"currency\":\"USD\",\"debit\":false,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"}}"));
        CardInfo response = client.paymentFeatures()
                .cards()
                .lookupBin(BinLookup.builder()
                        .card(BinLookupCard.card(CardPayload.builder()
                                .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                        .device(Device.builder()
                                                .model(DeviceModel.BBPOS_CHP)
                                                .serialNumber("1850010868")
                                                .build())
                                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                        .build()))
                                .build()))
                        .processingTerminalId("1234001")
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"card\": {\n"
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
                + "  \"type\": \"MASTERCARD\",\n"
                + "  \"cardNumber\": \"453985******7062\",\n"
                + "  \"country\": \"US\",\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"debit\": false,\n"
                + "  \"surcharging\": {\n"
                + "    \"allowed\": true,\n"
                + "    \"amount\": 87,\n"
                + "    \"percentage\": 3,\n"
                + "    \"disclosure\": \"A 3% surcharge is applied to cover processing fees.\"\n"
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
    public void testRetrieveFxRates() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"baseAmount\":10000,\"baseCurrency\":\"EUR\",\"inquiryResult\":{\"dccOffered\":true,\"causeOfRejection\":\"Service unavailable\"},\"dccOffer\":{\"accepted\":true,\"offerReference\":\"DCC123456789\",\"fxAmount\":16125,\"fxCurrency\":\"JPY\",\"fxCurrencyCode\":\"392\",\"fxCurrencyExponent\":0,\"fxRate\":161.2542,\"markup\":3,\"markupText\":\"3.5% mark-up applied.\",\"provider\":\"FEXCO\",\"source\":\"REUTERS WHOLESALE INTERBANK\"},\"cardInfo\":{\"type\":\"MASTERCARD\",\"cardNumber\":\"453985******7062\",\"country\":\"country\",\"currency\":\"AED\",\"debit\":true,\"surcharging\":{\"allowed\":true,\"amount\":87,\"percentage\":3,\"disclosure\":\"A 3% surcharge is applied to cover processing fees.\"}}}"));
        FxRate response = client.paymentFeatures()
                .cards()
                .retrieveFxRates(FxRateInquiry.builder()
                        .channel(FxRateInquiryChannel.WEB)
                        .processingTerminalId("1234001")
                        .baseAmount(10000L)
                        .baseCurrency(Currency.USD)
                        .paymentMethod(FxRateInquiryPaymentMethod.card(CardPayload.builder()
                                .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                        .device(Device.builder()
                                                .model(DeviceModel.BBPOS_CHP)
                                                .serialNumber("1850010868")
                                                .build())
                                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                        .build()))
                                .build()))
                        .operator("Jane")
                        .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"channel\": \"web\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"baseAmount\": 10000,\n"
                + "  \"baseCurrency\": \"USD\",\n"
                + "  \"paymentMethod\": {\n"
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
                + "  \"baseAmount\": 10000,\n"
                + "  \"baseCurrency\": \"EUR\",\n"
                + "  \"inquiryResult\": {\n"
                + "    \"dccOffered\": true,\n"
                + "    \"causeOfRejection\": \"Service unavailable\"\n"
                + "  },\n"
                + "  \"dccOffer\": {\n"
                + "    \"accepted\": true,\n"
                + "    \"offerReference\": \"DCC123456789\",\n"
                + "    \"fxAmount\": 16125,\n"
                + "    \"fxCurrency\": \"JPY\",\n"
                + "    \"fxCurrencyCode\": \"392\",\n"
                + "    \"fxCurrencyExponent\": 0,\n"
                + "    \"fxRate\": 161.2542,\n"
                + "    \"markup\": 3,\n"
                + "    \"markupText\": \"3.5% mark-up applied.\",\n"
                + "    \"provider\": \"FEXCO\",\n"
                + "    \"source\": \"REUTERS WHOLESALE INTERBANK\"\n"
                + "  },\n"
                + "  \"cardInfo\": {\n"
                + "    \"type\": \"MASTERCARD\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"country\": \"country\",\n"
                + "    \"currency\": \"AED\",\n"
                + "    \"debit\": true,\n"
                + "    \"surcharging\": {\n"
                + "      \"allowed\": true,\n"
                + "      \"amount\": 87,\n"
                + "      \"percentage\": 3,\n"
                + "      \"disclosure\": \"A 3% surcharge is applied to cover processing fees.\"\n"
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
