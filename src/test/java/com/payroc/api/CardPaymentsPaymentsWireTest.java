package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.cardpayments.payments.requests.PaymentAdjustment;
import com.payroc.api.resources.cardpayments.payments.requests.PaymentCapture;
import com.payroc.api.resources.cardpayments.payments.requests.PaymentRequest;
import com.payroc.api.resources.cardpayments.payments.requests.RetrievePaymentsRequest;
import com.payroc.api.resources.cardpayments.payments.types.PaymentAdjustmentAdjustmentsItem;
import com.payroc.api.resources.cardpayments.payments.types.PaymentRequestChannel;
import com.payroc.api.resources.cardpayments.payments.types.PaymentRequestPaymentMethod;
import com.payroc.api.types.Address;
import com.payroc.api.types.CardPayload;
import com.payroc.api.types.CardPayloadCardDetails;
import com.payroc.api.types.Currency;
import com.payroc.api.types.CustomField;
import com.payroc.api.types.Customer;
import com.payroc.api.types.CustomerAdjustment;
import com.payroc.api.types.Device;
import com.payroc.api.types.DeviceModel;
import com.payroc.api.types.ItemizedBreakdownRequest;
import com.payroc.api.types.LineItemRequest;
import com.payroc.api.types.OrderAdjustment;
import com.payroc.api.types.Payment;
import com.payroc.api.types.PaymentOrderRequest;
import com.payroc.api.types.RawCardDetails;
import com.payroc.api.types.RetrievedPayment;
import com.payroc.api.types.Shipping;
import java.util.Arrays;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CardPaymentsPaymentsWireTest {
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
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Large Pepperoni Pizza\",\"amount\":4999,\"currency\":\"USD\",\"dccOffer\":{\"accepted\":true,\"offerReference\":\"DCC123456789\",\"fxAmount\":3955,\"fxCurrency\":\"AED\",\"fxCurrencyCode\":\"CAD\",\"fxCurrencyExponent\":2,\"fxRate\":1.37,\"markup\":3.5,\"markupText\":\"3.5% mark-up applied.\",\"provider\":\"DCC Provider Inc.\",\"source\":\"European Central Bank\"},\"standingInstructions\":{\"sequence\":\"first\",\"processingModel\":\"unscheduled\",\"referenceDataOfFirstTxn\":{\"paymentId\":\"M2MJOG6O2Y\",\"cardSchemeReferenceId\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"}},\"breakdown\":{\"subtotal\":2899,\"cashbackAmount\":0,\"tip\":{\"type\":\"percentage\",\"amount\":500,\"percentage\":10},\"surcharge\":{\"bypass\":false,\"amount\":50,\"percentage\":2},\"dualPricing\":{\"offered\":false,\"choiceRate\":{\"applied\":true,\"rate\":2.5,\"amount\":75}},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}],\"dutyAmount\":0,\"freightAmount\":0,\"convenienceFee\":{\"amount\":25},\"items\":[{\"commodityCode\":\"5812-0111\",\"productCode\":\"PZA-001-LG\",\"description\":\"Large Pepperoni Pizza\",\"unitPrice\":2709,\"quantity\":1,\"discountRate\":5,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}]}]}},\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"CustomerCode234567\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"card\":{\"type\":\"MasterCard\",\"entryMethod\":\"keyed\",\"cardholderName\":\"Sarah Hazel Hopper\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"cardNumber\":\"453985******7062\",\"expiryDate\":\"1225\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"securityChecks\":{\"cvvResult\":\"M\",\"avsResult\":\"Y\"},\"emvTags\":[{\"hex\":\"9F36\",\"value\":\"001234\"},{\"hex\":\"5F2A\",\"value\":\"0840\"}],\"balances\":[{\"benefitCategory\":\"cash\",\"amount\":50000,\"currency\":\"USD\"},{\"benefitCategory\":\"foodStamp\",\"amount\":10000,\"currency\":\"USD\"}]},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"supportedOperations\":[\"capture\",\"fullyReverse\",\"partiallyReverse\",\"incrementAuthorization\",\"adjustTip\",\"setAsPending\"],\"transactionResult\":{\"type\":\"sale\",\"ebtType\":\"cashPurchase\",\"status\":\"ready\",\"approvalCode\":\"OK3\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"OK3\",\"processorResponseCode\":\"processorResponseCode\",\"cardSchemeReferenceId\":\"cardSchemeReferenceId\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Payment response = client.cardPayments()
                .payments()
                .create(PaymentRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .channel(PaymentRequestChannel.WEB)
                        .processingTerminalId("1234001")
                        .order(PaymentOrderRequest.builder()
                                .orderId("OrderRef6543")
                                .description("Large Pepperoni Pizza")
                                .amount(4999L)
                                .currency(Currency.USD)
                                .build())
                        .paymentMethod(PaymentRequestPaymentMethod.card(CardPayload.builder()
                                .cardDetails(CardPayloadCardDetails.raw(RawCardDetails.builder()
                                        .device(Device.builder()
                                                .model(DeviceModel.BBPOS_CHP)
                                                .serialNumber("1850010868")
                                                .build())
                                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                        .build()))
                                .build()))
                        .operator("Jane")
                        .customer(Customer.builder()
                                .firstName("Sarah")
                                .lastName("Hopper")
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
                + "  \"channel\": \"web\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\"\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
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
                + "    }\n"
                + "  },\n"
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
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"dccOffer\": {\n"
                + "      \"accepted\": true,\n"
                + "      \"offerReference\": \"DCC123456789\",\n"
                + "      \"fxAmount\": 3955,\n"
                + "      \"fxCurrency\": \"AED\",\n"
                + "      \"fxCurrencyCode\": \"CAD\",\n"
                + "      \"fxCurrencyExponent\": 2,\n"
                + "      \"fxRate\": 1.37,\n"
                + "      \"markup\": 3.5,\n"
                + "      \"markupText\": \"3.5% mark-up applied.\",\n"
                + "      \"provider\": \"DCC Provider Inc.\",\n"
                + "      \"source\": \"European Central Bank\"\n"
                + "    },\n"
                + "    \"standingInstructions\": {\n"
                + "      \"sequence\": \"first\",\n"
                + "      \"processingModel\": \"unscheduled\",\n"
                + "      \"referenceDataOfFirstTxn\": {\n"
                + "        \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "        \"cardSchemeReferenceId\": \"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 2899,\n"
                + "      \"cashbackAmount\": 0,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 500,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 50,\n"
                + "        \"percentage\": 2\n"
                + "      },\n"
                + "      \"dualPricing\": {\n"
                + "        \"offered\": false,\n"
                + "        \"choiceRate\": {\n"
                + "          \"applied\": true,\n"
                + "          \"rate\": 2.5,\n"
                + "          \"amount\": 75\n"
                + "        }\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 7,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ],\n"
                + "      \"dutyAmount\": 0,\n"
                + "      \"freightAmount\": 0,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"items\": [\n"
                + "        {\n"
                + "          \"commodityCode\": \"5812-0111\",\n"
                + "          \"productCode\": \"PZA-001-LG\",\n"
                + "          \"description\": \"Large Pepperoni Pizza\",\n"
                + "          \"unitPrice\": 2709,\n"
                + "          \"quantity\": 1,\n"
                + "          \"discountRate\": 5,\n"
                + "          \"taxes\": [\n"
                + "            {\n"
                + "              \"name\": \"Sales Tax\",\n"
                + "              \"rate\": 7,\n"
                + "              \"amount\": 190\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"CustomerCode234567\",\n"
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
                + "  \"card\": {\n"
                + "    \"type\": \"MasterCard\",\n"
                + "    \"entryMethod\": \"keyed\",\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"expiryDate\": \"1225\",\n"
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
                + "        \"amount\": 50000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"benefitCategory\": \"foodStamp\",\n"
                + "        \"amount\": 10000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      }\n"
                + "    ]\n"
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
                + "  \"supportedOperations\": [\n"
                + "    \"capture\",\n"
                + "    \"fullyReverse\",\n"
                + "    \"partiallyReverse\",\n"
                + "    \"incrementAuthorization\",\n"
                + "    \"adjustTip\",\n"
                + "    \"setAsPending\"\n"
                + "  ],\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"sale\",\n"
                + "    \"ebtType\": \"cashPurchase\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"approvalCode\": \"OK3\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"OK3\",\n"
                + "    \"processorResponseCode\": \"processorResponseCode\",\n"
                + "    \"cardSchemeReferenceId\": \"cardSchemeReferenceId\"\n"
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
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Large Pepperoni Pizza\",\"amount\":4999,\"currency\":\"USD\",\"dccOffer\":{\"accepted\":true,\"offerReference\":\"DCC123456789\",\"fxAmount\":3955,\"fxCurrency\":\"AED\",\"fxCurrencyCode\":\"CAD\",\"fxCurrencyExponent\":2,\"fxRate\":1.37,\"markup\":3.5,\"markupText\":\"3.5% mark-up applied.\",\"provider\":\"DCC Provider Inc.\",\"source\":\"European Central Bank\"},\"standingInstructions\":{\"sequence\":\"first\",\"processingModel\":\"unscheduled\",\"referenceDataOfFirstTxn\":{\"paymentId\":\"M2MJOG6O2Y\",\"cardSchemeReferenceId\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"}},\"breakdown\":{\"subtotal\":2899,\"cashbackAmount\":0,\"tip\":{\"type\":\"percentage\",\"amount\":500,\"percentage\":10},\"surcharge\":{\"bypass\":false,\"amount\":50,\"percentage\":2},\"dualPricing\":{\"offered\":false,\"choiceRate\":{\"applied\":true,\"rate\":2.5,\"amount\":75}},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}],\"dutyAmount\":0,\"freightAmount\":0,\"convenienceFee\":{\"amount\":25},\"items\":[{\"commodityCode\":\"5812-0111\",\"productCode\":\"PZA-001-LG\",\"description\":\"Large Pepperoni Pizza\",\"unitPrice\":2709,\"quantity\":1,\"discountRate\":5,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}]}]}},\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"CustomerCode234567\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"card\":{\"type\":\"MasterCard\",\"entryMethod\":\"keyed\",\"cardholderName\":\"Sarah Hazel Hopper\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"cardNumber\":\"453985******7062\",\"expiryDate\":\"1225\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"securityChecks\":{\"cvvResult\":\"M\",\"avsResult\":\"Y\"},\"emvTags\":[{\"hex\":\"9F36\",\"value\":\"001234\"},{\"hex\":\"5F2A\",\"value\":\"0840\"}],\"balances\":[{\"benefitCategory\":\"cash\",\"amount\":50000,\"currency\":\"USD\"},{\"benefitCategory\":\"foodStamp\",\"amount\":10000,\"currency\":\"USD\"}]},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"supportedOperations\":[\"capture\",\"fullyReverse\",\"partiallyReverse\",\"incrementAuthorization\",\"adjustTip\",\"setAsPending\"],\"transactionResult\":{\"type\":\"sale\",\"ebtType\":\"cashPurchase\",\"status\":\"ready\",\"approvalCode\":\"OK3\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"OK3\",\"processorResponseCode\":\"processorResponseCode\",\"cardSchemeReferenceId\":\"cardSchemeReferenceId\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        RetrievedPayment response = client.cardPayments()
                .payments()
                .retrieve("M2MJOG6O2Y", RetrievePaymentsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"dccOffer\": {\n"
                + "      \"accepted\": true,\n"
                + "      \"offerReference\": \"DCC123456789\",\n"
                + "      \"fxAmount\": 3955,\n"
                + "      \"fxCurrency\": \"AED\",\n"
                + "      \"fxCurrencyCode\": \"CAD\",\n"
                + "      \"fxCurrencyExponent\": 2,\n"
                + "      \"fxRate\": 1.37,\n"
                + "      \"markup\": 3.5,\n"
                + "      \"markupText\": \"3.5% mark-up applied.\",\n"
                + "      \"provider\": \"DCC Provider Inc.\",\n"
                + "      \"source\": \"European Central Bank\"\n"
                + "    },\n"
                + "    \"standingInstructions\": {\n"
                + "      \"sequence\": \"first\",\n"
                + "      \"processingModel\": \"unscheduled\",\n"
                + "      \"referenceDataOfFirstTxn\": {\n"
                + "        \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "        \"cardSchemeReferenceId\": \"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 2899,\n"
                + "      \"cashbackAmount\": 0,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 500,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 50,\n"
                + "        \"percentage\": 2\n"
                + "      },\n"
                + "      \"dualPricing\": {\n"
                + "        \"offered\": false,\n"
                + "        \"choiceRate\": {\n"
                + "          \"applied\": true,\n"
                + "          \"rate\": 2.5,\n"
                + "          \"amount\": 75\n"
                + "        }\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 7,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ],\n"
                + "      \"dutyAmount\": 0,\n"
                + "      \"freightAmount\": 0,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"items\": [\n"
                + "        {\n"
                + "          \"commodityCode\": \"5812-0111\",\n"
                + "          \"productCode\": \"PZA-001-LG\",\n"
                + "          \"description\": \"Large Pepperoni Pizza\",\n"
                + "          \"unitPrice\": 2709,\n"
                + "          \"quantity\": 1,\n"
                + "          \"discountRate\": 5,\n"
                + "          \"taxes\": [\n"
                + "            {\n"
                + "              \"name\": \"Sales Tax\",\n"
                + "              \"rate\": 7,\n"
                + "              \"amount\": 190\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"CustomerCode234567\",\n"
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
                + "  \"card\": {\n"
                + "    \"type\": \"MasterCard\",\n"
                + "    \"entryMethod\": \"keyed\",\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"expiryDate\": \"1225\",\n"
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
                + "        \"amount\": 50000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"benefitCategory\": \"foodStamp\",\n"
                + "        \"amount\": 10000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      }\n"
                + "    ]\n"
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
                + "  \"supportedOperations\": [\n"
                + "    \"capture\",\n"
                + "    \"fullyReverse\",\n"
                + "    \"partiallyReverse\",\n"
                + "    \"incrementAuthorization\",\n"
                + "    \"adjustTip\",\n"
                + "    \"setAsPending\"\n"
                + "  ],\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"sale\",\n"
                + "    \"ebtType\": \"cashPurchase\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"approvalCode\": \"OK3\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"OK3\",\n"
                + "    \"processorResponseCode\": \"processorResponseCode\",\n"
                + "    \"cardSchemeReferenceId\": \"cardSchemeReferenceId\"\n"
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
    public void testAdjust() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Example payment\",\"amount\":4999,\"currency\":\"USD\",\"dccOffer\":{\"accepted\":true,\"offerReference\":\"DCC123456789\",\"fxAmount\":3955,\"fxCurrency\":\"AED\",\"fxCurrencyCode\":\"CAD\",\"fxCurrencyExponent\":2,\"fxRate\":1.37,\"markup\":3.5,\"markupText\":\"3.5% mark-up applied.\",\"provider\":\"DCC Provider Inc.\",\"source\":\"European Central Bank\"},\"standingInstructions\":{\"sequence\":\"first\",\"processingModel\":\"unscheduled\",\"referenceDataOfFirstTxn\":{\"paymentId\":\"M2MJOG6O2Y\",\"cardSchemeReferenceId\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"}},\"breakdown\":{\"subtotal\":2899,\"cashbackAmount\":0,\"tip\":{\"type\":\"percentage\",\"amount\":500,\"percentage\":10},\"surcharge\":{\"bypass\":false,\"amount\":50,\"percentage\":2},\"dualPricing\":{\"offered\":false,\"choiceRate\":{\"applied\":true,\"rate\":2.5,\"amount\":75}},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}],\"dutyAmount\":0,\"freightAmount\":0,\"convenienceFee\":{\"amount\":25},\"items\":[{\"commodityCode\":\"5812-0111\",\"productCode\":\"PZA-001-LG\",\"description\":\"Large Pepperoni Pizza\",\"unitPrice\":2709,\"quantity\":1,\"discountRate\":5,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}]}]}},\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"CustomerCode234567\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"card\":{\"type\":\"MasterCard\",\"entryMethod\":\"keyed\",\"cardholderName\":\"Sarah Hazel Hopper\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"cardNumber\":\"453985******7062\",\"expiryDate\":\"1225\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"securityChecks\":{\"cvvResult\":\"M\",\"avsResult\":\"Y\"},\"emvTags\":[{\"hex\":\"9F36\",\"value\":\"001234\"},{\"hex\":\"5F2A\",\"value\":\"0840\"}],\"balances\":[{\"benefitCategory\":\"cash\",\"amount\":50000,\"currency\":\"USD\"},{\"benefitCategory\":\"foodStamp\",\"amount\":10000,\"currency\":\"USD\"}]},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"supportedOperations\":[\"capture\",\"fullyReverse\",\"partiallyReverse\",\"incrementAuthorization\",\"adjustTip\",\"setAsPending\"],\"transactionResult\":{\"type\":\"sale\",\"ebtType\":\"cashPurchase\",\"status\":\"ready\",\"approvalCode\":\"OK6\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"OK6\",\"processorResponseCode\":\"processorResponseCode\",\"cardSchemeReferenceId\":\"cardSchemeReferenceId\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Payment response = client.cardPayments()
                .payments()
                .adjust(
                        "M2MJOG6O2Y",
                        PaymentAdjustment.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .adjustments(Arrays.asList(
                                        PaymentAdjustmentAdjustmentsItem.customer(
                                                CustomerAdjustment.builder().build()),
                                        PaymentAdjustmentAdjustmentsItem.order(OrderAdjustment.builder()
                                                .amount(4999L)
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
                + "  \"adjustments\": [\n"
                + "    {\n"
                + "      \"type\": \"customer\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"amount\": 4999,\n"
                + "      \"type\": \"order\"\n"
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
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Example payment\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"dccOffer\": {\n"
                + "      \"accepted\": true,\n"
                + "      \"offerReference\": \"DCC123456789\",\n"
                + "      \"fxAmount\": 3955,\n"
                + "      \"fxCurrency\": \"AED\",\n"
                + "      \"fxCurrencyCode\": \"CAD\",\n"
                + "      \"fxCurrencyExponent\": 2,\n"
                + "      \"fxRate\": 1.37,\n"
                + "      \"markup\": 3.5,\n"
                + "      \"markupText\": \"3.5% mark-up applied.\",\n"
                + "      \"provider\": \"DCC Provider Inc.\",\n"
                + "      \"source\": \"European Central Bank\"\n"
                + "    },\n"
                + "    \"standingInstructions\": {\n"
                + "      \"sequence\": \"first\",\n"
                + "      \"processingModel\": \"unscheduled\",\n"
                + "      \"referenceDataOfFirstTxn\": {\n"
                + "        \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "        \"cardSchemeReferenceId\": \"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 2899,\n"
                + "      \"cashbackAmount\": 0,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 500,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 50,\n"
                + "        \"percentage\": 2\n"
                + "      },\n"
                + "      \"dualPricing\": {\n"
                + "        \"offered\": false,\n"
                + "        \"choiceRate\": {\n"
                + "          \"applied\": true,\n"
                + "          \"rate\": 2.5,\n"
                + "          \"amount\": 75\n"
                + "        }\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 7,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ],\n"
                + "      \"dutyAmount\": 0,\n"
                + "      \"freightAmount\": 0,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"items\": [\n"
                + "        {\n"
                + "          \"commodityCode\": \"5812-0111\",\n"
                + "          \"productCode\": \"PZA-001-LG\",\n"
                + "          \"description\": \"Large Pepperoni Pizza\",\n"
                + "          \"unitPrice\": 2709,\n"
                + "          \"quantity\": 1,\n"
                + "          \"discountRate\": 5,\n"
                + "          \"taxes\": [\n"
                + "            {\n"
                + "              \"name\": \"Sales Tax\",\n"
                + "              \"rate\": 7,\n"
                + "              \"amount\": 190\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"CustomerCode234567\",\n"
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
                + "  \"card\": {\n"
                + "    \"type\": \"MasterCard\",\n"
                + "    \"entryMethod\": \"keyed\",\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"expiryDate\": \"1225\",\n"
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
                + "        \"amount\": 50000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"benefitCategory\": \"foodStamp\",\n"
                + "        \"amount\": 10000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      }\n"
                + "    ]\n"
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
                + "  \"supportedOperations\": [\n"
                + "    \"capture\",\n"
                + "    \"fullyReverse\",\n"
                + "    \"partiallyReverse\",\n"
                + "    \"incrementAuthorization\",\n"
                + "    \"adjustTip\",\n"
                + "    \"setAsPending\"\n"
                + "  ],\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"sale\",\n"
                + "    \"ebtType\": \"cashPurchase\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"approvalCode\": \"OK6\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"OK6\",\n"
                + "    \"processorResponseCode\": \"processorResponseCode\",\n"
                + "    \"cardSchemeReferenceId\": \"cardSchemeReferenceId\"\n"
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
    public void testCapture() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"paymentId\":\"M2MJOG6O2Y\",\"processingTerminalId\":\"1234001\",\"operator\":\"Jane\",\"order\":{\"orderId\":\"OrderRef6543\",\"dateTime\":\"2024-07-02T15:30:00Z\",\"description\":\"Large Pepperoni Pizza\",\"amount\":4999,\"currency\":\"USD\",\"dccOffer\":{\"accepted\":true,\"offerReference\":\"DCC123456789\",\"fxAmount\":3955,\"fxCurrency\":\"AED\",\"fxCurrencyCode\":\"CAD\",\"fxCurrencyExponent\":2,\"fxRate\":1.37,\"markup\":3.5,\"markupText\":\"3.5% mark-up applied.\",\"provider\":\"DCC Provider Inc.\",\"source\":\"European Central Bank\"},\"standingInstructions\":{\"sequence\":\"first\",\"processingModel\":\"unscheduled\",\"referenceDataOfFirstTxn\":{\"paymentId\":\"M2MJOG6O2Y\",\"cardSchemeReferenceId\":\"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"}},\"breakdown\":{\"subtotal\":2899,\"cashbackAmount\":0,\"tip\":{\"type\":\"percentage\",\"amount\":500,\"percentage\":10},\"surcharge\":{\"bypass\":false,\"amount\":50,\"percentage\":2},\"dualPricing\":{\"offered\":false,\"choiceRate\":{\"applied\":true,\"rate\":2.5,\"amount\":75}},\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}],\"dutyAmount\":0,\"freightAmount\":0,\"convenienceFee\":{\"amount\":25},\"items\":[{\"commodityCode\":\"5812-0111\",\"productCode\":\"PZA-001-LG\",\"description\":\"Large Pepperoni Pizza\",\"unitPrice\":2709,\"quantity\":1,\"discountRate\":5,\"taxes\":[{\"name\":\"Sales Tax\",\"rate\":7,\"amount\":190}]}]}},\"customer\":{\"firstName\":\"Sarah\",\"lastName\":\"Hopper\",\"dateOfBirth\":\"1990-07-15\",\"referenceNumber\":\"CustomerCode234567\",\"billingAddress\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"shippingAddress\":{\"recipientName\":\"Sarah Hopper\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"}},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"notificationLanguage\":\"en\"},\"card\":{\"type\":\"MasterCard\",\"entryMethod\":\"keyed\",\"cardholderName\":\"Sarah Hazel Hopper\",\"cardholderSignature\":\"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\"cardNumber\":\"453985******7062\",\"expiryDate\":\"1225\",\"secureToken\":{\"secureTokenId\":\"MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa\",\"customerName\":\"Sarah Hazel Hopper\",\"token\":\"296753123456\",\"status\":\"notValidated\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}},\"securityChecks\":{\"cvvResult\":\"M\",\"avsResult\":\"Y\"},\"emvTags\":[{\"hex\":\"9F36\",\"value\":\"001234\"},{\"hex\":\"5F2A\",\"value\":\"0840\"}],\"balances\":[{\"benefitCategory\":\"cash\",\"amount\":50000,\"currency\":\"USD\"},{\"benefitCategory\":\"foodStamp\",\"amount\":10000,\"currency\":\"USD\"}]},\"refunds\":[{\"refundId\":\"CD3HN88U9F\",\"dateTime\":\"2024-07-14T12:25:00Z\",\"currency\":\"AED\",\"amount\":4999,\"status\":\"ready\",\"responseCode\":\"A\",\"responseMessage\":\"Transaction refunded\",\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}}],\"supportedOperations\":[\"capture\",\"fullyReverse\",\"partiallyReverse\",\"incrementAuthorization\",\"adjustTip\",\"setAsPending\"],\"transactionResult\":{\"type\":\"sale\",\"ebtType\":\"cashPurchase\",\"status\":\"ready\",\"approvalCode\":\"OK3\",\"authorizedAmount\":4999,\"currency\":\"USD\",\"responseCode\":\"A\",\"responseMessage\":\"OK3\",\"processorResponseCode\":\"processorResponseCode\",\"cardSchemeReferenceId\":\"cardSchemeReferenceId\"},\"customFields\":[{\"name\":\"yourCustomField\",\"value\":\"abc123\"}]}"));
        Payment response = client.cardPayments()
                .payments()
                .capture(
                        "M2MJOG6O2Y",
                        PaymentCapture.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .processingTerminalId("1234001")
                                .operator("Jane")
                                .amount(4999L)
                                .breakdown(ItemizedBreakdownRequest.builder()
                                        .subtotal(4999L)
                                        .dutyAmount(499L)
                                        .freightAmount(500L)
                                        .items(Optional.of(Arrays.asList(LineItemRequest.builder()
                                                .unitPrice(4000.0)
                                                .quantity(1.0)
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
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"amount\": 4999,\n"
                + "  \"breakdown\": {\n"
                + "    \"subtotal\": 4999,\n"
                + "    \"dutyAmount\": 499,\n"
                + "    \"freightAmount\": 500,\n"
                + "    \"items\": [\n"
                + "      {\n"
                + "        \"unitPrice\": 4000,\n"
                + "        \"quantity\": 1\n"
                + "      }\n"
                + "    ]\n"
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
                + "  \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "  \"processingTerminalId\": \"1234001\",\n"
                + "  \"operator\": \"Jane\",\n"
                + "  \"order\": {\n"
                + "    \"orderId\": \"OrderRef6543\",\n"
                + "    \"dateTime\": \"2024-07-02T15:30:00Z\",\n"
                + "    \"description\": \"Large Pepperoni Pizza\",\n"
                + "    \"amount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"dccOffer\": {\n"
                + "      \"accepted\": true,\n"
                + "      \"offerReference\": \"DCC123456789\",\n"
                + "      \"fxAmount\": 3955,\n"
                + "      \"fxCurrency\": \"AED\",\n"
                + "      \"fxCurrencyCode\": \"CAD\",\n"
                + "      \"fxCurrencyExponent\": 2,\n"
                + "      \"fxRate\": 1.37,\n"
                + "      \"markup\": 3.5,\n"
                + "      \"markupText\": \"3.5% mark-up applied.\",\n"
                + "      \"provider\": \"DCC Provider Inc.\",\n"
                + "      \"source\": \"European Central Bank\"\n"
                + "    },\n"
                + "    \"standingInstructions\": {\n"
                + "      \"sequence\": \"first\",\n"
                + "      \"processingModel\": \"unscheduled\",\n"
                + "      \"referenceDataOfFirstTxn\": {\n"
                + "        \"paymentId\": \"M2MJOG6O2Y\",\n"
                + "        \"cardSchemeReferenceId\": \"ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890\"\n"
                + "      }\n"
                + "    },\n"
                + "    \"breakdown\": {\n"
                + "      \"subtotal\": 2899,\n"
                + "      \"cashbackAmount\": 0,\n"
                + "      \"tip\": {\n"
                + "        \"type\": \"percentage\",\n"
                + "        \"amount\": 500,\n"
                + "        \"percentage\": 10\n"
                + "      },\n"
                + "      \"surcharge\": {\n"
                + "        \"bypass\": false,\n"
                + "        \"amount\": 50,\n"
                + "        \"percentage\": 2\n"
                + "      },\n"
                + "      \"dualPricing\": {\n"
                + "        \"offered\": false,\n"
                + "        \"choiceRate\": {\n"
                + "          \"applied\": true,\n"
                + "          \"rate\": 2.5,\n"
                + "          \"amount\": 75\n"
                + "        }\n"
                + "      },\n"
                + "      \"taxes\": [\n"
                + "        {\n"
                + "          \"name\": \"Sales Tax\",\n"
                + "          \"rate\": 7,\n"
                + "          \"amount\": 190\n"
                + "        }\n"
                + "      ],\n"
                + "      \"dutyAmount\": 0,\n"
                + "      \"freightAmount\": 0,\n"
                + "      \"convenienceFee\": {\n"
                + "        \"amount\": 25\n"
                + "      },\n"
                + "      \"items\": [\n"
                + "        {\n"
                + "          \"commodityCode\": \"5812-0111\",\n"
                + "          \"productCode\": \"PZA-001-LG\",\n"
                + "          \"description\": \"Large Pepperoni Pizza\",\n"
                + "          \"unitPrice\": 2709,\n"
                + "          \"quantity\": 1,\n"
                + "          \"discountRate\": 5,\n"
                + "          \"taxes\": [\n"
                + "            {\n"
                + "              \"name\": \"Sales Tax\",\n"
                + "              \"rate\": 7,\n"
                + "              \"amount\": 190\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"customer\": {\n"
                + "    \"firstName\": \"Sarah\",\n"
                + "    \"lastName\": \"Hopper\",\n"
                + "    \"dateOfBirth\": \"1990-07-15\",\n"
                + "    \"referenceNumber\": \"CustomerCode234567\",\n"
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
                + "  \"card\": {\n"
                + "    \"type\": \"MasterCard\",\n"
                + "    \"entryMethod\": \"keyed\",\n"
                + "    \"cardholderName\": \"Sarah Hazel Hopper\",\n"
                + "    \"cardholderSignature\": \"a1b1c012345678a000b000c0012345d0e0f010g10061a031i001j071k0a1b0c1d0e1234567890120f1g0h1i0j1k0a1b0123451c012d0e1f0g1h0i1j123k1a1b1c1d1e1f1g123h1i1j1k1a1b1c1d1e1f1g123h123i1j123k12340a120a12345b012c0123012d0d1e0f1g0h1i123j123k10000\",\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"expiryDate\": \"1225\",\n"
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
                + "        \"amount\": 50000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      },\n"
                + "      {\n"
                + "        \"benefitCategory\": \"foodStamp\",\n"
                + "        \"amount\": 10000,\n"
                + "        \"currency\": \"USD\"\n"
                + "      }\n"
                + "    ]\n"
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
                + "  \"supportedOperations\": [\n"
                + "    \"capture\",\n"
                + "    \"fullyReverse\",\n"
                + "    \"partiallyReverse\",\n"
                + "    \"incrementAuthorization\",\n"
                + "    \"adjustTip\",\n"
                + "    \"setAsPending\"\n"
                + "  ],\n"
                + "  \"transactionResult\": {\n"
                + "    \"type\": \"sale\",\n"
                + "    \"ebtType\": \"cashPurchase\",\n"
                + "    \"status\": \"ready\",\n"
                + "    \"approvalCode\": \"OK3\",\n"
                + "    \"authorizedAmount\": 4999,\n"
                + "    \"currency\": \"USD\",\n"
                + "    \"responseCode\": \"A\",\n"
                + "    \"responseMessage\": \"OK3\",\n"
                + "    \"processorResponseCode\": \"processorResponseCode\",\n"
                + "    \"cardSchemeReferenceId\": \"cardSchemeReferenceId\"\n"
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
