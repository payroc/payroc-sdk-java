package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.reporting.settlement.requests.ListDisputesStatusesSettlementRequest;
import com.payroc.api.resources.reporting.settlement.requests.RetrieveAchDepositSettlementRequest;
import com.payroc.api.resources.reporting.settlement.requests.RetrieveAuthorizationSettlementRequest;
import com.payroc.api.resources.reporting.settlement.requests.RetrieveBatchSettlementRequest;
import com.payroc.api.resources.reporting.settlement.requests.RetrieveTransactionSettlementRequest;
import com.payroc.api.types.AchDeposit;
import com.payroc.api.types.Authorization;
import com.payroc.api.types.Batch;
import com.payroc.api.types.DisputeStatus;
import com.payroc.api.types.Transaction;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReportingSettlementWireTest {
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
    public void testRetrieveBatch() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"batchId\":123,\"date\":\"2024-07-02\",\"createdDate\":\"2024-07-02\",\"lastModifiedDate\":\"2024-07-02\",\"saleAmount\":1000000,\"heldAmount\":1000000,\"returnAmount\":1000000,\"transactionCount\":1000000,\"currency\":\"currency\",\"merchant\":{\"merchantId\":\"4525644354\",\"doingBusinessAs\":\"Pizza Doe\",\"processingAccountId\":38765,\"link\":{\"rel\":\"processingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\"}},\"links\":[{\"rel\":\"transactions\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/transactions?batchId=123\"},{\"rel\":\"authorizations\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/authorizations?batchId=123\"}]}"));
        Batch response = client.reporting()
                .settlement()
                .retrieveBatch(1, RetrieveBatchSettlementRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"batchId\": 123,\n"
                + "  \"date\": \"2024-07-02\",\n"
                + "  \"createdDate\": \"2024-07-02\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02\",\n"
                + "  \"saleAmount\": 1000000,\n"
                + "  \"heldAmount\": 1000000,\n"
                + "  \"returnAmount\": 1000000,\n"
                + "  \"transactionCount\": 1000000,\n"
                + "  \"currency\": \"currency\",\n"
                + "  \"merchant\": {\n"
                + "    \"merchantId\": \"4525644354\",\n"
                + "    \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "    \"processingAccountId\": 38765,\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"processingAccount\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"transactions\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/transactions?batchId=123\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"rel\": \"authorizations\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/authorizations?batchId=123\"\n"
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
    public void testRetrieveTransaction() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/ReportingSettlementWireTest_testRetrieveTransaction_response.json")));
        Transaction response = client.reporting()
                .settlement()
                .retrieveTransaction(
                        1, RetrieveTransactionSettlementRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/ReportingSettlementWireTest_testRetrieveTransaction_response.json");
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
    public void testRetrieveAuthorization() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"authorizationId\":65,\"createdDate\":\"2024-07-02\",\"lastModifiedDate\":\"2024-07-02\",\"authorizationResponse\":\"successful\",\"preauthorizationRequestAmount\":10000,\"currency\":\"USD\",\"batch\":{\"batchId\":12,\"date\":\"2024-07-02\",\"cycle\":\"am\",\"link\":{\"rel\":\"batch\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/batches/12\"}},\"card\":{\"cardNumber\":\"453985******7062\",\"type\":\"visa\",\"cvvPresenceIndicator\":true,\"avsRequest\":true,\"avsResponse\":\"Y\"},\"merchant\":{\"merchantId\":\"4525644354\",\"doingBusinessAs\":\"Pizza Doe\",\"processingAccountId\":38765,\"link\":{\"rel\":\"processingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\"}},\"transaction\":{\"transactionId\":442233,\"type\":\"capture\",\"date\":\"2024-07-02\",\"entryMethod\":\"swiped\",\"amount\":100,\"link\":{\"rel\":\"transaction\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/transactions/12345\"}}}"));
        Authorization response = client.reporting()
                .settlement()
                .retrieveAuthorization(
                        1, RetrieveAuthorizationSettlementRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"authorizationId\": 65,\n"
                + "  \"createdDate\": \"2024-07-02\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02\",\n"
                + "  \"authorizationResponse\": \"successful\",\n"
                + "  \"preauthorizationRequestAmount\": 10000,\n"
                + "  \"currency\": \"USD\",\n"
                + "  \"batch\": {\n"
                + "    \"batchId\": 12,\n"
                + "    \"date\": \"2024-07-02\",\n"
                + "    \"cycle\": \"am\",\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"batch\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/batches/12\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"card\": {\n"
                + "    \"cardNumber\": \"453985******7062\",\n"
                + "    \"type\": \"visa\",\n"
                + "    \"cvvPresenceIndicator\": true,\n"
                + "    \"avsRequest\": true,\n"
                + "    \"avsResponse\": \"Y\"\n"
                + "  },\n"
                + "  \"merchant\": {\n"
                + "    \"merchantId\": \"4525644354\",\n"
                + "    \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "    \"processingAccountId\": 38765,\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"processingAccount\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"transaction\": {\n"
                + "    \"transactionId\": 442233,\n"
                + "    \"type\": \"capture\",\n"
                + "    \"date\": \"2024-07-02\",\n"
                + "    \"entryMethod\": \"swiped\",\n"
                + "    \"amount\": 100,\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"transaction\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/transactions/12345\"\n"
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

    @Test
    public void testListDisputesStatuses() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "[{\"disputeStatusId\":12345,\"status\":\"prearbitrationInProcess\",\"statusDate\":\"2024-02-01\"}]"));
        List<DisputeStatus> response = client.reporting()
                .settlement()
                .listDisputesStatuses(
                        1, ListDisputesStatusesSettlementRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "[\n"
                + "  {\n"
                + "    \"disputeStatusId\": 12345,\n"
                + "    \"status\": \"prearbitrationInProcess\",\n"
                + "    \"statusDate\": \"2024-02-01\"\n"
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
    public void testRetrieveAchDeposit() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"achDepositId\":99,\"associationDate\":\"2024-07-02\",\"achDate\":\"2024-07-02\",\"paymentDate\":\"2024-07-02\",\"transactions\":20,\"sales\":5000,\"returns\":100,\"dailyFees\":100,\"heldSales\":100,\"achAdjustment\":100,\"holdback\":100,\"reserveRelease\":100,\"netAmount\":5000,\"merchant\":{\"merchantId\":\"4525644354\",\"doingBusinessAs\":\"Pizza Doe\",\"processingAccountId\":38765,\"link\":{\"rel\":\"processingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\"}},\"links\":[{\"rel\":\"achDepositFees\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/ach-deposit-fees?achDepositId=99&merchantId=4525644354\"}]}"));
        AchDeposit response = client.reporting()
                .settlement()
                .retrieveAchDeposit(
                        1, RetrieveAchDepositSettlementRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"achDepositId\": 99,\n"
                + "  \"associationDate\": \"2024-07-02\",\n"
                + "  \"achDate\": \"2024-07-02\",\n"
                + "  \"paymentDate\": \"2024-07-02\",\n"
                + "  \"transactions\": 20,\n"
                + "  \"sales\": 5000,\n"
                + "  \"returns\": 100,\n"
                + "  \"dailyFees\": 100,\n"
                + "  \"heldSales\": 100,\n"
                + "  \"achAdjustment\": 100,\n"
                + "  \"holdback\": 100,\n"
                + "  \"reserveRelease\": 100,\n"
                + "  \"netAmount\": 5000,\n"
                + "  \"merchant\": {\n"
                + "    \"merchantId\": \"4525644354\",\n"
                + "    \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "    \"processingAccountId\": 38765,\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"processingAccount\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"achDepositFees\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/ach-deposit-fees?achDepositId=99&merchantId=4525644354\"\n"
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
