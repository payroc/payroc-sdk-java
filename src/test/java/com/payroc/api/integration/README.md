# Integration Tests

This directory contains integration tests that run against the Payroc UAT environment.

## Prerequisites

Before running integration tests, you must set the following environment variables:

- `PAYROC_API_KEY_PAYMENTS`: API key for payments operations
- `PAYROC_API_KEY_GENERIC`: API key for generic operations
- `TERMINAL_ID_AVS`: Terminal ID with AVS (Address Verification Service) enabled
- `TERMINAL_ID_NO_AVS`: Terminal ID without AVS

## Running Integration Tests

### Run all integration tests:
```bash
./gradlew test --tests "com.payroc.api.integration.*"
```

### Run specific integration test category:
```bash
# Card Payments Refunds tests
./gradlew test --tests "com.payroc.api.integration.cardpayments.refunds.*"
```

### Run a specific test:
```bash
./gradlew test --tests "com.payroc.api.integration.cardpayments.refunds.CreateTests.smokeTest"
```

### Run tests by tag:
```bash
# Run all integration tests
./gradlew test --tests "*" --tests "*.integration.*"

# Run specific category
./gradlew test --tests "*" --tests "*.CardPayments.Refunds.*"
```

## Test Structure

Integration tests follow this structure:

- **GlobalFixture**: Provides configured API clients and test configuration
- **TestDataLoader**: Utility for loading test data from JSON files
- **Test Data**: JSON files in `src/test/resources/testdata/` containing request templates
- **Test Classes**: Organized by API resource (e.g., `cardpayments/refunds/CreateTests.java`)

## Environment Configuration

The tests are configured to run against the UAT environment by default. This is set in `GlobalFixture.java`:

```java
private static PayrocApiClient createClient(String apiKey) {
    return PayrocApiClient.builder()
            .apiKey(apiKey)
            .environment(Environment.UAT)
            .build();
}
```

## Adding New Integration Tests

1. Create test data JSON file in `src/test/resources/testdata/`
2. Create test class in appropriate package under `com.payroc.api.integration`
3. Use `@Tag("integration")` and resource-specific tags (e.g., `@Tag("CardPayments.Refunds")`)
4. Load test data using `TestDataLoader.load()`
5. Use `GlobalFixture` for API clients and configuration

## Fern Ignore

This entire integration test directory is protected from Fern SDK regeneration via `.fernignore`:

```
# Integration tests
src/test/java/com/payroc/api/integration/
src/test/resources/testdata/
```

This ensures custom integration tests persist across SDK updates.
