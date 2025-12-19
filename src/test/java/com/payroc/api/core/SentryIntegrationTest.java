/**
 * Unit tests for SentryIntegration.
 */
package com.payroc.api.core;

import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the SentryIntegration class.
 *
 * <p>These tests verify that sensitive data is properly redacted before being sent to Sentry.
 */
public final class SentryIntegrationTest {

    private static final String REDACTED = "[REDACTED]";

    /**
     * Test that Authorization header is redacted.
     */
    @Test
    public void testAuthorizationHeaderRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer secret-token-12345");
        headers.put("Content-Type", "application/json");
        request.setHeaders(headers);
        event.setRequest(request);

        SentryEvent result = invokeBeforeSend(event);

        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Authorization"));
        Assertions.assertEquals(
                "application/json", result.getRequest().getHeaders().get("Content-Type"));
    }

    /**
     * Test that X-API-Key header is redacted (case variations).
     */
    @Test
    public void testApiKeyHeaderRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", "my-secret-api-key");
        headers.put("x-api-key", "another-secret-key");
        headers.put("API-Key", "yet-another-key");
        headers.put("api-key", "lowercase-key");
        request.setHeaders(headers);
        event.setRequest(request);

        SentryEvent result = invokeBeforeSend(event);

        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("X-API-Key"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("x-api-key"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("API-Key"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("api-key"));
    }

    /**
     * Test that Token and Auth-Token headers are redacted.
     */
    @Test
    public void testTokenHeaderRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        Map<String, String> headers = new HashMap<>();
        headers.put("Token", "secret-token");
        headers.put("token", "another-token");
        headers.put("Auth-Token", "auth-token-value");
        headers.put("auth-token", "lowercase-auth-token");
        headers.put("Access-Token", "access-token-value");
        headers.put("access-token", "lowercase-access-token");
        request.setHeaders(headers);
        event.setRequest(request);

        SentryEvent result = invokeBeforeSend(event);

        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Token"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("token"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Auth-Token"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("auth-token"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Access-Token"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("access-token"));
    }

    /**
     * Test that Secret and Client-Secret headers are redacted.
     */
    @Test
    public void testSecretHeaderRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        Map<String, String> headers = new HashMap<>();
        headers.put("Secret", "my-secret");
        headers.put("secret", "lowercase-secret");
        headers.put("Client-Secret", "client-secret-value");
        headers.put("client-secret", "lowercase-client-secret");
        request.setHeaders(headers);
        event.setRequest(request);

        SentryEvent result = invokeBeforeSend(event);

        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Secret"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("secret"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("Client-Secret"));
        Assertions.assertEquals(REDACTED, result.getRequest().getHeaders().get("client-secret"));
    }

    /**
     * Test that Bearer tokens in exception messages are redacted.
     */
    @Test
    public void testBearerTokenExceptionRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        SentryException exception = new SentryException();
        exception.setValue("Failed with Authorization: Bearer secret-token-12345 in request");
        event.setExceptions(Arrays.asList(exception));

        SentryEvent result = invokeBeforeSend(event);

        String value = result.getExceptions().get(0).getValue();
        Assertions.assertTrue(value.contains("[REDACTED]"));
        Assertions.assertFalse(value.contains("secret-token-12345"));
    }

    /**
     * Test that X-API-Key in exception messages is redacted.
     */
    @Test
    public void testXApiKeyExceptionRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        SentryException exception = new SentryException();
        exception.setValue("Request failed with x-api-key: my-secret-key in headers");
        event.setExceptions(Arrays.asList(exception));

        SentryEvent result = invokeBeforeSend(event);

        String value = result.getExceptions().get(0).getValue();
        Assertions.assertTrue(value.contains("[REDACTED]"));
        Assertions.assertFalse(value.contains("my-secret-key"));
    }

    /**
     * Test that api_key patterns in exception messages are redacted.
     */
    @Test
    public void testApiKeyPatternExceptionRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        SentryException exception = new SentryException();
        exception.setValue("Error with api_key=secret123 and api-key: another-secret");
        event.setExceptions(Arrays.asList(exception));

        SentryEvent result = invokeBeforeSend(event);

        String value = result.getExceptions().get(0).getValue();
        Assertions.assertFalse(value.contains("secret123"));
        Assertions.assertFalse(value.contains("another-secret"));
    }

    /**
     * Test that sensitive keys in extras are redacted.
     */
    @Test
    public void testExtraContextRedaction() throws Exception {
        SentryEvent event = new SentryEvent();
        event.setExtra("api_token", "secret-token-value");
        event.setExtra("user_key", "secret-key-value");
        event.setExtra("client_secret", "secret-value");
        event.setExtra("auth_header", "Bearer xyz");
        event.setExtra("user_password", "password123");
        event.setExtra("safe_field", "this-should-not-be-redacted");

        SentryEvent result = invokeBeforeSend(event);

        Map<String, Object> extras = result.getExtras();
        Assertions.assertEquals(REDACTED, extras.get("api_token"));
        Assertions.assertEquals(REDACTED, extras.get("user_key"));
        Assertions.assertEquals(REDACTED, extras.get("client_secret"));
        Assertions.assertEquals(REDACTED, extras.get("auth_header"));
        Assertions.assertEquals(REDACTED, extras.get("user_password"));
        Assertions.assertEquals("this-should-not-be-redacted", extras.get("safe_field"));
    }

    /**
     * Test that non-sensitive data is preserved.
     */
    @Test
    public void testNonSensitiveDataPreserved() throws Exception {
        SentryEvent event = new SentryEvent();
        Request request = new Request();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("User-Agent", "Payroc-Java-SDK/0.0.435");
        request.setHeaders(headers);
        event.setRequest(request);

        SentryException exception = new SentryException();
        exception.setValue("Connection timeout after 60 seconds");
        event.setExceptions(Arrays.asList(exception));

        event.setExtra("request_id", "abc-123");
        event.setExtra("endpoint", "/api/v1/payments");

        SentryEvent result = invokeBeforeSend(event);

        // Headers preserved
        Assertions.assertEquals(
                "application/json", result.getRequest().getHeaders().get("Content-Type"));
        Assertions.assertEquals(
                "application/json", result.getRequest().getHeaders().get("Accept"));
        Assertions.assertEquals(
                "Payroc-Java-SDK/0.0.435", result.getRequest().getHeaders().get("User-Agent"));

        // Exception message preserved
        Assertions.assertEquals(
                "Connection timeout after 60 seconds",
                result.getExceptions().get(0).getValue());

        // Extras preserved
        Assertions.assertEquals("abc-123", result.getExtras().get("request_id"));
        Assertions.assertEquals("/api/v1/payments", result.getExtras().get("endpoint"));
    }

    /**
     * Test handling of null request.
     */
    @Test
    public void testNullRequestHandling() throws Exception {
        SentryEvent event = new SentryEvent();
        event.setRequest(null);

        SentryEvent result = invokeBeforeSend(event);

        Assertions.assertNull(result.getRequest());
    }

    /**
     * Test handling of null exceptions.
     */
    @Test
    public void testNullExceptionsHandling() throws Exception {
        SentryEvent event = new SentryEvent();
        event.setExceptions(null);

        SentryEvent result = invokeBeforeSend(event);

        // Sentry SDK may return null or empty list - both are valid
        List<SentryException> exceptions = result.getExceptions();
        Assertions.assertTrue(exceptions == null || exceptions.isEmpty());
    }

    /**
     * Test handling of null extras.
     */
    @Test
    public void testNullExtrasHandling() throws Exception {
        SentryEvent event = new SentryEvent();
        // Don't set any extras

        SentryEvent result = invokeBeforeSend(event);

        // Should not throw, extras can be null
        Assertions.assertNotNull(result);
    }

    /**
     * Test that telemetry can be enabled and disabled.
     */
    @Test
    public void testTelemetryToggle() {
        // Store original state
        boolean originalState = SentryIntegration.isTelemetryEnabled();

        try {
            // Test disabling
            SentryIntegration.setTelemetryEnabled(false);
            Assertions.assertFalse(SentryIntegration.isTelemetryEnabled());

            // Test enabling
            SentryIntegration.setTelemetryEnabled(true);
            // Note: isTelemetryEnabled returns telemetryEnabled && initialized
            // So it may still be false if Sentry wasn't initialized
        } finally {
            // Restore original state
            SentryIntegration.setTelemetryEnabled(originalState);
        }
    }

    /**
     * Test that captureException does not throw when telemetry is disabled.
     */
    @Test
    public void testCaptureExceptionWithTelemetryDisabled() {
        boolean originalState = SentryIntegration.isTelemetryEnabled();
        try {
            SentryIntegration.setTelemetryEnabled(false);

            // Should not throw
            SentryIntegration.captureException(new RuntimeException("test"), "TestClient", "testMethod");
            SentryIntegration.captureException(new RuntimeException("test"));
        } finally {
            SentryIntegration.setTelemetryEnabled(originalState);
        }
    }

    /**
     * Test that addBreadcrumb does not throw when telemetry is disabled.
     */
    @Test
    public void testAddBreadcrumbWithTelemetryDisabled() {
        boolean originalState = SentryIntegration.isTelemetryEnabled();
        try {
            SentryIntegration.setTelemetryEnabled(false);

            // Should not throw
            SentryIntegration.addBreadcrumb("test message");
            Map<String, Object> data = new HashMap<>();
            data.put("key", "value");
            SentryIntegration.addBreadcrumb("test message with data", data);
        } finally {
            SentryIntegration.setTelemetryEnabled(originalState);
        }
    }

    /**
     * Test that captureException handles null values gracefully.
     */
    @Test
    public void testCaptureExceptionWithNullValues() {
        // Should not throw with null client/method names
        SentryIntegration.captureException(new RuntimeException("test"), null, null);
        SentryIntegration.captureException(new RuntimeException("test"), "TestClient", null);
        SentryIntegration.captureException(new RuntimeException("test"), null, "testMethod");
    }

    /**
     * Test that addBreadcrumb handles null data gracefully.
     */
    @Test
    public void testAddBreadcrumbWithNullData() {
        // Should not throw with null data
        SentryIntegration.addBreadcrumb("test message", null);
    }

    /**
     * Invoke the private beforeSend method using reflection.
     */
    private SentryEvent invokeBeforeSend(SentryEvent event) throws Exception {
        Method beforeSendMethod =
                SentryIntegration.class.getDeclaredMethod("beforeSend", SentryEvent.class, Hint.class);
        beforeSendMethod.setAccessible(true);
        return (SentryEvent) beforeSendMethod.invoke(null, event, new Hint());
    }
}
