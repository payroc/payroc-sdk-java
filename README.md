# Payroc Java Library

The Payroc Java library provides convenient access to the Payroc APIs from Java.

## Contents

- [Payroc Java Library](#payroc-java-library)
  - [Installation](#installation)
  - [Usage](#usage)
    - [API Key](#api-key)
    - [PayrocApiClient](#payrocapiclient)
    - [Advanced Usage with Custom Environment](#advanced-usage-with-custom-environment)
  - [Exception Handling](#exception-handling)
  - [Logging](#logging)
  - [Pagination](#pagination)
    - [Synchronous Iteration](#synchronous-iteration)
    - [Stream API](#stream-api)
    - [Async Iteration](#async-iteration)
    - [Pagination Gotcha](#pagination-gotcha)
  - [Request Parameters](#request-parameters)
  - [Polymorphic Types](#polymorphic-types)
    - [Creating Polymorphic Data](#creating-polymorphic-data)
    - [Handling Polymorphic Data](#handling-polymorphic-data)
  - [Advanced](#advanced)
    - [Custom Client](#custom-client)
    - [Retries](#retries)
    - [Timeouts](#timeouts)
    - [Custom Headers](#custom-headers)
    - [Access Raw Response Data](#access-raw-response-data)
    - [Error Telemetry](#error-telemetry)
      - [Opt-Out](#opt-out)
      - [Privacy](#privacy)
  - [Contributing](#contributing)
  - [References](#references)

## Installation

Add the Payroc SDK to your project:

**Maven:**
```xml
<dependency>
    <groupId>com.payroc</groupId>
    <artifactId>payroc-api</artifactId>
    <version>VERSION</version>
</dependency>
```

**Gradle:**
```gradle
implementation 'com.payroc:payroc-api:VERSION'
```

## Usage

### API Key

You need to provide your API Key to the `PayrocApiClient` builder. In this example we read it from an environment variable named `PAYROC_API_KEY`. In your own code you should consider security and compliance best practices, likely retrieving this value from a secure vault on demand.

### PayrocApiClient

Instantiate and use the client with the following:

```java
import com.payroc.api.PayrocApiClient;

String apiKey = System.getenv("PAYROC_API_KEY");
if (apiKey == null) {
    throw new RuntimeException("Payroc API Key not found");
}

PayrocApiClient client = PayrocApiClient
    .builder()
    .apiKey(apiKey)
    .build();
```

Then you can access the various API endpoints through the `client` object. For example, to create a payment:

```java
import com.payroc.api.resources.cardpayments.payments.requests.PaymentRequest;
import com.payroc.api.resources.cardpayments.payments.types.PaymentRequestChannel;
import com.payroc.api.resources.cardpayments.payments.types.PaymentRequestPaymentMethod;
import com.payroc.api.types.*;

client.cardPayments().payments().create(
    PaymentRequest.builder()
        .idempotencyKey("8e03978e-40d5-43e8-bc93-6894a57f9324")
        .channel(PaymentRequestChannel.WEB)
        .processingTerminalId("1234001")
        .operator("Jane")
        .order(PaymentOrderRequest.builder()
            .orderId("OrderRef6543")
            .description("Large Pepperoni Pizza")
            .amount(4999L)
            .currency(Currency.USD)
            .build())
        .customer(Customer.builder()
            .firstName("Sarah")
            .lastName("Hopper")
            .billingAddress(Address.builder()
                .address1("1 Example Ave.")
                .address2("Example Address Line 2")
                .address3("Example Address Line 3")
                .city("Chicago")
                .state("Illinois")
                .country("US")
                .postalCode("60056")
                .build())
            .shippingAddress(Shipping.builder()
                .recipientName("Sarah Hopper")
                .address(Address.builder()
                    .address1("1 Example Ave.")
                    .address2("Example Address Line 2")
                    .address3("Example Address Line 3")
                    .city("Chicago")
                    .state("Illinois")
                    .country("US")
                    .postalCode("60056")
                    .build())
                .build())
            .build())
        .paymentMethod(PaymentRequestPaymentMethod.card(
            CardPayload.builder()
                .cardDetails(CardPayloadCardDetails.raw(
                    RawCardDetails.builder()
                        .device(Device.builder()
                            .model(DeviceModel.BBPOS_CHP)
                            .serialNumber("PAX123456789")
                            .build())
                        .rawData("A1B2C3D4E5F67890ABCD1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF")
                        .build()))
                .build()))
        .customFields(Arrays.asList(
            CustomField.builder()
                .name("yourCustomField")
                .value("abc123")
                .build()))
        .build()
);
```

### Advanced Usage with Custom Environment

If you wish to use the SDK against a custom URL, such as a mock API server, you can provide a custom environment to the `PayrocApiClient` builder:

```java
import com.payroc.api.PayrocApiClient;
import com.payroc.api.core.Environment;

Environment mockEnvironment = Environment.custom()
    .api("http://localhost:3000")
    .identity("http://localhost:3001")
    .build();

PayrocApiClient client = PayrocApiClient
    .builder()
    .apiKey(apiKey)
    .environment(mockEnvironment)
    .build();
```

## Exception Handling

When the API returns a non-success status code (4xx or 5xx response), a subclass of the following error will be thrown.

```java
import com.payroc.api.core.PayrocApiApiException;

try {
    var response = client.cardPayments().payments().create(...);
} catch (PayrocApiApiException e) {
    System.out.println(e.body());
    System.out.println(e.statusCode());
}
```

Here are the specific exceptions:

- `BadRequestError`
- `UnauthorizedError`
- `ForbiddenError`
- `NotFoundError`
- `NotAcceptableError`
- `ConflictError`
- `ContentTooLargeError`
- `UnsupportedMediaTypeError`
- `InternalServerError`

Catching a specific exception will allow reading the specific data structure, and easier reading of messages. For example:

```java
import com.payroc.api.errors.BadRequestError;
import com.payroc.api.core.PayrocApiApiException;

try {
    var response = client.cardPayments().payments().create(...);
} catch (BadRequestError e) {
    // Specific handling of a specific error, `BadRequestError`, allows reading more detail
    // Detail has the overall message, e.g. "Validation error..."
    System.out.println(e.body().getDetail());
    
    if (e.body().getErrors().isPresent()) {
        for (var error : e.body().getErrors().get()) {
            // These individual messages will list details, e.g. individual validation errors
            System.out.println(error.getMessage());
        }
    }

    System.out.println(e.statusCode());
} catch (PayrocApiApiException e) {
    // Fallback to generic exception
    System.out.println(e.body());
    System.out.println(e.statusCode());
}
```

## Logging

> [!WARNING]  
> Be careful when configuring your logging not to log the headers of outbound HTTP requests, lest you leak an API key or access token.

## Pagination

List endpoints are paginated. The SDK provides multiple approaches to iterate through results.

### Synchronous Iteration

The SDK provides an `Iterable` so that you can simply loop over all items across all pages:

```java
import com.payroc.api.core.pagination.PayrocPager;
import com.payroc.api.types.RetrievedPayment;
import com.payroc.api.resources.cardpayments.payments.requests.ListPaymentsRequest;

// Auto-paginate through all items
PayrocPager<RetrievedPayment> pager = client.cardPayments().payments().list(
    ListPaymentsRequest.builder()
        .processingTerminalId("1234001")
        .build()
);

for (RetrievedPayment payment : pager) {
    System.out.println(payment.getPaymentId());
}
```

### Stream API

You can also use Java 8 Stream API for filtering and processing:

```java
import java.util.stream.Collectors;
import java.util.List;

List<RetrievedPayment> filtered = pager.stream()
    .filter(p -> p.getAmount() > 1000)
    .collect(Collectors.toList());
```

For page-by-page processing:

```java
import com.payroc.api.core.pagination.Page;

for (Page<RetrievedPayment> page : pager.pages()) {
    System.out.println("Processing " + page.size() + " items");
    batchProcess(page.getItems());
}
```

### Async Iteration

For asynchronous operations, use the async pager:

```java
import com.payroc.api.core.pagination.AsyncPayrocPager;
import com.payroc.api.resources.cardpayments.payments.requests.ListPaymentsRequest;
import java.util.concurrent.CompletableFuture;

client.cardPayments().payments().listAsync(
    ListPaymentsRequest.builder()
        .processingTerminalId("1234001")
        .build()
).thenCompose(pagerFuture -> pagerFuture)
 .thenCompose(pager -> pager.getAllItemsAsync())
 .thenAccept(allPayments -> {
     System.out.println("Total payments: " + allPayments.size());
 });
```

### Pagination Gotcha

Beware of iterating the items on a single page and thinking that they are all there are. In the following example, there are only the items from the first page, not all available items:

```java
import java.util.ArrayList;

PayrocPager<RetrievedPayment> pager = client.cardPayments().payments().list(
    ListPaymentsRequest.builder()
        .processingTerminalId("1234001")
        .build()
);

List<String> ids = new ArrayList<>();
for (RetrievedPayment payment : pager.getCurrentPage().getItems()) {
    ids.add(payment.getPaymentId());
}
// This only has items from the first page!
```

This might be helpful when you only want to process the first few results, but to iterate all items, use the `for (RetrievedPayment payment : pager)` approach shown above.

## Request Parameters

Sometimes you need to filter results, for example, retrieving results from a given date. Raw API calls might use query parameters. The SDK equivalent pattern is setting the values in the request object itself.

Examples of setting different query parameters via the request object:

```java
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

ListPaymentsRequest.builder()
    .processingTerminalId("1234001")
    .dateFrom(OffsetDateTime.of(2024, 7, 1, 15, 30, 0, 0, ZoneOffset.UTC))
    .build()
```

```java
ListPaymentsRequest.builder()
    .processingTerminalId("1234001")
    .dateTo(OffsetDateTime.of(2024, 7, 3, 15, 30, 0, 0, ZoneOffset.UTC))
    .build()
```

```java
ListPaymentsRequest.builder()
    .processingTerminalId("1234001")
    .after("8516")
    .build()
```

```java
ListPaymentsRequest.builder()
    .processingTerminalId("1234001")
    .before("2571")
    .build()
```

Inspect the code definition of your particular `...Request` class in your IDE to see what properties can be used for filtering.

## Polymorphic Types

Our API makes frequent use of polymorphic data structures. This is when a value might be one of multiple types, and the type is determined at runtime. For example, a contact method can be one of several methods, such as `Email` or `Fax`. The SDK provides static factory methods on polymorphic types to help you create and work with these values.

### Creating Polymorphic Data

When dealing with polymorphic types in Java, the SDK provides static factory methods to create instances of specific variants. Each polymorphic type has factory methods named after the variant types.

For example, the `ContactMethod` type has static methods like `ContactMethod.email()`, `ContactMethod.fax()`, etc. to create specific contact method types:

```java
import com.payroc.api.types.ContactMethod;
import com.payroc.api.types.ContactMethodEmail;
import com.payroc.api.types.ContactMethodFax;

// Create an email contact method
ContactMethod emailContact = ContactMethod.email(
    ContactMethodEmail.builder()
        .value("jane.doe@example.com")
        .build()
);

// Create a fax contact method
ContactMethod faxContact = ContactMethod.fax(
    ContactMethodFax.builder()
        .value("2025550110")
        .build()
);
```

This pattern is used throughout the SDK. You can see it in the payment creation example above, where `PaymentRequestPaymentMethod.card()` and `CardPayloadCardDetails.raw()` are used to create polymorphic payment method and card details objects.

### Handling Polymorphic Data

When working with polymorphic types returned from API responses, the SDK provides several approaches to handle different variants:

```java
import com.payroc.api.resources.boarding.owners.requests.RetrieveOwnersRequest;
import com.payroc.api.types.ContactMethod;

var owners = client.boarding().owners().retrieve(
    RetrieveOwnersRequest.builder()
        .ownerId(4564)
        .build()
);

for (ContactMethod contactMethod : owners.getContactMethods()) {
    // Access common properties available on all variants
    System.out.println("Contact Method: " + contactMethod.getType() + " - " + contactMethod.getValue());
    
    // Check the variant type and cast to access variant-specific properties
    if (contactMethod.getEmail().isPresent()) {
        var email = contactMethod.getEmail().get();
        // Access email-specific properties if any exist
        System.out.println("Email: " + email.getValue());
    }
    
    // Use the visitor pattern to handle different types with specific logic
    contactMethod.visit(new ContactMethod.Visitor<Void>() {
        @Override
        public Void visitEmail(ContactMethodEmail email) {
            someService.sendWelcomeEmail(email.getValue());
            return null;
        }
        
        @Override
        public Void visitPhone(ContactMethodPhone phone) {
            // Handle phone contact
            return null;
        }
        
        @Override
        public Void visitMobile(ContactMethodMobile mobile) {
            // Handle mobile contact
            return null;
        }
        
        @Override
        public Void visitFax(ContactMethodFax fax) {
            someService.sendWelcomeFax(fax.getValue());
            return null;
        }
        
        @Override
        public Void visitUnknown(String unknownType) {
            System.out.println("Unknown contact method type: " + unknownType);
            return null;
        }
    });
}
```

The visitor pattern is particularly useful when you need to perform different actions based on the variant type, ensuring type safety and compile-time checking that all variants are handled.

## Advanced

### Custom Client

This SDK is built to work with any instance of `OkHttpClient`. By default, if no client is provided, the SDK will construct one.
However, you can pass your own client like so:

```java
import com.payroc.api.PayrocApiClient;
import okhttp3.OkHttpClient;

OkHttpClient customClient = ...;

PayrocApiClient client = PayrocApiClient
    .builder()
    .httpClient(customClient)
    .build();
```

### Retries

The SDK is instrumented with automatic retries with exponential backoff. A request will be retried as long
as the request is deemed retryable and the number of retry attempts has not grown larger than the configured
retry limit (default: 2). Before defaulting to exponential backoff, the SDK will first attempt to respect
the `Retry-After` header (as either in seconds or as an HTTP date), and then the `X-RateLimit-Reset` header
(as a Unix timestamp in epoch seconds); failing both of those, it will fall back to exponential backoff.

A request is deemed retryable when any of the following HTTP status codes is returned:

- [408](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/408) (Timeout)
- [429](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/429) (Too Many Requests)
- [5XX](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/500) (Internal Server Errors)

Use the `maxRetries` client option to configure this behavior.

```java
import com.payroc.api.PayrocApiClient;

PayrocApiClient client = PayrocApiClient
    .builder()
    .maxRetries(1)
    .build();
```

### Timeouts

The SDK defaults to a 60 second timeout. You can configure this with a timeout option at the client or request level.

```java
import com.payroc.api.PayrocApiClient;
import com.payroc.api.core.RequestOptions;

// Client level
PayrocApiClient client = PayrocApiClient
    .builder()
    .timeout(10)
    .build();

// Request level
client.paymentLinks().create(
    ...,
    RequestOptions
        .builder()
        .timeout(10)
        .build()
);
```

### Custom Headers

The SDK allows you to add custom headers to requests. You can configure headers at the client level or at the request level.

```java
import com.payroc.api.PayrocApiClient;
import com.payroc.api.core.RequestOptions;

// Client level
PayrocApiClient client = PayrocApiClient
    .builder()
    .addHeader("X-Custom-Header", "custom-value")
    .addHeader("X-Request-Id", "abc-123")
    .build();
;

// Request level
client.paymentLinks().create(
    ...,
    RequestOptions
        .builder()
        .addHeader("X-Request-Header", "request-value")
        .build()
);
```

### Access Raw Response Data

The SDK provides access to raw response data, including headers, through the `withRawResponse()` method.
The `withRawResponse()` method returns a raw client that wraps all responses with `body()` and `headers()` methods.
(A normal client's `response` is identical to a raw client's `response.body()`.)

```java
CreateHttpResponse response = client.paymentLinks().withRawResponse().create(...);

System.out.println(response.body());
System.out.println(response.headers().get("X-My-Header"));
```

### Error Telemetry

The SDK automatically reports anonymous errors to help improve the SDK quality. This is enabled by default but can be disabled.

#### Opt-Out

To disable error telemetry, set the `PAYROC_DISABLE_SENTRY` environment variable:

```bash
export PAYROC_DISABLE_SENTRY=true
```

Or programmatically:

```java
import com.payroc.api.core.SentryIntegration;

SentryIntegration.setTelemetryEnabled(false);
```

#### Privacy

All sensitive data (API keys, tokens, passwords, PII) is automatically scrubbed before transmission.

## Contributing

While we value open-source contributions to this SDK, this library is generated programmatically.
Additions made directly to this library would have to be moved over to our generation code,
otherwise they would be overwritten upon the next generated release. Feel free to open a PR as
a proof of concept, but know that we will not be able to merge it as-is. We suggest opening
an issue first to discuss with us!

On the other hand, contributions to the README are always very welcome!

**For details on setting up your development environment, running tests, and code quality standards, please see [CONTRIBUTING.md](./CONTRIBUTING.md).** This guide includes comprehensive instructions for executing tests, including prerequisites, common commands, and troubleshooting tips.

## References

A full reference for this library is available [here](./reference.md).

The Payroc Java SDK is generated via [Fern](https://www.buildwithfern.com/).

[![fern shield](https://img.shields.io/badge/%F0%9F%8C%BF-Built%20with%20Fern-brightgreen)](https://buildwithfern.com?utm_source=github&utm_medium=github&utm_campaign=readme&utm_source=https%3A%2F%2Fgithub.com%2Fpayroc%2Fpayroc-sdk-java)