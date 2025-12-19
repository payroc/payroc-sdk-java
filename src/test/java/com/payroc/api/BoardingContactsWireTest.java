package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.contacts.requests.DeleteContactsRequest;
import com.payroc.api.resources.boarding.contacts.requests.RetrieveContactsRequest;
import com.payroc.api.resources.boarding.contacts.requests.UpdateContactsRequest;
import com.payroc.api.types.Contact;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.ContactType;
import com.payroc.api.types.Identifier;
import com.payroc.api.types.IdentifierType;
import java.util.Arrays;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingContactsWireTest {
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
                                "{\"contactId\":1543,\"type\":\"manager\",\"firstName\":\"Jane\",\"middleName\":\"Helen\",\"lastName\":\"Doe\",\"identifiers\":[{\"type\":\"nationalId\",\"value\":\"xxxxx4320\"}],\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"},{\"value\":\"2025550164\",\"type\":\"phone\"},{\"value\":\"8445557624\",\"type\":\"mobile\"},{\"value\":\"2025550110\",\"type\":\"fax\"}]}"));
        Contact response = client.boarding()
                .contacts()
                .retrieve(1, RetrieveContactsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"contactId\": 1543,\n"
                + "  \"type\": \"manager\",\n"
                + "  \"firstName\": \"Jane\",\n"
                + "  \"middleName\": \"Helen\",\n"
                + "  \"lastName\": \"Doe\",\n"
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
                + "    },\n"
                + "    {\n"
                + "      \"value\": \"2025550164\",\n"
                + "      \"type\": \"phone\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"value\": \"8445557624\",\n"
                + "      \"type\": \"mobile\"\n"
                + "    },\n"
                + "    {\n"
                + "      \"value\": \"2025550110\",\n"
                + "      \"type\": \"fax\"\n"
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
        client.boarding()
                .contacts()
                .update(
                        1,
                        UpdateContactsRequest.builder()
                                .body(Contact.builder()
                                        .type(ContactType.MANAGER)
                                        .firstName("Jane")
                                        .lastName("Doe")
                                        .middleName("Helen")
                                        .identifiers(Arrays.asList(Identifier.builder()
                                                .type(IdentifierType.NATIONAL_ID)
                                                .value("000-00-4320")
                                                .build()))
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
                + "  \"type\": \"manager\",\n"
                + "  \"firstName\": \"Jane\",\n"
                + "  \"middleName\": \"Helen\",\n"
                + "  \"lastName\": \"Doe\",\n"
                + "  \"identifiers\": [\n"
                + "    {\n"
                + "      \"type\": \"nationalId\",\n"
                + "      \"value\": \"000-00-4320\"\n"
                + "    }\n"
                + "  ],\n"
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
        client.boarding().contacts().delete(1, DeleteContactsRequest.builder().build());
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
