package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.notifications.eventsubscriptions.requests.CreateEventSubscriptionsRequest;
import com.payroc.api.resources.notifications.eventsubscriptions.requests.DeleteEventSubscriptionsRequest;
import com.payroc.api.resources.notifications.eventsubscriptions.requests.PartiallyUpdateEventSubscriptionsRequest;
import com.payroc.api.resources.notifications.eventsubscriptions.requests.RetrieveEventSubscriptionsRequest;
import com.payroc.api.resources.notifications.eventsubscriptions.requests.UpdateEventSubscriptionsRequest;
import com.payroc.api.types.EventSubscription;
import com.payroc.api.types.Notification;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.Webhook;
import java.util.Arrays;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NotificationsEventSubscriptionsWireTest {
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
                                "{\"id\":2565435189324,\"enabled\":true,\"status\":\"registered\",\"eventTypes\":[\"processingAccount.status.changed\"],\"notifications\":[{\"uri\":\"https://my-server/notification/endpoint\",\"secret\":\"aBcD1234eFgH5678iJkL9012mNoP3456\",\"supportEmailAddress\":\"supportEmailAddress\",\"type\":\"webhook\"}],\"metadata\":{\"yourCustomField\":\"abc123\"}}"));
        EventSubscription response = client.notifications()
                .eventSubscriptions()
                .create(CreateEventSubscriptionsRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .body(EventSubscription.builder()
                                .enabled(true)
                                .eventTypes(Arrays.asList("processingAccount.status.changed"))
                                .notifications(Arrays.asList(Notification.webhook(Webhook.builder()
                                        .uri("https://my-server/notification/endpoint")
                                        .secret("aBcD1234eFgH5678iJkL9012mNoP3456")
                                        .supportEmailAddress("supportEmailAddress")
                                        .build())))
                                .metadata(new HashMap<String, Object>() {
                                    {
                                        put("yourCustomField", "abc123");
                                    }
                                })
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
                + "  \"enabled\": true,\n"
                + "  \"eventTypes\": [\n"
                + "    \"processingAccount.status.changed\"\n"
                + "  ],\n"
                + "  \"notifications\": [\n"
                + "    {\n"
                + "      \"uri\": \"https://my-server/notification/endpoint\",\n"
                + "      \"secret\": \"aBcD1234eFgH5678iJkL9012mNoP3456\",\n"
                + "      \"supportEmailAddress\": \"supportEmailAddress\",\n"
                + "      \"type\": \"webhook\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
                + "  \"id\": 2565435189324,\n"
                + "  \"enabled\": true,\n"
                + "  \"status\": \"registered\",\n"
                + "  \"eventTypes\": [\n"
                + "    \"processingAccount.status.changed\"\n"
                + "  ],\n"
                + "  \"notifications\": [\n"
                + "    {\n"
                + "      \"uri\": \"https://my-server/notification/endpoint\",\n"
                + "      \"secret\": \"aBcD1234eFgH5678iJkL9012mNoP3456\",\n"
                + "      \"supportEmailAddress\": \"supportEmailAddress\",\n"
                + "      \"type\": \"webhook\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
    public void testRetrieve() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"id\":2565435189324,\"enabled\":true,\"status\":\"registered\",\"eventTypes\":[\"processingAccount.status.changed\"],\"notifications\":[{\"uri\":\"https://my-server/notification/endpoint\",\"secret\":\"aBcD1234eFgH5678iJkL9012mNoP3456\",\"supportEmailAddress\":\"supportEmailAddress\",\"type\":\"webhook\"}],\"metadata\":{\"yourCustomField\":\"abc123\"}}"));
        EventSubscription response = client.notifications()
                .eventSubscriptions()
                .retrieve(1, RetrieveEventSubscriptionsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"id\": 2565435189324,\n"
                + "  \"enabled\": true,\n"
                + "  \"status\": \"registered\",\n"
                + "  \"eventTypes\": [\n"
                + "    \"processingAccount.status.changed\"\n"
                + "  ],\n"
                + "  \"notifications\": [\n"
                + "    {\n"
                + "      \"uri\": \"https://my-server/notification/endpoint\",\n"
                + "      \"secret\": \"aBcD1234eFgH5678iJkL9012mNoP3456\",\n"
                + "      \"supportEmailAddress\": \"supportEmailAddress\",\n"
                + "      \"type\": \"webhook\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
    public void testUpdate() throws Exception {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        client.notifications()
                .eventSubscriptions()
                .update(
                        1,
                        UpdateEventSubscriptionsRequest.builder()
                                .body(EventSubscription.builder()
                                        .enabled(true)
                                        .eventTypes(Arrays.asList("processingAccount.status.changed"))
                                        .notifications(Arrays.asList(Notification.webhook(Webhook.builder()
                                                .uri("https://my-server/notification/endpoint")
                                                .secret("aBcD1234eFgH5678iJkL9012mNoP3456")
                                                .supportEmailAddress("supportEmailAddress")
                                                .build())))
                                        .metadata(new HashMap<String, Object>() {
                                            {
                                                put("yourCustomField", "abc123");
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
                + "  \"enabled\": true,\n"
                + "  \"eventTypes\": [\n"
                + "    \"processingAccount.status.changed\"\n"
                + "  ],\n"
                + "  \"notifications\": [\n"
                + "    {\n"
                + "      \"uri\": \"https://my-server/notification/endpoint\",\n"
                + "      \"secret\": \"aBcD1234eFgH5678iJkL9012mNoP3456\",\n"
                + "      \"supportEmailAddress\": \"supportEmailAddress\",\n"
                + "      \"type\": \"webhook\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
        client.notifications()
                .eventSubscriptions()
                .delete(1, DeleteEventSubscriptionsRequest.builder().build());
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
                                "{\"id\":2565435189324,\"enabled\":false,\"status\":\"registered\",\"eventTypes\":[\"processingAccount.status.changed\"],\"notifications\":[{\"uri\":\"https://my-server/notification/endpoint\",\"secret\":\"aBcD1234eFgH5678iJkL9012mNoP3456\",\"supportEmailAddress\":\"supportEmailAddress\",\"type\":\"webhook\"}],\"metadata\":{\"yourCustomField\":\"abc123\"}}"));
        EventSubscription response = client.notifications()
                .eventSubscriptions()
                .partiallyUpdate(
                        1,
                        PartiallyUpdateEventSubscriptionsRequest.builder()
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
                + "  \"id\": 2565435189324,\n"
                + "  \"enabled\": false,\n"
                + "  \"status\": \"registered\",\n"
                + "  \"eventTypes\": [\n"
                + "    \"processingAccount.status.changed\"\n"
                + "  ],\n"
                + "  \"notifications\": [\n"
                + "    {\n"
                + "      \"uri\": \"https://my-server/notification/endpoint\",\n"
                + "      \"secret\": \"aBcD1234eFgH5678iJkL9012mNoP3456\",\n"
                + "      \"supportEmailAddress\": \"supportEmailAddress\",\n"
                + "      \"type\": \"webhook\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"yourCustomField\": \"abc123\"\n"
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
