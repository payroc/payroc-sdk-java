package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.funding.fundingaccounts.requests.DeleteFundingAccountsRequest;
import com.payroc.api.resources.funding.fundingaccounts.requests.RetrieveFundingAccountsRequest;
import com.payroc.api.resources.funding.fundingaccounts.requests.UpdateFundingAccountsRequest;
import com.payroc.api.types.FundingAccount;
import com.payroc.api.types.FundingAccountType;
import com.payroc.api.types.FundingAccountUse;
import com.payroc.api.types.PaymentMethodAch;
import com.payroc.api.types.PaymentMethodsItem;
import java.util.Arrays;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FundingFundingAccountsWireTest {
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
                                "{\"fundingAccountId\":123,\"createdDate\":\"2024-07-02T15:30:00Z\",\"lastModifiedDate\":\"2024-07-02T15:30:00Z\",\"status\":\"pending\",\"type\":\"checking\",\"use\":\"credit\",\"nameOnAccount\":\"Jane Doe\",\"paymentMethods\":[{\"value\":{\"routingNumber\":\"123456789\",\"accountNumber\":\"1234567890\"},\"type\":\"ach\"}],\"metadata\":{\"yourCustomField\":\"abc123\"},\"links\":[{\"rel\":\"parent\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/processing-accounts/4525644354\"}]}"));
        FundingAccount response = client.funding()
                .fundingAccounts()
                .retrieve(1, RetrieveFundingAccountsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"fundingAccountId\": 123,\n"
                + "  \"createdDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T15:30:00Z\",\n"
                + "  \"status\": \"pending\",\n"
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
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/4525644354\"\n"
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
                .fundingAccounts()
                .update(
                        1,
                        UpdateFundingAccountsRequest.builder()
                                .body(FundingAccount.builder()
                                        .type(FundingAccountType.SAVINGS)
                                        .use(FundingAccountUse.CREDIT)
                                        .nameOnAccount("Fred Nerk")
                                        .paymentMethods(Arrays.asList(PaymentMethodsItem.ach(
                                                PaymentMethodAch.builder().build())))
                                        .metadata(new HashMap<String, String>() {
                                            {
                                                put("responsiblePerson", "Jane Doe");
                                            }
                                        })
                                        .build())
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PUT", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody = ""
                + "{\n"
                + "  \"type\": \"savings\",\n"
                + "  \"use\": \"credit\",\n"
                + "  \"nameOnAccount\": \"Fred Nerk\",\n"
                + "  \"paymentMethods\": [\n"
                + "    {\n"
                + "      \"type\": \"ach\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"responsiblePerson\": \"Jane Doe\"\n"
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
    }

    @Test
    public void testDelete() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.funding()
                .fundingAccounts()
                .delete(1, DeleteFundingAccountsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
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
