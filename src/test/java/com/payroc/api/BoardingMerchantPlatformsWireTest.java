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
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"merchantPlatformId\":\"12345\",\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"business\":{\"name\":\"Example Corp\",\"taxId\":\"xxxxx6789\",\"organizationType\":\"privateCorporation\",\"countryOfOperation\":\"US\",\"addresses\":[{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\",\"type\":\"legalAddress\"}],\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"processingAccounts\":[{\"processingAccountId\":\"38765\",\"doingBusinessAs\":\"Pizza Doe\",\"status\":\"pending\",\"link\":{\"rel\":\"processingAccount\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\",\"method\":\"get\"},\"signature\":{\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"},\"type\":\"requestedViaDirectLink\"}}],\"metadata\":{\"customerId\":\"2345\"},\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}]}"));
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
        String expectedRequestBody = ""
                + "{\n"
                + "  \"business\": {\n"
                + "    \"name\": \"Example Corp\",\n"
                + "    \"taxId\": \"12-3456789\",\n"
                + "    \"organizationType\": \"privateCorporation\",\n"
                + "    \"countryOfOperation\": \"US\",\n"
                + "    \"addresses\": [\n"
                + "      {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\",\n"
                + "        \"type\": \"legalAddress\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"processingAccounts\": [\n"
                + "    {\n"
                + "      \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "      \"owners\": [\n"
                + "        {\n"
                + "          \"firstName\": \"Jane\",\n"
                + "          \"middleName\": \"Helen\",\n"
                + "          \"lastName\": \"Doe\",\n"
                + "          \"dateOfBirth\": \"1964-03-22\",\n"
                + "          \"address\": {\n"
                + "            \"address1\": \"1 Example Ave.\",\n"
                + "            \"address2\": \"Example Address Line 2\",\n"
                + "            \"address3\": \"Example Address Line 3\",\n"
                + "            \"city\": \"Chicago\",\n"
                + "            \"state\": \"Illinois\",\n"
                + "            \"country\": \"US\",\n"
                + "            \"postalCode\": \"60056\"\n"
                + "          },\n"
                + "          \"identifiers\": [\n"
                + "            {\n"
                + "              \"type\": \"nationalId\",\n"
                + "              \"value\": \"000-00-4320\"\n"
                + "            }\n"
                + "          ],\n"
                + "          \"contactMethods\": [\n"
                + "            {\n"
                + "              \"value\": \"jane.doe@example.com\",\n"
                + "              \"type\": \"email\"\n"
                + "            }\n"
                + "          ],\n"
                + "          \"relationship\": {\n"
                + "            \"equityPercentage\": 48.5,\n"
                + "            \"title\": \"CFO\",\n"
                + "            \"isControlProng\": true,\n"
                + "            \"isAuthorizedSignatory\": false\n"
                + "          }\n"
                + "        }\n"
                + "      ],\n"
                + "      \"website\": \"www.example.com\",\n"
                + "      \"businessType\": \"restaurant\",\n"
                + "      \"categoryCode\": 5999,\n"
                + "      \"merchandiseOrServiceSold\": \"Pizza\",\n"
                + "      \"businessStartDate\": \"2020-01-01\",\n"
                + "      \"timezone\": \"America/Chicago\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      },\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"processing\": {\n"
                + "        \"transactionAmounts\": {\n"
                + "          \"average\": 5000,\n"
                + "          \"highest\": 10000\n"
                + "        },\n"
                + "        \"monthlyAmounts\": {\n"
                + "          \"average\": 50000,\n"
                + "          \"highest\": 100000\n"
                + "        },\n"
                + "        \"volumeBreakdown\": {\n"
                + "          \"cardPresent\": 77,\n"
                + "          \"mailOrTelephone\": 3,\n"
                + "          \"ecommerce\": 20\n"
                + "        },\n"
                + "        \"isSeasonal\": true,\n"
                + "        \"monthsOfOperation\": [\n"
                + "          \"jan\",\n"
                + "          \"feb\"\n"
                + "        ],\n"
                + "        \"ach\": {\n"
                + "          \"naics\": \"5812\",\n"
                + "          \"previouslyTerminatedForAch\": false,\n"
                + "          \"refunds\": {\n"
                + "            \"writtenRefundPolicy\": true,\n"
                + "            \"refundPolicyUrl\": \"www.example.com/refund-poilcy-url\"\n"
                + "          },\n"
                + "          \"estimatedMonthlyTransactions\": 3000,\n"
                + "          \"limits\": {\n"
                + "            \"singleTransaction\": 10000,\n"
                + "            \"dailyDeposit\": 200000,\n"
                + "            \"monthlyDeposit\": 6000000\n"
                + "          },\n"
                + "          \"transactionTypes\": [\n"
                + "            \"prearrangedPayment\",\n"
                + "            \"other\"\n"
                + "          ],\n"
                + "          \"transactionTypesOther\": \"anotherTransactionType\"\n"
                + "        },\n"
                + "        \"cardAcceptance\": {\n"
                + "          \"debitOnly\": false,\n"
                + "          \"hsaFsa\": false,\n"
                + "          \"cardsAccepted\": [\n"
                + "            \"visa\",\n"
                + "            \"mastercard\"\n"
                + "          ],\n"
                + "          \"specialityCards\": {\n"
                + "            \"americanExpressDirect\": {\n"
                + "              \"enabled\": true,\n"
                + "              \"merchantNumber\": \"abc1234567\"\n"
                + "            },\n"
                + "            \"electronicBenefitsTransfer\": {\n"
                + "              \"enabled\": true,\n"
                + "              \"fnsNumber\": \"6789012\"\n"
                + "            },\n"
                + "            \"other\": {\n"
                + "              \"wexMerchantNumber\": \"abc1234567\",\n"
                + "              \"voyagerMerchantId\": \"abc1234567\",\n"
                + "              \"fleetMerchantId\": \"abc1234567\"\n"
                + "            }\n"
                + "          }\n"
                + "        }\n"
                + "      },\n"
                + "      \"funding\": {\n"
                + "        \"fundingSchedule\": \"nextday\",\n"
                + "        \"acceleratedFundingFee\": 1999,\n"
                + "        \"dailyDiscount\": false,\n"
                + "        \"fundingAccounts\": [\n"
                + "          {\n"
                + "            \"type\": \"checking\",\n"
                + "            \"use\": \"creditAndDebit\",\n"
                + "            \"nameOnAccount\": \"Jane Doe\",\n"
                + "            \"paymentMethods\": [\n"
                + "              {\n"
                + "                \"type\": \"ach\"\n"
                + "              }\n"
                + "            ],\n"
                + "            \"metadata\": {\n"
                + "              \"yourCustomField\": \"abc123\"\n"
                + "            }\n"
                + "          }\n"
                + "        ]\n"
                + "      },\n"
                + "      \"pricing\": {\n"
                + "        \"pricingIntentId\": \"6123\",\n"
                + "        \"type\": \"intent\"\n"
                + "      },\n"
                + "      \"signature\": {\n"
                + "        \"type\": \"requestedViaDirectLink\"\n"
                + "      },\n"
                + "      \"contacts\": [\n"
                + "        {\n"
                + "          \"type\": \"manager\",\n"
                + "          \"firstName\": \"Jane\",\n"
                + "          \"middleName\": \"Helen\",\n"
                + "          \"lastName\": \"Doe\",\n"
                + "          \"identifiers\": [\n"
                + "            {\n"
                + "              \"type\": \"nationalId\",\n"
                + "              \"value\": \"000-00-4320\"\n"
                + "            }\n"
                + "          ],\n"
                + "          \"contactMethods\": [\n"
                + "            {\n"
                + "              \"value\": \"jane.doe@example.com\",\n"
                + "              \"type\": \"email\"\n"
                + "            }\n"
                + "          ]\n"
                + "        }\n"
                + "      ],\n"
                + "      \"metadata\": {\n"
                + "        \"customerId\": \"2345\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
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
                + "  \"merchantPlatformId\": \"12345\",\n"
                + "  \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"business\": {\n"
                + "    \"name\": \"Example Corp\",\n"
                + "    \"taxId\": \"xxxxx6789\",\n"
                + "    \"organizationType\": \"privateCorporation\",\n"
                + "    \"countryOfOperation\": \"US\",\n"
                + "    \"addresses\": [\n"
                + "      {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\",\n"
                + "        \"type\": \"legalAddress\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"processingAccounts\": [\n"
                + "    {\n"
                + "      \"processingAccountId\": \"38765\",\n"
                + "      \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "      \"status\": \"pending\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"processingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\",\n"
                + "        \"method\": \"get\"\n"
                + "      },\n"
                + "      \"signature\": {\n"
                + "        \"link\": {\n"
                + "          \"rel\": \"previous\",\n"
                + "          \"method\": \"get\",\n"
                + "          \"href\": \"<uri>\"\n"
                + "        },\n"
                + "        \"type\": \"requestedViaDirectLink\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
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
                                "{\"merchantPlatformId\":\"12345\",\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"business\":{\"name\":\"Example Corp\",\"taxId\":\"xxxxx6789\",\"organizationType\":\"privateCorporation\",\"countryOfOperation\":\"US\",\"addresses\":[{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\",\"type\":\"legalAddress\"}],\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}]},\"processingAccounts\":[{\"processingAccountId\":\"38765\",\"doingBusinessAs\":\"Pizza Doe\",\"status\":\"approved\",\"link\":{\"rel\":\"processingAccount\",\"href\":\"https://api.payroc.com/v1/processing-accounts/38765\",\"method\":\"get\"},\"signature\":{\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"},\"type\":\"requestedViaDirectLink\"}}],\"metadata\":{\"customerId\":\"2345\"},\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}]}"));
        MerchantPlatform response = client.boarding()
                .merchantPlatforms()
                .retrieve("12345", RetrieveMerchantPlatformsRequest.builder().build());
        RecordedRequest request = server.takeRequest();
        Assertions.assertNotNull(request);
        Assertions.assertEquals("GET", request.getMethod());

        // Validate response body
        Assertions.assertNotNull(response, "Response should not be null");
        String actualResponseJson = objectMapper.writeValueAsString(response);
        String expectedResponseBody = ""
                + "{\n"
                + "  \"merchantPlatformId\": \"12345\",\n"
                + "  \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"business\": {\n"
                + "    \"name\": \"Example Corp\",\n"
                + "    \"taxId\": \"xxxxx6789\",\n"
                + "    \"organizationType\": \"privateCorporation\",\n"
                + "    \"countryOfOperation\": \"US\",\n"
                + "    \"addresses\": [\n"
                + "      {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\",\n"
                + "        \"type\": \"legalAddress\"\n"
                + "      }\n"
                + "    ],\n"
                + "    \"contactMethods\": [\n"
                + "      {\n"
                + "        \"value\": \"jane.doe@example.com\",\n"
                + "        \"type\": \"email\"\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"processingAccounts\": [\n"
                + "    {\n"
                + "      \"processingAccountId\": \"38765\",\n"
                + "      \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "      \"status\": \"approved\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"processingAccount\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/processing-accounts/38765\",\n"
                + "        \"method\": \"get\"\n"
                + "      },\n"
                + "      \"signature\": {\n"
                + "        \"link\": {\n"
                + "          \"rel\": \"previous\",\n"
                + "          \"method\": \"get\",\n"
                + "          \"href\": \"<uri>\"\n"
                + "        },\n"
                + "        \"type\": \"requestedViaDirectLink\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
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
    public void testCreateProcessingAccount() throws Exception {
        server.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setBody(
                                "{\"processingAccountId\":\"38765\",\"createdDate\":\"2024-07-02T12:00:00Z\",\"lastModifiedDate\":\"2024-07-02T12:00:00Z\",\"status\":\"entered\",\"doingBusinessAs\":\"Pizza Doe\",\"owners\":[{\"ownerId\":4564,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"link\":{\"rel\":\"owner\",\"href\":\"https://api.payroc.com/v1/owners/4564\",\"method\":\"get\"}}],\"website\":\"www.example.com\",\"businessType\":\"restaurant\",\"categoryCode\":5999,\"merchandiseOrServiceSold\":\"Pizza\",\"businessStartDate\":\"2020-01-01\",\"timezone\":\"America/Chicago\",\"address\":{\"address1\":\"1 Example Ave.\",\"address2\":\"Example Address Line 2\",\"address3\":\"Example Address Line 3\",\"city\":\"Chicago\",\"state\":\"Illinois\",\"country\":\"US\",\"postalCode\":\"60056\"},\"contactMethods\":[{\"value\":\"jane.doe@example.com\",\"type\":\"email\"}],\"processing\":{\"merchantId\":\"4525644354\",\"transactionAmounts\":{\"average\":5000,\"highest\":10000},\"monthlyAmounts\":{\"average\":50000,\"highest\":100000},\"volumeBreakdown\":{\"cardPresent\":77,\"mailOrTelephone\":3,\"ecommerce\":20},\"isSeasonal\":true,\"monthsOfOperation\":[\"jan\",\"feb\"],\"ach\":{\"naics\":\"5812\",\"previouslyTerminatedForAch\":false,\"refunds\":{\"writtenRefundPolicy\":true,\"refundPolicyUrl\":\"www.example.com/refund-poilcy-url\"},\"estimatedMonthlyTransactions\":3000,\"limits\":{\"singleTransaction\":10000,\"dailyDeposit\":200000,\"monthlyDeposit\":6000000},\"transactionTypes\":[\"prearrangedPayment\",\"other\"],\"transactionTypesOther\":\"anotherTransactionType\"},\"cardAcceptance\":{\"debitOnly\":false,\"hsaFsa\":false,\"cardsAccepted\":[\"visa\",\"mastercard\"],\"specialityCards\":{\"americanExpressDirect\":{\"enabled\":true,\"merchantNumber\":\"abc1234567\"},\"electronicBenefitsTransfer\":{\"enabled\":true,\"fnsNumber\":\"6789012\"},\"other\":{\"wexMerchantNumber\":\"abc1234567\",\"voyagerMerchantId\":\"abc1234567\",\"fleetMerchantId\":\"abc1234567\"}}}},\"funding\":{\"status\":\"enabled\",\"fundingSchedule\":\"nextday\",\"acceleratedFundingFee\":1999,\"dailyDiscount\":false,\"fundingAccounts\":[{\"fundingAccountId\":123,\"status\":\"pending\",\"link\":{\"rel\":\"fundingAccount\",\"method\":\"get\",\"href\":\"https://api.payroc.com/v1/funding-accounts/123\"}}]},\"pricing\":{\"link\":{\"rel\":\"pricing\",\"href\":\"https://api.payroc.com/v1/processing-accounts/12345/pricing\",\"method\":\"get\"}},\"contacts\":[{\"contactId\":1543,\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"link\":{\"rel\":\"contact\",\"href\":\"https://api.payroc.com/v1/contacts/1543\",\"method\":\"get\"}}],\"signature\":{\"link\":{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"},\"type\":\"requestedViaDirectLink\"},\"metadata\":{\"customerId\":\"2345\"},\"links\":[{\"rel\":\"previous\",\"method\":\"get\",\"href\":\"<uri>\"}]}"));
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
        String expectedRequestBody = ""
                + "{\n"
                + "  \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"middleName\": \"Helen\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"dateOfBirth\": \"1964-03-22\",\n"
                + "      \"address\": {\n"
                + "        \"address1\": \"1 Example Ave.\",\n"
                + "        \"address2\": \"Example Address Line 2\",\n"
                + "        \"address3\": \"Example Address Line 3\",\n"
                + "        \"city\": \"Chicago\",\n"
                + "        \"state\": \"Illinois\",\n"
                + "        \"country\": \"US\",\n"
                + "        \"postalCode\": \"60056\"\n"
                + "      },\n"
                + "      \"identifiers\": [\n"
                + "        {\n"
                + "          \"type\": \"nationalId\",\n"
                + "          \"value\": \"000-00-4320\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"relationship\": {\n"
                + "        \"equityPercentage\": 51.5,\n"
                + "        \"title\": \"CFO\",\n"
                + "        \"isControlProng\": true,\n"
                + "        \"isAuthorizedSignatory\": false\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"website\": \"www.example.com\",\n"
                + "  \"businessType\": \"restaurant\",\n"
                + "  \"categoryCode\": 5999,\n"
                + "  \"merchandiseOrServiceSold\": \"Pizza\",\n"
                + "  \"businessStartDate\": \"2020-01-01\",\n"
                + "  \"timezone\": \"America/Chicago\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
                + "    \"address2\": \"Example Address Line 2\",\n"
                + "    \"address3\": \"Example Address Line 3\",\n"
                + "    \"city\": \"Chicago\",\n"
                + "    \"state\": \"Illinois\",\n"
                + "    \"country\": \"US\",\n"
                + "    \"postalCode\": \"60056\"\n"
                + "  },\n"
                + "  \"contactMethods\": [\n"
                + "    {\n"
                + "      \"value\": \"jane.doe@example.com\",\n"
                + "      \"type\": \"email\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"processing\": {\n"
                + "    \"transactionAmounts\": {\n"
                + "      \"average\": 5000,\n"
                + "      \"highest\": 10000\n"
                + "    },\n"
                + "    \"monthlyAmounts\": {\n"
                + "      \"average\": 50000,\n"
                + "      \"highest\": 100000\n"
                + "    },\n"
                + "    \"volumeBreakdown\": {\n"
                + "      \"cardPresent\": 77,\n"
                + "      \"mailOrTelephone\": 3,\n"
                + "      \"ecommerce\": 20\n"
                + "    },\n"
                + "    \"isSeasonal\": true,\n"
                + "    \"monthsOfOperation\": [\n"
                + "      \"jan\",\n"
                + "      \"feb\"\n"
                + "    ],\n"
                + "    \"ach\": {\n"
                + "      \"naics\": \"5812\",\n"
                + "      \"previouslyTerminatedForAch\": false,\n"
                + "      \"refunds\": {\n"
                + "        \"writtenRefundPolicy\": true,\n"
                + "        \"refundPolicyUrl\": \"www.example.com/refund-poilcy-url\"\n"
                + "      },\n"
                + "      \"estimatedMonthlyTransactions\": 3000,\n"
                + "      \"limits\": {\n"
                + "        \"singleTransaction\": 10000,\n"
                + "        \"dailyDeposit\": 200000,\n"
                + "        \"monthlyDeposit\": 6000000\n"
                + "      },\n"
                + "      \"transactionTypes\": [\n"
                + "        \"prearrangedPayment\",\n"
                + "        \"other\"\n"
                + "      ],\n"
                + "      \"transactionTypesOther\": \"anotherTransactionType\"\n"
                + "    },\n"
                + "    \"cardAcceptance\": {\n"
                + "      \"debitOnly\": false,\n"
                + "      \"hsaFsa\": false,\n"
                + "      \"cardsAccepted\": [\n"
                + "        \"visa\",\n"
                + "        \"mastercard\"\n"
                + "      ],\n"
                + "      \"specialityCards\": {\n"
                + "        \"americanExpressDirect\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"merchantNumber\": \"abc1234567\"\n"
                + "        },\n"
                + "        \"electronicBenefitsTransfer\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"fnsNumber\": \"6789012\"\n"
                + "        },\n"
                + "        \"other\": {\n"
                + "          \"wexMerchantNumber\": \"abc1234567\",\n"
                + "          \"voyagerMerchantId\": \"abc1234567\",\n"
                + "          \"fleetMerchantId\": \"abc1234567\"\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"funding\": {\n"
                + "    \"fundingSchedule\": \"nextday\",\n"
                + "    \"acceleratedFundingFee\": 1999,\n"
                + "    \"dailyDiscount\": false,\n"
                + "    \"fundingAccounts\": [\n"
                + "      {\n"
                + "        \"type\": \"checking\",\n"
                + "        \"use\": \"creditAndDebit\",\n"
                + "        \"nameOnAccount\": \"Jane Doe\",\n"
                + "        \"paymentMethods\": [\n"
                + "          {\n"
                + "            \"type\": \"ach\"\n"
                + "          }\n"
                + "        ],\n"
                + "        \"metadata\": {\n"
                + "          \"yourCustomField\": \"abc123\"\n"
                + "        }\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"pricing\": {\n"
                + "    \"pricingIntentId\": \"6123\",\n"
                + "    \"type\": \"intent\"\n"
                + "  },\n"
                + "  \"signature\": {\n"
                + "    \"type\": \"requestedViaDirectLink\"\n"
                + "  },\n"
                + "  \"contacts\": [\n"
                + "    {\n"
                + "      \"type\": \"manager\",\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"middleName\": \"Helen\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"identifiers\": [\n"
                + "        {\n"
                + "          \"type\": \"nationalId\",\n"
                + "          \"value\": \"000-00-4320\"\n"
                + "        }\n"
                + "      ],\n"
                + "      \"contactMethods\": [\n"
                + "        {\n"
                + "          \"value\": \"jane.doe@example.com\",\n"
                + "          \"type\": \"email\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ],\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
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
                + "  \"processingAccountId\": \"38765\",\n"
                + "  \"createdDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"lastModifiedDate\": \"2024-07-02T12:00:00Z\",\n"
                + "  \"status\": \"entered\",\n"
                + "  \"doingBusinessAs\": \"Pizza Doe\",\n"
                + "  \"owners\": [\n"
                + "    {\n"
                + "      \"ownerId\": 4564,\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"owner\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/owners/4564\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"website\": \"www.example.com\",\n"
                + "  \"businessType\": \"restaurant\",\n"
                + "  \"categoryCode\": 5999,\n"
                + "  \"merchandiseOrServiceSold\": \"Pizza\",\n"
                + "  \"businessStartDate\": \"2020-01-01\",\n"
                + "  \"timezone\": \"America/Chicago\",\n"
                + "  \"address\": {\n"
                + "    \"address1\": \"1 Example Ave.\",\n"
                + "    \"address2\": \"Example Address Line 2\",\n"
                + "    \"address3\": \"Example Address Line 3\",\n"
                + "    \"city\": \"Chicago\",\n"
                + "    \"state\": \"Illinois\",\n"
                + "    \"country\": \"US\",\n"
                + "    \"postalCode\": \"60056\"\n"
                + "  },\n"
                + "  \"contactMethods\": [\n"
                + "    {\n"
                + "      \"value\": \"jane.doe@example.com\",\n"
                + "      \"type\": \"email\"\n"
                + "    }\n"
                + "  ],\n"
                + "  \"processing\": {\n"
                + "    \"merchantId\": \"4525644354\",\n"
                + "    \"transactionAmounts\": {\n"
                + "      \"average\": 5000,\n"
                + "      \"highest\": 10000\n"
                + "    },\n"
                + "    \"monthlyAmounts\": {\n"
                + "      \"average\": 50000,\n"
                + "      \"highest\": 100000\n"
                + "    },\n"
                + "    \"volumeBreakdown\": {\n"
                + "      \"cardPresent\": 77,\n"
                + "      \"mailOrTelephone\": 3,\n"
                + "      \"ecommerce\": 20\n"
                + "    },\n"
                + "    \"isSeasonal\": true,\n"
                + "    \"monthsOfOperation\": [\n"
                + "      \"jan\",\n"
                + "      \"feb\"\n"
                + "    ],\n"
                + "    \"ach\": {\n"
                + "      \"naics\": \"5812\",\n"
                + "      \"previouslyTerminatedForAch\": false,\n"
                + "      \"refunds\": {\n"
                + "        \"writtenRefundPolicy\": true,\n"
                + "        \"refundPolicyUrl\": \"www.example.com/refund-poilcy-url\"\n"
                + "      },\n"
                + "      \"estimatedMonthlyTransactions\": 3000,\n"
                + "      \"limits\": {\n"
                + "        \"singleTransaction\": 10000,\n"
                + "        \"dailyDeposit\": 200000,\n"
                + "        \"monthlyDeposit\": 6000000\n"
                + "      },\n"
                + "      \"transactionTypes\": [\n"
                + "        \"prearrangedPayment\",\n"
                + "        \"other\"\n"
                + "      ],\n"
                + "      \"transactionTypesOther\": \"anotherTransactionType\"\n"
                + "    },\n"
                + "    \"cardAcceptance\": {\n"
                + "      \"debitOnly\": false,\n"
                + "      \"hsaFsa\": false,\n"
                + "      \"cardsAccepted\": [\n"
                + "        \"visa\",\n"
                + "        \"mastercard\"\n"
                + "      ],\n"
                + "      \"specialityCards\": {\n"
                + "        \"americanExpressDirect\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"merchantNumber\": \"abc1234567\"\n"
                + "        },\n"
                + "        \"electronicBenefitsTransfer\": {\n"
                + "          \"enabled\": true,\n"
                + "          \"fnsNumber\": \"6789012\"\n"
                + "        },\n"
                + "        \"other\": {\n"
                + "          \"wexMerchantNumber\": \"abc1234567\",\n"
                + "          \"voyagerMerchantId\": \"abc1234567\",\n"
                + "          \"fleetMerchantId\": \"abc1234567\"\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"funding\": {\n"
                + "    \"status\": \"enabled\",\n"
                + "    \"fundingSchedule\": \"nextday\",\n"
                + "    \"acceleratedFundingFee\": 1999,\n"
                + "    \"dailyDiscount\": false,\n"
                + "    \"fundingAccounts\": [\n"
                + "      {\n"
                + "        \"fundingAccountId\": 123,\n"
                + "        \"status\": \"pending\",\n"
                + "        \"link\": {\n"
                + "          \"rel\": \"fundingAccount\",\n"
                + "          \"method\": \"get\",\n"
                + "          \"href\": \"https://api.payroc.com/v1/funding-accounts/123\"\n"
                + "        }\n"
                + "      }\n"
                + "    ]\n"
                + "  },\n"
                + "  \"pricing\": {\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"pricing\",\n"
                + "      \"href\": \"https://api.payroc.com/v1/processing-accounts/12345/pricing\",\n"
                + "      \"method\": \"get\"\n"
                + "    }\n"
                + "  },\n"
                + "  \"contacts\": [\n"
                + "    {\n"
                + "      \"contactId\": 1543,\n"
                + "      \"firstName\": \"Jane\",\n"
                + "      \"lastName\": \"Doe\",\n"
                + "      \"link\": {\n"
                + "        \"rel\": \"contact\",\n"
                + "        \"href\": \"https://api.payroc.com/v1/contacts/1543\",\n"
                + "        \"method\": \"get\"\n"
                + "      }\n"
                + "    }\n"
                + "  ],\n"
                + "  \"signature\": {\n"
                + "    \"link\": {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
                + "    },\n"
                + "    \"type\": \"requestedViaDirectLink\"\n"
                + "  },\n"
                + "  \"metadata\": {\n"
                + "    \"customerId\": \"2345\"\n"
                + "  },\n"
                + "  \"links\": [\n"
                + "    {\n"
                + "      \"rel\": \"previous\",\n"
                + "      \"method\": \"get\",\n"
                + "      \"href\": \"<uri>\"\n"
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
