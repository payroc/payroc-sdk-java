package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.paymentlinks.requests.CreatePaymentLinksRequest;
import com.payroc.api.resources.paymentlinks.requests.DeactivatePaymentLinksRequest;
import com.payroc.api.resources.paymentlinks.requests.PartiallyUpdatePaymentLinksRequest;
import com.payroc.api.resources.paymentlinks.requests.RetrievePaymentLinksRequest;
import com.payroc.api.resources.paymentlinks.types.CreatePaymentLinksRequestBody;
import com.payroc.api.resources.paymentlinks.types.CreatePaymentLinksResponse;
import com.payroc.api.resources.paymentlinks.types.DeactivatePaymentLinksResponse;
import com.payroc.api.resources.paymentlinks.types.PartiallyUpdatePaymentLinksResponse;
import com.payroc.api.resources.paymentlinks.types.RetrievePaymentLinksResponse;
import com.payroc.api.types.Currency;
import com.payroc.api.types.MultiUsePaymentLink;
import com.payroc.api.types.MultiUsePaymentLinkAuthType;
import com.payroc.api.types.MultiUsePaymentLinkOrder;
import com.payroc.api.types.MultiUsePaymentLinkOrderCharge;
import com.payroc.api.types.MultiUsePaymentLinkPaymentMethodsItem;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.PromptPaymentLinkCharge;
import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PaymentLinksWireTest {
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
                                "{\"paymentLinkId\":\"JZURRJBUPS\",\"merchantReference\":\"LinkRef6543\",\"order\":{\"description\":\"Pie It Forward charitable trust donation\",\"charge\":{\"currency\":\"AED\",\"type\":\"prompt\"}},\"authType\":\"sale\",\"paymentMethods\":[\"card\"],\"customLabels\":[{\"element\":\"paymentButton\",\"label\":\"SUPPORT US\"}],\"assets\":{\"paymentUrl\":\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\"paymentButton\":\"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"},\"status\":\"active\",\"createdOn\":\"2024-07-02\",\"expiresOn\":\"2024-08-02\",\"credentialOnFile\":{\"tokenize\":true,\"mitAgreement\":\"unscheduled\"},\"type\":\"multiUse\"}"));
        CreatePaymentLinksResponse response = client.paymentLinks()
                .create(
                        "1234001",
                        CreatePaymentLinksRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(CreatePaymentLinksRequestBody.multiUse(MultiUsePaymentLink.builder()
                                        .merchantReference("LinkRef6543")
                                        .order(MultiUsePaymentLinkOrder.builder()
                                                .charge(MultiUsePaymentLinkOrderCharge.prompt(
                                                        PromptPaymentLinkCharge.builder()
                                                                .currency(Currency.AED)
                                                                .build()))
                                                .build())
                                        .authType(MultiUsePaymentLinkAuthType.SALE)
                                        .paymentMethods(Arrays.asList(MultiUsePaymentLinkPaymentMethodsItem.CARD))
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
                + "  \"merchantReference\": \"LinkRef6543\",\n"
                + "  \"order\": {\n"
                + "    \"charge\": {\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"type\": \"prompt\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"authType\": \"sale\",\n"
                + "  \"paymentMethods\": [\n"
                + "    \"card\"\n"
                + "  ],\n"
                + "  \"type\": \"multiUse\"\n"
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
                + "  \"paymentLinkId\": \"JZURRJBUPS\",\n"
                + "  \"merchantReference\": \"LinkRef6543\",\n"
                + "  \"order\": {\n"
                + "    \"description\": \"Pie It Forward charitable trust donation\",\n"
                + "    \"charge\": {\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"type\": \"prompt\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"authType\": \"sale\",\n"
                + "  \"paymentMethods\": [\n"
                + "    \"card\"\n"
                + "  ],\n"
                + "  \"customLabels\": [\n"
                + "    {\n"
                + "      \"element\": \"paymentButton\",\n"
                + "      \"label\": \"SUPPORT US\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"assets\": {\n"
                + "    \"paymentUrl\": \"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\n"
                + "    \"paymentButton\": \"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"\n"
                + "  },\n"
                + "  \"status\": \"active\",\n"
                + "  \"createdOn\": \"2024-07-02\",\n"
                + "  \"expiresOn\": \"2024-08-02\",\n"
                + "  \"credentialOnFile\": {\n"
                + "    \"tokenize\": true,\n"
                + "    \"mitAgreement\": \"unscheduled\"\n"
                + "  },\n"
                + "  \"type\": \"multiUse\"\n"
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
                                "{\"paymentLinkId\":\"JZURRJBUPS\",\"merchantReference\":\"LinkRef6543\",\"order\":{\"description\":\"Pie It Forward charitable trust donation\",\"charge\":{\"currency\":\"AED\",\"type\":\"prompt\"}},\"authType\":\"sale\",\"paymentMethods\":[\"card\"],\"customLabels\":[{\"element\":\"paymentButton\",\"label\":\"SUPPORT US\"}],\"assets\":{\"paymentUrl\":\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\"paymentButton\":\"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"},\"status\":\"active\",\"createdOn\":\"2024-07-02\",\"expiresOn\":\"2024-08-02\",\"credentialOnFile\":{\"tokenize\":true,\"mitAgreement\":\"unscheduled\"},\"type\":\"multiUse\"}"));
        RetrievePaymentLinksResponse response = client.paymentLinks()
                .retrieve("JZURRJBUPS", RetrievePaymentLinksRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"paymentLinkId\": \"JZURRJBUPS\",\n"
                + "  \"merchantReference\": \"LinkRef6543\",\n"
                + "  \"order\": {\n"
                + "    \"description\": \"Pie It Forward charitable trust donation\",\n"
                + "    \"charge\": {\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"type\": \"prompt\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"authType\": \"sale\",\n"
                + "  \"paymentMethods\": [\n"
                + "    \"card\"\n"
                + "  ],\n"
                + "  \"customLabels\": [\n"
                + "    {\n"
                + "      \"element\": \"paymentButton\",\n"
                + "      \"label\": \"SUPPORT US\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"assets\": {\n"
                + "    \"paymentUrl\": \"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\n"
                + "    \"paymentButton\": \"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"\n"
                + "  },\n"
                + "  \"status\": \"active\",\n"
                + "  \"createdOn\": \"2024-07-02\",\n"
                + "  \"expiresOn\": \"2024-08-02\",\n"
                + "  \"credentialOnFile\": {\n"
                + "    \"tokenize\": true,\n"
                + "    \"mitAgreement\": \"unscheduled\"\n"
                + "  },\n"
                + "  \"type\": \"multiUse\"\n"
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
                                "{\"paymentLinkId\":\"JZURRJBUPS\",\"merchantReference\":\"LinkRef6543\",\"order\":{\"description\":\"Pie It Forward charitable trust donation\",\"charge\":{\"currency\":\"AED\",\"type\":\"prompt\"}},\"authType\":\"sale\",\"paymentMethods\":[\"card\"],\"customLabels\":[{\"element\":\"paymentButton\",\"label\":\"SUPPORT US\"}],\"assets\":{\"paymentUrl\":\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\"paymentButton\":\"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"},\"status\":\"active\",\"createdOn\":\"2024-07-02\",\"expiresOn\":\"2024-08-02\",\"credentialOnFile\":{\"tokenize\":true,\"mitAgreement\":\"unscheduled\"},\"type\":\"multiUse\"}"));
        PartiallyUpdatePaymentLinksResponse response = client.paymentLinks()
                .partiallyUpdate(
                        "JZURRJBUPS",
                        PartiallyUpdatePaymentLinksRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(Arrays.asList(PatchDocument.remove(
                                        PatchRemove.builder().path("path").build())))
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
        String expectedRequestBody =
                "" + "[\n" + "  {\n" + "    \"path\": \"path\",\n" + "    \"op\": \"remove\"\n" + "  }\n" + "]";
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
                + "  \"paymentLinkId\": \"JZURRJBUPS\",\n"
                + "  \"merchantReference\": \"LinkRef6543\",\n"
                + "  \"order\": {\n"
                + "    \"description\": \"Pie It Forward charitable trust donation\",\n"
                + "    \"charge\": {\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"type\": \"prompt\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"authType\": \"sale\",\n"
                + "  \"paymentMethods\": [\n"
                + "    \"card\"\n"
                + "  ],\n"
                + "  \"customLabels\": [\n"
                + "    {\n"
                + "      \"element\": \"paymentButton\",\n"
                + "      \"label\": \"SUPPORT US\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"assets\": {\n"
                + "    \"paymentUrl\": \"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\n"
                + "    \"paymentButton\": \"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"\n"
                + "  },\n"
                + "  \"status\": \"active\",\n"
                + "  \"createdOn\": \"2024-07-02\",\n"
                + "  \"expiresOn\": \"2024-08-02\",\n"
                + "  \"credentialOnFile\": {\n"
                + "    \"tokenize\": true,\n"
                + "    \"mitAgreement\": \"unscheduled\"\n"
                + "  },\n"
                + "  \"type\": \"multiUse\"\n"
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
                                "{\"paymentLinkId\":\"JZURRJBUPS\",\"merchantReference\":\"LinkRef6543\",\"order\":{\"description\":\"Pie It Forward charitable trust donation\",\"charge\":{\"currency\":\"AED\",\"type\":\"prompt\"}},\"authType\":\"sale\",\"paymentMethods\":[\"card\"],\"customLabels\":[{\"element\":\"paymentButton\",\"label\":\"SUPPORT US\"}],\"assets\":{\"paymentUrl\":\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\"paymentButton\":\"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"},\"status\":\"active\",\"createdOn\":\"2024-07-02\",\"expiresOn\":\"2024-08-02\",\"credentialOnFile\":{\"tokenize\":true,\"mitAgreement\":\"unscheduled\"},\"type\":\"multiUse\"}"));
        DeactivatePaymentLinksResponse response = client.paymentLinks()
                .deactivate(
                        "JZURRJBUPS", DeactivatePaymentLinksRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("POST", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"paymentLinkId\": \"JZURRJBUPS\",\n"
                + "  \"merchantReference\": \"LinkRef6543\",\n"
                + "  \"order\": {\n"
                + "    \"description\": \"Pie It Forward charitable trust donation\",\n"
                + "    \"charge\": {\n"
                + "      \"currency\": \"AED\",\n"
                + "      \"type\": \"prompt\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"authType\": \"sale\",\n"
                + "  \"paymentMethods\": [\n"
                + "    \"card\"\n"
                + "  ],\n"
                + "  \"customLabels\": [\n"
                + "    {\n"
                + "      \"element\": \"paymentButton\",\n"
                + "      \"label\": \"SUPPORT US\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"assets\": {\n"
                + "    \"paymentUrl\": \"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\",\n"
                + "    \"paymentButton\": \"<a href=\\\"https://payments.payroc.com/merchant/pay-by-link?token=7c2fc08c-cb0e-44ba-8bcd-cf6de6eb3206\\\" \\ntarget=\\\"_blank\\\" style=\\\"color: #ffffff; background-color: #6C7A89; font-size: 18px; font-family: Helvetica, Arial, sans-serif; \\ntext-decoration: none; border-radius: 30px; padding: 14px 28px; display: inline-block;\\\">Pay Now</a>\\n\"\n"
                + "  },\n"
                + "  \"status\": \"active\",\n"
                + "  \"createdOn\": \"2024-07-02\",\n"
                + "  \"expiresOn\": \"2024-08-02\",\n"
                + "  \"credentialOnFile\": {\n"
                + "    \"tokenize\": true,\n"
                + "    \"mitAgreement\": \"unscheduled\"\n"
                + "  },\n"
                + "  \"type\": \"multiUse\"\n"
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
