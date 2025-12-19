package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.terminalorders.requests.RetrieveTerminalOrdersRequest;
import com.payroc.api.types.TerminalOrder;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingTerminalOrdersWireTest {
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
                                "{\"terminalOrderId\":\"12345\",\"status\":\"open\",\"trainingProvider\":\"payroc\",\"shipping\":{\"preferences\":{\"method\":\"nextDay\",\"saturdayDelivery\":true},\"address\":{\"recipientName\":\"Recipient Name\",\"businessName\":\"Company Ltd\",\"addressLine1\":\"1 Example Ave.\",\"addressLine2\":\"Example Address Line 2\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"postalCode\":\"60056\",\"email\":\"example@mail.com\",\"phone\":\"2025550164\"}},\"orderItems\":[{\"links\":[{\"processingTerminalId\":\"processingTerminalId\",\"link\":{\"href\":\"https://api.payroc.com/v1/processing-terminals/38765\",\"rel\":\"processingTerminal\",\"method\":\"get\"}}],\"type\":\"solution\",\"solutionTemplateId\":\"Roc Services_DX8000\",\"solutionQuantity\":1,\"deviceCondition\":\"new\",\"solutionSetup\":{\"timezone\":\"America/Chicago\",\"industryTemplateId\":\"Retail\",\"gatewaySettings\":{\"merchantPortfolioId\":\"Company Ltd\",\"merchantTemplateId\":\"Company Ltd Merchant Template\",\"userTemplateId\":\"Company Ltd User Template\",\"terminalTemplateId\":\"Company Ltd Terminal Template\"},\"applicationSettings\":{\"clerkPrompt\":false,\"security\":{\"refundPassword\":true,\"keyedSalePassword\":false,\"reversalPassword\":true}},\"deviceSettings\":{\"numberOfMobileUsers\":2,\"communicationType\":\"wifi\"},\"batchClosure\":{\"batchCloseTime\":\"23:40\",\"batchCloseType\":\"automatic\"},\"receiptNotifications\":{\"emailReceipt\":true,\"smsReceipt\":false},\"taxes\":[{\"taxRate\":6,\"taxLabel\":\"Sales Tax\"}],\"tips\":{\"enabled\":false},\"tokenization\":true}}],\"createdDate\":\"2020-09-08T12:00:00Z\",\"lastModifiedDate\":\"2020-09-09T12:00:00Z\"}"));
        TerminalOrder response = client.boarding()
                .terminalOrders()
                .retrieve("12345", RetrieveTerminalOrdersRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

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
                + "            \"href\": \"https://api.payroc.com/v1/processing-terminals/38765\",\n"
                + "            \"rel\": \"processingTerminal\",\n"
                + "            \"method\": \"get\"\n"
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
                + "  \"createdDate\": \"2020-09-08T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2020-09-09T12:00:00Z\"\n"
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
