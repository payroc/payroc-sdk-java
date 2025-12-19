package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.processingterminals.requests.RetrieveHostConfigurationProcessingTerminalsRequest;
import com.payroc.api.resources.boarding.processingterminals.requests.RetrieveProcessingTerminalsRequest;
import com.payroc.api.types.HostConfiguration;
import com.payroc.api.types.ProcessingTerminal;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingProcessingTerminalsWireTest {
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
                                "{\"processingTerminalId\":\"011234001\",\"status\":\"active\",\"timezone\":\"Pacific/Midway\",\"program\":\"Payroc Cloud\",\"gateway\":{\"gateway\":\"payroc\",\"terminalTemplateId\":\"Roc Services_DX8000\"},\"batchClosure\":{\"batchCloseTime\":\"23:40\",\"batchCloseType\":\"automatic\"},\"applicationSettings\":{\"invoiceNumberPrompt\":true,\"clerkPrompt\":false},\"features\":{\"tips\":{\"enabled\":false,\"tipPrompt\":false,\"tipAdjust\":true,\"suggestedTips\":{\"enabled\":true,\"tipPercentages\":[\"tipPercentages\",\"tipPercentages\"]}},\"enhancedProcessing\":{\"enabled\":true,\"transactionDataLevel\":\"level2\",\"shippingAddressMode\":\"fullAddress\"},\"ebt\":{\"enabled\":true,\"ebtType\":\"foodStamp\",\"fnsNumber\":\"1234567890\"},\"pinDebitCashback\":false,\"recurringPayments\":true,\"paymentLinks\":{\"enabled\":true,\"logoUrl\":\"LogoPayLink\",\"footerNotes\":\"FooterNotesPayLink\"},\"preAuthorizations\":true,\"offlinePayments\":true},\"taxes\":[{\"taxRate\":1.1,\"taxLabel\":\"taxLabel\"}],\"security\":{\"tokenization\":true,\"avsPrompt\":true,\"avsLevel\":\"fullAddress\",\"cvvPrompt\":true},\"receiptNotifications\":{\"emailReceipt\":true,\"smsReceipt\":true},\"devices\":[{\"manufacturer\":\"manufacturer\",\"model\":\"model\",\"serialNumber\":\"serialNumber\",\"communicationType\":\"bluetooth\"}]}"));
        ProcessingTerminal response = client.boarding()
                .processingTerminals()
                .retrieve(
                        "1234001", RetrieveProcessingTerminalsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"processingTerminalId\": \"011234001\",\n"
                + "  \"status\": \"active\",\n"
                + "  \"timezone\": \"Pacific/Midway\",\n"
                + "  \"program\": \"Payroc Cloud\",\n"
                + "  \"gateway\": {\n"
                + "    \"gateway\": \"payroc\",\n"
                + "    \"terminalTemplateId\": \"Roc Services_DX8000\"\n"
                + "  },\n"
                + "  \"batchClosure\": {\n"
                + "    \"batchCloseTime\": \"23:40\",\n"
                + "    \"batchCloseType\": \"automatic\"\n"
                + "  },\n"
                + "  \"applicationSettings\": {\n"
                + "    \"invoiceNumberPrompt\": true,\n"
                + "    \"clerkPrompt\": false\n"
                + "  },\n"
                + "  \"features\": {\n"
                + "    \"tips\": {\n"
                + "      \"enabled\": false,\n"
                + "      \"tipPrompt\": false,\n"
                + "      \"tipAdjust\": true,\n"
                + "      \"suggestedTips\": {\n"
                + "        \"enabled\": true,\n"
                + "        \"tipPercentages\": [\n"
                + "          \"tipPercentages\",\n"
                + "          \"tipPercentages\"\n"
                + "        ]\n"
                + "      }\n"
                + "    },\n"
                + "    \"enhancedProcessing\": {\n"
                + "      \"enabled\": true,\n"
                + "      \"transactionDataLevel\": \"level2\",\n"
                + "      \"shippingAddressMode\": \"fullAddress\"\n"
                + "    },\n"
                + "    \"ebt\": {\n"
                + "      \"enabled\": true,\n"
                + "      \"ebtType\": \"foodStamp\",\n"
                + "      \"fnsNumber\": \"1234567890\"\n"
                + "    },\n"
                + "    \"pinDebitCashback\": false,\n"
                + "    \"recurringPayments\": true,\n"
                + "    \"paymentLinks\": {\n"
                + "      \"enabled\": true,\n"
                + "      \"logoUrl\": \"LogoPayLink\",\n"
                + "      \"footerNotes\": \"FooterNotesPayLink\"\n"
                + "    },\n"
                + "    \"preAuthorizations\": true,\n"
                + "    \"offlinePayments\": true\n"
                + "  },\n"
                + "  \"taxes\": [\n"
                + "    {\n"
                + "      \"taxRate\": 1.1,\n"
                + "      \"taxLabel\": \"taxLabel\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"security\": {\n"
                + "    \"tokenization\": true,\n"
                + "    \"avsPrompt\": true,\n"
                + "    \"avsLevel\": \"fullAddress\",\n"
                + "    \"cvvPrompt\": true\n"
                + "  },\n"
                + "  \"receiptNotifications\": {\n"
                + "    \"emailReceipt\": true,\n"
                + "    \"smsReceipt\": true\n"
                + "  },\n"
                + "  \"devices\": [\n"
                + "    {\n"
                + "      \"manufacturer\": \"manufacturer\",\n"
                + "      \"model\": \"model\",\n"
                + "      \"serialNumber\": \"serialNumber\",\n"
                + "      \"communicationType\": \"bluetooth\"\n"
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
    public void testRetrieveHostConfiguration() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingTerminalId\":\"0123451001\",\"processingAccountId\":\"23451\",\"configuration\":{\"merchant\":{\"posMid\":\"123456789101\",\"chainNumber\":\"222222\",\"settlementAgent\":\"0001\",\"abaNumber\":\"967854108\",\"binNumber\":\"888888\",\"agentBankNumber\":\"000001\",\"reimbursementAttribute\":\"Z\",\"locationNumber\":\"000001\"},\"terminal\":{\"terminalId\":\"V500000\",\"terminalNumber\":\"1111\",\"authenticationCode\":\"A1B2C3\",\"sharingGroups\":\"3E7HULY8NQWZG\",\"motoAllowed\":true,\"internetAllowed\":true,\"cardPresentAllowed\":true},\"processor\":\"tsys\"}}"));
        HostConfiguration response = client.boarding()
                .processingTerminals()
                .retrieveHostConfiguration(
                        "1234001",
                        RetrieveHostConfigurationProcessingTerminalsRequest.builder()
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"processingTerminalId\": \"0123451001\",\n"
                + "  \"processingAccountId\": \"23451\",\n"
                + "  \"configuration\": {\n"
                + "    \"merchant\": {\n"
                + "      \"posMid\": \"123456789101\",\n"
                + "      \"chainNumber\": \"222222\",\n"
                + "      \"settlementAgent\": \"0001\",\n"
                + "      \"abaNumber\": \"967854108\",\n"
                + "      \"binNumber\": \"888888\",\n"
                + "      \"agentBankNumber\": \"000001\",\n"
                + "      \"reimbursementAttribute\": \"Z\",\n"
                + "      \"locationNumber\": \"000001\"\n"
                + "    },\n"
                + "    \"terminal\": {\n"
                + "      \"terminalId\": \"V500000\",\n"
                + "      \"terminalNumber\": \"1111\",\n"
                + "      \"authenticationCode\": \"A1B2C3\",\n"
                + "      \"sharingGroups\": \"3E7HULY8NQWZG\",\n"
                + "      \"motoAllowed\": true,\n"
                + "      \"internetAllowed\": true,\n"
                + "      \"cardPresentAllowed\": true\n"
                + "    },\n"
                + "    \"processor\": \"tsys\"\n"
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
