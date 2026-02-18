package com.payroc.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroc.api.core.Environment;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.resources.boarding.merchantplatforms.requests.CreateMerchantAccount;
import com.payroc.api.resources.boarding.merchantplatforms.requests.CreateProcessingAccountMerchantPlatformsRequest;
import com.payroc.api.resources.boarding.merchantplatforms.requests.RetrieveMerchantPlatformsRequest;
import com.payroc.api.types.Address;
import com.payroc.api.types.AddressTypeType;
import com.payroc.api.types.Business;
import com.payroc.api.types.BusinessCountryOfOperation;
import com.payroc.api.types.BusinessOrganizationType;
import com.payroc.api.types.CommonFundingFundingSchedule;
import com.payroc.api.types.Contact;
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.ContactType;
import com.payroc.api.types.CreateFunding;
import com.payroc.api.types.CreateProcessingAccount;
import com.payroc.api.types.CreateProcessingAccountBusinessType;
import com.payroc.api.types.FundingAccount;
import com.payroc.api.types.FundingAccountType;
import com.payroc.api.types.FundingAccountUse;
import com.payroc.api.types.Identifier;
import com.payroc.api.types.IdentifierType;
import com.payroc.api.types.LegalAddress;
import com.payroc.api.types.MerchantPlatform;
import com.payroc.api.types.Owner;
import com.payroc.api.types.OwnerRelationship;
import com.payroc.api.types.PaymentMethodAch;
import com.payroc.api.types.PaymentMethodsItem;
import com.payroc.api.types.Pricing;
import com.payroc.api.types.PricingTemplate;
import com.payroc.api.types.Processing;
import com.payroc.api.types.ProcessingAccount;
import com.payroc.api.types.ProcessingAch;
import com.payroc.api.types.ProcessingAchLimits;
import com.payroc.api.types.ProcessingAchRefunds;
import com.payroc.api.types.ProcessingAchTransactionTypesItem;
import com.payroc.api.types.ProcessingCardAcceptance;
import com.payroc.api.types.ProcessingCardAcceptanceCardsAcceptedItem;
import com.payroc.api.types.ProcessingCardAcceptanceSpecialityCards;
import com.payroc.api.types.ProcessingCardAcceptanceSpecialityCardsAmericanExpressDirect;
import com.payroc.api.types.ProcessingCardAcceptanceSpecialityCardsElectronicBenefitsTransfer;
import com.payroc.api.types.ProcessingCardAcceptanceSpecialityCardsOther;
import com.payroc.api.types.ProcessingMonthlyAmounts;
import com.payroc.api.types.ProcessingMonthsOfOperationItem;
import com.payroc.api.types.ProcessingTransactionAmounts;
import com.payroc.api.types.ProcessingVolumeBreakdown;
import com.payroc.api.types.Signature;
import com.payroc.api.types.SignatureByDirectLink;
import com.payroc.api.types.Timezone;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardingMerchantPlatformsWireTest {
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
                        "/wire-tests/BoardingMerchantPlatformsWireTest_testCreate_response.json")));
        MerchantPlatform response = client.boarding()
                .merchantPlatforms()
                .create(CreateMerchantAccount.builder()
                        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                        .business(Business.builder()
                                .name("Example Corp")
                                .taxId("12-3456789")
                                .organizationType(BusinessOrganizationType.PRIVATE_CORPORATION)
                                .countryOfOperation(BusinessCountryOfOperation.US)
                                .addresses(Arrays.asList(LegalAddress.builder()
                                        .type(AddressTypeType.LEGAL_ADDRESS)
                                        .address1("1 Example Ave.")
                                        .city("Chicago")
                                        .state("Illinois")
                                        .country("US")
                                        .postalCode("60056")
                                        .address2("Example Address Line 2")
                                        .address3("Example Address Line 3")
                                        .build()))
                                .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                        .value("jane.doe@example.com")
                                        .build())))
                                .build())
                        .processingAccounts(Arrays.asList(CreateProcessingAccount.builder()
                                .doingBusinessAs("Pizza Doe")
                                .businessType(CreateProcessingAccountBusinessType.RESTAURANT)
                                .categoryCode(5999)
                                .merchandiseOrServiceSold("Pizza")
                                .businessStartDate(LocalDate.parse("2020-01-01"))
                                .timezone(Timezone.AMERICA_CHICAGO)
                                .address(Address.builder()
                                        .address1("1 Example Ave.")
                                        .city("Chicago")
                                        .state("Illinois")
                                        .country("US")
                                        .postalCode("60056")
                                        .address2("Example Address Line 2")
                                        .address3("Example Address Line 3")
                                        .build())
                                .processing(Processing.builder()
                                        .transactionAmounts(ProcessingTransactionAmounts.builder()
                                                .average(5000)
                                                .highest(10000)
                                                .build())
                                        .monthlyAmounts(ProcessingMonthlyAmounts.builder()
                                                .average(50000)
                                                .highest(100000)
                                                .build())
                                        .volumeBreakdown(ProcessingVolumeBreakdown.builder()
                                                .cardPresent(77)
                                                .mailOrTelephone(3)
                                                .ecommerce(20)
                                                .build())
                                        .isSeasonal(true)
                                        .monthsOfOperation(Optional.of(Arrays.asList(
                                                ProcessingMonthsOfOperationItem.JAN,
                                                ProcessingMonthsOfOperationItem.FEB)))
                                        .ach(ProcessingAch.builder()
                                                .refunds(ProcessingAchRefunds.builder()
                                                        .writtenRefundPolicy(true)
                                                        .refundPolicyUrl("www.example.com/refund-poilcy-url")
                                                        .build())
                                                .estimatedMonthlyTransactions(3000)
                                                .limits(ProcessingAchLimits.builder()
                                                        .singleTransaction(10000)
                                                        .dailyDeposit(200000)
                                                        .monthlyDeposit(6000000)
                                                        .build())
                                                .naics("5812")
                                                .previouslyTerminatedForAch(false)
                                                .transactionTypes(Optional.of(Arrays.asList(
                                                        ProcessingAchTransactionTypesItem.PREARRANGED_PAYMENT,
                                                        ProcessingAchTransactionTypesItem.OTHER)))
                                                .transactionTypesOther("anotherTransactionType")
                                                .build())
                                        .cardAcceptance(ProcessingCardAcceptance.builder()
                                                .debitOnly(false)
                                                .hsaFsa(false)
                                                .cardsAccepted(Optional.of(Arrays.asList(
                                                        ProcessingCardAcceptanceCardsAcceptedItem.VISA,
                                                        ProcessingCardAcceptanceCardsAcceptedItem.MASTERCARD)))
                                                .specialityCards(ProcessingCardAcceptanceSpecialityCards.builder()
                                                        .americanExpressDirect(
                                                                ProcessingCardAcceptanceSpecialityCardsAmericanExpressDirect
                                                                        .builder()
                                                                        .enabled(true)
                                                                        .merchantNumber("abc1234567")
                                                                        .build())
                                                        .electronicBenefitsTransfer(
                                                                ProcessingCardAcceptanceSpecialityCardsElectronicBenefitsTransfer
                                                                        .builder()
                                                                        .enabled(true)
                                                                        .fnsNumber("6789012")
                                                                        .build())
                                                        .other(ProcessingCardAcceptanceSpecialityCardsOther.builder()
                                                                .wexMerchantNumber("abc1234567")
                                                                .voyagerMerchantId("abc1234567")
                                                                .fleetMerchantId("abc1234567")
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .funding(CreateFunding.builder()
                                        .fundingSchedule(CommonFundingFundingSchedule.NEXTDAY)
                                        .acceleratedFundingFee(1999)
                                        .dailyDiscount(false)
                                        .fundingAccounts(Optional.of(Arrays.asList(FundingAccount.builder()
                                                .type(FundingAccountType.CHECKING)
                                                .use(FundingAccountUse.CREDIT_AND_DEBIT)
                                                .nameOnAccount("Jane Doe")
                                                .paymentMethods(Arrays.asList(PaymentMethodsItem.ach(
                                                        PaymentMethodAch.builder()
                                                                .build())))
                                                .metadata(new HashMap<String, String>() {
                                                    {
                                                        put("yourCustomField", "abc123");
                                                    }
                                                })
                                                .build())))
                                        .build())
                                .pricing(Pricing.intent(PricingTemplate.builder()
                                        .pricingIntentId("6123")
                                        .build()))
                                .signature(Signature.requestedViaDirectLink(
                                        SignatureByDirectLink.builder().build()))
                                .owners(Arrays.asList(Owner.builder()
                                        .firstName("Jane")
                                        .lastName("Doe")
                                        .dateOfBirth(LocalDate.parse("1964-03-22"))
                                        .address(Address.builder()
                                                .address1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .country("US")
                                                .postalCode("60056")
                                                .address2("Example Address Line 2")
                                                .address3("Example Address Line 3")
                                                .build())
                                        .relationship(OwnerRelationship.builder()
                                                .isControlProng(true)
                                                .equityPercentage(48.5f)
                                                .title("CFO")
                                                .isAuthorizedSignatory(false)
                                                .build())
                                        .middleName("Helen")
                                        .identifiers(Arrays.asList(Identifier.builder()
                                                .type(IdentifierType.NATIONAL_ID)
                                                .value("000-00-4320")
                                                .build()))
                                        .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                .value("jane.doe@example.com")
                                                .build())))
                                        .build()))
                                .website("www.example.com")
                                .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                        .value("jane.doe@example.com")
                                        .build())))
                                .contacts(Optional.of(Arrays.asList(Contact.builder()
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
                                        .build())))
                                .metadata(new HashMap<String, String>() {
                                    {
                                        put("customerId", "2345");
                                    }
                                })
                                .build()))
                        .metadata(new HashMap<String, String>() {
                            {
                                put("customerId", "2345");
                            }
                        })
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
        String expectedRequestBody =
                TestResources.loadResource("/wire-tests/BoardingMerchantPlatformsWireTest_testCreate_request.json");
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
                TestResources.loadResource("/wire-tests/BoardingMerchantPlatformsWireTest_testCreate_response.json");
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
                        "/wire-tests/BoardingMerchantPlatformsWireTest_testRetrieve_response.json")));
        MerchantPlatform response = client.boarding()
                .merchantPlatforms()
                .retrieve("12345", RetrieveMerchantPlatformsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody =
                TestResources.loadResource("/wire-tests/BoardingMerchantPlatformsWireTest_testRetrieve_response.json");
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
    public void testCreateProcessingAccount() throws Exception {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(TestResources.loadResource(
                        "/wire-tests/BoardingMerchantPlatformsWireTest_testCreateProcessingAccount_response.json")));
        ProcessingAccount response = client.boarding()
                .merchantPlatforms()
                .createProcessingAccount(
                        "12345",
                        CreateProcessingAccountMerchantPlatformsRequest.builder()
                                .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
                                .body(CreateProcessingAccount.builder()
                                        .doingBusinessAs("Pizza Doe")
                                        .businessType(CreateProcessingAccountBusinessType.RESTAURANT)
                                        .categoryCode(5999)
                                        .merchandiseOrServiceSold("Pizza")
                                        .businessStartDate(LocalDate.parse("2020-01-01"))
                                        .timezone(Timezone.AMERICA_CHICAGO)
                                        .address(Address.builder()
                                                .address1("1 Example Ave.")
                                                .city("Chicago")
                                                .state("Illinois")
                                                .country("US")
                                                .postalCode("60056")
                                                .address2("Example Address Line 2")
                                                .address3("Example Address Line 3")
                                                .build())
                                        .processing(Processing.builder()
                                                .transactionAmounts(ProcessingTransactionAmounts.builder()
                                                        .average(5000)
                                                        .highest(10000)
                                                        .build())
                                                .monthlyAmounts(ProcessingMonthlyAmounts.builder()
                                                        .average(50000)
                                                        .highest(100000)
                                                        .build())
                                                .volumeBreakdown(ProcessingVolumeBreakdown.builder()
                                                        .cardPresent(77)
                                                        .mailOrTelephone(3)
                                                        .ecommerce(20)
                                                        .build())
                                                .isSeasonal(true)
                                                .monthsOfOperation(Optional.of(Arrays.asList(
                                                        ProcessingMonthsOfOperationItem.JAN,
                                                        ProcessingMonthsOfOperationItem.FEB)))
                                                .ach(ProcessingAch.builder()
                                                        .refunds(ProcessingAchRefunds.builder()
                                                                .writtenRefundPolicy(true)
                                                                .refundPolicyUrl("www.example.com/refund-poilcy-url")
                                                                .build())
                                                        .estimatedMonthlyTransactions(3000)
                                                        .limits(ProcessingAchLimits.builder()
                                                                .singleTransaction(10000)
                                                                .dailyDeposit(200000)
                                                                .monthlyDeposit(6000000)
                                                                .build())
                                                        .naics("5812")
                                                        .previouslyTerminatedForAch(false)
                                                        .transactionTypes(Optional.of(Arrays.asList(
                                                                ProcessingAchTransactionTypesItem.PREARRANGED_PAYMENT,
                                                                ProcessingAchTransactionTypesItem.OTHER)))
                                                        .transactionTypesOther("anotherTransactionType")
                                                        .build())
                                                .cardAcceptance(ProcessingCardAcceptance.builder()
                                                        .debitOnly(false)
                                                        .hsaFsa(false)
                                                        .cardsAccepted(Optional.of(Arrays.asList(
                                                                ProcessingCardAcceptanceCardsAcceptedItem.VISA,
                                                                ProcessingCardAcceptanceCardsAcceptedItem.MASTERCARD)))
                                                        .specialityCards(
                                                                ProcessingCardAcceptanceSpecialityCards.builder()
                                                                        .americanExpressDirect(
                                                                                ProcessingCardAcceptanceSpecialityCardsAmericanExpressDirect
                                                                                        .builder()
                                                                                        .enabled(true)
                                                                                        .merchantNumber("abc1234567")
                                                                                        .build())
                                                                        .electronicBenefitsTransfer(
                                                                                ProcessingCardAcceptanceSpecialityCardsElectronicBenefitsTransfer
                                                                                        .builder()
                                                                                        .enabled(true)
                                                                                        .fnsNumber("6789012")
                                                                                        .build())
                                                                        .other(
                                                                                ProcessingCardAcceptanceSpecialityCardsOther
                                                                                        .builder()
                                                                                        .wexMerchantNumber("abc1234567")
                                                                                        .voyagerMerchantId("abc1234567")
                                                                                        .fleetMerchantId("abc1234567")
                                                                                        .build())
                                                                        .build())
                                                        .build())
                                                .build())
                                        .funding(CreateFunding.builder()
                                                .fundingSchedule(CommonFundingFundingSchedule.NEXTDAY)
                                                .acceleratedFundingFee(1999)
                                                .dailyDiscount(false)
                                                .fundingAccounts(Optional.of(Arrays.asList(FundingAccount.builder()
                                                        .type(FundingAccountType.CHECKING)
                                                        .use(FundingAccountUse.CREDIT_AND_DEBIT)
                                                        .nameOnAccount("Jane Doe")
                                                        .paymentMethods(Arrays.asList(PaymentMethodsItem.ach(
                                                                PaymentMethodAch.builder()
                                                                        .build())))
                                                        .metadata(new HashMap<String, String>() {
                                                            {
                                                                put("yourCustomField", "abc123");
                                                            }
                                                        })
                                                        .build())))
                                                .build())
                                        .pricing(Pricing.intent(PricingTemplate.builder()
                                                .pricingIntentId("6123")
                                                .build()))
                                        .signature(Signature.requestedViaDirectLink(
                                                SignatureByDirectLink.builder().build()))
                                        .owners(Arrays.asList(Owner.builder()
                                                .firstName("Jane")
                                                .lastName("Doe")
                                                .dateOfBirth(LocalDate.parse("1964-03-22"))
                                                .address(Address.builder()
                                                        .address1("1 Example Ave.")
                                                        .city("Chicago")
                                                        .state("Illinois")
                                                        .country("US")
                                                        .postalCode("60056")
                                                        .address2("Example Address Line 2")
                                                        .address3("Example Address Line 3")
                                                        .build())
                                                .relationship(OwnerRelationship.builder()
                                                        .isControlProng(true)
                                                        .equityPercentage(51.5f)
                                                        .title("CFO")
                                                        .isAuthorizedSignatory(false)
                                                        .build())
                                                .middleName("Helen")
                                                .identifiers(Arrays.asList(Identifier.builder()
                                                        .type(IdentifierType.NATIONAL_ID)
                                                        .value("000-00-4320")
                                                        .build()))
                                                .contactMethods(
                                                        Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                                .value("jane.doe@example.com")
                                                                .build())))
                                                .build()))
                                        .website("www.example.com")
                                        .contactMethods(Arrays.asList(ContactMethod.email(ContactMethodEmail.builder()
                                                .value("jane.doe@example.com")
                                                .build())))
                                        .contacts(Optional.of(Arrays.asList(Contact.builder()
                                                .type(ContactType.MANAGER)
                                                .firstName("Jane")
                                                .lastName("Doe")
                                                .middleName("Helen")
                                                .identifiers(Arrays.asList(Identifier.builder()
                                                        .type(IdentifierType.NATIONAL_ID)
                                                        .value("000-00-4320")
                                                        .build()))
                                                .contactMethods(Arrays.asList(ContactMethod.email(
                                                        ContactMethodEmail.builder()
                                                                .value("jane.doe@example.com")
                                                                .build())))
                                                .build())))
                                        .metadata(new HashMap<String, String>() {
                                            {
                                                put("customerId", "2345");
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
        String expectedRequestBody = TestResources.loadResource(
                "/wire-tests/BoardingMerchantPlatformsWireTest_testCreateProcessingAccount_request.json");
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
                "/wire-tests/BoardingMerchantPlatformsWireTest_testCreateProcessingAccount_response.json");
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
