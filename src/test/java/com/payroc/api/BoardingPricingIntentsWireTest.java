package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.pricingintents.requests.CreatePricingIntentsRequest;
import com.payroc.api.resources.boarding.pricingintents.requests.DeletePricingIntentsRequest;
import com.payroc.api.resources.boarding.pricingintents.requests.PartiallyUpdatePricingIntentsRequest;
import com.payroc.api.resources.boarding.pricingintents.requests.RetrievePricingIntentsRequest;
import com.payroc.api.resources.boarding.pricingintents.requests.UpdatePricingIntentsRequest;
import com.payroc.api.types.Ach;
import com.payroc.api.types.AchFees;
import com.payroc.api.types.BaseUs;
import com.payroc.api.types.BaseUsAnnualFee;
import com.payroc.api.types.BaseUsAnnualFeeBillInMonth;
import com.payroc.api.types.BaseUsMonthly;
import com.payroc.api.types.BaseUsPlatinumSecurity;
import com.payroc.api.types.GatewayUs50;
import com.payroc.api.types.GatewayUs50Fees;
import com.payroc.api.types.HardwareAdvantagePlan;
import com.payroc.api.types.InterchangePlus;
import com.payroc.api.types.InterchangePlusFees;
import com.payroc.api.types.PatchDocument;
import com.payroc.api.types.PatchRemove;
import com.payroc.api.types.PricingAgreementUs50Country;
import com.payroc.api.types.PricingAgreementUs50Processor;
import com.payroc.api.types.PricingAgreementUs50ProcessorCard;
import com.payroc.api.types.PricingAgreementUs50Version;
import com.payroc.api.types.PricingIntent50;
import com.payroc.api.types.ProcessorFee;
import com.payroc.api.types.ServiceUs50;
import java.util.Arrays;
import java.util.HashMap;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingPricingIntentsWireTest {
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingPricingIntentsWireTest_testCreate_response.json")));
        PricingIntent50 response = client.boarding()
                .pricingIntents()
                .create(CreatePricingIntentsRequest.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .body(PricingIntent50.builder()
                                .key("Your-Unique-Identifier")
                                .country(PricingAgreementUs50Country.US)
                                .version(PricingAgreementUs50Version.FIVE_0)
                                .base(BaseUs.builder()
                                        .annualFee(BaseUsAnnualFee.builder()
                                                .amount(9900)
                                                .billInMonth(BaseUsAnnualFeeBillInMonth.JUNE)
                                                .build())
                                        .maintenance(500)
                                        .minimum(100)
                                        .batch(1500)
                                        .addressVerification(5)
                                        .regulatoryAssistanceProgram(15)
                                        .pciNonCompliance(4995)
                                        .merchantAdvantage(10)
                                        .platinumSecurity(BaseUsPlatinumSecurity.monthly(
                                                BaseUsMonthly.builder().build()))
                                        .voiceAuthorization(95)
                                        .chargeback(2500)
                                        .retrieval(1500)
                                        .earlyTermination(57500)
                                        .build())
                                .metadata(new HashMap<String, String>() {
                                    {
                                        put("yourCustomField", "abc123");
                                    }
                                })
                                .processor(PricingAgreementUs50Processor.builder()
                                        .card(PricingAgreementUs50ProcessorCard.interchangePlus(
                                                InterchangePlus.builder()
                                                        .fees(InterchangePlusFees.builder()
                                                                .mastercardVisaDiscover(ProcessorFee.builder()
                                                                        .build())
                                                                .build())
                                                        .build()))
                                        .build())
                                .services(
                                        Arrays.asList(ServiceUs50.hardwareAdvantagePlan(HardwareAdvantagePlan.builder()
                                                .enabled(true)
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
                + "  \"country\": \"US\",\n"
                + "  \"version\": \"5.0\",\n"
                + "  \"base\": {\n"
                + "    \"addressVerification\": 5,\n"
                + "    \"annualFee\": {\n"
                + "      \"billInMonth\": \"june\",\n"
                + "      \"amount\": 9900\n"
                + "    },\n"
                + "    \"regulatoryAssistanceProgram\": 15,\n"
                + "    \"pciNonCompliance\": 4995,\n"
                + "    \"merchantAdvantage\": 10,\n"
                + "    \"platinumSecurity\": {\n"
                + "      \"billingFrequency\": \"monthly\"\n"
                + "    },\n"
                + "    \"maintenance\": 500,\n"
                + "    \"minimum\": 100,\n"
                + "    \"voiceAuthorization\": 95,\n"
                + "    \"chargeback\": 2500,\n"
                + "    \"retrieval\": 1500,\n"
                + "    \"batch\": 1500,\n"
                + "    \"earlyTermination\": 57500\n"
                + "  },\n"
                + "  \"processor\": {\n"
                + "    \"card\": {\n"
                + "      \"fees\": {\n"
                + "        \"mastercardVisaDiscover\": {}\n"
                + "      },\n"
                + "      \"planType\": \"interchangePlus\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"services\": [\n"
                + "    {\n"
                + "      \"enabled\": true,\n"
                + "      \"name\": \"hardwareAdvantagePlan\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"key\": \"Your-Unique-Identifier\",\n"
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
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/BoardingPricingIntentsWireTest_testCreate_response.json");
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
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingPricingIntentsWireTest_testRetrieve_response.json")));
        PricingIntent50 response = client.boarding()
                .pricingIntents()
                .retrieve("5", RetrievePricingIntentsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/BoardingPricingIntentsWireTest_testRetrieve_response.json");
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
                .pricingIntents()
                .update(
                        "5",
                        UpdatePricingIntentsRequest.builder()
                                .body(PricingIntent50.builder()
                                        .key("Your-Unique-Identifier")
                                        .country(PricingAgreementUs50Country.US)
                                        .version(PricingAgreementUs50Version.FIVE_0)
                                        .base(BaseUs.builder()
                                                .annualFee(BaseUsAnnualFee.builder()
                                                        .amount(9900)
                                                        .billInMonth(BaseUsAnnualFeeBillInMonth.JUNE)
                                                        .build())
                                                .maintenance(500)
                                                .minimum(100)
                                                .batch(1500)
                                                .addressVerification(5)
                                                .regulatoryAssistanceProgram(15)
                                                .pciNonCompliance(4995)
                                                .merchantAdvantage(10)
                                                .platinumSecurity(BaseUsPlatinumSecurity.monthly(
                                                        BaseUsMonthly.builder().build()))
                                                .voiceAuthorization(95)
                                                .chargeback(2500)
                                                .retrieval(1500)
                                                .earlyTermination(57500)
                                                .build())
                                        .metadata(new HashMap<String, String>() {
                                            {
                                                put("yourCustomField", "abc123");
                                            }
                                        })
                                        .processor(PricingAgreementUs50Processor.builder()
                                                .card(PricingAgreementUs50ProcessorCard.interchangePlus(
                                                        InterchangePlus.builder()
                                                                .fees(InterchangePlusFees.builder()
                                                                        .mastercardVisaDiscover(ProcessorFee.builder()
                                                                                .build())
                                                                        .build())
                                                                .build()))
                                                .ach(Ach.builder()
                                                        .fees(AchFees.builder()
                                                                .transaction(50)
                                                                .batch(5)
                                                                .returns(400)
                                                                .unauthorizedReturn(1999)
                                                                .statement(800)
                                                                .monthlyMinimum(20000)
                                                                .accountVerification(10)
                                                                .discountRateUnder10000(5.25)
                                                                .discountRateAbove10000(10.0)
                                                                .build())
                                                        .build())
                                                .build())
                                        .gateway(GatewayUs50.builder()
                                                .fees(GatewayUs50Fees.builder()
                                                        .monthly(2000)
                                                        .setup(5000)
                                                        .perTransaction(2000)
                                                        .perDeviceMonthly(10)
                                                        .build())
                                                .build())
                                        .services(Arrays.asList(
                                                ServiceUs50.hardwareAdvantagePlan(HardwareAdvantagePlan.builder()
                                                        .enabled(true)
                                                        .build())))
                                        .build())
                                .build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("PUT", request.getMethod());
        // Validate request body
        String actualRequestBody = request.getBody().readUtf8();
        String expectedRequestBody =
                TestResources.loadResource("/wire-tests/BoardingPricingIntentsWireTest_testUpdate_request.json");
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
        client.boarding()
                .pricingIntents()
                .delete("5", DeletePricingIntentsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("DELETE", request.getMethod());
    }

    @Test
    public void testPartiallyUpdate() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingPricingIntentsWireTest_testPartiallyUpdate_response.json")));
        PricingIntent50 response = client.boarding()
                .pricingIntents()
                .partiallyUpdate(
                        "5",
                        PartiallyUpdatePricingIntentsRequest.builder()
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
        String expectedResponseBody = TestResources.loadResource(
                "/wire-tests/BoardingPricingIntentsWireTest_testPartiallyUpdate_response.json");
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
