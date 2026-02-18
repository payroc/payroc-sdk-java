# Reference
## Payment links
<details><summary><code>client.paymentLinks.list(processingTerminalId) -> PayrocPager&amp;lt;PaymentLinkPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of payment links linked to a processing terminal.  

**Note:** If you want to view the details of a specific payment link and you have its paymentLinkId, use our [Retrieve Payment Link](https://docs.payroc.com/api/schema/payment-links/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for only active links or multi-use links.  

Our gateway returns the following information about each payment link in the list:  
- **type** - Indicates whether the link can be used only once or if it can be used multiple times.  
- **authType** - Indicates whether the transaction is a sale or a pre-authorization.  
- **paymentMethods** - Indicates the payment method that the merchant accepts.  
- **charge** - Indicates whether the merchant or the customer enters the amount for the transaction.  
- **status** - Indicates if the payment link is active.  

For each payment link, we also return a paymentLinkId, which you can use for follow-on actions. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().list(
    "1234001",
    ListPaymentLinksRequest
        .builder()
        .merchantReference("LinkRef6543")
        .linkType(ListPaymentLinksRequestLinkType.MULTI_USE)
        .chargeType(ListPaymentLinksRequestChargeType.PRESET)
        .status(ListPaymentLinksRequestStatus.ACTIVE)
        .recipientName("Sarah Hazel Hopper")
        .recipientEmail("sarah.hopper@example.com")
        .createdOn(LocalDate.parse("2024-07-02"))
        .expiresOn(LocalDate.parse("2024-08-02"))
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**merchantReference:** `Optional<String>` — Filter results by the unique identifier that the merchant assigned to the payment link.
    
</dd>
</dl>

<dl>
<dd>

**linkType:** `Optional<ListPaymentLinksRequestLinkType>` — Filter results by the type of link. Send a value of <code>singleUse</code> or <code>multiUse</code>.
    
</dd>
</dl>

<dl>
<dd>

**chargeType:** `Optional<ListPaymentLinksRequestChargeType>` 

Filter results by the user that entered the amount. Send one of the following values:
- <code>prompt</code> - Customer entered the amount.
- <code>preset</code> - Merchant entered the amount.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListPaymentLinksRequestStatus>` — Filter results by the status of the payment link. Send a value of <code>active</code>, <code>completed</code>,<code>deactived</code>, or <code>expired</code>.
    
</dd>
</dl>

<dl>
<dd>

**recipientName:** `Optional<String>` — Filter results by the customer's name.
    
</dd>
</dl>

<dl>
<dd>

**recipientEmail:** `Optional<String>` — Filter results by the customer's email address.
    
</dd>
</dl>

<dl>
<dd>

**createdOn:** `Optional<String>` — Filter results by the link's created date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**expiresOn:** `Optional<String>` — Filter results by the link's expiry date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentLinks.create(processingTerminalId, request) -> CreatePaymentLinksResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a payment link that a customer can use to make a payment for goods or services.  

The request includes the following settings:
- **type** - Indicates whether the link can be used only once or if it can be used multiple times.
- **authType** - Indicates whether the transaction is a sale or a pre-authorization.
- **paymentMethod** - Indicates the payment methods that the merchant accepts.
- **charge** - Indicates whether the merchant or the customer enters the amount for the transaction.  

If your request is successful, our gateway returns a paymentLinkId, which you can use to perform follow-on actions.  

**Note:** To share the payment link with a customer, use our [Share Payment Link](https://docs.payroc.com/api/schema/payment-links/sharing-events/share) method.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().create(
    "1234001",
    CreatePaymentLinksRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            CreatePaymentLinksRequestBody.multiUse(
                MultiUsePaymentLink
                    .builder()
                    .merchantReference("LinkRef6543")
                    .order(
                        MultiUsePaymentLinkOrder
                            .builder()
                            .charge(
                                MultiUsePaymentLinkOrderCharge.prompt(
                                    PromptPaymentLinkCharge
                                        .builder()
                                        .currency(Currency.AED)
                                        .build()
                                )
                            )
                            .build()
                    )
                    .authType(MultiUsePaymentLinkAuthType.SALE)
                    .paymentMethods(
                        Arrays.asList(MultiUsePaymentLinkPaymentMethodsItem.CARD)
                    )
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `CreatePaymentLinksRequestBody` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentLinks.retrieve(paymentLinkId) -> RetrievePaymentLinksResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a payment link.  

To retrieve a payment link, you need its paymentLinkId. Our gateway returned the paymentLinkId in the response of the [Create Payment Link](https://docs.payroc.com/api/schema/payment-links/create) method.  

**Note:** If you don't have the paymentLinkId, use our [List Payment Links](https://docs.payroc.com/api/schema/payment-links/list) method to search for the payment link.  

Our gateway returns the following information about the payment link:  
- **type** - Indicates whether the link can be used only once or if it can be used multiple times.  
- **authType** - Indicates whether the transaction is a sale or a pre-authorization.  
- **paymentMethods** - Indicates the payment method that the merchant accepts.  
- **charge** - Indicates whether the merchant or the customer enters the amount for the transaction.
- **status** - Indicates if the payment link is active.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().retrieve(
    "JZURRJBUPS",
    RetrievePaymentLinksRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentLinkId:** `String` — Unique identifier that we assigned to the payment link.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentLinks.partiallyUpdate(paymentLinkId, request) -> PartiallyUpdatePaymentLinksResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update a payment link. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.

To update a payment link, you need its paymentLinkId, which we sent you in the response of the [Create Payment Link](https://docs.payroc.com/api/schema/payment-links/create) method.  

**Note:** If you don't have the paymentLinkId, use our [List Payment Links](https://docs.payroc.com/api/schema/payment-links/list) method to search for the payment link.  

You can update the following properties of a multi-use link:
- **expiresOn parameter** - Expiration date of the link.
- **customLabels object** - Label for the payment button.
- **credentialOnFile object** - Settings for saving the customer's payment details.

You can update the following properties of a single-use link:
- **expiresOn parameter** - Expiration date of the link.
- **authType parameter** - Transaction type of the payment link.
- **amount parameter** - Total amount of the transaction.
- **currency parameter** - Currency of the transaction.
- **description parameter** - Brief description of the transaction.
- **customLabels object** - Label for the payment button.
- **credentialOnFile object** - Settings for saving the customer's payment details.

**Note:** When a merchant updates a single-use link, we update the payment URL and HTML code in the assets object. The customer can't use the original link to make a payment.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().partiallyUpdate(
    "JZURRJBUPS",
    PartiallyUpdatePaymentLinksRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentLinkId:** `String` — Unique identifier that we assigned to the payment link.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentLinks.deactivate(paymentLinkId) -> DeactivatePaymentLinksResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to deactivate a payment link.  

To deactivate a payment link, you need its paymentLinkId. Our gateway returned the paymentLinkId in the response of the [Create Payment Link](https://docs.payroc.com/api/schema/payment-links/create) method.  

**Note:** If you don't have the paymentLinkId, use our [List Payment Links](https://docs.payroc.com/api/schema/payment-links/list) method to search for the payment link.  

If your request is successful, our gateway deactivates the payment link. The customer can't use the link to make a payment, and you can't reactivate the payment link.    
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().deactivate(
    "JZURRJBUPS",
    DeactivatePaymentLinksRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentLinkId:** `String` — Unique identifier that we assigned to the payment link.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Hosted Fields
<details><summary><code>client.hostedFields.create(processingTerminalId, request) -> HostedFieldsCreateSessionResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a Hosted Fields session token. You need to generate a new session token each time you load Hosted Fields on a webpage.  

In your request, you need to indicate whether the merchant is using Hosted Fields to run a sale, save payment details, or update saved payment details.  

In the response, our gateway returns the session token and the time that it expires. You need the session token when you configure the JavaScript for Hosted Fields.  

For more information about adding Hosted Fields to a webpage, go to [Hosted Fields](https://docs.payroc.com/guides/take-payments/hosted-fields). 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.hostedFields().create(
    "1234001",
    HostedFieldsCreateSessionRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .libVersion("1.1.0.123456")
        .scenario(HostedFieldsCreateSessionRequestScenario.PAYMENT)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**libVersion:** `String` 

Version of the Hosted Fields JavaScript library that you are using.  

The current production version is `1.6.0.172441`.
    
</dd>
</dl>

<dl>
<dd>

**scenario:** `HostedFieldsCreateSessionRequestScenario` 

Indicates if a merchant wants to take a payment or tokenize a customer's payment details:  

- `payment` - The merchant wants to run a sale or run a sale and tokenize in the same transaction.  
- `tokenization` - The merchant wants to save the customer's payment details to take a payment later or to update a customer's payment details that they've already saved.  
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `Optional<String>` 

Unique identifier that represents a customer's payment details.  

If a merchant wants to update a customer's payment details that are linked to a secure token, include the secureTokenId in your request.  
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## ApplePaySessions
<details><summary><code>client.applePaySessions.create(processingTerminalId, request) -> ApplePayResponseSession</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to start an Apple Pay session for your merchant.  

In the response, we return the startSessionObject that you send to Apple when you retrieve the cardholder's encrypted payment details.  

**Note:** For more information about how to integrate with Apple Pay, go to [Apple Pay](https://docs.payroc.com/guides/take-payments/apple-pay).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.applePaySessions().create(
    "1234001",
    ApplePaySessions
        .builder()
        .appleDomainId("DUHDZJHGYY")
        .appleValidationUrl("https://apple-pay-gateway.apple.com/paymentservices/startSession")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**appleDomainId:** `String` — Unique appleDomainId of the merchant's domain that we assigned when you added their domain to our Self-Care Portal.
    
</dd>
</dl>

<dl>
<dd>

**appleValidationUrl:** `String` — Validation URL from the Apple Pay JS API.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Attachments
<details><summary><code>client.attachments.uploadToProcessingAccount(processingAccountId, request) -> Attachment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> Before you upload an attachment, make sure that you follow local privacy regulations and get the merchant's consent to process their information.  

**Note:** You need the ID of the processing account before you can upload an attachment. If you don't know the processingAccountId, go to the [Retrieve a Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/retrieve) method.  

The attachment must be an uncompressed file under 30MB in one of the following formats:
- .bmp, csv, .doc, .docx, .gif, .htm, .html, .jpg, .jpeg, .msg, .pdf, .png, .ppt, .pptx, .tif, .tiff, .txt, .xls, .xlsx  

In the request, include the attachment that you want to upload and the following information about the attachment:
- **type** - Type of attachment that you want to upload.
- **description** - Short description of the attachment.  

In the response, our gateway returns information about the attachment including its upload status and an attachmentId that you can use to [Retrieve the details of the Attachment](https://docs.payroc.com/api/schema/attachments/retrieve).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.attachments().uploadToProcessingAccount(
    "38765",
    UploadAttachment
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .attachment(
            UploadToProcessingAccountAttachmentsRequestAttachment
                .builder()
                .type(UploadToProcessingAccountAttachmentsRequestAttachmentType.BANKING_EVIDENCE)
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.attachments.retrieve(attachmentId) -> Attachment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve the details of an attachment.  

To retrieve the details of an attachment you need its attachmentId. Our gateway returned the attachmentId in the response of the method that you used to upload the attachment.  

Our gateway returns information about the attachment, including its upload status and the entity that the attachment is linked to. Our gateway doesn't return the file that you uploaded.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.attachments().retrieve(
    "12876",
    RetrieveAttachmentsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**attachmentId:** `String` — Unique identifier of the attachment
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Auth
<details><summary><code>client.auth.retrieveToken() -> GetTokenResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Obtain an access token using client credentials
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.auth().retrieveToken(
    RetrieveTokenAuthRequest
        .builder()
        .apiKey("x-api-key")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**apiKey:** `String` — The API key of the application
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## BankTransferPayments Payments
<details><summary><code>client.bankTransferPayments.payments.list() -> PayrocPager&amp;lt;BankTransferPaymentPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of payments.  

**Note:** If you want to view the details of a specific payment and you have its paymentId, use our [Retrieve Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for payments for a customer, a date range, or a settlement state.  

Our gateway returns the following information about each payment in the list:  

- Order details, including the transaction amount and when it was processed.  
- Bank account details, including the customer’s name and account number.  
- Customer's details, including the customer’s phone number.  
- Transaction details, including any refunds or re-presentments.  

For each transaction, we also return the paymentId and an optional secureTokenId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().payments().list(
    ListPaymentsRequest
        .builder()
        .processingTerminalId("1234001")
        .orderId("OrderRef6543")
        .nameOnAccount("Sarah%20Hazel%20Hopper")
        .last4("7890")
        .dateFrom(OffsetDateTime.parse("2024-07-01T00:00:00Z"))
        .dateTo(OffsetDateTime.parse("2024-07-31T23:59:59Z"))
        .settlementState(ListPaymentsRequestSettlementState.SETTLED)
        .settlementDate(LocalDate.parse("2024-07-15"))
        .paymentLinkId("JZURRJBUPS")
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Filter results by the unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**orderId:** `Optional<String>` — Filter results by the order ID of the payment.
    
</dd>
</dl>

<dl>
<dd>

**nameOnAccount:** `Optional<String>` — Filter results by the account holder's name.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter results by the last four digits of the account number.
    
</dd>
</dl>

<dl>
<dd>

**type:** `Optional<ListPaymentsRequestTypeItem>` — Filter results by transaction type.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListPaymentsRequestStatusItem>` — Filter results by the status of the payment.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `Optional<OffsetDateTime>` — Filter results by payments that the merchant ran after a specific date. The value follows the [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) standard.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `Optional<OffsetDateTime>` — Filter results by payments that the merchant ran before a specific date. The value follows the [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) standard.
    
</dd>
</dl>

<dl>
<dd>

**settlementState:** `Optional<ListPaymentsRequestSettlementState>` — Filter results by the settlement status.
    
</dd>
</dl>

<dl>
<dd>

**settlementDate:** `Optional<String>` — Filter results by the settlement date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**paymentLinkId:** `Optional<String>` — Filter results by the paymentLinkId.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.payments.create(request) -> BankTransferPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to run a sale with a customer's bank account details.  

In the response, our gateway returns information about the bank transfer payment and a paymentId, which you need for the following methods:  
-	[Retrieve payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/retrieve) - View the details of the bank transfer payment.
-	[Reverse payment](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/reverse-payment) - Cancel the bank transfer payment if it's an open batch.
-	[Refund payment](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/refund) - Run a referenced refund to return funds to the customer's bank account.

**Payment methods**  

Our gateway accepts the following payment methods:  
-	Automated clearing house (ACH) details
-	Pre-authorized debit (PAD) details  

You can also use [secure tokens](https://docs.payroc.com/api/schema/payments/secure-tokens/overview) and [single-use tokens](https://docs.payroc.com/api/schema/tokenization/single-use-tokens/create) that you created from ACH details or PAD details. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().payments().create(
    BankTransferPaymentRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .order(
            BankTransferPaymentRequestOrder
                .builder()
                .orderId("OrderRef6543")
                .description("Large Pepperoni Pizza")
                .amount(4999L)
                .currency(Currency.USD)
                .breakdown(
                    BankTransferRequestBreakdown
                        .builder()
                        .subtotal(4347L)
                        .tip(
                            Tip
                                .builder()
                                .type(TipType.PERCENTAGE)
                                .percentage(10.0)
                                .build()
                        )
                        .taxes(
                            Optional.of(
                                Arrays.asList(
                                    TaxRate
                                        .builder()
                                        .type(TaxRateType.RATE)
                                        .rate(5.0)
                                        .name("Sales Tax")
                                        .build()
                                )
                            )
                        )
                        .build()
                )
                .build()
        )
        .paymentMethod(
            BankTransferPaymentRequestPaymentMethod.ach(
                AchPayload
                    .builder()
                    .nameOnAccount("Shara Hazel Hopper")
                    .accountNumber("1234567890")
                    .routingNumber("123456789")
                    .build()
            )
        )
        .customer(
            BankTransferCustomer
                .builder()
                .notificationLanguage(BankTransferCustomerNotificationLanguage.EN)
                .contactMethods(
                    Optional.of(
                        Arrays.asList(
                            ContactMethod.email(
                                ContactMethodEmail
                                    .builder()
                                    .value("jane.doe@example.com")
                                    .build()
                            )
                        )
                    )
                )
                .build()
        )
        .credentialOnFile(
            SchemasCredentialOnFile
                .builder()
                .tokenize(true)
                .build()
        )
        .customFields(
            Optional.of(
                Arrays.asList(
                    CustomField
                        .builder()
                        .name("yourCustomField")
                        .value("abc123")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**order:** `BankTransferPaymentRequestOrder` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<BankTransferCustomer>` 
    
</dd>
</dl>

<dl>
<dd>

**credentialOnFile:** `Optional<SchemasCredentialOnFile>` 
    
</dd>
</dl>

<dl>
<dd>

**paymentMethod:** `BankTransferPaymentRequestPaymentMethod` 

Polymorphic object that contains payment detail information.  

The value of the type parameter determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`pad` - Pre-authorized debit (PAD) details
-	`secureToken` - Secure token details
-	`singleUseToken` - Single-use token details
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.payments.retrieve(paymentId) -> BankTransferPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a bank transfer payment.  

To retrieve a payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/create) method.  

Note: If you don’t have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/list) method to search for the payment.  

Our gateway returns the following information about the payment:  

-	Order details, including the transaction amount and when it was processed.  
-	Bank account details, including the customer’s name and account number.  
-	Customer’s details, including the customer’s phone number.  
-	Transaction details, including any refunds or re-presentments.  

If the merchant saved the customer’s bank account details, our gateway returns a secureTokenID, which you can use to perform follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().payments().retrieve(
    "M2MJOG6O2Y",
    RetrievePaymentsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier that our gateway assigned to the payment.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.payments.represent(paymentId, request) -> BankTransferPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to re-present an ACH payment.  

To re-present a payment, you need the paymentId of the return. To get the paymentId of the return, complete the following steps:  

1.	Use our [Retrieve Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/retrieve) method  to view the details of the original payment.  
2.	From the [returns object](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/retrieve#response.body.returns) in the response, get the paymentId of the return.  

Our gateway uses the bank account details from the original payment. If you want to update the customer's bank account details, send the new bank account details in the request.  

If your request is successful, our gateway re-presents the payment.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().payments().represent(
    "M2MJOG6O2Y",
    Representment
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .paymentMethod(
            RepresentmentPaymentMethod.ach(
                AchPayload
                    .builder()
                    .nameOnAccount("Shara Hazel Hopper")
                    .accountNumber("1234567890")
                    .routingNumber("123456789")
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier that our gateway assigned to the payment.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**paymentMethod:** `Optional<RepresentmentPaymentMethod>` 

Polymorphic object that contains the customer's updated payment details.  

The value of the type parameter determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`secureToken` - Secure token details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## BankTransferPayments Refunds
<details><summary><code>client.bankTransferPayments.refunds.reversePayment(paymentId) -> BankTransferPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a bank transfer payment in an open batch. This is also known as voiding a payment.  

To cancel a bank transfer payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/create) method.  

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/list) method to search for the bank transfer payment.  

If your request is successful, our gateway removes the bank transfer payment from the merchant’s open batch and no funds are taken from the customer's bank account.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().reversePayment(
    "M2MJOG6O2Y",
    ReversePaymentRefundsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier that our gateway assigned to the payment.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.refunds.refund(paymentId, request) -> BankTransferPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to refund a bank transfer payment that is in a closed batch.  

To refund a bank transfer payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/create) method.  

**Note:** If you don’t have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/bank-transfer-payments/payments/list) method to search for the bank transfer payment.  

If your refund is successful, our gateway returns the payment amount to the customer's account.  

**Things to consider**  
- If the merchant refunds a bank transfer payment that is in an open batch, our gateway reverses the bank transfer payment.  
- Some merchants can run unreferenced refunds, which means that they don’t need a paymentId to return an amount to a customer. For more information about how to run an unreferenced refund, go to [Create Refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/create).  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().refund(
    "M2MJOG6O2Y",
    BankTransferReferencedRefund
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .amount(4999L)
        .description("amount to refund")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier that our gateway assigned to the payment.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**amount:** `Long` — Total amount of the refund. The value is in the currency's lowest denomination, for example, cents.
    
</dd>
</dl>

<dl>
<dd>

**description:** `String` — Description of the refund.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.refunds.list() -> PayrocPager&amp;lt;BankTransferRefundPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of bank transfer refunds.  

**Note:** If you want to view the details of a specific refund and you have its refundId, use our [Retrieve Refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for refunds for a customer, an orderId, or a date range.  

Our gateway returns the following information about each refund in the list:  

-	Order details, including the refund amount and when it was processed.  
-	Bank account details, including the customer’s name and account number.  

For referenced refunds, our gateway also returns details about the payment that the refund is linked to.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().list(
    ListRefundsRequest
        .builder()
        .processingTerminalId("1234001")
        .orderId("OrderRef6543")
        .nameOnAccount("Sarah%20Hazel%20Hopper")
        .last4("7062")
        .dateFrom(OffsetDateTime.parse("2024-07-01T00:00:00Z"))
        .dateTo(OffsetDateTime.parse("2024-07-31T23:59:59Z"))
        .settlementState(ListRefundsRequestSettlementState.SETTLED)
        .settlementDate(LocalDate.parse("2024-07-15"))
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Filter results by the unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**orderId:** `Optional<String>` — Filter results by the order ID of the refund.
    
</dd>
</dl>

<dl>
<dd>

**nameOnAccount:** `Optional<String>` — Filter results by the accountholder's name.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter results by the last four digits of the account number.
    
</dd>
</dl>

<dl>
<dd>

**type:** `Optional<ListRefundsRequestTypeItem>` — Filter results by transaction type.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListRefundsRequestStatusItem>` — Filter results by the status of the refund.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `Optional<OffsetDateTime>` — Filter results by refunds that the merchant ran after a specific date. The value follows the [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) standard.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `Optional<OffsetDateTime>` — Filter results by refunds that the merchant ran before a specific date. The value follows the [ISO 8601](https://www.iso.org/iso-8601-date-and-time-format.html) standard.
    
</dd>
</dl>

<dl>
<dd>

**settlementState:** `Optional<ListRefundsRequestSettlementState>` — Filter results by the settlement status.
    
</dd>
</dl>

<dl>
<dd>

**settlementDate:** `Optional<String>` — Filter results by the settlement date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.refunds.create(request) -> BankTransferRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create an unreferenced refund. An unreferenced refund is a refund that isn’t linked to a bank transfer payment.  

**Note:** If you have the paymentId of the payment you want to refund, use our [Refund Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/refund) method. If you use our Refund Payment method, our gateway sends the refund amount to the customer’s original payment method and links the refund to the payment.  

In the request, you must provide the customer’s payment method and information about the order including the refund amount.  

In the response, our gateway returns information about the refund and a refundId, which you need for the following methods:  

-	[Retrieve refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/retrieve) – View the details of the refund.  
-	[Reverse refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/reverse-refund) – Cancel the refund if it’s in an open batch.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().create(
    BankTransferUnreferencedRefund
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .order(
            BankTransferRefundOrder
                .builder()
                .orderId("OrderRef6543")
                .description("Refund for order OrderRef6543")
                .amount(4999L)
                .currency(Currency.USD)
                .build()
        )
        .refundMethod(
            BankTransferUnreferencedRefundRefundMethod.ach(
                AchPayload
                    .builder()
                    .nameOnAccount("Shara Hazel Hopper")
                    .accountNumber("1234567890")
                    .routingNumber("123456789")
                    .build()
            )
        )
        .customer(
            BankTransferCustomer
                .builder()
                .notificationLanguage(BankTransferCustomerNotificationLanguage.EN)
                .contactMethods(
                    Optional.of(
                        Arrays.asList(
                            ContactMethod.email(
                                ContactMethodEmail
                                    .builder()
                                    .value("jane.doe@example.com")
                                    .build()
                            )
                        )
                    )
                )
                .build()
        )
        .customFields(
            Optional.of(
                Arrays.asList(
                    CustomField
                        .builder()
                        .name("yourCustomField")
                        .value("abc123")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**order:** `BankTransferRefundOrder` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<BankTransferCustomer>` 
    
</dd>
</dl>

<dl>
<dd>

**refundMethod:** `BankTransferUnreferencedRefundRefundMethod` 

Polymorphic object that contains payment details for the refund.  

The value of the type parameter determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`secureToken` - Secure token details
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.refunds.retrieve(refundId) -> BankTransferRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a refund.  

To retrieve a refund, you need its refundId. Our gateway returned the refundId in the response of the [Refund Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/refund) method or the [Create Refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/create) method.  

**Note:** If you don’t have the refundId, use our [List Refunds](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/list) method to search for the refund.  

Our gateway returns the following information about the refund:  

- Order details, including the refund amount and when it was processed.  
- Bank account details, including the customer’s name and account number.  

If the refund is a referenced refund, our gateway also returns details about the payment that the refund is linked to.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().retrieve(
    "CD3HN88U9F",
    RetrieveRefundsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundId:** `String` — Unique identifier that our gateway assigned to the refund.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.bankTransferPayments.refunds.reverseRefund(refundId) -> BankTransferRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a bank transfer refund in an open batch.  

To cancel a refund, you need its refundId. Our gateway returned the refundId in the response of the [Refund Payment](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/refund) or [Create Refund](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/create) method.  

**Note:** If you don’t have the refundId, use our [List Refunds](https://docs.payroc.com/api/schema/bank-transfer-payments/refunds/list) method to search for the refund.  

If your request is successful, the gateway removes the refund from the merchant’s open batch, and no funds are returned to the cardholder’s account.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.bankTransferPayments().refunds().reverseRefund(
    "CD3HN88U9F",
    ReverseRefundRefundsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundId:** `String` — Unique identifier that our gateway assigned to the refund.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding Owners
<details><summary><code>client.boarding.owners.retrieve(ownerId) -> Owner</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve details about an owner of a processing account or an owner associated with a funding recipient.  

To retrieve an owner, you need their ownerId. Our gateway returned the ownerId in the response of the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method or the [Create Funding Recipient Owner](https://docs.payroc.com/api/schema/funding/funding-recipients/create-owner) method.  

**Note:** If you don't have the ownerId, use the [Retrieve Processing Account](https://docs.payroc.com/api/schema/boarding/processing-accounts/retrieve) method if you are searching for a processing account owner, or use the [List Funding Recipient Owners](https://docs.payroc.com/api/schema/funding/funding-recipients/list-owners) method if you are searching for a funding recipient owner.  

Our gateway returns the following information about an owner:  
- Name, date of birth, and address.  
- Contact details, including their email address.  
- Relationship to the business, including whether they are a control prong or authorized signatory, and their equity stake in the business. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().owners().retrieve(
    1,
    RetrieveOwnersRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**ownerId:** `Integer` — Unique identifier that we assigned to the owner.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.owners.update(ownerId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can't update the details of an owner of a processing account.  

Use this method to update the details of an owner associated with a funding recipient.  

To update an owner, you need their ownerId. Our gateway returned the ownerId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method and the [Create Funding Recipient Owner](https://docs.payroc.com/api/schema/funding/funding-recipients/create-owner) method.   

**Note:** If you don't have the ownerId, use the [List Funding Recipient Owners](https://docs.payroc.com/api/schema/funding/funding-recipients/list-owners) method, the [Retrieve Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/retrieve) method, or the [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient owner.  

You can update the following details about an owner:  

- Personal details, including their name, date of birth, and address.  
- Identification details, including their identification type and number.  
- Contact details, including their email address.  
- Relationship to the business, including whether they are a control prong.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().owners().update(
    1,
    UpdateOwnersRequest
        .builder()
        .body(
            Owner
                .builder()
                .firstName("Jane")
                .lastName("Doe")
                .dateOfBirth(LocalDate.parse("1964-03-22"))
                .address(
                    Address
                        .builder()
                        .address1("1 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .address2("Example Address Line 2")
                        .address3("Example Address Line 3")
                        .build()
                )
                .relationship(
                    OwnerRelationship
                        .builder()
                        .isControlProng(true)
                        .equityPercentage(48.5f)
                        .title("CFO")
                        .isAuthorizedSignatory(false)
                        .build()
                )
                .middleName("Helen")
                .identifiers(
                    Arrays.asList(
                        Identifier
                            .builder()
                            .type(IdentifierType.NATIONAL_ID)
                            .value("000-00-4320")
                            .build()
                    )
                )
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**ownerId:** `Integer` — Unique identifier that we assigned to the owner.
    
</dd>
</dl>

<dl>
<dd>

**request:** `Owner` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.owners.delete(ownerId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can't delete an owner of a processing account. 

Use this method to delete an owner associated with a funding recipient. You can delete an owner only if the funding recipient has more than one owner.  

To delete an owner, you need their ownerId. Our gateway returned the ownerId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method and the [Create Funding Recipient Owner](https://docs.payroc.com/api/schema/funding/funding-recipients/create-owner) method.  

**Note:** If you don't have the ownerId, use the [List Funding Recipient Owners](https://docs.payroc.com/api/schema/funding/funding-recipients/list-owners) method, the [Retrieve Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/retrieve) method, or the [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient owner.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().owners().delete(
    1,
    DeleteOwnersRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**ownerId:** `Integer` — Unique identifier that we assigned to the owner.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding PricingIntents
<details><summary><code>client.boarding.pricingIntents.list() -> PayrocPager&amp;lt;PaginatedPricingIntent&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of pricing intents associated with the ISV.  

**Note:** If you want to view the details of a specific pricing intent and you have its pricingIntentId, use our [Retrieve Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/retrieve) method.  

Our gateway returns the following information about each pricing intent in the list:  

- Information about the fees, including the base fees, gateway fees, and processor fees.  
- Status of the pricing intent, including whether we approved the pricing intent.  

For each pricing intent, we also return its pricingIntentId which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().list(
    ListPricingIntentsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.pricingIntents.create(request) -> PricingIntent50</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a pricing intent that you can assign to a processing account.  

In the request, you must provide the following:
-	Processing fees, including the pricing program and the fee to process each transaction.
-	Gateway fees, including the fee for each transaction handled by our gateway.
-	Base fees, including maintenance and PCI fees.

In the response, our gateway returns information about the pricing intent and the pricingIntentId, which you need for the following methods:
-	[Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) - Assign the pricing intent to a processing account, when you create the merchant platform and its processing accounts.
-	[Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) - Assign the pricing intent to a processing account.
-	[Retrieve Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/retrieve) - Retrieve information about a pricing intent.
-	[Update Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/update) - Update the details of a pricing intent. 
-	[Delete Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/delete) - Delete a pricing intent.
-	[Partially Update Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/partially-update) - Partially update the details of a pricing intent.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().create(
    CreatePricingIntentsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            PricingIntent50
                .builder()
                .key("Your-Unique-Identifier")
                .country(PricingAgreementUs50Country.US)
                .version(PricingAgreementUs50Version.FIVE_0)
                .base(
                    BaseUs
                        .builder()
                        .annualFee(
                            BaseUsAnnualFee
                                .builder()
                                .amount(9900)
                                .billInMonth(BaseUsAnnualFeeBillInMonth.JUNE)
                                .build()
                        )
                        .maintenance(500)
                        .minimum(100)
                        .batch(1500)
                        .addressVerification(5)
                        .regulatoryAssistanceProgram(15)
                        .pciNonCompliance(4995)
                        .merchantAdvantage(10)
                        .platinumSecurity(
                            BaseUsPlatinumSecurity.monthly(
                                BaseUsMonthly
                                    .builder()
                                    .build()
                            )
                        )
                        .voiceAuthorization(95)
                        .chargeback(2500)
                        .retrieval(1500)
                        .earlyTermination(57500)
                        .build()
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("yourCustomField", "abc123");
                    }}
                )
                .processor(
                    PricingAgreementUs50Processor
                        .builder()
                        .card(
                            PricingAgreementUs50ProcessorCard.interchangePlus(
                                InterchangePlus
                                    .builder()
                                    .fees(
                                        InterchangePlusFees
                                            .builder()
                                            .mastercardVisaDiscover(
                                                ProcessorFee
                                                    .builder()
                                                    .build()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                        )
                        .build()
                )
                .services(
                    Arrays.asList(
                        ServiceUs50.hardwareAdvantagePlan(
                            HardwareAdvantagePlan
                                .builder()
                                .enabled(true)
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `PricingIntent50` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.pricingIntents.retrieve(pricingIntentId) -> PricingIntent50</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a pricing intent.  

To retrieve a pricing intent, you need its pricingIntentId. Our gateway returned the pricingIntentId in the response of the [Create Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/create) method.  

**Note:** If you don't have the pricingIntentId, use our [List Pricing Intents](https://docs.payroc.com/api/schema/boarding/pricing-intents/list) method to search for the pricing intent.  

Our gateway returns the following information about the pricing intent:  

- Information about the fees, including the base fees, gateway fees, and processor fees.  
- Status of the pricing intent, including whether we approved the pricing intent.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().retrieve(
    "5",
    RetrievePricingIntentsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pricingIntentId:** `String` — Unique identifier that we assigned to the pricing intent.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.pricingIntents.update(pricingIntentId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to update the details of a pricing intent. If you update a pricing intent, it won't affect merchant that you've previously onboarded.  

To update a pricing intent, you need its pricingIntentId. Our gateway returned the pricingIntentId in the response of the [Create Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/create) method.  

**Note:** If you don't have the pricingIntentId, use our [List Pricing Intents](https://docs.payroc.com/api/schema/boarding/pricing-intents/list) method to search for the pricing intent.  

You can update the following details about a pricing intent:  

- Fees, including the base fees, processor fees, and gateway fees.  
- Custom name for the pricing intent.  
- Additional services that merchants can sign up for.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().update(
    "5",
    UpdatePricingIntentsRequest
        .builder()
        .body(
            PricingIntent50
                .builder()
                .key("Your-Unique-Identifier")
                .country(PricingAgreementUs50Country.US)
                .version(PricingAgreementUs50Version.FIVE_0)
                .base(
                    BaseUs
                        .builder()
                        .annualFee(
                            BaseUsAnnualFee
                                .builder()
                                .amount(9900)
                                .billInMonth(BaseUsAnnualFeeBillInMonth.JUNE)
                                .build()
                        )
                        .maintenance(500)
                        .minimum(100)
                        .batch(1500)
                        .addressVerification(5)
                        .regulatoryAssistanceProgram(15)
                        .pciNonCompliance(4995)
                        .merchantAdvantage(10)
                        .platinumSecurity(
                            BaseUsPlatinumSecurity.monthly(
                                BaseUsMonthly
                                    .builder()
                                    .build()
                            )
                        )
                        .voiceAuthorization(95)
                        .chargeback(2500)
                        .retrieval(1500)
                        .earlyTermination(57500)
                        .build()
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("yourCustomField", "abc123");
                    }}
                )
                .processor(
                    PricingAgreementUs50Processor
                        .builder()
                        .card(
                            PricingAgreementUs50ProcessorCard.interchangePlus(
                                InterchangePlus
                                    .builder()
                                    .fees(
                                        InterchangePlusFees
                                            .builder()
                                            .mastercardVisaDiscover(
                                                ProcessorFee
                                                    .builder()
                                                    .build()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                        )
                        .ach(
                            Ach
                                .builder()
                                .fees(
                                    AchFees
                                        .builder()
                                        .transaction(50)
                                        .batch(5)
                                        .returns(400)
                                        .unauthorizedReturn(1999)
                                        .statement(800)
                                        .monthlyMinimum(20000)
                                        .accountVerification(10)
                                        .discountRateUnder10000(5.25)
                                        .discountRateAbove10000(10.0)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .gateway(
                    GatewayUs50
                        .builder()
                        .fees(
                            GatewayUs50Fees
                                .builder()
                                .monthly(2000)
                                .setup(5000)
                                .perTransaction(2000)
                                .perDeviceMonthly(10)
                                .build()
                        )
                        .build()
                )
                .services(
                    Arrays.asList(
                        ServiceUs50.hardwareAdvantagePlan(
                            HardwareAdvantagePlan
                                .builder()
                                .enabled(true)
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pricingIntentId:** `String` — Unique identifier that we assigned to the pricing intent.
    
</dd>
</dl>

<dl>
<dd>

**request:** `PricingIntent50` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.pricingIntents.delete(pricingIntentId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete a pricing intent.  

> **Important:** When you delete a pricing intent, you can't recover it. You also won't be able to assign the pricing intent to a merchant's boarding application.  

To delete a pricing intent, you need its pricingIntentId. Our gateway returned the pricingIntentId in the response of the [Create Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/create) method.  

**Note:** If you don't have the pricingIntentId, use our [List Pricing Intents](https://docs.payroc.com/api/schema/boarding/pricing-intents/list) method to search for the pricing intent.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().delete(
    "5",
    DeletePricingIntentsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pricingIntentId:** `String` — Unique identifier that we assigned to the pricing intent.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.pricingIntents.partiallyUpdate(pricingIntentId, request) -> PricingIntent50</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update a pricing intent. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.  

If you update a pricing intent, it won't affect merchants you've previously onboarded.  

To update a pricing intent, you need its pricingIntentId. Our gateway returned the pricingIntentId in the response of the [Create Pricing Intent](https://docs.payroc.com/api/schema/boarding/pricing-intents/create) method.  

**Note:** If you don't have the pricingIntentId, use our [List Pricing Intents](https://docs.payroc.com/api/schema/boarding/pricing-intents/list) method to search for the pricing intent.  

You can update the following details about a pricing intent:  

- Fees, including the base fees, processor fees, and gateway fees.  
- Custom name for the pricing intent.  
- Additional services that merchants can sign up for.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().pricingIntents().partiallyUpdate(
    "5",
    PartiallyUpdatePricingIntentsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**pricingIntentId:** `String` — Unique identifier that we assigned to the pricing intent.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding MerchantPlatforms
<details><summary><code>client.boarding.merchantPlatforms.list() -> PayrocPager&amp;lt;PaginatedMerchants&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of merchant platforms that are linked to your ISV account.  

**Note**: If you want to view the details of a specific merchant platform and you have its merchantPlatformId, use our [Retrieve Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/retrieve) method.  

Our gateway returns the following information about each merchant platform in the list:  
- Legal information, including its legal name and address.  
- Contact information, including the email address for the business.  
- Processing  account information, including the processingAccountId and status of each processing account that's linked to the merchant platform.  

For each merchant platform, we also return its merchantPlatformId and its linked processingAccountIds, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().merchantPlatforms().list(
    ListMerchantPlatformsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.merchantPlatforms.create(request) -> MerchantPlatform</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to board a merchant with Payroc.  

**Note**: This method is part of our Boarding solution. To help you understand how this method works with other Boarding methods, go to [Board a Merchant](https://docs.payroc.com/guides/board-merchants/boarding).  

In the request, include the following information:  
- Legal information, including its legal name and address.  
- Contact information, including the email address for the business.  
- Processing account information, including the pricing model, owners, and contacts for the processing account.  

When you send a successful request, we review the merchant's information. After we complete our review and approve the merchant, we assign:  
- **merchantPlatformId** - Unique identifier for the merchant platform.  
- **processingAccountId** - Unique identifier for each processing account linked to the merchant platform.  

You need to keep these to perform follow-on actions, for example, you need the processingAccountId to [order terminals](https://docs.payroc.com/api/schema/boarding/processing-accounts/create-terminal-order) for the processing account.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().merchantPlatforms().create(
    CreateMerchantAccount
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .business(
            Business
                .builder()
                .name("Example Corp")
                .taxId("12-3456789")
                .organizationType(BusinessOrganizationType.PRIVATE_CORPORATION)
                .countryOfOperation(BusinessCountryOfOperation.US)
                .addresses(
                    Arrays.asList(
                        LegalAddress
                            .builder()
                            .type(AddressTypeType.LEGAL_ADDRESS)
                            .address1("1 Example Ave.")
                            .city("Chicago")
                            .state("Illinois")
                            .country("US")
                            .postalCode("60056")
                            .address2("Example Address Line 2")
                            .address3("Example Address Line 3")
                            .build()
                    )
                )
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        )
                    )
                )
                .build()
        )
        .processingAccounts(
            Arrays.asList(
                CreateProcessingAccount
                    .builder()
                    .doingBusinessAs("Pizza Doe")
                    .businessType(CreateProcessingAccountBusinessType.RESTAURANT)
                    .categoryCode(5999)
                    .merchandiseOrServiceSold("Pizza")
                    .businessStartDate(LocalDate.parse("2020-01-01"))
                    .timezone(Timezone.AMERICA_CHICAGO)
                    .address(
                        Address
                            .builder()
                            .address1("1 Example Ave.")
                            .city("Chicago")
                            .state("Illinois")
                            .country("US")
                            .postalCode("60056")
                            .address2("Example Address Line 2")
                            .address3("Example Address Line 3")
                            .build()
                    )
                    .processing(
                        Processing
                            .builder()
                            .transactionAmounts(
                                ProcessingTransactionAmounts
                                    .builder()
                                    .average(5000)
                                    .highest(10000)
                                    .build()
                            )
                            .monthlyAmounts(
                                ProcessingMonthlyAmounts
                                    .builder()
                                    .average(50000)
                                    .highest(100000)
                                    .build()
                            )
                            .volumeBreakdown(
                                ProcessingVolumeBreakdown
                                    .builder()
                                    .cardPresent(77)
                                    .mailOrTelephone(3)
                                    .ecommerce(20)
                                    .build()
                            )
                            .isSeasonal(true)
                            .monthsOfOperation(
                                Optional.of(
                                    Arrays.asList(ProcessingMonthsOfOperationItem.JAN, ProcessingMonthsOfOperationItem.FEB)
                                )
                            )
                            .ach(
                                ProcessingAch
                                    .builder()
                                    .refunds(
                                        ProcessingAchRefunds
                                            .builder()
                                            .writtenRefundPolicy(true)
                                            .refundPolicyUrl("www.example.com/refund-poilcy-url")
                                            .build()
                                    )
                                    .estimatedMonthlyTransactions(3000)
                                    .limits(
                                        ProcessingAchLimits
                                            .builder()
                                            .singleTransaction(10000)
                                            .dailyDeposit(200000)
                                            .monthlyDeposit(6000000)
                                            .build()
                                    )
                                    .naics("5812")
                                    .previouslyTerminatedForAch(false)
                                    .transactionTypes(
                                        Optional.of(
                                            Arrays.asList(ProcessingAchTransactionTypesItem.PREARRANGED_PAYMENT, ProcessingAchTransactionTypesItem.OTHER)
                                        )
                                    )
                                    .transactionTypesOther("anotherTransactionType")
                                    .build()
                            )
                            .cardAcceptance(
                                ProcessingCardAcceptance
                                    .builder()
                                    .debitOnly(false)
                                    .hsaFsa(false)
                                    .cardsAccepted(
                                        Optional.of(
                                            Arrays.asList(ProcessingCardAcceptanceCardsAcceptedItem.VISA, ProcessingCardAcceptanceCardsAcceptedItem.MASTERCARD)
                                        )
                                    )
                                    .specialityCards(
                                        ProcessingCardAcceptanceSpecialityCards
                                            .builder()
                                            .americanExpressDirect(
                                                ProcessingCardAcceptanceSpecialityCardsAmericanExpressDirect
                                                    .builder()
                                                    .enabled(true)
                                                    .merchantNumber("abc1234567")
                                                    .build()
                                            )
                                            .electronicBenefitsTransfer(
                                                ProcessingCardAcceptanceSpecialityCardsElectronicBenefitsTransfer
                                                    .builder()
                                                    .enabled(true)
                                                    .fnsNumber("6789012")
                                                    .build()
                                            )
                                            .other(
                                                ProcessingCardAcceptanceSpecialityCardsOther
                                                    .builder()
                                                    .wexMerchantNumber("abc1234567")
                                                    .voyagerMerchantId("abc1234567")
                                                    .fleetMerchantId("abc1234567")
                                                    .build()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .funding(
                        CreateFunding
                            .builder()
                            .fundingSchedule(CommonFundingFundingSchedule.NEXTDAY)
                            .acceleratedFundingFee(1999)
                            .dailyDiscount(false)
                            .fundingAccounts(
                                Optional.of(
                                    Arrays.asList(
                                        FundingAccount
                                            .builder()
                                            .type(FundingAccountType.CHECKING)
                                            .use(FundingAccountUse.CREDIT_AND_DEBIT)
                                            .nameOnAccount("Jane Doe")
                                            .paymentMethods(
                                                Arrays.asList(
                                                    PaymentMethodsItem.ach(
                                                        PaymentMethodAch
                                                            .builder()
                                                            .build()
                                                    )
                                                )
                                            )
                                            .metadata(
                                                new HashMap<String, String>() {{
                                                    put("yourCustomField", "abc123");
                                                }}
                                            )
                                            .build()
                                    )
                                )
                            )
                            .build()
                    )
                    .pricing(
                        Pricing.intent(
                            PricingTemplate
                                .builder()
                                .pricingIntentId("6123")
                                .build()
                        )
                    )
                    .signature(
                        Signature.requestedViaDirectLink(
                            SignatureByDirectLink
                                .builder()
                                .build()
                        )
                    )
                    .owners(
                        Arrays.asList(
                            Owner
                                .builder()
                                .firstName("Jane")
                                .lastName("Doe")
                                .dateOfBirth(LocalDate.parse("1964-03-22"))
                                .address(
                                    Address
                                        .builder()
                                        .address1("1 Example Ave.")
                                        .city("Chicago")
                                        .state("Illinois")
                                        .country("US")
                                        .postalCode("60056")
                                        .address2("Example Address Line 2")
                                        .address3("Example Address Line 3")
                                        .build()
                                )
                                .relationship(
                                    OwnerRelationship
                                        .builder()
                                        .isControlProng(true)
                                        .equityPercentage(48.5f)
                                        .title("CFO")
                                        .isAuthorizedSignatory(false)
                                        .build()
                                )
                                .middleName("Helen")
                                .identifiers(
                                    Arrays.asList(
                                        Identifier
                                            .builder()
                                            .type(IdentifierType.NATIONAL_ID)
                                            .value("000-00-4320")
                                            .build()
                                    )
                                )
                                .contactMethods(
                                    Arrays.asList(
                                        ContactMethod.email(
                                            ContactMethodEmail
                                                .builder()
                                                .value("jane.doe@example.com")
                                                .build()
                                        )
                                    )
                                )
                                .build()
                        )
                    )
                    .website("www.example.com")
                    .contactMethods(
                        Arrays.asList(
                            ContactMethod.email(
                                ContactMethodEmail
                                    .builder()
                                    .value("jane.doe@example.com")
                                    .build()
                            )
                        )
                    )
                    .contacts(
                        Optional.of(
                            Arrays.asList(
                                Contact
                                    .builder()
                                    .type(ContactType.MANAGER)
                                    .firstName("Jane")
                                    .lastName("Doe")
                                    .middleName("Helen")
                                    .identifiers(
                                        Arrays.asList(
                                            Identifier
                                                .builder()
                                                .type(IdentifierType.NATIONAL_ID)
                                                .value("000-00-4320")
                                                .build()
                                        )
                                    )
                                    .contactMethods(
                                        Arrays.asList(
                                            ContactMethod.email(
                                                ContactMethodEmail
                                                    .builder()
                                                    .value("jane.doe@example.com")
                                                    .build()
                                            )
                                        )
                                    )
                                    .build()
                            )
                        )
                    )
                    .metadata(
                        new HashMap<String, String>() {{
                            put("customerId", "2345");
                        }}
                    )
                    .build()
            )
        )
        .metadata(
            new HashMap<String, String>() {{
                put("customerId", "2345");
            }}
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**business:** `Business` 
    
</dd>
</dl>

<dl>
<dd>

**processingAccounts:** `List<CreateProcessingAccount>` — Array of processingAccounts objects.
    
</dd>
</dl>

<dl>
<dd>

**metadata:** `Optional<Map<String, String>>` — Object that you can send to include custom data in the request.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.merchantPlatforms.retrieve(merchantPlatformId) -> MerchantPlatform</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a merchant platform.  

To retrieve a merchant platform, you need its merchantPlatformId. Our gateway returned the merchantPlatformId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method.  

**Note:** If you don't have the merchantPlatformId, use our [List Merchant Platforms](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list) method to search for the merchant platform.

Our gateway returns the following information about the merchant platform:
-	Legal information, including its legal name and address.
-	Contact information, including the email address for the business. 
-	Processing account information, including the processingAccountId and status of each processing account that's linked to the merchant platform.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().merchantPlatforms().retrieve(
    "12345",
    RetrieveMerchantPlatformsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**merchantPlatformId:** `String` — Unique identifier of the merchant platform that we sent to you when you created the merchant platform.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.merchantPlatforms.listProcessingAccounts(merchantPlatformId) -> PayrocPager&amp;lt;PaginatedProcessingAccounts&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of processing accounts linked to a merchant platform.  

**Note**: If you want to view the details of a specific processing account and you have its processingAccountId, use our [Retrieve Processing Account](https://docs.payroc.com/api/schema/boarding/processing-accounts/retrieve) method.  

Use the query parameters to filter the list of results that we return, for example, to search for only closed processing accounts.  

To list the processing accounts for a merchant platform, you need its merchantPlatformId. If you don't have the merchantPlatformId, use our [List Merchant Platforms](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list) method to search for the merchant platform.

Our gateway returns the following information about eahc processing account in the list:  
- Business details, including its status, time zone, and address.  
- Owners' details, including their contact details.  
- Funding, pricing, and processing information, including its pricing model and funding accounts.  

For each processing account, we also return its processingAccountId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().merchantPlatforms().listProcessingAccounts(
    "12345",
    ListBoardingMerchantPlatformProcessingAccountsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .includeClosed(true)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**merchantPlatformId:** `String` — Unique identifier of the merchant platform that we sent to you when you created the merchant platform.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**includeClosed:** `Optional<Boolean>` 

Indicates if you want to return closed processing accounts. This includes processing accounts that have a status of `terminated`, `cancelled`, or `rejected`.  
**Note**: By default, we return only open processing accounts.  
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.merchantPlatforms.createProcessingAccount(merchantPlatformId, request) -> ProcessingAccount</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to add an additional processing account to a merchant platform.  

To add a processing account to a merchant platform, you need the merchantPlatformId. Our gateway returned the merchantPlatformId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method.  

**Note**: If you don't have the merchantPlatformId, use our [List Merchant Platforms](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list) method to search for the merchant platform.  

In the request, include the following information:  
- Business details, including its business type, time zone, and address.  
- Owners' details, including their contact details.  
- Funding, pricing, and processing information, including its pricing model and funding accounts.  

When you send a successful request, we review the information about the processing account. After we complete our review and approve the processing account, we assign a processingAccountId, which you need to perform follow-on actions.  

**Note**: You can subscribe to our processingAccount.status.changed event to get notifications when we update the status of a processing account. For more information about how to subscribe to events, go to [Events List](https://docs.payroc.com/knowledge/events/events-list).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().merchantPlatforms().createProcessingAccount(
    "12345",
    CreateProcessingAccountMerchantPlatformsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            CreateProcessingAccount
                .builder()
                .doingBusinessAs("Pizza Doe")
                .businessType(CreateProcessingAccountBusinessType.RESTAURANT)
                .categoryCode(5999)
                .merchandiseOrServiceSold("Pizza")
                .businessStartDate(LocalDate.parse("2020-01-01"))
                .timezone(Timezone.AMERICA_CHICAGO)
                .address(
                    Address
                        .builder()
                        .address1("1 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .address2("Example Address Line 2")
                        .address3("Example Address Line 3")
                        .build()
                )
                .processing(
                    Processing
                        .builder()
                        .transactionAmounts(
                            ProcessingTransactionAmounts
                                .builder()
                                .average(5000)
                                .highest(10000)
                                .build()
                        )
                        .monthlyAmounts(
                            ProcessingMonthlyAmounts
                                .builder()
                                .average(50000)
                                .highest(100000)
                                .build()
                        )
                        .volumeBreakdown(
                            ProcessingVolumeBreakdown
                                .builder()
                                .cardPresent(77)
                                .mailOrTelephone(3)
                                .ecommerce(20)
                                .build()
                        )
                        .isSeasonal(true)
                        .monthsOfOperation(
                            Optional.of(
                                Arrays.asList(ProcessingMonthsOfOperationItem.JAN, ProcessingMonthsOfOperationItem.FEB)
                            )
                        )
                        .ach(
                            ProcessingAch
                                .builder()
                                .refunds(
                                    ProcessingAchRefunds
                                        .builder()
                                        .writtenRefundPolicy(true)
                                        .refundPolicyUrl("www.example.com/refund-poilcy-url")
                                        .build()
                                )
                                .estimatedMonthlyTransactions(3000)
                                .limits(
                                    ProcessingAchLimits
                                        .builder()
                                        .singleTransaction(10000)
                                        .dailyDeposit(200000)
                                        .monthlyDeposit(6000000)
                                        .build()
                                )
                                .naics("5812")
                                .previouslyTerminatedForAch(false)
                                .transactionTypes(
                                    Optional.of(
                                        Arrays.asList(ProcessingAchTransactionTypesItem.PREARRANGED_PAYMENT, ProcessingAchTransactionTypesItem.OTHER)
                                    )
                                )
                                .transactionTypesOther("anotherTransactionType")
                                .build()
                        )
                        .cardAcceptance(
                            ProcessingCardAcceptance
                                .builder()
                                .debitOnly(false)
                                .hsaFsa(false)
                                .cardsAccepted(
                                    Optional.of(
                                        Arrays.asList(ProcessingCardAcceptanceCardsAcceptedItem.VISA, ProcessingCardAcceptanceCardsAcceptedItem.MASTERCARD)
                                    )
                                )
                                .specialityCards(
                                    ProcessingCardAcceptanceSpecialityCards
                                        .builder()
                                        .americanExpressDirect(
                                            ProcessingCardAcceptanceSpecialityCardsAmericanExpressDirect
                                                .builder()
                                                .enabled(true)
                                                .merchantNumber("abc1234567")
                                                .build()
                                        )
                                        .electronicBenefitsTransfer(
                                            ProcessingCardAcceptanceSpecialityCardsElectronicBenefitsTransfer
                                                .builder()
                                                .enabled(true)
                                                .fnsNumber("6789012")
                                                .build()
                                        )
                                        .other(
                                            ProcessingCardAcceptanceSpecialityCardsOther
                                                .builder()
                                                .wexMerchantNumber("abc1234567")
                                                .voyagerMerchantId("abc1234567")
                                                .fleetMerchantId("abc1234567")
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .funding(
                    CreateFunding
                        .builder()
                        .fundingSchedule(CommonFundingFundingSchedule.NEXTDAY)
                        .acceleratedFundingFee(1999)
                        .dailyDiscount(false)
                        .fundingAccounts(
                            Optional.of(
                                Arrays.asList(
                                    FundingAccount
                                        .builder()
                                        .type(FundingAccountType.CHECKING)
                                        .use(FundingAccountUse.CREDIT_AND_DEBIT)
                                        .nameOnAccount("Jane Doe")
                                        .paymentMethods(
                                            Arrays.asList(
                                                PaymentMethodsItem.ach(
                                                    PaymentMethodAch
                                                        .builder()
                                                        .build()
                                                )
                                            )
                                        )
                                        .metadata(
                                            new HashMap<String, String>() {{
                                                put("yourCustomField", "abc123");
                                            }}
                                        )
                                        .build()
                                )
                            )
                        )
                        .build()
                )
                .pricing(
                    Pricing.intent(
                        PricingTemplate
                            .builder()
                            .pricingIntentId("6123")
                            .build()
                    )
                )
                .signature(
                    Signature.requestedViaDirectLink(
                        SignatureByDirectLink
                            .builder()
                            .build()
                    )
                )
                .owners(
                    Arrays.asList(
                        Owner
                            .builder()
                            .firstName("Jane")
                            .lastName("Doe")
                            .dateOfBirth(LocalDate.parse("1964-03-22"))
                            .address(
                                Address
                                    .builder()
                                    .address1("1 Example Ave.")
                                    .city("Chicago")
                                    .state("Illinois")
                                    .country("US")
                                    .postalCode("60056")
                                    .address2("Example Address Line 2")
                                    .address3("Example Address Line 3")
                                    .build()
                            )
                            .relationship(
                                OwnerRelationship
                                    .builder()
                                    .isControlProng(true)
                                    .equityPercentage(51.5f)
                                    .title("CFO")
                                    .isAuthorizedSignatory(false)
                                    .build()
                            )
                            .middleName("Helen")
                            .identifiers(
                                Arrays.asList(
                                    Identifier
                                        .builder()
                                        .type(IdentifierType.NATIONAL_ID)
                                        .value("000-00-4320")
                                        .build()
                                )
                            )
                            .contactMethods(
                                Arrays.asList(
                                    ContactMethod.email(
                                        ContactMethodEmail
                                            .builder()
                                            .value("jane.doe@example.com")
                                            .build()
                                    )
                                )
                            )
                            .build()
                    )
                )
                .website("www.example.com")
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        )
                    )
                )
                .contacts(
                    Optional.of(
                        Arrays.asList(
                            Contact
                                .builder()
                                .type(ContactType.MANAGER)
                                .firstName("Jane")
                                .lastName("Doe")
                                .middleName("Helen")
                                .identifiers(
                                    Arrays.asList(
                                        Identifier
                                            .builder()
                                            .type(IdentifierType.NATIONAL_ID)
                                            .value("000-00-4320")
                                            .build()
                                    )
                                )
                                .contactMethods(
                                    Arrays.asList(
                                        ContactMethod.email(
                                            ContactMethodEmail
                                                .builder()
                                                .value("jane.doe@example.com")
                                                .build()
                                        )
                                    )
                                )
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("customerId", "2345");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**merchantPlatformId:** `String` — Unique identifier of the merchant platform that we sent to you when you created the merchant platform.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `CreateProcessingAccount` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding ProcessingAccounts
<details><summary><code>client.boarding.processingAccounts.retrieve(processingAccountId) -> ProcessingAccount</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a specific processing account.  

To retrieve a processing account, you need its processingAccountId. Our gateway returned the processingAccountId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method or the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

**Note:** If you don't have the processingAccountId, use our [List Merchant Platform's Processing Accounts](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list-processing-accounts) method to search for the processing account.  

Our gateway returns the following information about the processing account:  

-	Business information, including the Merchant Category Code (MCC), status of the processing account, and address of the business.  
-	Processing information, including the merchant’s refund policies and card types that the merchant accepts.  
-	Funding information, including funding schedules, funding fees, and details for the merchant’s funding accounts.  
-	Pricing information, including [HATEOAS](https://docs.payroc.com/knowledge/basic-concepts/hypermedia-as-the-engine-of-application-state-hateoas) links to retrieve the pricing program for the processing account.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().retrieve(
    "38765",
    RetrieveProcessingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.listProcessingAccountFundingAccounts(processingAccountId) -> List&amp;lt;FundingAccount&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a list of funding accounts linked to a processing acccount.  

To retrieve a list of funding accounts for a processing account, you need the processingAccountId. Our gateway returned the processingAccountId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method or the [Create Proccessing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

Our gateway returns information about the following for each funding account in the list:  
- Account information, including the name on the account and payment methods.  
- Status, including whether we have approved or rejected the account.  

For each funding account, we also return its fundingAccountId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().listProcessingAccountFundingAccounts(
    "38765",
    ListProcessingAccountFundingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.listContacts(processingAccountId) -> PaginatedContacts</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a list of contacts for a processing account.    

**Note:** If you want to view the details of a specific contact and you have their contactId, use our [Retrieve Contact](https://docs.payroc.com/api/schema/boarding/contacts/retrieve) method.  

To list contacts for a processing account, you need the processingAccountId. Our gateway returned the processingAccountId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method or the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

Our gateway returns the following information about each contact:  

- Name and contact method, including their phone number or mobile number.  
- Role within the business, for example, if they are a manager.  

For each contact, we also return a contactId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().listContacts(
    "38765",
    ListContactsProcessingAccountsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.getProcessingAccountPricingAgreement(processingAccountId) -> GetProcessingAccountPricingAgreementProcessingAccountsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve the pricing agreement that we apply to a processing account.  

To retrieve the pricing agreement of a processing account, you need the processingAccountId. Our gateway returned the processingAccountId in the response to the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method and [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

**Note:** If you don't have the processingAccountId, use our [List Merchant Platform’s Processing Accounts](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list-processing-accounts) method to search for the processing account.  

Our gateway returns the following information about the pricing agreement that we apply to the processing account:  

- Base fees, including the annual fee and the fees for each chargeback and retrieval.  
- Processor fees, including the fees that we apply for processing card and ACH payments.  
- Gateway fees, including the setup fee and the fees for each transaction.  
- Service fees, including the fee that we apply if the merchant has signed up to a Hardware Advantage Plan.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().getProcessingAccountPricingAgreement(
    "38765",
    GetProcessingAccountPricingAgreementProcessingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.listOwners(processingAccountId) -> PayrocPager&amp;lt;PaginatedOwners&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a list of owners of a processing account.  

**Note:** If you want to view the details of a specific owner and you have the ownerId, go to our [Retrieve Owner](https://docs.payroc.com/api/schema/boarding/owners/retrieve) method.  

To list the owners of a processing account, you need its processingAccountId. If you don't have the processingAccountId, use our [List Merchant Platform's Processing Accounts](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list-processing-accounts) method to search for the processing account.  

Our gateway returns the following information about each owner in the list: 

- Name, date of birth, and address.  
- Contact details, including their email address.  
- Relationship to the business, including whether they are a control prong or authorized signatory, and their equity stake in the business.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().listOwners(
    "38765",
    ListProcessingAccountOwnersRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.createReminder(processingAccountId, request) -> CreateReminderProcessingAccountsResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to prompt a merchant to sign their pricing agreement.  

You can create a reminder only if you requested the merchant’s signature by email when you created the processing account for the merchant.  

To create a reminder, you need the processingAccountId. Our gateway returned the processingAccountId in the response of the [Create Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create) method or [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method. 

**Note:** If you don’t know the processingAccountId, use our [List Merchant Platform’s Processing Accounts](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list-processing-accounts) method to search for the processing account.  

When you send a successful request, we send an email to the merchant that prompts them to sign their pricing agreement.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().createReminder(
    "38765",
    CreateReminderProcessingAccountsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            CreateReminderProcessingAccountsRequestBody.pricingAgreement(
                PricingAgreementReminder
                    .builder()
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `CreateReminderProcessingAccountsRequestBody` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.listTerminalOrders(processingAccountId) -> List&amp;lt;TerminalOrder&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of terminal orders associated with a processing account.  

**Note:** If you want to view the details of a specific terminal order and you have its terminalOrderId, use our [Retrieve Terminal Order](https://docs.payroc.com/api/schema/boarding/terminal-orders/retrieve) method.  

Use the query parameters to filter the list of results that we return, for example, to search for terminal orders by their status.  

To list the terminal orders for a processing account, you need its processingAccountId. If you don't have the processingAccountId, use our [List Merchant Platforms](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list) method to search for a merchant platform and its processing accounts.

Our gateway returns the following information for each terminal order in the list:  

- Status of the order  
- Items in the order  
- Training provider  
- Shipping information  

For each terminal order, we also return its terminalOrderId, which you can use to perform follow-on actions.   
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().listTerminalOrders(
    "38765",
    ListTerminalOrdersProcessingAccountsRequest
        .builder()
        .status(ListTerminalOrdersProcessingAccountsRequestStatus.OPEN)
        .fromDateTime(OffsetDateTime.parse("2024-09-08T12:00:00Z"))
        .toDateTime(OffsetDateTime.parse("2024-12-08T11:00:00Z"))
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListTerminalOrdersProcessingAccountsRequestStatus>` 
    
</dd>
</dl>

<dl>
<dd>

**fromDateTime:** `Optional<OffsetDateTime>` 
    
</dd>
</dl>

<dl>
<dd>

**toDateTime:** `Optional<OffsetDateTime>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.createTerminalOrder(processingAccountId, request) -> TerminalOrder</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to order and configure terminals for a processing account.  

**Note**: You need the ID of the processing account before you can create an order. If you don't know the processingAccountId, go to the [Retrieve a Merchant Platform](https://docs.payroc.com/api/schema/boarding/merchant-platforms/retrieve) method.  

In the request, specify the gateway settings, device settings, and application settings for the terminal.  

In the response, our gateway returns information about the terminal order including its status and terminalOrderId that you can use to [retrieve the terminal order](https://docs.payroc.com/api/schema/boarding/terminal-orders/retrieve).  

**Note**: You can subscribe to the terminalOrder.status.changed event to get notifications when we update the status of a terminal order. For more information about how to subscribe to events, go to [Events Subscriptions](https://docs.payroc.com/guides/board-merchants/event-subscriptions).  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().createTerminalOrder(
    "38765",
    CreateTerminalOrder
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .orderItems(
            Arrays.asList(
                OrderItem
                    .builder()
                    .type(OrderItemType.SOLUTION)
                    .solutionTemplateId("Roc Services_DX8000")
                    .solutionQuantity(1)
                    .deviceCondition(OrderItemDeviceCondition.NEW)
                    .solutionSetup(
                        OrderItemSolutionSetup
                            .builder()
                            .timezone(SchemasTimezone.AMERICA_CHICAGO)
                            .industryTemplateId("Retail")
                            .gatewaySettings(
                                OrderItemSolutionSetupGatewaySettings
                                    .builder()
                                    .merchantPortfolioId("Company Ltd")
                                    .merchantTemplateId("Company Ltd Merchant Template")
                                    .userTemplateId("Company Ltd User Template")
                                    .terminalTemplateId("Company Ltd Terminal Template")
                                    .build()
                            )
                            .applicationSettings(
                                OrderItemSolutionSetupApplicationSettings
                                    .builder()
                                    .clerkPrompt(false)
                                    .security(
                                        OrderItemSolutionSetupApplicationSettingsSecurity
                                            .builder()
                                            .refundPassword(true)
                                            .keyedSalePassword(false)
                                            .reversalPassword(true)
                                            .build()
                                    )
                                    .build()
                            )
                            .deviceSettings(
                                OrderItemSolutionSetupDeviceSettings
                                    .builder()
                                    .numberOfMobileUsers(2)
                                    .communicationType(OrderItemSolutionSetupDeviceSettingsCommunicationType.WIFI)
                                    .build()
                            )
                            .batchClosure(
                                OrderItemSolutionSetupBatchClosure.automatic(
                                    AutomaticBatchClose
                                        .builder()
                                        .build()
                                )
                            )
                            .receiptNotifications(
                                OrderItemSolutionSetupReceiptNotifications
                                    .builder()
                                    .emailReceipt(true)
                                    .smsReceipt(false)
                                    .build()
                            )
                            .taxes(
                                Optional.of(
                                    Arrays.asList(
                                        OrderItemSolutionSetupTaxesItem
                                            .builder()
                                            .taxRate(6f)
                                            .taxLabel("Sales Tax")
                                            .build()
                                    )
                                )
                            )
                            .tips(
                                OrderItemSolutionSetupTips
                                    .builder()
                                    .enabled(false)
                                    .build()
                            )
                            .tokenization(true)
                            .build()
                    )
                    .build()
            )
        )
        .trainingProvider(TrainingProvider.PAYROC)
        .shipping(
            CreateTerminalOrderShipping
                .builder()
                .preferences(
                    CreateTerminalOrderShippingPreferences
                        .builder()
                        .method(CreateTerminalOrderShippingPreferencesMethod.NEXT_DAY)
                        .saturdayDelivery(true)
                        .build()
                )
                .address(
                    CreateTerminalOrderShippingAddress
                        .builder()
                        .recipientName("Recipient Name")
                        .addressLine1("1 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .postalCode("60056")
                        .email("example@mail.com")
                        .businessName("Company Ltd")
                        .addressLine2("Example Address Line 2")
                        .phone("2025550164")
                        .build()
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**trainingProvider:** `Optional<TrainingProvider>` 
    
</dd>
</dl>

<dl>
<dd>

**shipping:** `Optional<CreateTerminalOrderShipping>` — Object that contains the shipping details for the terminal order. If you don't provide a shipping address, we use the Doing Business As (DBA) address of the processing account.
    
</dd>
</dl>

<dl>
<dd>

**orderItems:** `List<OrderItem>` — Array of order items. Provide a minimum of 1 order item and a maximum of 10 order items.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingAccounts.listProcessingTerminals(processingAccountId) -> PayrocPager&amp;lt;PaginatedProcessingTerminals&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of processing terminals associated with a processing account.  

**Note:** If you want to view the details of a specific processing terminal and you have its processingTerminalId, use our [Retrieve Processing Terminal](https://docs.payroc.com/api/schema/boarding/processing-terminals/retrieve) method.  

To list the terminals for a processing account, you need its processingAccountId. If you don't have the processingAccountId, use our [List Merchant Platforms](https://docs.payroc.com/api/schema/boarding/merchant-platforms/list) method to search for a merchant platform and its processing accounts.   

Our gateway returns the following information for each processing terminal in the list:  

- Status indicating whether the terminal is active or inactive.  
- Configuration settings, including gateway settings and application settings.  
- Features, receipt settings, and security settings.  
- Devices that use the processing terminal's configuration.  

For each processing terminal, we also return its processingTerminalId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingAccounts().listProcessingTerminals(
    "38765",
    ListProcessingTerminalsProcessingAccountsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingAccountId:** `String` — Unique identifier that we assigned to the processing account.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding ProcessingTerminals
<details><summary><code>client.boarding.processingTerminals.retrieve(processingTerminalId) -> ProcessingTerminal</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

**Important:** You can retrieve a processing terminal only if the terminal order was created using the Payroc API.

Use this method to retrieve information about a processing terminal.  

To retrieve a processing terminal, you need its processingTerminalId. Our gateway returned the processingTerminalId in the response of the [Create Terminal Order](https://docs.payroc.com/api/schema/boarding/processing-accounts/create-terminal-order) method.  

**Note:** If you don't have the processingTerminalId, use our [Retrieve Terminal Order](https://docs.payroc.com/api/schema/boarding/terminal-orders/retrieve) method or our [List Processing Terminals](https://docs.payroc.com/api/schema/boarding/processing-accounts/list-processing-terminals) method to search for the processing terminal.  

Our gateway returns the following information about the processing terminal:  

- Status indicating whether the terminal is active or inactive.  
- Configuration settings, including gateway settings and application settings.  
- Features, receipt settings, and security settings.  
- Devices that use the processing terminal's configuration.  
  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingTerminals().retrieve(
    "1234001",
    RetrieveProcessingTerminalsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.processingTerminals.retrieveHostConfiguration(processingTerminalId) -> HostConfiguration</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve the host processor configuration of a processing terminal. Integrate with this method only if you use your own gateway and want to validate the processor configuration.  

Our gateway returns the configuration settings for the merchant and the payment terminal.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().processingTerminals().retrieveHostConfiguration(
    "1234001",
    RetrieveHostConfigurationProcessingTerminalsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding Contacts
<details><summary><code>client.boarding.contacts.retrieve(contactId) -> Contact</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve details about a contact.  

To retrieve a contact, you need its contactId. Our gateway returned the contactId in the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

**Note:** If you don't have the contactId, use the [List Contacts](https://docs.payroc.com/api/schema/boarding/processing-accounts/list-contacts) method to search for the contact.  

Our gateway returns the following information about a contact:  

-	Name and contact method, including their phone number or mobile number.  
-	Role within the business, for example, if they are a manager. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().contacts().retrieve(
    1,
    RetrieveContactsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**contactId:** `Integer` — Unique identifier for the contact.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.contacts.update(contactId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to update a contact of a processing account.  

To update a contact, you need its contactId. Our gateway returned the contactId in the response of the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

**Note:** If you don't have the contactId, use our [List Contacts](https://docs.payroc.com/api/schema/boarding/processing-accounts/list-contacts) method to search for the contact.  

You can update the following details about a contact:  

-	First name and last name.  
-	Contact details, including their phone number or mobile number.  
-	Identification details, including their identification type and number.  
-	Role within the business, for example, if they are a manager.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().contacts().update(
    1,
    UpdateContactsRequest
        .builder()
        .body(
            Contact
                .builder()
                .type(ContactType.MANAGER)
                .firstName("Jane")
                .lastName("Doe")
                .middleName("Helen")
                .identifiers(
                    Arrays.asList(
                        Identifier
                            .builder()
                            .type(IdentifierType.NATIONAL_ID)
                            .value("000-00-4320")
                            .build()
                    )
                )
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**contactId:** `Integer` — Unique identifier for the contact.
    
</dd>
</dl>

<dl>
<dd>

**request:** `Contact` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.boarding.contacts.delete(contactId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete a contact associated with a processing account.  

To delete a contact, you need their contactId. Our gateway returned the contactId in the response of the [Create Processing Account](https://docs.payroc.com/api/schema/boarding/merchant-platforms/create-processing-account) method.  

**Note:** If you don’t have the contactId, use our [Retrieve Processing Account](https://docs.payroc.com/api/schema/boarding/processing-accounts/retrieve) method or our [List Contacts](https://docs.payroc.com/api/schema/boarding/processing-accounts/list-contacts) method to search for the contact.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().contacts().delete(
    1,
    DeleteContactsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**contactId:** `Integer` — Unique identifier for the contact.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Boarding TerminalOrders
<details><summary><code>client.boarding.terminalOrders.retrieve(terminalOrderId) -> TerminalOrder</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a terminal order.  

To retrieve a terminal order, you need it's terminalOrderId. Our gateway returned the terminalOrderId in the response of the [Create Terminal Order](https://docs.payroc.com/api/schema/boarding/processing-accounts/create-terminal-order) method.  

**Note**: If you don't have the terminalOrderId, use our [List Terminal Orders](https://docs.payroc.com/api/schema/boarding/processing-accounts/list-terminal-orders) method to search for the terminal order.  

Our gateway returns the following information about the terminal order:  
- Status of the order  
- Items in the order  
- Training provider  
- Shipping information  

**Note**: You can subscribe to our terminalOrder.status.changed event to get notifications when we update the status of a terminal order. For more information about how to subscribe to events, go to [Events Subscriptions](https://docs.payroc.com/guides/board-merchants/event-subscriptions).  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.boarding().terminalOrders().retrieve(
    "12345",
    RetrieveTerminalOrdersRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**terminalOrderId:** `String` — Unique identifier of the terminal order.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## CardPayments Payments
<details><summary><code>client.cardPayments.payments.list() -> PayrocPager&amp;lt;PaymentPaginatedListForRead&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of payments. 

**Note:** If you want to view the details of a specific payment and you have its paymentId, use our [Retrieve Payment](https://docs.payroc.com/api/schema/card-payments/payments/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for payments for a customer, a tip mode, or a date range.  

Our gateway returns the following information about each payment in the list:  

- Order details, including the transaction amount and when it was processed.  
- Payment card details, including the masked card number, expiry date, and payment method. 
- Cardholder details, including their contact information and shipping address. 
- Payment details, including the payment type, status, and response. 
 
For each transaction, we also return the paymentId and an optional secureTokenId, which you can use to perform follow-on actions. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().payments().list(
    ListPaymentsRequest
        .builder()
        .processingTerminalId("1234001")
        .orderId("OrderRef6543")
        .operator("Jane")
        .cardholderName("Sarah%20Hazel%20Hopper")
        .first6("453985")
        .last4("7062")
        .tender(ListPaymentsRequestTender.EBT)
        .dateFrom(OffsetDateTime.parse("2024-07-01T15:30:00Z"))
        .dateTo(OffsetDateTime.parse("2024-07-03T15:30:00Z"))
        .settlementState(ListPaymentsRequestSettlementState.SETTLED)
        .settlementDate(LocalDate.parse("2024-07-02"))
        .paymentLinkId("JZURRJBUPS")
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `Optional<String>` — Filter by terminal ID.
    
</dd>
</dl>

<dl>
<dd>

**orderId:** `Optional<String>` — Filter payments by order ID.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Filter payments by operator.
    
</dd>
</dl>

<dl>
<dd>

**cardholderName:** `Optional<String>` — Filter payments by the cardholder’s name.
    
</dd>
</dl>

<dl>
<dd>

**first6:** `Optional<String>` — Filter payments by the first six digits of the card number that the customer used in the transaction.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter payments by the last four digits of the card number that the customer used in the transaction.
    
</dd>
</dl>

<dl>
<dd>

**tender:** `Optional<ListPaymentsRequestTender>` — Filter by tender type.
    
</dd>
</dl>

<dl>
<dd>

**tipMode:** `Optional<ListPaymentsRequestTipModeItem>` — Filter payments by tip.
    
</dd>
</dl>

<dl>
<dd>

**type:** `Optional<ListPaymentsRequestTypeItem>` — Filter payments by transaction type.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListPaymentsRequestStatusItem>` — Filter payments by the status of the transaction.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `Optional<OffsetDateTime>` — Filter by payments that the processor processed after a specific date. The date format follows the ISO 8601 standard.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `Optional<OffsetDateTime>` — Filter by payments that the processer processed before a specific date. The date format follows the ISO 8601 standard.
    
</dd>
</dl>

<dl>
<dd>

**settlementState:** `Optional<ListPaymentsRequestSettlementState>` — Filter payments by the settlement status of the transaction.
    
</dd>
</dl>

<dl>
<dd>

**settlementDate:** `Optional<String>` — Filter by payments that the processor settled on a specific date in the format **YYYY-MM-DD**.
    
</dd>
</dl>

<dl>
<dd>

**paymentLinkId:** `Optional<String>` — Unique identifier that our gateway assigned to the payment link.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.payments.create(request) -> Payment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to run a sale or a pre-authorization with a customer's payment card. 

In the response, our gateway returns information about the card payment and a paymentId, which you need for the following methods:

-	[Retrieve payment](https://docs.payroc.com/api/schema/card-payments/payments/retrieve) - View the details of the card payment.
-	[Adjust payment](https://docs.payroc.com/api/schema/card-payments/payments/adjust) - Update the details of the card payment.
-	[Capture payment](https://docs.payroc.com/api/schema/card-payments/payments/capture)  - Capture the pre-authorization.
-	[Reverse payment](https://docs.payroc.com/api/schema/card-payments/refunds/reverse)  - Cancel the card payment if it's in an open batch.
-	[Refund payment](https://docs.payroc.com/api/schema/card-payments/refunds/create-referenced-refund)  - Run a referenced refund to return funds to the payment card.

**Payment methods** 

- **Cards** - Credit, debit, and EBT
- **Digital wallets** - [Apple Pay®](https://docs.payroc.com/guides/take-payments/apple-pay) and [Google Pay®](https://docs.payroc.com/guides/take-payments/google-pay) 
- **Tokens** - Secure tokens and single-use tokens

**Features** 

Our Create Payment method also supports the following features: 

- [Repeat payments](https://docs.payroc.com/guides/take-payments/repeat-payments/use-your-own-software) - Run multiple payments as part of a payment schedule that you manage with your own software. 
- **Offline sales** - Run a sale or a pre-authorization if the terminal loses its connection to our gateway. 
- [Tokenization](https://docs.payroc.com/guides/take-payments/save-payment-details) - Save card details to use in future transactions. 
- [3-D Secure](https://docs.payroc.com/guides/take-payments/3-d-secure) - Verify the identity of the cardholder. 
- [Custom fields](https://docs.payroc.com/guides/take-payments/add-custom-fields) - Add your own data to a payment. 
- **Tips** - Add tips to the card payment.  
- **Taxes** - Add local taxes to the card payment. 
- **Surcharging** - Add a surcharge to the card payment. 
- **Dual pricing** - Offer different prices based on payment method, for example, if you use our RewardPay Choice pricing program. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().payments().create(
    PaymentRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .channel(PaymentRequestChannel.WEB)
        .processingTerminalId("1234001")
        .order(
            PaymentOrderRequest
                .builder()
                .orderId("OrderRef6543")
                .description("Large Pepperoni Pizza")
                .amount(4999L)
                .currency(Currency.USD)
                .build()
        )
        .paymentMethod(
            PaymentRequestPaymentMethod.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .customer(
            Customer
                .builder()
                .firstName("Sarah")
                .lastName("Hopper")
                .billingAddress(
                    Address
                        .builder()
                        .address1("1 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .address2("Example Address Line 2")
                        .address3("Example Address Line 3")
                        .build()
                )
                .shippingAddress(
                    Shipping
                        .builder()
                        .recipientName("Sarah Hopper")
                        .address(
                            Address
                                .builder()
                                .address1("1 Example Ave.")
                                .city("Chicago")
                                .state("Illinois")
                                .country("US")
                                .postalCode("60056")
                                .address2("Example Address Line 2")
                                .address3("Example Address Line 3")
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .customFields(
            Optional.of(
                Arrays.asList(
                    CustomField
                        .builder()
                        .name("yourCustomField")
                        .value("abc123")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**channel:** `PaymentRequestChannel` — Channel that the merchant used to receive the payment details.
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who ran the transaction.
    
</dd>
</dl>

<dl>
<dd>

**order:** `PaymentOrderRequest` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**ipAddress:** `Optional<IpAddress>` 
    
</dd>
</dl>

<dl>
<dd>

**paymentMethod:** `PaymentRequestPaymentMethod` 

Polymorphic object that contains payment details.  

The value of the type parameter determines which variant you should use:  
-	`card` - Payment card details
-	`secureToken` - Secure token details
-	`digitalWallet` - Digital wallet details
-	`singleUseToken` - Single-use token details
    
</dd>
</dl>

<dl>
<dd>

**threeDSecure:** `Optional<PaymentRequestThreeDSecure>` 

Polymorphic object that contains authentication information from 3-D Secure.  

The value of the serviceProvider parameter determines which variant you should use:  
-	`gateway` - Use our gateway to run a 3-D Secure check.
-	`thirdParty` - Use a third party to run a 3-D Secure check.
    
</dd>
</dl>

<dl>
<dd>

**credentialOnFile:** `Optional<SchemasCredentialOnFile>` 
    
</dd>
</dl>

<dl>
<dd>

**offlineProcessing:** `Optional<OfflineProcessing>` 
    
</dd>
</dl>

<dl>
<dd>

**autoCapture:** `Optional<Boolean>` 

Indicates if we should automatically capture the payment amount.  

- `true` - Run a sale and automatically capture the transaction.
- `false`- Run a pre-authorization and capture the transaction later.  
  
**Note:** If you send `false` and the terminal doesn't support pre-authorization, we set the transaction's status to pending. The merchant must capture the transaction to take payment from the customer.
    
</dd>
</dl>

<dl>
<dd>

**processAsSale:** `Optional<Boolean>` 

Indicates if we should immediately settle the sale transaction. The merchant cannot adjust the transaction if we immediately settle it.  
**Note:** If the value for **processAsSale** is `true`, the gateway ignores the value in **autoCapture**.
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.payments.retrieve(paymentId) -> RetrievedPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a card payment.  

To retrieve a payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/card-payments/payments/create) method.  

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/card-payments/payments/list) method to search for the payment.  

Our gateway returns the following information about the payment:  

- Order details, including the transaction amount and when it was processed.  
- Payment card details, including the masked card number, expiry date, and payment method.  
- Cardholder details, including their contact information and shipping address.  
- Payment details, including the payment type, status, and response.  

If the merchant saved the customer's card details, our gateway returns a secureTokenID, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().payments().retrieve(
    "M2MJOG6O2Y",
    RetrievePaymentsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier of the payment that the merchant wants to retrieve.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.payments.adjust(paymentId, request) -> Payment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to adjust a payment in an open batch. 

To adjust a payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/card-payments/payments/create) method.

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/card-payments/payments/list) method to search for the payment. 

You can adjust the following details of the payment:
- Sale amount and tip amount
- Payment status
- Cardholder shipping address and contact information
- Cardholder signature data

Our gateway returns information about the adjusted payment, including information about the payment card and the cardholder.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().payments().adjust(
    "M2MJOG6O2Y",
    PaymentAdjustment
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .adjustments(
            Arrays.asList(
                PaymentAdjustmentAdjustmentsItem.customer(
                    CustomerAdjustment
                        .builder()
                        .build()
                ),
                PaymentAdjustmentAdjustmentsItem.order(
                    OrderAdjustment
                        .builder()
                        .amount(4999L)
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier of the payment that the merchant wants to retrieve.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who adjusted the payment.
    
</dd>
</dl>

<dl>
<dd>

**adjustments:** `List<PaymentAdjustmentAdjustmentsItem>` 

Array of polymorphic objects which contain information about adjustments to a payment.  

The value of the type parameter determines which variant you should use:
-	`order` - Tip information.
-	`status` - Status of the transaction.
-	`customer` - Customer's contact information and shipping address.
-	`signature` - Customer's signature.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.payments.capture(paymentId, request) -> Payment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to capture a pre-authorization. 

To capture a pre-authorization, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/card-payments/payments/create) method.

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/card-payments/payments/list) method to search for the payment.

Depending on the amount you want to capture, complete the following:
-	**Capture the full amount of the pre-authorization** - Don't send a value for the amount parameter in your request.
-	**Capture less than the amount of the pre-authorization** - Send a value for the amount parameter in your request. 
-	**Capture more than the amount of the pre-authorization** - Adjust the pre-authorization before you capture it. For more information about adjusting a pre-authorization, go to [Adjust Payment](https://docs.payroc.com/api/schema/card-payments/payments/adjust).

If your request is successful, our gateway takes the amount from the payment card. 

**Note:** For more information about pre-authorizations and captures, go to [Run a pre-authorization](https://docs.payroc.com/guides/integrate/run-a-pre-authorization).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().payments().capture(
    "M2MJOG6O2Y",
    PaymentCapture
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .operator("Jane")
        .amount(4999L)
        .breakdown(
            ItemizedBreakdownRequest
                .builder()
                .subtotal(4999L)
                .dutyAmount(499L)
                .freightAmount(500L)
                .items(
                    Optional.of(
                        Arrays.asList(
                            LineItemRequest
                                .builder()
                                .unitPrice(4000L)
                                .quantity(1.0)
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier of the payment that the merchant wants to retrieve.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `Optional<String>` — Unique identifier that our gateway assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who captured the payment.
    
</dd>
</dl>

<dl>
<dd>

**amount:** `Optional<Long>` 

Amount that the merchant wants to capture. The value is in the currency's lowest denomination, for example, cents.  
**Note:** If the merchant does not send an amount, we capture the total amount of the transaction.
    
</dd>
</dl>

<dl>
<dd>

**breakdown:** `Optional<ItemizedBreakdownRequest>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## CardPayments Refunds
<details><summary><code>client.cardPayments.refunds.reverse(paymentId, request) -> Payment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel or to partially cancel a payment in an open batch. This is also known as voiding a payment.  

To cancel a payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/card-payments/payments/create) method.  

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/card-payments/payments/list) method to search for the payment.  

If your request is successful, our gateway removes the payment from the merchant's open batch and no funds are taken from the cardholder's account. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().reverse(
    "M2MJOG6O2Y",
    PaymentReversal
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .amount(4999L)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier of the payment that the merchant wants to retrieve.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who reversed the payment.
    
</dd>
</dl>

<dl>
<dd>

**amount:** `Optional<Long>` 

Amount of the payment that the merchant wants to reverse. The value is in the currency’s lowest denomination, for example, cents.  
**Note:** If the merchant doesn’t send an amount, we reverse the total amount of the transaction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.createReferencedRefund(paymentId, request) -> Payment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to refund a payment that is in a closed batch.  

To refund a payment, you need its paymentId. Our gateway returned the paymentId in the response of the [Create Payment](https://docs.payroc.com/api/schema/card-payments/payments/create) method.  

**Note:** If you don't have the paymentId, use our [List Payments](https://docs.payroc.com/api/schema/card-payments/payments/list) method to search for the payment.  

If your refund is successful, our gateway returns the payment amount to the cardholder's account.  

**Things to consider**  

- If the merchant refunds a payment that is in an open batch, our gateway reverses the payment.
- Some merchants can run unreferenced refunds, which means that they don't need a paymentId to return an amount to a customer. For more information about how to run an unreferenced refund, go to [Create Refund](https://docs.payroc.com/api/schema/card-payments/refunds/create-unreferenced-refund).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().createReferencedRefund(
    "M2MJOG6O2Y",
    ReferencedRefund
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .amount(4999L)
        .description("Refund for order OrderRef6543")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentId:** `String` — Unique identifier of the payment that the merchant wants to retrieve.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who refunded the payment.
    
</dd>
</dl>

<dl>
<dd>

**amount:** `Long` — Amount of the payment that the merchant wants to refund. The value is in the currency’s lowest denomination, for example, cents.
    
</dd>
</dl>

<dl>
<dd>

**description:** `String` — Reason for the refund.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.list() -> PayrocPager&amp;lt;RefundPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of refunds.  

**Note:** If you want to view the details of a specific refund and you have its refundId, use our [Retrieve Refund](https://docs.payroc.com/api/schema/card-payments/refunds/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for refunds for a customer, a tender type, or a date range.
Our gateway returns the following information about each refund in the list:  
- Order details, including the refund amount and when we processed the refund.
- Payment card details, including the masked card number, expiry date, and payment method.
- Cardholder details, including their contact information and shipping address.  

For referenced refunds, our gateway also returns details about the payment that the refund is linked to.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().list(
    ListRefundsRequest
        .builder()
        .processingTerminalId("1234001")
        .orderId("OrderRef6543")
        .operator("Jane")
        .cardholderName("Sarah%20Hazel%20Hopper")
        .first6("453985")
        .last4("7062")
        .tender(ListRefundsRequestTender.EBT)
        .dateFrom(OffsetDateTime.parse("2024-07-01T15:30:00Z"))
        .dateTo(OffsetDateTime.parse("2024-07-03T15:30:00Z"))
        .settlementState(ListRefundsRequestSettlementState.SETTLED)
        .settlementDate(LocalDate.parse("2024-07-02"))
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `Optional<String>` — Filter by terminal ID.
    
</dd>
</dl>

<dl>
<dd>

**orderId:** `Optional<String>` — Filter refunds by the unique identifier that the merchant assigned to the order.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Filter refunds by the operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**cardholderName:** `Optional<String>` — Filter refunds by cardholder name.
    
</dd>
</dl>

<dl>
<dd>

**first6:** `Optional<String>` — Filter refunds by the first six digits of the card number.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter refunds by the last four digits of the card number.
    
</dd>
</dl>

<dl>
<dd>

**tender:** `Optional<ListRefundsRequestTender>` — Filter by tender type.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListRefundsRequestStatusItem>` — Filter refunds by the current status of the refund.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `Optional<OffsetDateTime>` — Filter by refunds processed after a specific date. The date format follows the ISO 8601 standard.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `Optional<OffsetDateTime>` — Filter by refunds processed before a specific date. The date format follows the ISO 8601 standard.
    
</dd>
</dl>

<dl>
<dd>

**settlementState:** `Optional<ListRefundsRequestSettlementState>` — Status of the settlement.
    
</dd>
</dl>

<dl>
<dd>

**settlementDate:** `Optional<String>` — Date the transaction was settled.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.createUnreferencedRefund(request) -> RetrievedRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create an unreferenced refund. An unreferenced refund is a refund that isn't linked to a payment.  

**Note:** If you have the paymentId of the payment you want to refund, use our [Refund Payment](https://docs.payroc.com/api/schema/card-payments/refunds/create-referenced-refund) method. If you use our Refund Payment method, our gateway sends the refund amount to the customer's original payment method and links the refund to the payment.  

In the request, you must provide the customer's payment details and the refund amount.  

In the response, our gateway returns information about the refund and a refundId, which you need for the following methods:  

- [Retrieve refund](https://docs.payroc.com/api/schema/card-payments/refunds/retrieve) - View the details of the refund.  
- [Adjust refund](https://docs.payroc.com/api/schema/card-payments/refunds/adjust) - Update the details of the refund.  
- [Reverse refund](https://docs.payroc.com/api/schema/card-payments/refunds/reverse-refund) - Cancel the refund if it's in an open batch.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().createUnreferencedRefund(
    UnreferencedRefund
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .channel(UnreferencedRefundChannel.POS)
        .processingTerminalId("1234001")
        .order(
            RefundOrder
                .builder()
                .orderId("OrderRef6543")
                .description("Refund for order OrderRef6543")
                .amount(4999L)
                .currency(Currency.USD)
                .build()
        )
        .refundMethod(
            UnreferencedRefundRefundMethod.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .customFields(
            Optional.of(
                Arrays.asList(
                    CustomField
                        .builder()
                        .name("yourCustomField")
                        .value("abc123")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**channel:** `UnreferencedRefundChannel` — Channel that the merchant used to request the refund.
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**order:** `RefundOrder` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**ipAddress:** `Optional<IpAddress>` 
    
</dd>
</dl>

<dl>
<dd>

**refundMethod:** `UnreferencedRefundRefundMethod` 

Polymorphic object that contains information about the payment method that the merchant uses to refund the customer.  

The value of the type parameter determines which variant you should use:
-	`card` - Payment card details
-	`secureToken` - Secure token details
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.retrieve(refundId) -> RetrievedRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a refund.  

To retrieve a refund, you need its refundId. Our gateway returned the refundId in the response of the [Refund Payment](https://docs.payroc.com/api/schema/card-payments/refunds/create-referenced-refund) method or the [Create Refund](https://docs.payroc.com/api/schema/card-payments/refunds/create-unreferenced-refund) method.  

**Note:** If you don't have the refundId, use our [List Refunds](https://docs.payroc.com/api/schema/card-payments/refunds/list) method to search for the refund.  

Our gateway returns the following information about the refund:  
- Order details, including the refund amount and when we processed the refund.
- Payment card details, including the masked card number, expiry date, and payment method.
- Cardholder details, including their contact information and shipping address.  

If the refund is a referenced refund, our gateway also returns details about the payment that the refund is linked to.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().retrieve(
    "CD3HN88U9F",
    RetrieveRefundsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundId:** `String` — Unique identifier that our gateway assigned to the refund.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.adjust(refundId, request) -> RetrievedRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to adjust a refund in an open batch.  

To adjust a refund, you need its refundId. Our gateway returned the refundId in the response of the [Refund Payment](https://docs.payroc.com/api/schema/card-payments/refunds/create-referenced-refund) method or the [Create Refund](https://docs.payroc.com/api/schema/card-payments/refunds/create-unreferenced-refund) method.  

**Note:** If you don’t have the refundId, use our [List Refunds](https://docs.payroc.com/api/schema/card-payments/refunds/list) method to search for the refund.  

You can adjust the following details of the refund:
- Customer details, including shipping address and contact information.
- Status of the refund.  

Our gateway returns information about the adjusted refund, including:
- Order details, including the refund amount and when we processed the refund.
- Payment card details, including the masked card number, expiry date, and payment method.
- Cardholder details, including their contact information and shipping address.  

If the refund is a referenced refund, our gateway also returns details about the payment that the refund is linked to.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().adjust(
    "CD3HN88U9F",
    RefundAdjustment
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .adjustments(
            Arrays.asList(
                RefundAdjustmentAdjustmentsItem.customer(
                    CustomerAdjustment
                        .builder()
                        .build()
                )
            )
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundId:** `String` — Unique identifier that our gateway assigned to the refund.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who requested the adjustment to the refund.
    
</dd>
</dl>

<dl>
<dd>

**adjustments:** `List<RefundAdjustmentAdjustmentsItem>` 

Array of polymorphic objects that contain information about adjustments to the refund.  

The value of the type parameter determines which variant you should use:  
-	`status` - Status of the transaction.
-	`customer` - Customer's contact information and shipping address.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.cardPayments.refunds.reverseRefund(refundId) -> RetrievedRefund</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a refund in an open batch.  

To cancel a refund, you need its refundId. Our gateway returned the refundId in the response of the [Refund Payment](https://docs.payroc.com/api/schema/card-payments/refunds/create-referenced-refund) or [Create Refund](https://docs.payroc.com/api/schema/card-payments/refunds/create-unreferenced-refund) method.  

**Note:** If you don’t have the refundId, use our [List Refunds](https://docs.payroc.com/api/schema/card-payments/refunds/list) method to search for the refund.  

If your request is successful, the gateway removes the refund from the merchant’s open batch and no funds are returned to the cardholder’s account.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.cardPayments().refunds().reverseRefund(
    "CD3HN88U9F",
    ReverseRefundRefundsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundId:** `String` — Unique identifier that our gateway assigned to the refund.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Funding FundingRecipients
<details><summary><code>client.funding.fundingRecipients.list() -> PayrocPager&amp;lt;PaginatedFundRecipients&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of funding recipients linked to your account.  

Note: If you want to view the details of a specific funding recipient and you have its recipientId, use our [Retrieve Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/retrieve) method.  

Our gateway returns the following information about each funding recipient in the list:  
- Tax ID and Doing Business As (DBA) name.  
- Address and contact details.  
- Funding accounts linked to the funding recipient.  

For each funding recipient, we also return the recipientId, which you can use to perform follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().list(
    ListFundingRecipientsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.create(request) -> FundingRecipient</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a funding recipient. 

A funding recipient is a business or organization that can receive funds but can't run transactions, for example, a charity.  

In the request, include the following information:  
-	Legal information, including its tax ID, Doing Business As (DBA) name, and address.  
-	Contact information, including the email address.  
-	Owners' details, including their contact details. 
-	Funding account details.  

Our gateway returns the recipientId of the funding recipient, which you can use to run follow-on actions. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().create(
    CreateFundingRecipient
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .recipientType(CreateFundingRecipientRecipientType.PRIVATE_CORPORATION)
        .taxId("12-3456789")
        .doingBusinessAs("Pizza Doe")
        .address(
            Address
                .builder()
                .address1("1 Example Ave.")
                .city("Chicago")
                .state("Illinois")
                .country("US")
                .postalCode("60056")
                .address2("Example Address Line 2")
                .address3("Example Address Line 3")
                .build()
        )
        .contactMethods(
            Arrays.asList(
                ContactMethod.email(
                    ContactMethodEmail
                        .builder()
                        .value("jane.doe@example.com")
                        .build()
                ),
                ContactMethod.phone(
                    ContactMethodPhone
                        .builder()
                        .value("2025550164")
                        .build()
                )
            )
        )
        .owners(
            Arrays.asList(
                Owner
                    .builder()
                    .firstName("Jane")
                    .lastName("Doe")
                    .dateOfBirth(LocalDate.parse("1964-03-22"))
                    .address(
                        Address
                            .builder()
                            .address1("1 Example Ave.")
                            .city("Chicago")
                            .state("Illinois")
                            .country("US")
                            .postalCode("60056")
                            .build()
                    )
                    .relationship(
                        OwnerRelationship
                            .builder()
                            .isControlProng(true)
                            .equityPercentage(48.5f)
                            .title("CFO")
                            .isAuthorizedSignatory(false)
                            .build()
                    )
                    .middleName("Helen")
                    .identifiers(
                        Arrays.asList(
                            Identifier
                                .builder()
                                .type(IdentifierType.NATIONAL_ID)
                                .value("000-00-4320")
                                .build()
                        )
                    )
                    .contactMethods(
                        Arrays.asList(
                            ContactMethod.email(
                                ContactMethodEmail
                                    .builder()
                                    .value("jane.doe@example.com")
                                    .build()
                            ),
                            ContactMethod.phone(
                                ContactMethodPhone
                                    .builder()
                                    .value("2025550164")
                                    .build()
                            )
                        )
                    )
                    .build()
            )
        )
        .fundingAccounts(
            Arrays.asList(
                FundingAccount
                    .builder()
                    .type(FundingAccountType.CHECKING)
                    .use(FundingAccountUse.CREDIT)
                    .nameOnAccount("Jane Doe")
                    .paymentMethods(
                        Arrays.asList(
                            PaymentMethodsItem.ach(
                                PaymentMethodAch
                                    .builder()
                                    .build()
                            )
                        )
                    )
                    .build()
            )
        )
        .metadata(
            new HashMap<String, String>() {{
                put("yourCustomField", "abc123");
            }}
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**recipientType:** `CreateFundingRecipientRecipientType` — Type or legal structure of the funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**taxId:** `String` — Employer identification number (EIN) or Social Security number (SSN).
    
</dd>
</dl>

<dl>
<dd>

**charityId:** `Optional<String>` — Government identifier of the charity.
    
</dd>
</dl>

<dl>
<dd>

**doingBusinessAs:** `String` — Trading name of the business or organization.
    
</dd>
</dl>

<dl>
<dd>

**address:** `Address` — Polymorphic object that contains address information for a funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**contactMethods:** `List<ContactMethod>` 

Array of polymorphic objects, which contain contact information.  

**Note:** You must provide an email address.

The value of the type parameter determines which variant you should use:  
-	`email` - Email address 
-	`phone` - Phone number
-	`mobile` - Mobile number
-	`fax` - Fax number
    
</dd>
</dl>

<dl>
<dd>

**metadata:** `Optional<Map<String, String>>` — [Metadata](https://docs.payroc.com/api/metadata) object you can use to include custom data with your request.
    
</dd>
</dl>

<dl>
<dd>

**owners:** `List<Owner>` — Array of owner objects. Each object contains information about an individual who owns or manages the funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**fundingAccounts:** `List<FundingAccount>` — Array of fundingAccount objects that you can use to add funding accounts to the funding recipient.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.retrieve(recipientId) -> FundingRecipient</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a funding recipient.  

To retrieve a funding recipient, you need its recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method.  

**Note:** If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  

Our gateway returns the following information about the funding recipient:  

- Tax ID and Doing Business As (DBA) name.  
- Address and contact details.  
- Funding accounts linked to the funding recipient.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().retrieve(
    1,
    RetrieveFundingRecipientsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.update(recipientId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to update the details of a funding recipient. If a request contains significant changes, we might need to re-approve the funding recipient.  

To update a funding recipient, you need it's recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method.  

**Note**: If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  

You can update the following details of a funding recipient:  
- Doing Business As (DBA) name  
- Tax ID and charity ID  
- Address and contact methods  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().update(
    1,
    UpdateFundingRecipientsRequest
        .builder()
        .body(
            FundingRecipient
                .builder()
                .recipientType(FundingRecipientRecipientType.PRIVATE_CORPORATION)
                .taxId("12-3456789")
                .doingBusinessAs("Doe Hot Dogs")
                .address(
                    Address
                        .builder()
                        .address1("2 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .address2("Example Address Line 2")
                        .address3("Example Address Line 3")
                        .build()
                )
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        ),
                        ContactMethod.phone(
                            ContactMethodPhone
                                .builder()
                                .value("2025550164")
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("responsiblePerson", "Jane Doe");
                    }}
                )
                .owners(
                    Optional.of(
                        Arrays.asList(
                            FundingRecipientOwnersItem
                                .builder()
                                .ownerId(12346)
                                .link(
                                    FundingRecipientOwnersItemLink
                                        .builder()
                                        .rel("owner")
                                        .href("https://api.payroc.com/v1/owners/12346")
                                        .method("get")
                                        .build()
                                )
                                .build()
                        )
                    )
                )
                .fundingAccounts(
                    Optional.of(
                        Arrays.asList(
                            FundingRecipientFundingAccountsItem
                                .builder()
                                .fundingAccountId(124)
                                .status(FundingRecipientFundingAccountsItemStatus.APPROVED)
                                .link(
                                    FundingRecipientFundingAccountsItemLink
                                        .builder()
                                        .rel("fundingAccount")
                                        .href("https://api.payroc.com/v1/funding-accounts/124")
                                        .method("get")
                                        .build()
                                )
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**request:** `FundingRecipient` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.delete(recipientId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete a funding recipient, including its funding accounts and owners.  

To delete a funding recipient, you need its recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method.   

**Note**: If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().delete(
    1,
    DeleteFundingRecipientsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.listAccounts(recipientId) -> List&amp;lt;FundingAccount&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use  this method to return a list of funding accounts associated with a funding recipient.  

**Note:** If you want to view the details of a specific funding account and you have its fundingAccountId, use our [Retrieve Funding Account](https://docs.payroc.com/api/schema/funding/funding-accounts/retrieve) method.  

To retrieve the funding accounts associated with a funding recipient, you need the recipientId. If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  
        
Our gateway returns the following information about each funding account:  
-	Name of the account holder.  
-	ACH details for the account.  
-	Status of the account.  

Our gateway also returns the fundingAccountId, which you can use to run follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().listAccounts(
    1,
    ListFundingRecipientFundingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.createAccount(recipientId, request) -> FundingAccount</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a funding account and add it to a funding recipient.  

To add a funding account to a funding recipient, you need the recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method.  

**Note:** If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  

In the request, include the following information:  
-	Account type, for example, if the account is a checking or savings account.  
-	Account holder's name.  
-	ACH information, including the routing number and account number of the account.  

Our gateway returns the fundingAccountId, which you can use to run follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().createAccount(
    1,
    CreateAccountFundingRecipientsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            FundingAccount
                .builder()
                .type(FundingAccountType.SAVINGS)
                .use(FundingAccountUse.CREDIT)
                .nameOnAccount("Fred Nerk")
                .paymentMethods(
                    Arrays.asList(
                        PaymentMethodsItem.ach(
                            PaymentMethodAch
                                .builder()
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("responsiblePerson", "Jane Doe");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `FundingAccount` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.listOwners(recipientId) -> List&amp;lt;Owner&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a list of owners of a funding recipient.  

**Note:** If you want to view the details of a specific owner and you have their ownerId, use our [Retrieve Owner](https://docs.payroc.com/api/schema/boarding/owners/retrieve) method.  

To list the owners of a funding recipient, you need its recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method. If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  

Our gateway returns the following information about each owner in the list:  
-	Name, date of birth, and address.  
-	Contact details, including their email address.  
-	Relationship to the funding recipient.  

Our gateway also returns the ownerId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().listOwners(
    1,
    ListFundingRecipientOwnersRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingRecipients.createOwner(recipientId, request) -> Owner</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to add an additional owner to a funding recipient.  

To add an owner to a funding recipient, you need the recipientId. Our gateway returned the recipientId in the response of the [Create Funding Recipient](https://docs.payroc.com/api/schema/funding/funding-recipients/create) method.  

**Note:** If you don't have the recipientId, use our [List Funding Recipients](https://docs.payroc.com/api/schema/funding/funding-recipients/list) method to search for the funding recipient.  

In the request, include the following information about the owner:  

- Name, date of birth, and address.  
- Contact details, including their email address.  
- Relationship to the funding recipient, including whether they are a control prong.  

In the response, our gateway returns the ownerId, which you can use to run follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingRecipients().createOwner(
    1,
    CreateOwnerFundingRecipientsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Owner
                .builder()
                .firstName("Fred")
                .lastName("Nerk")
                .dateOfBirth(LocalDate.parse("1980-01-19"))
                .address(
                    Address
                        .builder()
                        .address1("2 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .build()
                )
                .relationship(
                    OwnerRelationship
                        .builder()
                        .isControlProng(false)
                        .equityPercentage(51.5f)
                        .title("CEO")
                        .isAuthorizedSignatory(true)
                        .build()
                )
                .middleName("Jim")
                .identifiers(
                    Arrays.asList(
                        Identifier
                            .builder()
                            .type(IdentifierType.NATIONAL_ID)
                            .value("000-00-9876")
                            .build()
                    )
                )
                .contactMethods(
                    Arrays.asList(
                        ContactMethod.email(
                            ContactMethodEmail
                                .builder()
                                .value("jane.doe@example.com")
                                .build()
                        ),
                        ContactMethod.phone(
                            ContactMethodPhone
                                .builder()
                                .value("2025550164")
                                .build()
                        )
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**recipientId:** `Integer` — Unique identifier that we assigned to the funding recipient.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `Owner` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Funding FundingAccounts
<details><summary><code>client.funding.fundingAccounts.list() -> PayrocPager&amp;lt;ListFundingAccounts&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of funding accounts associated with your account.  

**Note:** If you want to view the details of a specific funding account and you have its fundingAccountId, use our [Retrieve Funding Account](https://docs.payroc.com/api/schema/funding/funding-accounts/retrieve) method.  

Our gateway returns the following information about each funding account in the list:  
- Name of the account holder and ACH details for the account.  
- Status of the account.  
- Whether we send funds to the account, withdraw funds from the account, or both.  

For each funding account, we also return the fundingAccountId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingAccounts().list(
    ListFundingAccountsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingAccounts.retrieve(fundingAccountId) -> FundingAccount</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a funding account.  

To retrieve a funding account, you need its fundingAccountId. Our gateway returned the fundingAccountId when you created the funding account.  

**Note:** If you don't have the fundingAccountId, use our [List Funding Accounts](https://docs.payroc.com/api/schema/funding/funding-accounts/list) method to search for the account.  

Our gateway returns the following information about the funding account:  
- Name of the account holder and ACH details for the account.  
- Status of the account.  
- Whether we send funds to the account, withdraw funds from the account, or both.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingAccounts().retrieve(
    1,
    RetrieveFundingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**fundingAccountId:** `Integer` — Unique identifier of the funding account.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingAccounts.update(fundingAccountId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can't update the details of a funding account that is associated with a processing account.  

Use this method to update the details of a funding account that is associated with a funding recipient.  

To update a funding account, you need its fundingAccountId. Our gateway returned the fundingAccountId when you created the funding account.  

**Note:** If you don’t have the fundingAccountId, use our [List Funding Accounts](https://docs.payroc.com/api/schema/funding/funding-accounts/list) method to search for the funding account.  

You can update the following details about the funding account: 
-	Account type. 
-	Account holder's name. 
-	ACH information for the account. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingAccounts().update(
    1,
    UpdateFundingAccountsRequest
        .builder()
        .body(
            FundingAccount
                .builder()
                .type(FundingAccountType.SAVINGS)
                .use(FundingAccountUse.CREDIT)
                .nameOnAccount("Fred Nerk")
                .paymentMethods(
                    Arrays.asList(
                        PaymentMethodsItem.ach(
                            PaymentMethodAch
                                .builder()
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("responsiblePerson", "Jane Doe");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**fundingAccountId:** `Integer` — Unique identifier of the funding account.
    
</dd>
</dl>

<dl>
<dd>

**request:** `FundingAccount` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingAccounts.delete(fundingAccountId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can't delete a funding account that is associated with a processing account.  

Use this method to delete a funding account that is associated with a funding recipient.  

To delete a funding account, you need its fundingAccountId. Our gateway returned the fundingAccountId when you created the funding account.  

**Note:** If you don't have the fundingAccountId, use our [List Funding Accounts](https://docs.payroc.com/api/schema/funding/funding-accounts/list) method to search for the funding account.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingAccounts().delete(
    1,
    DeleteFundingAccountsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**fundingAccountId:** `Integer` — Unique identifier of the funding account.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Funding FundingInstructions
<details><summary><code>client.funding.fundingInstructions.list() -> PayrocPager&amp;lt;ListFundingInstructionsResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> Important: You can return a list of funding instructions from only the previous two years. If you want to view a funding instruction from more than two years ago and you have its instructionId, use our [Retrieve Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/retrieve) method.  

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of funding instructions within a specific date range.  

**Note:** If you want to view the details of a specific funding instruction and you have its instructionId, use our [Retrieve Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/retrieve) method.  

Our gateway returns the following information for each instruction in the list:  
-	Status of the funding instruction.  
-	Funding information, including which merchant's funding balance we distribute and the funding account that we send the balance to.  

For each funding instruction, we also return the instructionId, which you can use to perform follow-on actions. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingInstructions().list(
    ListFundingInstructionsRequest
        .builder()
        .dateFrom(LocalDate.parse("2024-07-01"))
        .dateTo(LocalDate.parse("2024-07-03"))
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `String` — Filter by funding instructions sent after a specific date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `String` — Filter by funding instructions sent before a specific date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingInstructions.create(request) -> Instruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a funding instruction that tells us how to distribute the funds from your merchants' transactions.  

**Note:** Before you create a funding instruction, you can use our [List Funding Balances](https://docs.payroc.com/api/schema/funding/funding-activity/retrieve-balance) method to view the amount of available funds that a merchant has.  

In your request, include an array of merchantInstruction objects. Each merchantInstruction object contains the following:  
-	Merchant ID (MID) of the merchant whose funding balance you want to distribute.  
-	Funding account that you want to send funds to.  
-	Amount that you want to send to the funding account.  

Our gateway returns the instructionId, which you can use to run follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingInstructions().create(
    CreateFundingInstructionsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Instruction
                .builder()
                .merchants(
                    Optional.of(
                        Arrays.asList(
                            InstructionMerchantsItem
                                .builder()
                                .merchantId("4525644354")
                                .recipients(
                                    Arrays.asList(
                                        InstructionMerchantsItemRecipientsItem
                                            .builder()
                                            .fundingAccountId(123)
                                            .paymentMethod(InstructionMerchantsItemRecipientsItemPaymentMethod.ACH)
                                            .amount(
                                                InstructionMerchantsItemRecipientsItemAmount
                                                    .builder()
                                                    .value(120000)
                                                    .currency(InstructionMerchantsItemRecipientsItemAmountCurrency.USD)
                                                    .build()
                                            )
                                            .metadata(
                                                new HashMap<String, String>() {{
                                                    put("yourCustomField", "abc123");
                                                }}
                                            )
                                            .build()
                                    )
                                )
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("yourCustomField", "abc123");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `Instruction` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingInstructions.retrieve(instructionId) -> Instruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a funding instruction.  

To retrieve a funding instruction, you need its instructionId. Our gateway returned the instructionId in the response of the [Create Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/create) method. 

**Note:** If you don't have the instructionId, use our [List Funding Instructions](https://docs.payroc.com/api/schema/funding/funding-instructions/list) method to search for the funding instruction.  

Our gateway returns the following information about the funding instruction:  
-	Status of the funding instruction.  
-	Funding information, including which merchant's funding balance we distribute and the funding account that we send the balance to.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingInstructions().retrieve(
    1,
    RetrieveFundingInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**instructionId:** `Integer` — Unique identifier that we assigned to the funding instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingInstructions.update(instructionId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can update a funding instruction only if its status is `accepted`. To view the status of a funding instruction, use our [Retrieve Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/retrieve) method. 

Use this method to update the details of a funding instruction.  

To update a funding instruction, you need its instructionId. Our gateway returned the instructionId in the response of the [Create Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/create) method.  

**Note:** If you don't have the fundingInstructionId, use our [List Funding Instructions](https://docs.payroc.com/api/schema/funding/funding-instructions/list) method to search for the funding instruction.  

You can modify the following information for the funding instruction:  
-	Merchant ID (MID) of the merchant whose funding balance you want to distribute.  
-	Funding account that you want to send funds to.  
-	Amount that you want to send to the funding account.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingInstructions().update(
    1,
    UpdateFundingInstructionsRequest
        .builder()
        .body(
            Instruction
                .builder()
                .merchants(
                    Optional.of(
                        Arrays.asList(
                            InstructionMerchantsItem
                                .builder()
                                .merchantId("9876543219")
                                .recipients(
                                    Arrays.asList(
                                        InstructionMerchantsItemRecipientsItem
                                            .builder()
                                            .fundingAccountId(124)
                                            .paymentMethod(InstructionMerchantsItemRecipientsItemPaymentMethod.ACH)
                                            .amount(
                                                InstructionMerchantsItemRecipientsItemAmount
                                                    .builder()
                                                    .value(69950)
                                                    .currency(InstructionMerchantsItemRecipientsItemAmountCurrency.USD)
                                                    .build()
                                            )
                                            .metadata(
                                                new HashMap<String, String>() {{
                                                    put("supplier", "IT Support Services");
                                                }}
                                            )
                                            .build()
                                    )
                                )
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, String>() {{
                        put("instructionCreatedBy", "Jane Doe");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**instructionId:** `Integer` — Unique identifier that we assigned to the funding instruction.
    
</dd>
</dl>

<dl>
<dd>

**request:** `Instruction` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingInstructions.delete(instructionId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** You can delete a funding instruction only if its status is `accepted`. To view the status of a funding instruction, use our [Retrieve Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/retrieve) method.  

Use this method to delete a funding instruction.  

To delete a funding instruction, you need its instructionId. Our gateway returned the instructionId in the response of the [Create Funding Instruction](https://docs.payroc.com/api/schema/funding/funding-instructions/create) method.  

**Note:** If you don't have the instructionId, use our [List Funding Instructions](https://docs.payroc.com/api/schema/funding/funding-instructions/list) method to search for the funding instruction.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingInstructions().delete(
    1,
    DeleteFundingInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**instructionId:** `Integer` — Unique identifier that we assigned to the funding instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Funding FundingActivity
<details><summary><code>client.funding.fundingActivity.retrieveBalance() -> RetrieveBalanceFundingActivityResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of funding balances available for each merchant linked to your account.  

Use query parameters to filter the list of results we return, for example, to search for the funding balance for a specific merchant.  

Our gateway returns the following information about each merchant in the list:  
- Total funds for the merchant.  
- Available funds that you can use for funding instructions.  
- Pending funds that we have not yet sent to funding accounts.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingActivity().retrieveBalance(
    RetrieveBalanceFundingActivityRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.funding.fundingActivity.list() -> PayrocPager&amp;lt;ListFundingActivityResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of activity associated with your merchants' funding balances within a specific date range.  

Use query parameters to filter the list of results we return, for example, to view the activity for a specific merchant's funding balance.  

Our gateway returns the following information about each activity in the list:
- Name of the merchant who owns the funding balance.
-	Amount of funds added or removed from the funding balance.
-	Funding account that received funds from the funding balance.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.funding().fundingActivity().list(
    ListFundingActivityRequest
        .builder()
        .dateFrom(LocalDate.parse("2024-07-02"))
        .dateTo(LocalDate.parse("2024-07-03"))
        .before("2571")
        .after("8516")
        .limit(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**dateFrom:** `String` — Filter by activity after a specific date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**dateTo:** `String` — Filter by activity before a specific date. Send a value in **YYYY-MM-DD** format.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Notifications EventSubscriptions
<details><summary><code>client.notifications.eventSubscriptions.list() -> PayrocPager&amp;lt;PaginatedEventSubscriptions&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of event subscriptions that are linked to your ISV account.  

**Note:** If you want to view the details of a specific event subscription and you have its id, use our [Retrieve Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for subscriptions with a specific status or an event type.  

Our gateway returns the following information about each subscription in the list:  
- Event types that you have subscribed to.  
- Whether you have enabled notifications for the subscription.  
- How we contact you when an event occurs, including the endpoint that send notifications to.  
- If there are any issues when we try to send you a notification, for example, if we can't contact your endpoint.  

For each event subscription, we also return its id, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().list(
    ListEventSubscriptionsRequest
        .builder()
        .status(ListEventSubscriptionsRequestStatus.REGISTERED)
        .event("processingAccount.status.changed")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**status:** `Optional<ListEventSubscriptionsRequestStatus>` — Filter event subscriptions by subscription status.
    
</dd>
</dl>

<dl>
<dd>

**event:** `Optional<String>` — Filter event subscriptions by an event type. For a list of event types, go to [Events List](https://docs.payroc.com/knowledge/events/events-list).
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.notifications.eventSubscriptions.create(request) -> EventSubscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create an event subscription that we use to notify you when an event occurs, for example, when we change the status of a processing account.  

In the request, include the events that you want to subscribe to and the public endpoint that we send event notifications to. For a complete list of events that you can subscribe to, go to [Events List](https://docs.payroc.com/knowledge/events/events-list).  

In the response, our gateway returns the id of the event subscription, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().create(
    CreateEventSubscriptionsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            EventSubscription
                .builder()
                .enabled(true)
                .eventTypes(
                    Arrays.asList("processingAccount.status.changed")
                )
                .notifications(
                    Arrays.asList(
                        Notification.webhook(
                            Webhook
                                .builder()
                                .uri("https://my-server/notification/endpoint")
                                .secret("aBcD1234eFgH5678iJkL9012mNoP3456")
                                .supportEmailAddress("supportEmailAddress")
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, Object>() {{
                        put("yourCustomField", "abc123");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `EventSubscription` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.notifications.eventSubscriptions.retrieve(subscriptionId) -> EventSubscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve the details of an event subscription.  

In your request, include the subscriptionId that we sent to you when we created the event subscription.  
  
**Note:** If you don't know the subscriptionId of the event subscription, go to [List event subscriptions](https://docs.payroc.com/api/schema/notifications/event-subscriptions/list).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().retrieve(
    1,
    RetrieveEventSubscriptionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**subscriptionId:** `Integer` 

Unique identifier that we assigned to the event subscription.  
**Note:** Our gateway returned the subscriptionId in the id field in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.notifications.eventSubscriptions.update(subscriptionId, request)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to update the details of an event subscription.  

To update an event subscription, you need its subscriptionId. Our gateway returned the subscriptionId in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  

**Note:** If you don’t have the subscriptionId, use our [List Event Subscriptions](https://docs.payroc.com/api/schema/notifications/event-subscriptions/list) method to search for the event subscription.  

You can update the following details about an event subscription:  

- Status of the event subscription.  
- Events that you have subscribed to. For a list of events that you can subscribe to, go to [Events list](https://docs.payroc.com/knowledge/events/events-list).  
- Information about how we contact you when an event occurs.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().update(
    1,
    UpdateEventSubscriptionsRequest
        .builder()
        .body(
            EventSubscription
                .builder()
                .enabled(true)
                .eventTypes(
                    Arrays.asList("processingAccount.status.changed")
                )
                .notifications(
                    Arrays.asList(
                        Notification.webhook(
                            Webhook
                                .builder()
                                .uri("https://my-server/notification/endpoint")
                                .secret("aBcD1234eFgH5678iJkL9012mNoP3456")
                                .supportEmailAddress("supportEmailAddress")
                                .build()
                        )
                    )
                )
                .metadata(
                    new HashMap<String, Object>() {{
                        put("yourCustomField", "abc123");
                    }}
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**subscriptionId:** `Integer` 

Unique identifier that we assigned to the event subscription.  
**Note:** Our gateway returned the subscriptionId in the id field in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  
    
</dd>
</dl>

<dl>
<dd>

**request:** `EventSubscription` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.notifications.eventSubscriptions.delete(subscriptionId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete an event subscription.  

> **Important:** After you delete an event subscription, you can’t recover it. You won't receive event notifications from the event subscription.  

To delete an event subscription, you need its subscriptionId. Our gateway returned the subscriptionId in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  

If you want to stop receiving event notifications but don't want to delete the event subscription, use our [Update Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/update) method to deactivate it.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().delete(
    1,
    DeleteEventSubscriptionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**subscriptionId:** `Integer` 

Unique identifier that we assigned to the event subscription.  
**Note:** Our gateway returned the subscriptionId in the id field in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.notifications.eventSubscriptions.partiallyUpdate(subscriptionId, request) -> EventSubscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update an event subscription. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.  

To update an event subscription, you need its subscriptionId. Our gateway returned the subscriptionId in the id field in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List Event Subscriptions](https://docs.payroc.com/api/schema/notifications/event-subscriptions/list) method to search for the subscription.  

You can update the following properties of an event subscription:  
- **eventTypes** - Subscribe to new events or remove events that you are subscribed to.  
- **notifications** - Information about your endpoint and who we email if we can't contact your endpoint.  
- **enabled** - Turn on or turn off notifications for the subscription.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.notifications().eventSubscriptions().partiallyUpdate(
    1,
    PartiallyUpdateEventSubscriptionsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**subscriptionId:** `Integer` 

Unique identifier that we assigned to the event subscription.  
**Note:** Our gateway returned the subscriptionId in the id field in the response of the [Create Event Subscription](https://docs.payroc.com/api/schema/notifications/event-subscriptions/create) method.  
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PaymentFeatures Cards
<details><summary><code>client.paymentFeatures.cards.verifyCard(request) -> CardVerificationResult</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to verify a customer’s card details.  

In the request, send the customer’s card details.  

In the response, our gateway indicates if the card details are valid and if you should use them in follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentFeatures().cards().verifyCard(
    CardVerificationRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .card(
            CardVerificationRequestCard.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who requested to verify the card.
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**card:** `CardVerificationRequestCard` — Polymorphic object that contains payment details.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentFeatures.cards.viewEbtBalance(request) -> Balance</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to view the balance of an Electronic Benefit Transfer (EBT) card.  

If the request is successful, our gateway returns the current balance of an EBT card. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentFeatures().cards().viewEbtBalance(
    BalanceInquiry
        .builder()
        .processingTerminalId("1234001")
        .currency(Currency.USD)
        .card(
            BalanceInquiryCard.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who requested the balance inquiry.
    
</dd>
</dl>

<dl>
<dd>

**currency:** `Currency` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**card:** `BalanceInquiryCard` 

Polymorphic object that contains payment details.  

The value of the type parameter determines which variant you should use:  
-	`card` - Payment card details
-	`singleUseToken` - Single-use token details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentFeatures.cards.lookupBin(request) -> CardInfo</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a debit card, a credit card, or an EBT card. If you apply surcharges to transactions, you can also check if the card supports surcharging.  

In the response, our gateway returns the following information about the card:  

- **Card details** - Information about the card, for example, the issuing bank and the masked card number.  

- **Surcharging information** - If you apply a surcharge to transactions, our gateway checks that the card supports surcharging and returns information about the surcharge. For more information about surcharging, go to [Credit card surcharging](https://docs.payroc.com/knowledge/card-payments/credit-card-surcharging). 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentFeatures().cards().lookupBin(
    BinLookup
        .builder()
        .card(
            BinLookupCard.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .processingTerminalId("1234001")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `Optional<String>` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**amount:** `Optional<Long>` — Transaction amount that you send to check the surcharge amount. The value is in the currency's lowest denomination, for example, cents.
    
</dd>
</dl>

<dl>
<dd>

**currency:** `Optional<Currency>` 
    
</dd>
</dl>

<dl>
<dd>

**card:** `BinLookupCard` 

Polymorphic object that contains payment details.  

The value of the type parameter determines which variant you should use:  
-	`card` - Payment card details
-	`cardBin` - Bank identification number (BIN) of the payment card
-	`secureToken` - Secure token details
-	`digitalWallet` - Digital wallet details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentFeatures.cards.retrieveFxRates(request) -> FxRate</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

> **Important:** There are restrictions on which merchants can use this method. For more information, go to [Dynamic Currency Conversion](https://docs.payroc.com/knowledge/card-payments/dynamic-currency-conversion).  

Use this method to check if a card is eligible for Dynamic Currency Conversion (DCC) and to retrieve the conversion rate for a transaction amount. DCC provides a customer with the option to use their card's currency instead of the merchant's currency, for example, in Ireland, an American customer can pay in US dollars instead of Euros.  

The request includes the following:  

- **Payment method** - Card information, a secure token, or digital wallet.  
- **Transaction information** - Amount and currency of the transaction in the merchant's currency.  

If the card is eligible for DCC, our gateway returns the transaction amount in the card's currency and a dccOffer object that contains information about the conversion rate. The dccOffer object contains the following fields that you need when you [run a sale](https://docs.payroc.com/api/schema/card-payments/payments/create) or [unreferenced refund](https://docs.payroc.com/api/schema/card-payments/refunds/create-unreferenced-refund) with DCC:  
- fxAmount  
- fxCurrency  
- fxRate  
- markup  
- accepted  
- offerReference  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentFeatures().cards().retrieveFxRates(
    FxRateInquiry
        .builder()
        .channel(FxRateInquiryChannel.WEB)
        .processingTerminalId("1234001")
        .baseAmount(10000L)
        .baseCurrency(Currency.USD)
        .paymentMethod(
            FxRateInquiryPaymentMethod.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**channel:** `FxRateInquiryChannel` — Channel that the merchant used to receive payment details for the transaction.
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who ran the transaction.
    
</dd>
</dl>

<dl>
<dd>

**baseAmount:** `Long` — Total amount of the transaction in the merchant’s currency. The value is in the currency’s lowest denomination, for example, cents.
    
</dd>
</dl>

<dl>
<dd>

**baseCurrency:** `Currency` 
    
</dd>
</dl>

<dl>
<dd>

**paymentMethod:** `FxRateInquiryPaymentMethod` 

Polymorphic object that contains payment details.  

The value of the type parameter determines which variant you should use:  
-	`card` - Payment card details
-	`secureToken` - Secure token details
-	`digitalWallet` - Digital wallet details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PaymentFeatures Bank
<details><summary><code>client.paymentFeatures.bank.verify(request) -> BankAccountVerificationResult</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to verify a customer's bank account details.  

In the request, send the customer's bank account details. Our gateway can verify the following types of bank details:  
- Automated Clearing House (ACH) details  
- Pre-Authorized Debit (PAD) details  

In the response, our gateway indicates if the account details are valid and if you should use them in follow-on actions.  
  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentFeatures().bank().verify(
    BankAccountVerificationRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .bankAccount(
            BankAccountVerificationRequestBankAccount.pad(
                PadPayload
                    .builder()
                    .nameOnAccount("Sarah Hazel Hopper")
                    .accountNumber("1234567890")
                    .transitNumber("76543")
                    .institutionNumber("543")
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**bankAccount:** `BankAccountVerificationRequestBankAccount` 

Polymorphic object that contains bank account information.  

The value of the type field determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`pad` - Pre-authorized debit (PAD) details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PaymentLinks SharingEvents
<details><summary><code>client.paymentLinks.sharingEvents.list(paymentLinkId) -> PayrocPager&amp;lt;SharingEventPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of sharing events for a payment link. A sharing event occurs when a merchant shares a payment link with a customer.  

To list the sharing events for a payment link, you need its paymentLinkId. Our gateway returned the paymentLinkId in the response of the [Create Payment Link](https://docs.payroc.com/api/schema/payment-links/create) method.  

**Note:** If you don't have the paymentLinkId, use our [List Payment Links](https://docs.payroc.com/api/schema/payment-links/list) method to search for the payment link.  

Use query parameters to filter the list of results that we return, for example, to search for links sent to a specific customer.  

Our gateway returns the following information for each sharing event in the list:  
- Customer that the merchant sent the link to.  
- Date that the merchant sent the link.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().sharingEvents().list(
    "JZURRJBUPS",
    ListSharingEventsRequest
        .builder()
        .recipientName("Sarah Hazel Hopper")
        .recipientEmail("sarah.hopper@example.com")
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentLinkId:** `String` — Unique identifier that we assigned to the payment link.
    
</dd>
</dl>

<dl>
<dd>

**recipientName:** `Optional<String>` — Filter results by the customer's name.
    
</dd>
</dl>

<dl>
<dd>

**recipientEmail:** `Optional<String>` — Filter results by the customer's email address.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.paymentLinks.sharingEvents.share(paymentLinkId, request) -> PaymentLinkEmailShareEvent</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to email a payment link to a customer.  

To email a payment link, you need its paymentLinkId. Our gateway returned the paymentLinkId in the response of the [Create Payment Link](https://docs.payroc.com/api/schema/payment-links/create) method.  

**Note:** If you don't have the paymentLinkId, use our [List Payment Links](https://docs.payroc.com/api/schema/payment-links/list) method to search for the payment link.  

In the request, you must provide the recipient's name and email address.  

In the response, our gateway returns a sharingEventId, which you can use to [List Payment Link Sharing Events](https://docs.payroc.com/api/schema/payment-links/sharing-events/list).  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.paymentLinks().sharingEvents().share(
    "JZURRJBUPS",
    ShareSharingEventsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            PaymentLinkEmailShareEvent
                .builder()
                .sharingMethod(PaymentLinkEmailShareEventSharingMethod.EMAIL)
                .merchantCopy(true)
                .message("Dear Sarah,\n\nYour insurance is expiring this month.\nPlease, pay the renewal fee by the end of the month to renew it.\n")
                .recipients(
                    Arrays.asList(
                        PaymentLinkEmailRecipient
                            .builder()
                            .name("Sarah Hazel Hopper")
                            .email("sarah.hopper@example.com")
                            .build()
                    )
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentLinkId:** `String` — Unique identifier that we assigned to the payment link.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `PaymentLinkEmailShareEvent` — Polymorphic object that contains information about how to share a payment link.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PayrocCloud PaymentInstructions
<details><summary><code>client.payrocCloud.paymentInstructions.submit(serialNumber, request) -> PaymentInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to submit an instruction request to initiate a sale on a payment device.  

In the request, include the order amount and currency.  

When you send a successful request, our gateway returns information about the payment instruction and a paymentInstructionId, which you need for the following methods:
- [Retrieve payment instruction](https://docs.payroc.com/api/schema/payroc-cloud/payment-instructions/retrieve) - View the details of the payment instruction.
- [Cancel payment instruction](https://docs.payroc.com/api/schema/payroc-cloud/payment-instructions/delete) - Cancel the payment instruction.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().paymentInstructions().submit(
    "1850010868",
    PaymentInstructionRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .order(
            PaymentInstructionOrder
                .builder()
                .orderId("OrderRef6543")
                .amount(4999L)
                .currency(Currency.USD)
                .build()
        )
        .operator("Jane")
        .customizationOptions(
            CustomizationOptions
                .builder()
                .entryMethod(CustomizationOptionsEntryMethod.DEVICE_READ)
                .build()
        )
        .autoCapture(true)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serialNumber:** `String` — Serial number of the merchant’s payment device.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**order:** `PaymentInstructionOrder` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**ipAddress:** `Optional<IpAddress>` 
    
</dd>
</dl>

<dl>
<dd>

**credentialOnFile:** `Optional<SchemasCredentialOnFile>` 
    
</dd>
</dl>

<dl>
<dd>

**customizationOptions:** `Optional<CustomizationOptions>` 
    
</dd>
</dl>

<dl>
<dd>

**autoCapture:** `Optional<Boolean>` 

Indicates if we should automatically capture the payment amount.  

- `true` - Run a sale and automatically capture the transaction.
- `false`- Run a pre-authorization and capture the transaction later.  

**Note:** If you send `false` and the terminal doesn't support pre-authorization, we set the transaction's status to pending. The merchant must capture the transaction to take payment from the customer.
    
</dd>
</dl>

<dl>
<dd>

**processAsSale:** `Optional<Boolean>` 

Indicates if we should immediately settle the sale transaction. The merchant cannot adjust the transaction if we immediately settle it.  
**Note:** If the value for **processAsSale** is `true`, the gateway ignores the value in **autoCapture**.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.paymentInstructions.retrieve(paymentInstructionId) -> PaymentInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a payment instruction.  

To retrieve a payment instruction, you need its paymentInstructionId. Our gateway returned the paymentInstructionId in the response of the [Submit Payment Instruction](https://docs.payroc.com/api/schema/payroc-cloud/payment-instructions/submit) method.  

Our gateway returns the status of the payment instruction. If the payment device completed the payment instruction, the response also includes a link to the payment.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().paymentInstructions().retrieve(
    "e743a9165d134678a9100ebba3b29597",
    RetrievePaymentInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentInstructionId:** `String` — Unique identifier that we assigned to the payment instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.paymentInstructions.delete(paymentInstructionId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a payment instruction.  

You can cancel a payment instruction only if its status is `inProgress`. To retrieve the status of a payment instruction, use our [Retrieve Payment Instruction](https://docs.payroc.com/api/schema/payroc-cloud/payment-instructions/retrieve) method.  

To cancel a payment instruction, you need its paymentInstructionId. Our gateway returned the paymentInstructionId in the response of the [Submit Payment Instruction](https://docs.payroc.com/api/schema/payroc-cloud/payment-instructions/submit) method.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().paymentInstructions().delete(
    "e743a9165d134678a9100ebba3b29597",
    DeletePaymentInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**paymentInstructionId:** `String` — Unique identifier that we assigned to the payment instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PayrocCloud RefundInstructions
<details><summary><code>client.payrocCloud.refundInstructions.submit(serialNumber, request) -> RefundInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to submit an instruction request to initiate a refund on a payment device.  

In the request, include the refund amount and currency.  

If the request is successful, our gateway returns information about the refund instruction and a refundInstructionId, which you need for the following methods:
- [Retrieve refund instruction](https://docs.payroc.com/api/schema/payroc-cloud/refund-instructions/retrieve) - View the details of the refund instruction.
- [Cancel refund instruction](https://docs.payroc.com/api/schema/payroc-cloud/refund-instructions/delete) - Cancel the refund instruction.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().refundInstructions().submit(
    "1850010868",
    RefundInstructionRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .order(
            RefundInstructionOrder
                .builder()
                .orderId("OrderRef6543")
                .amount(4999L)
                .currency(Currency.USD)
                .description("Refund for order OrderRef6543")
                .build()
        )
        .operator("Jane")
        .customizationOptions(
            CustomizationOptions
                .builder()
                .entryMethod(CustomizationOptionsEntryMethod.MANUAL_ENTRY)
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serialNumber:** `String` — Serial number that identifies the merchant’s payment device.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**order:** `RefundInstructionOrder` 
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**ipAddress:** `Optional<IpAddress>` 
    
</dd>
</dl>

<dl>
<dd>

**customizationOptions:** `Optional<CustomizationOptions>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.refundInstructions.retrieve(refundInstructionId) -> RefundInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a refund instruction.  

To retrieve a refund instruction, you need its refundInstructionId. Our gateway returned the refundInstructionId in the response of the [Submit Refund Instruction](https://docs.payroc.com/api/schema/payroc-cloud/refund-instructions/submit) method.  

Our gateway returns the status of the refund instruction. If the payment device completed the refund instruction, the response also includes a link to the refund.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().refundInstructions().retrieve(
    "a37439165d134678a9100ebba3b29597",
    RetrieveRefundInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundInstructionId:** `String` — Unique identifier that we assigned to the refund instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.refundInstructions.delete(refundInstructionId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a refund instruction.  

You can cancel a refund instruction only if its status is `inProgress`. To retrieve the status of a refund instruction, use our [Retrieve Refund Instruction](https://docs.payroc.com/api/schema/payroc-cloud/refund-instructions/retrieve) method.  

To cancel a refund instruction, you need its refundInstructionId. Our gateway returned the refundInstructionId in the response of the [Submit Refund Instruction](https://docs.payroc.com/api/schema/payroc-cloud/refund-instructions/submit) method. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().refundInstructions().delete(
    "a37439165d134678a9100ebba3b29597",
    DeleteRefundInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**refundInstructionId:** `String` — Unique identifier that we assigned to the refund instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PayrocCloud SignatureInstructions
<details><summary><code>client.payrocCloud.signatureInstructions.submit(serialNumber, request) -> SignatureInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to submit an instruction to capture a customer's signature on a payment device.  

Our gateway returns information about the signature instruction and a signatureInstructionId, which you need for the following methods:
- [Retrieve signature instruction](https://docs.payroc.com/api/schema/payroc-cloud/signature-instructions/retrieve) - View the details of the signature instruction.
- [Cancel signature instruction](https://docs.payroc.com/api/schema/payroc-cloud/signature-instructions/delete) - Cancel the signature instruction.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().signatureInstructions().submit(
    "1850010868",
    SignatureInstructionRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .processingTerminalId("1234001")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**serialNumber:** `String` — Serial number that identifies the merchant’s payment device.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.signatureInstructions.retrieve(signatureInstructionId) -> SignatureInstruction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a signature instruction.  

To retrieve a signature instruction, you need its signatureInstructionId. Our gateway returned the signatureInstructionId in the response of the [Submit Signature Instruction](https://docs.payroc.com/api/schema/payroc-cloud/signature-instructions/submit) method.  

Our gateway returns the status of the instruction. If the payment device completed the instruction, the response also includes a link to retrieve the signature.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().signatureInstructions().retrieve(
    "a37439165d134678a9100ebba3b29597",
    RetrieveSignatureInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**signatureInstructionId:** `String` — Unique identifier that our gateway assigned to the signature instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.payrocCloud.signatureInstructions.delete(signatureInstructionId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to cancel a signature instruction.  

To cancel a signature instruction, you need its signatureInstructionId. Our gateway returned the signatureInstructionId in the response of the [Submit signature instruction](https://docs.payroc.com/api/schema/payroc-cloud/signature-instructions/submit) method.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().signatureInstructions().delete(
    "a37439165d134678a9100ebba3b29597",
    DeleteSignatureInstructionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**signatureInstructionId:** `String` — Unique identifier that our gateway assigned to the signature instruction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## PayrocCloud Signatures
<details><summary><code>client.payrocCloud.signatures.retrieve(signatureId) -> RetrieveSignaturesResponse</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve a signature that a payment device captured using Payroc Cloud. 

Our gateway returns the following information about the signature:
- Image of the signature
- Format of the image
- Date that the device captured the image
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.payrocCloud().signatures().retrieve(
    "JDN4ILZB0T",
    RetrieveSignaturesRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**signatureId:** `String` — Unique identifier that we assigned to the signature.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## RepeatPayments PaymentPlans
<details><summary><code>client.repeatPayments.paymentPlans.list(processingTerminalId) -> PayrocPager&amp;lt;PaymentPlanPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of payment plans for a processing terminal.  

**Note:** If you want to view the details of a specific payment plan and you have its paymentPlanId, use our [Retrieve Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/retrieve) method.  

Our gateway returns the following information about each payment plan in the list:  

  -	Name, length, and currency of the plan  
  -	How often our gateway collects each payment  
  -	How much our gateway collects for each payment  
  -	What happens if the merchant updates or deletes the plan  

For each payment plan, we return the paymentPlanId, which you can use to perform follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().paymentPlans().list(
    "1234001",
    ListPaymentPlansRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.paymentPlans.create(processingTerminalId, request) -> PaymentPlan</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a payment schedule that you can assign customers to.  

**Note:** This method is part of our Repeat Payments feature. To help you understand how this method works with our Subscriptions endpoints, go to [Repeat Payments](https://docs.payroc.com/guides/take-payments/repeat-payments).  

When you create a payment plan you need to provide a unique paymentPlanId that you use to run follow-on actions:  

-	[Retrieve Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/retrieve)  - View the details of the payment plan.  
-	[Update Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/partially-update)  - Update the details of the payment plan.  
-	[Delete Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/delete)  - Delete the payment plan.  
-	[Create Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create)  - Subscribe a customer to the payment plan.  

The request includes the following settings:  

-	**type** - Indicates if our gateway or the merchant collects payments. If the merchant manually collects payments, integrate with the [Pay Manual Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/pay) method.  
-	**recurringOrder** - Amount of each payment if the gateway automatically collect payments.  
-	**setupOrder** - Setup fee that our gateway immediately collects from the customer's payment method.  
-	**onUpdate and onDelete** - Indicates what happens to associated subscriptions if the merchant updates or deletes the payment plan.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().paymentPlans().create(
    "1234001",
    CreatePaymentPlansRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            PaymentPlan
                .builder()
                .paymentPlanId("PlanRef8765")
                .name("Premium Club")
                .currency(Currency.USD)
                .type(PaymentPlanBaseType.AUTOMATIC)
                .frequency(PaymentPlanBaseFrequency.MONTHLY)
                .onUpdate(PaymentPlanBaseOnUpdate.CONTINUE)
                .onDelete(PaymentPlanBaseOnDelete.COMPLETE)
                .description("Monthly Premium Club subscription")
                .length(12)
                .customFieldNames(
                    Optional.of(
                        Arrays.asList("yourCustomField")
                    )
                )
                .setupOrder(
                    PaymentPlanSetupOrder
                        .builder()
                        .amount(4999L)
                        .description("Initial setup fee for Premium Club subscription")
                        .breakdown(
                            PaymentPlanOrderBreakdown
                                .builder()
                                .subtotal(4347L)
                                .taxes(
                                    Optional.of(
                                        Arrays.asList(
                                            RetrievedTax
                                                .builder()
                                                .name("Sales Tax")
                                                .rate(5.0)
                                                .build()
                                        )
                                    )
                                )
                                .build()
                        )
                        .build()
                )
                .recurringOrder(
                    PaymentPlanRecurringOrder
                        .builder()
                        .amount(4999L)
                        .description("Monthly Premium Club subscription")
                        .breakdown(
                            PaymentPlanOrderBreakdown
                                .builder()
                                .subtotal(4347L)
                                .taxes(
                                    Optional.of(
                                        Arrays.asList(
                                            RetrievedTax
                                                .builder()
                                                .name("Sales Tax")
                                                .rate(5.0)
                                                .build()
                                        )
                                    )
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `PaymentPlan` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.paymentPlans.retrieve(processingTerminalId, paymentPlanId) -> PaymentPlan</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a payment plan.  

To retrieve a payment plan, you need its paymentPlanId. Our gateway returned the paymentPlanId in the response of the [Create Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/create) method.  

**Note:** If you don't have the paymentPlanId, use our [List Payment Plans](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/list) method to search for the payment plan.  

Our gateway returns the following information about the payment plan:  

  -	Name, length, and currency of the plan  
  -	How often our gateway collects each payment  
  -	How much our gateway collects for each payment  
  -	What happens if the merchant updates or deletes the plan  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().paymentPlans().retrieve(
    "1234001",
    "PlanRef8765",
    RetrievePaymentPlansRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**paymentPlanId:** `String` — Unique identifier that the merchant assigned to the payment plan.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.paymentPlans.delete(processingTerminalId, paymentPlanId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete a payment plan.  

> **Important:** When you delete a payment plan, you can’t recover it. You also won’t be able to add subscriptions to the payment plan.  

To delete a payment plan, you need its paymentPlanId, which you sent in the request of the [Create Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/create) method.  

**Note:** If you don't have the paymentPlanId, use our [List Payment Plans](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/list) method to search for the payment plan.  

The value you sent for the onDelete parameter when you created the payment plan indicates what happens to associated subscriptions when you delete the plan:  

  -	`complete` - Our gateway stops taking payments for the subscriptions associated with the payment plan.  
  -	`continue` - Our gateway continues to take payments for the subscriptions associated with the payment plan. To stop a subscription for a cancelled payment plan, go to the [Deactivate Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/deactivate) method.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().paymentPlans().delete(
    "1234001",
    "PlanRef8765",
    DeletePaymentPlansRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**paymentPlanId:** `String` — Unique identifier that the merchant assigned to the payment plan.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.paymentPlans.partiallyUpdate(processingTerminalId, paymentPlanId, request) -> PaymentPlan</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update a payment plan. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.  

To update a payment plan, you need its paymentPlanId, which you sent in the request of the [Create Payment Plan](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/create) method.  

**Note:** If you don't have the paymentPlanId, use our [List Payment Plans](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/list) method to search for the payment plan.  

You can update all of the properties of the payment plan except for the paymentPlanId.  

The value you sent for the onUpdate parameter when you created the payment plan indicates what happens to the associated subscriptions when you update the plan:  
- `update` - Our gateway updates the subscriptions associated with the payment plan.
- `continue` - Our  gateway doesn't update the subscriptions associated with the payment plan.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().paymentPlans().partiallyUpdate(
    "1234001",
    "PlanRef8765",
    PartiallyUpdatePaymentPlansRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**paymentPlanId:** `String` — Unique identifier that the merchant assigned to the payment plan.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## RepeatPayments Subscriptions
<details><summary><code>client.repeatPayments.subscriptions.list(processingTerminalId) -> PayrocPager&amp;lt;SubscriptionPaginatedList&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of subscriptions.  

Note: If you want to view the details of a specific subscription and you have its subscriptionId, use our [Retrieve subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for subscriptions for a customer, a payment plan, or frequency.  

Our gateway returns information about the following for each subscription in the list:  

-	Payment plan the subscription is linked to.  
-	Secure token that represents cardholder’s payment details.  
-	Current state of the subscription, including its status, next due date, and invoices.  
-	Fees for setup and the cost of the recurring order.  
-	Subscription length, end date, and frequency.  

For each subscription, we also return the subscriptionId, the paymentPlanId, and the secureTokenId, which you can use to perform follow-actions. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().list(
    "1234001",
    ListSubscriptionsRequest
        .builder()
        .customerName("Sarah%20Hazel%20Hopper")
        .last4("7062")
        .paymentPlan("Premium%20Club")
        .frequency(ListSubscriptionsRequestFrequency.WEEKLY)
        .status(ListSubscriptionsRequestStatus.ACTIVE)
        .endDate(LocalDate.parse("2025-07-01"))
        .nextDueDate(LocalDate.parse("2024-08-01"))
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**customerName:** `Optional<String>` — Filter by the customer's name.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter by the last four digits of the card or account number.
    
</dd>
</dl>

<dl>
<dd>

**paymentPlan:** `Optional<String>` — Filter by the name of the payment plan.
    
</dd>
</dl>

<dl>
<dd>

**frequency:** `Optional<ListSubscriptionsRequestFrequency>` — Filter by the frequency of subscription payments.
    
</dd>
</dl>

<dl>
<dd>

**status:** `Optional<ListSubscriptionsRequestStatus>` — Filter by the current status of the subscription.
    
</dd>
</dl>

<dl>
<dd>

**endDate:** `Optional<String>` 

Format: `YYYY-MM-DD`  
Filter subscriptions that end on a specific date.
    
</dd>
</dl>

<dl>
<dd>

**nextDueDate:** `Optional<String>` 

Format: `YYYY-MM-DD`  
Filter subscriptions by the date that the next payment is collected.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.create(processingTerminalId, request) -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to assign a customer to a payment plan.  

**Note:** This method is part of our Repeat Payments feature. To help you understand how this method works with our Payment plans endpoints, go to [Repeat Payments](https://docs.payroc.com/guides/take-payments/repeat-payments).  

When you create a subscription you need to provide a unique subscriptionId that you use to run follow-on actions:  

- [Retrieve Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/retrieve) - View the details of the subscription.
- [Update Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/partially-update) - Update the details of the subscription.
- [Deactivate Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/deactivate) - Stop taking payments for the subscription.
- [Re-activate Subscription](https://docs.payroc.com/api/schema/payments/subscriptions/reactivate) - Start taking payments again for the subscription.
- [Pay Manual Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/pay) - Manually collect a payment for the subscription.

The request includes the following settings:
- **paymentPlanId** - Unique identifier of the payment plan that the merchant wants to use. If you don't have the paymentPlanId, use our [List Payment Plans](https://docs.payroc.com/api/schema/repeat-payments/payment-plans/list) method to search for the payment plan.
- **paymentMethod** - Object that contains information about the secure token, which represents the customer's card details or bank account details.
- **startDate** - Date that you want to start to take payments.

You can also update the settings that the subscription inherited from the payment plan, for example, you can change the amount for each payment. If you change the settings for the subscription, it doesn't change the settings in the payment plan that it's linked to. 
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().create(
    "1234001",
    SubscriptionRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .subscriptionId("SubRef7654")
        .paymentPlanId("PlanRef8765")
        .paymentMethod(
            SubscriptionRequestPaymentMethod.secureToken(
                SecureTokenPayload
                    .builder()
                    .token("1234567890123456789")
                    .build()
            )
        )
        .startDate(LocalDate.parse("2024-07-02"))
        .name("Premium Club")
        .description("Premium Club subscription")
        .setupOrder(
            SubscriptionPaymentOrderRequest
                .builder()
                .orderId("OrderRef6543")
                .amount(4999L)
                .description("Initial setup fee for Premium Club subscription")
                .build()
        )
        .recurringOrder(
            SubscriptionRecurringOrderRequest
                .builder()
                .amount(4999L)
                .description("Monthly Premium Club subscription")
                .breakdown(
                    SubscriptionOrderBreakdownRequest
                        .builder()
                        .subtotal(4347L)
                        .taxes(
                            Optional.of(
                                Arrays.asList(
                                    TaxRate
                                        .builder()
                                        .type(TaxRateType.RATE)
                                        .rate(5.0)
                                        .name("Sales Tax")
                                        .build()
                                )
                            )
                        )
                        .build()
                )
                .build()
        )
        .endDate(LocalDate.parse("2025-07-01"))
        .length(12)
        .pauseCollectionFor(0)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>

<dl>
<dd>

**paymentPlanId:** `String` — Unique identifier that the merchant assigned to the payment plan.
    
</dd>
</dl>

<dl>
<dd>

**paymentMethod:** `SubscriptionRequestPaymentMethod` — Polymorphic object that contains information about the secure token.
    
</dd>
</dl>

<dl>
<dd>

**name:** `Optional<String>` 

Name of the subscription. 
This value replaces the name inherited from the payment plan.
    
</dd>
</dl>

<dl>
<dd>

**description:** `Optional<String>` 

Description of the subscription. 
This value replaces the description inherited from the payment plan.
    
</dd>
</dl>

<dl>
<dd>

**setupOrder:** `Optional<SubscriptionPaymentOrderRequest>` 
    
</dd>
</dl>

<dl>
<dd>

**recurringOrder:** `Optional<SubscriptionRecurringOrderRequest>` 
    
</dd>
</dl>

<dl>
<dd>

**startDate:** `String` 

Format: **YYYY-MM-DD**  
Subscription's start date.
    
</dd>
</dl>

<dl>
<dd>

**endDate:** `Optional<String>` 

Format: **YYYY-MM-DD**  
Subscription's end date.  
**Note:** If you provide values for both **length** and **endDate**, 
our gateway uses the value for **endDate** to determine when the subscription should end.
    
</dd>
</dl>

<dl>
<dd>

**length:** `Optional<Integer>` 

Total number of billing cycles. To indicate that the subscription should run indefinitely, send a value of `0`.
This value replaces the **length** inherited from the payment plan.  
**Note:** If you provide values for both **length** and **endDate**, 
our gateway uses the value for **endDate** to determine when the subscription should end.
    
</dd>
</dl>

<dl>
<dd>

**pauseCollectionFor:** `Optional<Integer>` 

Number of billing cycles that the merchant wants to pause payments for. 
For example, if the merchant wants to offer a free trial period.
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.retrieve(processingTerminalId, subscriptionId) -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a subscription.  

To retrieve a subscription, you need its subscriptionId. You sent the subscriptionId in the request of the [Create subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List subscriptions](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/list) method to search for the subscription.  

Our gateway returns information about the following for the subscription:  

-	Payment plan the subscription is linked to.  
-	Secure token that represents cardholder’s payment details.  
-	Current state of the subscription, including its status, next due date, and invoices.  
-	Fees for setup and the cost of the recurring order.  
-	Subscription length, end date, and frequency.  

We also return the paymentPlanId and the secureTokenId, which you can use to perform follow-on actions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().retrieve(
    "1234001",
    "SubRef7654",
    RetrieveSubscriptionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.partiallyUpdate(processingTerminalId, subscriptionId, request) -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update a subscription. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.  

To update a subscription, you need its subscriptionId, which you sent in the request of the [Create subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List subscriptions](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/list) method to search for the payment.  

You can update all of the properties of the subscription except for the following:  

**Can't delete**  
- recurringOrder
- description
- name

**Can't perform any PATCH operation**  
- currentState
- type
- frequency
- paymentPlan
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().partiallyUpdate(
    "1234001",
    "SubRef7654",
    PartiallyUpdateSubscriptionsRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.deactivate(processingTerminalId, subscriptionId) -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to deactivate a subscription.  

To deactivate a subscription, you need its subscriptionId, which you sent in the request of the [Create Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List Subscriptions](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/list) method to search for the subscription.  

If your request is successful, our gateway stops taking payments from the customer.  

To reactivate the subscription, use our [Reactivate Subscription](https://docs.payroc.com/api/schema/payments/subscriptions/reactivate) method.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().deactivate(
    "1234001",
    "SubRef7654",
    DeactivateSubscriptionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.reactivate(processingTerminalId, subscriptionId) -> Subscription</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to reactivate a subscription.  

To reactivate a subscription, you need its subscriptionId, which you sent in the request of the [Create Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List Subscriptions](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/list) method to search for the subscription.  

If your request is successful, our gateway restarts taking payments from the customer.  

To deactivate the subscription, use our [Deactivate Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/deactivate) method.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().reactivate(
    "1234001",
    "SubRef7654",
    ReactivateSubscriptionsRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.repeatPayments.subscriptions.pay(processingTerminalId, subscriptionId, request) -> SubscriptionPayment</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to manually collect a payment linked to a subscription. You can manually collect a payment only if the merchant chose not to let our gateway automatically collect each payment.  

To manually collect a payment, you need the subscriptionId of the subscription that's linked to the payment. You sent the subscriptionId in the request of the [Create Subscription](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/create) method.  

**Note:** If you don't have the subscriptionId, use our [List Subscriptions](https://docs.payroc.com/api/schema/repeat-payments/subscriptions/list) method to search for the subscription.  

The request includes an order object that contains information about the amount that you want to collect.  

In the response, our gateway returns information about the payment and a paymentId. You can use the paymentId in follow-on actions with the [Payments](https://docs.payroc.com/api/schema/card-payments/payments) endpoints or [Bank Transfer Payments](https://docs.payroc.com/api/schema/bank-transfer-payments/payments) endpoints.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.repeatPayments().subscriptions().pay(
    "1234001",
    "SubRef7654",
    SubscriptionPaymentRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .order(
            SubscriptionPaymentOrder
                .builder()
                .orderId("OrderRef6543")
                .amount(4999L)
                .description("Monthly Premium Club subscription")
                .build()
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**subscriptionId:** `String` — Unique identifier that the merchant assigned to the subscription.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**order:** `SubscriptionPaymentOrder` — Object that contains information about the payment.
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Reporting Settlement
<details><summary><code>client.reporting.settlement.listBatches() -> PayrocPager&amp;lt;ListBatchesSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of batches that your merchants submitted to the processor on a specific date.  

**Note:** If you want to view the details of a specific batch and you have its batchId, use our [Retrieve Batch](https://docs.payroc.com/api/schema/reporting/settlement/retrieve-batch) method.  

Use query parameters to filter the list of results that we return, for example, to search for batches that were submitted by a specific merchant.  

> **Important:** You must provide a value for the date query parameter.  

Our gateway returns the following information about each batch in the list:  
-	Transaction information, including the number of transactions and total value of sales.  
-	Merchant information, including the merchant ID (MID) and the processing account that the batch is associated with.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listBatches(
    ListReportingSettlementBatchesRequest
        .builder()
        .date(LocalDate.parse("2027-07-02"))
        .before("2571")
        .after("8516")
        .limit(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `String` — Filter batches by the date that they were submitted. The format of this value is **YYYY-MM-DD**.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.retrieveBatch(batchId) -> Batch</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a batch.  

**Note:** To retrieve a batch, you need its batchId. If you don't have the batchId, use our [List Batches](https://docs.payroc.com/api/schema/reporting/settlement/list-batches) method to search for the batch.  

Our gateway returns the following information about the batch:  

-	Transaction information, including the number of transactions and total value of sales.  
-	Merchant information, including the merchant ID (MID) and the processing account that the batch is associated with.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().retrieveBatch(
    1,
    RetrieveBatchSettlementRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**batchId:** `Integer` — Unique identifier that we assigned to the batch.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listTransactions() -> PayrocPager&amp;lt;ListTransactionsSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a paginated list of your merchants’ transactions.  

**Note:** If you want to view the details of a specific transaction and you have its transactionId, use our [Retrieve Transaction](https://docs.payroc.com/api/schema/reporting/settlement/retrieve-transaction) method.  

Use query parameters to filter the list of results that we return, for example, to search for transactions for a specific merchant.  

> **Important:** You must provide a value for either the date query parameter or the batchId query parameter.  

Our gateway returns the following information about each transaction in the list:  

-	Merchant and processing account that ran the transaction.  
-	Transaction type, date, amount, and the payment method that the customer used.  
-	Batch that contains the transaction, and authorization details for the transaction.  
-	Processor that settled the transaction and the ACH deposit containing the transaction.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listTransactions(
    ListReportingSettlementTransactionsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .date(LocalDate.parse("2024-07-02"))
        .batchId(1)
        .merchantId("4525644354")
        .transactionType(ListTransactionsSettlementRequestTransactionType.CAPTURE)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `Optional<String>` 

Filter transactions by the date that the merchant submitted the batch that contains the transaction. The format of this value is **YYYY-MM-DD**.  

You must provide either the batchId or the date. 
    
</dd>
</dl>

<dl>
<dd>

**batchId:** `Optional<Integer>` 

Filter transactions by the unique identifier of the batch that contains the transaction.  

You must provide either the batchId or the date. 
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>

<dl>
<dd>

**transactionType:** `Optional<ListTransactionsSettlementRequestTransactionType>` — Filter transactions by transaction type.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.retrieveTransaction(transactionId) -> Transaction</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a transaction.  

**Note:** To retrieve a transaction, you need its transactionId. If you don't have the transactionId, use our [List Transactions](https://docs.payroc.com/api/schema/reporting/settlement/list-transactions) method to search for the transaction.  

Our gateway returns the following information about the transaction:  

-	Merchant and processing account that ran the transaction.  
-	Transaction type, date, amount, and the payment method that the customer used.  
-	Batch that contains the transaction, and authorization details for the transaction.   
-	Processor that settled the transaction and the ACH deposit containing the transaction.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().retrieveTransaction(
    1,
    RetrieveTransactionSettlementRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**transactionId:** `Integer` — Unique identifier of the transaction.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listAuthorizations() -> PayrocPager&amp;lt;ListAuthorizationsSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve a [paginated](https://docs.payroc.com/api/pagination) list of authorizations.  

Use query parameters to filter the list of results that we return, for example, to search for authorizations linked to a specific merchant.  

> **Important:** You must provide a value for either the date query parameter or the batchId query parameter.  

Our gateway returns the following information about each authorization in the list:
- Authorization response from the issuing bank.
- Amount that the issuing bank authorized.
- Merchant that ran the authorization.
- Details about the customer's card, the transaction, and the batch.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listAuthorizations(
    ListReportingSettlementAuthorizationsRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .date(LocalDate.parse("2024-07-02"))
        .batchId(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `Optional<String>` 

Filter transactions by the date that the merchant submitted the batch that contains the transaction. The format of this value is **YYYY-MM-DD**.  

You must provide either the batchId or the date. 
    
</dd>
</dl>

<dl>
<dd>

**batchId:** `Optional<Integer>` 

Filter transactions by the unique identifier of the batch that contains the transaction.  

You must provide either the batchId or the date. 
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.retrieveAuthorization(authorizationId) -> Authorization</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about an authorization.  

**Note:** To retrieve an authorization, you need its authorizationId. If you don't have the authorizationId, use our [List Authorizations](https://docs.payroc.com/api/schema/reporting/settlement/list-authorizations) method to search for the authorization.  

Our gateway returns the following information about the authorization:
- Authorization response from the issuing bank.
- Amount that the issuing bank authorized.
- Merchant that ran the authorization.
- Details about the customer's card, the transaction, and the batch.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().retrieveAuthorization(
    1,
    RetrieveAuthorizationSettlementRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**authorizationId:** `Integer` — Unique identifier of the authorization.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listDisputes() -> PayrocPager&amp;lt;ListDisputesSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of disputes.  

Use query parameters to filter the list of results that we return, for example, to search for disputes linked to a specific merchant.  

> **Important:** You must provide a value for the date query parameter.  

Our gateway returns the following information about each dispute in the list:  
- Its status, type, and description.  
- Transaction that the dispute is linked to, including the transaction date, merchant who ran the transaction, and the payment method that the cardholder used.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listDisputes(
    ListReportingSettlementDisputesRequest
        .builder()
        .date(LocalDate.parse("2024-07-02"))
        .before("2571")
        .after("8516")
        .limit(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `String` — Filter results by the date that the dispute was submitted.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listDisputesStatuses(disputeId) -> List&amp;lt;DisputeStatus&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return the status history of a dispute.  

To view the status history of a dispute, you need its disputeId. If you don't have the disputeId, use our [List Disputes](https://docs.payroc.com/api/schema/reporting/settlement/list-disputes) method to search for the dispute. 

Our gateway returns a list that contains each status change, the date it was changed, and its updated status.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listDisputesStatuses(
    1,
    ListDisputesStatusesSettlementRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**disputeId:** `Integer` — Unique identifier of the dispute.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listAchDeposits() -> PayrocPager&amp;lt;ListAchDepositsSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of ACH deposits that we paid to your merchants.  

**Note:** If you want to view the details of a specific ACH deposit and you have its achDepositId, use our [Retrieve ACH Deposit](https://docs.payroc.com/api/schema/reporting/settlement/retrieve-ach-deposit) method.  

Use query parameters to filter the list of results that we return, for example, to search for ACH deposits that we paid to a specific merchant.  

> **Important:** You must provide a value for the date query parameter.  

Our gateway returns the following information about each ACH deposit in the list: 
- Merchant that we sent the ACH deposit to.
- Total amount that we paid the merchant.
- Breakdown of sales, returns, and fees.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listAchDeposits(
    ListReportingSettlementAchDepositsRequest
        .builder()
        .date(LocalDate.parse("2024-07-02"))
        .before("2571")
        .after("8516")
        .limit(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `String` — Filter results by the date that the merchant received the ACH deposit.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.retrieveAchDeposit(achDepositId) -> AchDeposit</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about an ACH deposit that we paid to a merchant.  

**Note:** To retrieve an ACH deposit, you need its achDepositId. If you don't have the achDepositId, use our [List ACH Deposits](https://docs.payroc.com/api/schema/reporting/settlement/list-ach-deposits) method to search for the ACH deposit.  

Our gateway returns the following information about the ACH deposit:  

- Merchant that we sent the ACH deposit to.  
- Total amount that we paid the merchant.  
- Breakdown of sales, returns, and fees.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().retrieveAchDeposit(
    1,
    RetrieveAchDepositSettlementRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**achDepositId:** `Integer` — Unique identifier of the ACH deposit.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.reporting.settlement.listAchDepositFees() -> PayrocPager&amp;lt;ListAchDepositFeesSettlementResponse&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Retrieve a list of ACH deposit fees.

> **Important:** You must provide a value for either the 'date' query parameter or the 'achDepositId' query parameter.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.reporting().settlement().listAchDepositFees(
    ListReportingSettlementAchDepositFeesRequest
        .builder()
        .before("2571")
        .after("8516")
        .limit(1)
        .date(LocalDate.parse("2024-07-02"))
        .achDepositId(1)
        .merchantId("4525644354")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>

<dl>
<dd>

**date:** `Optional<String>` — Date to retrieve results from. You must provide either the 'achDepositId' or the 'date'.
    
</dd>
</dl>

<dl>
<dd>

**achDepositId:** `Optional<Integer>` — Unique identifier of the ACH deposit. You must provide either the 'achDepositId' or the 'date'.
    
</dd>
</dl>

<dl>
<dd>

**merchantId:** `Optional<String>` — Filter results by the unique identifier that the processor assigned to the merchant.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Tokenization SecureTokens
<details><summary><code>client.tokenization.secureTokens.list(processingTerminalId) -> PayrocPager&amp;lt;SecureTokenPaginatedListWithAccountType&amp;gt;</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to return a [paginated](https://docs.payroc.com/api/pagination) list of secure tokens.  

**Note:** If you want to view the details of a specific secure token and you have its secureTokenId, use our [Retrieve Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/retrieve) method.  

Use query parameters to filter the list of results that we return, for example, to search for secure tokens by customer or by the first four digits of a card number.  

Our gateway returns information about the following for each secure token in the list:  

  -	Payment details that the secure token represents.  
  -	Customer details, including shipping and billing addresses.  
  -	Secure token that you can use to carry out transactions.  

  For each secure token, we also return the secureTokenId, which you can use to perform follow-on actions.
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().list(
    "1234001",
    ListSecureTokensRequest
        .builder()
        .secureTokenId("MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa")
        .customerName("Sarah%20Hazel%20Hopper")
        .phone("2025550165")
        .email("sarah.hopper@example.com")
        .token("296753123456")
        .first6("453985")
        .last4("7062")
        .before("2571")
        .after("8516")
        .limit(1)
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `Optional<String>` — Unique identifier that the merchant assigned to the secure token.
    
</dd>
</dl>

<dl>
<dd>

**customerName:** `Optional<String>` — Filter by the customer's name.
    
</dd>
</dl>

<dl>
<dd>

**phone:** `Optional<String>` — Filter by the customer's phone number.
    
</dd>
</dl>

<dl>
<dd>

**email:** `Optional<String>` — Filter by the customer's email address.
    
</dd>
</dl>

<dl>
<dd>

**token:** `Optional<String>` — Filter by the token that the merchant used in a transaction to represent the customer's payment details.
    
</dd>
</dl>

<dl>
<dd>

**first6:** `Optional<String>` — Filter by the first six digits of the card number.
    
</dd>
</dl>

<dl>
<dd>

**last4:** `Optional<String>` — Filter by the last four digits of the card or account number.
    
</dd>
</dl>

<dl>
<dd>

**before:** `Optional<String>` 

Return the previous page of results before the value that you specify.  

You can’t send the before parameter in the same request as the after parameter. 
    
</dd>
</dl>

<dl>
<dd>

**after:** `Optional<String>` 

Return the next page of results after the value that you specify.  

You can’t send the after parameter in the same request as the before parameter. 
    
</dd>
</dl>

<dl>
<dd>

**limit:** `Optional<Integer>` — Limit the maximum number of results that we return for each page.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.tokenization.secureTokens.create(processingTerminalId, request) -> SecureToken</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a secure token that represents a customer's payment details.  

When you create a secure token, you need to generate and provide a secureTokenId that you use to run follow-on actions:  
- [Retrieve Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/retrieve) – View the details of the secure token.  
- [Delete Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/delete) – Delete the secure token.  
- [Update Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/partially-update) – Update the details of the secure token.  
- [Update Account Details](https://docs.payroc.com/api/schema/tokenization/secure-tokens/update-account) – Update the secure token with the details from a single-use token.  

**Note:** If you don't generate a secureTokenId to identify the token, our gateway generates a unique identifier and returns it in the response.  

If the request is successful, our gateway returns a token that the merchant can use in transactions instead of the customer's sensitive payment details, for example, when they [run a sale](https://docs.payroc.com/api/schema/card-payments/payments/create).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().create(
    "1234001",
    TokenizationRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .source(
            TokenizationRequestSource.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .mitAgreement(TokenizationRequestMitAgreement.UNSCHEDULED)
        .customer(
            Customer
                .builder()
                .firstName("Sarah")
                .lastName("Hopper")
                .dateOfBirth(LocalDate.parse("1990-07-15"))
                .referenceNumber("Customer-12")
                .billingAddress(
                    Address
                        .builder()
                        .address1("1 Example Ave.")
                        .city("Chicago")
                        .state("Illinois")
                        .country("US")
                        .postalCode("60056")
                        .address2("Example Address Line 2")
                        .address3("Example Address Line 3")
                        .build()
                )
                .shippingAddress(
                    Shipping
                        .builder()
                        .recipientName("Sarah Hopper")
                        .address(
                            Address
                                .builder()
                                .address1("1 Example Ave.")
                                .city("Chicago")
                                .state("Illinois")
                                .country("US")
                                .postalCode("60056")
                                .address2("Example Address Line 2")
                                .address3("Example Address Line 3")
                                .build()
                        )
                        .build()
                )
                .contactMethods(
                    Optional.of(
                        Arrays.asList(
                            ContactMethod.email(
                                ContactMethodEmail
                                    .builder()
                                    .value("jane.doe@example.com")
                                    .build()
                            )
                        )
                    )
                )
                .notificationLanguage(CustomerNotificationLanguage.EN)
                .build()
        )
        .ipAddress(
            IpAddress
                .builder()
                .type(IpAddressType.IPV_4)
                .value("104.18.24.203")
                .build()
        )
        .customFields(
            Optional.of(
                Arrays.asList(
                    CustomField
                        .builder()
                        .name("yourCustomField")
                        .value("abc123")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `Optional<String>` 

Unique identifier that the merchant created for the secure token that represents the customer's payment details. 
If the merchant doesn't create a secureTokenId, the gateway generates one and returns it in the response.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who saved the customer's payment details.
    
</dd>
</dl>

<dl>
<dd>

**mitAgreement:** `Optional<TokenizationRequestMitAgreement>` 

Indicates how the merchant can use the customer's card details, as agreed by the customer:  

- `unscheduled` - Transactions for a fixed or variable amount that are run at a certain pre-defined event.  
- `recurring` - Transactions for a fixed amount that are run at regular intervals, for example, monthly. Recurring transactions don't have a fixed duration and run until the customer cancels the agreement.  
- `installment` - Transactions for a fixed amount that are run at regular intervals, for example, monthly. Installment transactions have a fixed duration.
    
</dd>
</dl>

<dl>
<dd>

**customer:** `Optional<Customer>` 
    
</dd>
</dl>

<dl>
<dd>

**ipAddress:** `Optional<IpAddress>` 
    
</dd>
</dl>

<dl>
<dd>

**source:** `TokenizationRequestSource` 

Polymorphic object that contains the payment method to tokenize.  

The value of the type parameter determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`pad` - Pre-authorized debit (PAD) details
-	`card` - Payment card details
-	`singleUseToken` - Single-use token details
    
</dd>
</dl>

<dl>
<dd>

**threeDSecure:** `Optional<TokenizationRequestThreeDSecure>` 

Polymorphic object that contains authentication information from 3-D Secure.  

The value of the type parameter determines which variant you should use:  
-	`gatewayThreeDSecure` - Use our gateway to run a 3-D Secure check.
-	`thirdPartyThreeDSecure` - Use a third party to run a 3-D Secure check.
    
</dd>
</dl>

<dl>
<dd>

**customFields:** `Optional<List<CustomField>>` — Array of customField objects.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.tokenization.secureTokens.retrieve(processingTerminalId, secureTokenId) -> SecureTokenWithAccountType</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to retrieve information about a secure token.  

To retrieve a secure token, you need its secureTokenID, which you sent in the request of the [Create Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/create) method.  

**Note:** If you don't have the secureTokenId, use our [List Secure Tokens](https://docs.payroc.com/api/schema/tokenization/secure-tokens/list) method to search for the secure token.  

Our gateway returns the following information about the secure token:  

  -	Payment details that the secure token represents.  
  -	Customer details, including shipping and billing addresses.  
  -	Secure token that you can use to carry out transactions.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().retrieve(
    "1234001",
    "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
    RetrieveSecureTokensRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `String` — Unique identifier that the merchant assigned to the secure token.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.tokenization.secureTokens.delete(processingTerminalId, secureTokenId)</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to delete a secure token and its related payment details from our vault.  

To delete a secure token, you need its secureTokenId, which you sent in the request of the [Create Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/create) method.  

**Note:** If you don’t have the secureTokenId, use our [List Secure Tokens](https://docs.payroc.com/api/schema/tokenization/secure-tokens/list) method to search for the secure token.  

When you delete a secure token, you can’t recover it, and you can’t reuse its identifier for a new token.  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().delete(
    "1234001",
    "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
    DeleteSecureTokensRequest
        .builder()
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `String` — Unique identifier that the merchant assigned to the secure token.
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.tokenization.secureTokens.partiallyUpdate(processingTerminalId, secureTokenId, request) -> SecureToken</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to partially update a secure token. Structure your request to follow the [RFC 6902](https://datatracker.ietf.org/doc/html/rfc6902) standard.  

To update a secure token, you need its secureTokenId, which you sent in the request of the [Create Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/create) method.  

**Note:** If you don't have the secureTokenId, use our [List Secure Tokens](https://docs.payroc.com/api/schema/tokenization/secure-tokens/list) method to search  for the payment.  

You can update all of the properties of the secure token, except the following:  
- processingTerminalId  
- type  
- token  
- status  
- source/Card  
  - type  
  - cardNumber  
  - cardType  
  - currency  
  - debit  
  - surcharging  
- source/ACH account  
  - accountNumber  
  - routingNumber  
- source/PAD account  
  - type  
  - accountNumber  
  - transitNumber  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().partiallyUpdate(
    "1234001",
    "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
    PartiallyUpdateSecureTokensRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            Arrays.asList(
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                ),
                PatchDocument.remove(
                    PatchRemove
                        .builder()
                        .path("path")
                        .build()
                )
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `String` — Unique identifier that the merchant assigned to the secure token.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `List<PatchDocument>` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

<details><summary><code>client.tokenization.secureTokens.updateAccount(processingTerminalId, secureTokenId, request) -> SecureToken</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to update a secure token if you have a single-use token from Hosted Fields.  

**Note:** If you don't have a single-use token, you can update saved payment details with our [Update Secure Token](https://docs.payroc.com/api/resources#updateSecureToken) method. For more information about our two options to update a secure token, go to [Update saved payment details](https://docs.payroc.com/guides/take-payments/update-saved-payment-details).  
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().secureTokens().updateAccount(
    "1234001",
    "MREF_abc1de23-f4a5-6789-bcd0-12e345678901fa",
    UpdateAccountSecureTokensRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .body(
            AccountUpdate.singleUseToken(
                SingleUseTokenAccountUpdate
                    .builder()
                    .token("abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890")
                    .build()
            )
        )
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that we assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**secureTokenId:** `String` — Unique identifier that the merchant assigned to the secure token.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**request:** `AccountUpdate` 
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>

## Tokenization SingleUseTokens
<details><summary><code>client.tokenization.singleUseTokens.create(processingTerminalId, request) -> SingleUseToken</code></summary>
<dl>
<dd>

#### 📝 Description

<dl>
<dd>

<dl>
<dd>

Use this method to create a single-use token that represents a customer’s payment details.  

A single-use token expires after 30 minutes and merchants can use them only once.  

**Note:** To create a reusable permanent token, go to [Create Secure Token](https://docs.payroc.com/api/schema/tokenization/secure-tokens/create).  

In the request, send the customer’s payment details. If the request is successful, our gateway returns a token that you can use in a follow-on action, for example, [run a sale](https://docs.payroc.com/api/schema/card-payments/payments/create).
</dd>
</dl>
</dd>
</dl>

#### 🔌 Usage

<dl>
<dd>

<dl>
<dd>

```java
client.tokenization().singleUseTokens().create(
    "1234001",
    SingleUseTokenRequest
        .builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .channel(SingleUseTokenRequestChannel.WEB)
        .source(
            SingleUseTokenRequestSource.card(
                CardPayload
                    .builder()
                    .cardDetails(
                        CardPayloadCardDetails.raw(
                            RawCardDetails
                                .builder()
                                .device(
                                    Device
                                        .builder()
                                        .model(DeviceModel.BBPOS_CHP)
                                        .serialNumber("1850010868")
                                        .build()
                                )
                                .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                                .build()
                        )
                    )
                    .build()
            )
        )
        .operator("Jane")
        .build()
);
```
</dd>
</dl>
</dd>
</dl>

#### ⚙️ Parameters

<dl>
<dd>

<dl>
<dd>

**processingTerminalId:** `String` — Unique identifier that our gateway assigned to the terminal.
    
</dd>
</dl>

<dl>
<dd>

**idempotencyKey:** `String` — Unique identifier that you generate for each request. You must use the [UUID v4 format](https://www.rfc-editor.org/rfc/rfc4122) for the identifier. For more information about the idempotency key, go to [Idempotency](https://docs.payroc.com/api/idempotency).
    
</dd>
</dl>

<dl>
<dd>

**channel:** `SingleUseTokenRequestChannel` — Channel that the merchant used to receive the payment details.
    
</dd>
</dl>

<dl>
<dd>

**operator:** `Optional<String>` — Operator who initiated the request.
    
</dd>
</dl>

<dl>
<dd>

**source:** `SingleUseTokenRequestSource` 

Polymorphic object that contains the payment method to tokenize.  

The value of the type parameter determines which variant you should use:  
-	`ach` - Automated Clearing House (ACH) details
-	`pad` - Pre-authorized debit (PAD) details
-	`card` - Payment card details
    
</dd>
</dl>
</dd>
</dl>


</dd>
</dl>
</details>
