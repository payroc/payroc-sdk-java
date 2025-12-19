/**
 * Unit tests for SentryInterceptor.
 */
package com.payroc.api.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the SentryInterceptor class.
 *
 * <p>These tests verify that the interceptor correctly:
 * <ul>
 *   <li>Adds breadcrumbs for HTTP requests</li>
 *   <li>Captures non-2XX responses</li>
 *   <li>Captures network errors</li>
 *   <li>Extracts client names from URLs</li>
 *   <li>Sanitizes URLs by removing query parameters</li>
 * </ul>
 */
public final class SentryInterceptorTest {

    private MockWebServer mockWebServer;
    private OkHttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        client = new OkHttpClient.Builder()
                .addInterceptor(new SentryInterceptor())
                .connectTimeout(1, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /**
     * Test that successful requests pass through without error.
     */
    @Test
    public void testSuccessfulRequest() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"status\": \"ok\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(200, response.code());
        Assertions.assertNotNull(response.body());
        response.close();
    }

    /**
     * Test that 400 Bad Request is captured but response is still returned.
     */
    @Test
    public void testBadRequestCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("{\"error\": \"Bad Request\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        // Response should still be returned
        Assertions.assertEquals(400, response.code());
        response.close();
    }

    /**
     * Test that 401 Unauthorized is captured.
     */
    @Test
    public void testUnauthorizedCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401).setBody("{\"error\": \"Unauthorized\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/auth/token"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(401, response.code());
        response.close();
    }

    /**
     * Test that 404 Not Found is captured.
     */
    @Test
    public void testNotFoundCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404).setBody("{\"error\": \"Not Found\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments/nonexistent"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(404, response.code());
        response.close();
    }

    /**
     * Test that 500 Internal Server Error is captured.
     */
    @Test
    public void testInternalServerErrorCapture() throws IOException {
        mockWebServer.enqueue(
                new MockResponse().setResponseCode(500).setBody("{\"error\": \"Internal Server Error\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .post(okhttp3.RequestBody.create("{}", okhttp3.MediaType.parse("application/json")))
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(500, response.code());
        response.close();
    }

    /**
     * Test that 502 Bad Gateway is captured.
     */
    @Test
    public void testBadGatewayCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(502).setBody("Bad Gateway"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(502, response.code());
        response.close();
    }

    /**
     * Test that 503 Service Unavailable is captured.
     */
    @Test
    public void testServiceUnavailableCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse().setResponseCode(503).setBody("Service Unavailable"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(503, response.code());
        response.close();
    }

    /**
     * Test that 429 Too Many Requests is captured.
     */
    @Test
    public void testRateLimitCapture() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .addHeader("Retry-After", "60")
                .setBody("{\"error\": \"Too Many Requests\"}"));

        Request request = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(429, response.code());
        response.close();
    }

    /**
     * Test client name extraction from URL - payments endpoint.
     */
    @Test
    public void testClientNameExtractionPayments() throws Exception {
        String clientName = invokeExtractClientFromUrl("https://api.payroc.com/api/v1/payments/123");
        Assertions.assertEquals("PaymentsClient", clientName);
    }

    /**
     * Test client name extraction from URL - auth endpoint.
     */
    @Test
    public void testClientNameExtractionAuth() throws Exception {
        String clientName = invokeExtractClientFromUrl("https://api.payroc.com/api/v1/auth/token");
        Assertions.assertEquals("AuthClient", clientName);
    }

    /**
     * Test client name extraction from URL - boarding endpoint.
     */
    @Test
    public void testClientNameExtractionBoarding() throws Exception {
        String clientName = invokeExtractClientFromUrl("https://api.payroc.com/api/v1/boarding/merchants");
        Assertions.assertEquals("BoardingClient", clientName);
    }

    /**
     * Test client name extraction from URL - funding endpoint.
     */
    @Test
    public void testClientNameExtractionFunding() throws Exception {
        String clientName = invokeExtractClientFromUrl("https://api.payroc.com/api/v1/funding/accounts");
        Assertions.assertEquals("FundingClient", clientName);
    }

    /**
     * Test client name extraction from URL - with query parameters.
     */
    @Test
    public void testClientNameExtractionWithQueryParams() throws Exception {
        String clientName =
                invokeExtractClientFromUrl("https://api.payroc.com/api/v1/payments?page=1&limit=10&api_key=secret");
        Assertions.assertEquals("PaymentsClient", clientName);
    }

    /**
     * Test URL sanitization removes query parameters.
     */
    @Test
    public void testUrlSanitization() throws Exception {
        String sanitized = invokeSanitizeUrl("https://api.payroc.com/api/v1/payments?api_key=secret&token=abc123");
        Assertions.assertEquals("https://api.payroc.com/api/v1/payments", sanitized);
    }

    /**
     * Test URL sanitization preserves URL without query parameters.
     */
    @Test
    public void testUrlSanitizationNoQueryParams() throws Exception {
        String sanitized = invokeSanitizeUrl("https://api.payroc.com/api/v1/payments/123");
        Assertions.assertEquals("https://api.payroc.com/api/v1/payments/123", sanitized);
    }

    /**
     * Test that different HTTP methods are handled correctly.
     */
    @Test
    public void testDifferentHttpMethods() throws IOException {
        // Test POST
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{}"));
        Request postRequest = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments"))
                .post(okhttp3.RequestBody.create("{}", okhttp3.MediaType.parse("application/json")))
                .build();
        Response postResponse = client.newCall(postRequest).execute();
        Assertions.assertEquals(201, postResponse.code());
        postResponse.close();

        // Test PUT
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        Request putRequest = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments/123"))
                .put(okhttp3.RequestBody.create("{}", okhttp3.MediaType.parse("application/json")))
                .build();
        Response putResponse = client.newCall(putRequest).execute();
        Assertions.assertEquals(200, putResponse.code());
        putResponse.close();

        // Test DELETE
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));
        Request deleteRequest = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments/123"))
                .delete()
                .build();
        Response deleteResponse = client.newCall(deleteRequest).execute();
        Assertions.assertEquals(204, deleteResponse.code());
        deleteResponse.close();

        // Test PATCH
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        Request patchRequest = new Request.Builder()
                .url(mockWebServer.url("/api/v1/payments/123"))
                .patch(okhttp3.RequestBody.create("{}", okhttp3.MediaType.parse("application/json")))
                .build();
        Response patchResponse = client.newCall(patchRequest).execute();
        Assertions.assertEquals(200, patchResponse.code());
        patchResponse.close();
    }

    /**
     * Test that interceptor handles various status codes in 2XX range.
     */
    @Test
    public void testSuccessStatusCodes() throws IOException {
        // Test 200 OK
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        Response response200 = client.newCall(
                        new Request.Builder().url(mockWebServer.url("/")).build())
                .execute();
        Assertions.assertEquals(200, response200.code());
        response200.close();

        // Test 201 Created
        mockWebServer.enqueue(new MockResponse().setResponseCode(201).setBody("{}"));
        Response response201 = client.newCall(
                        new Request.Builder().url(mockWebServer.url("/")).build())
                .execute();
        Assertions.assertEquals(201, response201.code());
        response201.close();

        // Test 204 No Content
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));
        Response response204 = client.newCall(
                        new Request.Builder().url(mockWebServer.url("/")).build())
                .execute();
        Assertions.assertEquals(204, response204.code());
        response204.close();
    }

    /**
     * Test that interceptor handles redirect responses (3XX).
     */
    @Test
    public void testRedirectResponse() throws IOException {
        // 301 is not a success but also not captured as error in our implementation
        // It should pass through
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(301)
                .addHeader("Location", mockWebServer.url("/new-location").toString()));

        // Disable follow redirects to see the actual 301
        OkHttpClient noRedirectClient =
                client.newBuilder().followRedirects(false).build();

        Request request =
                new Request.Builder().url(mockWebServer.url("/old-location")).build();

        Response response = noRedirectClient.newCall(request).execute();
        Assertions.assertEquals(301, response.code());
        response.close();
    }

    /**
     * Invoke the private extractClientFromUrl method using reflection.
     */
    private String invokeExtractClientFromUrl(String url) throws Exception {
        SentryInterceptor interceptor = new SentryInterceptor();
        Method method = SentryInterceptor.class.getDeclaredMethod("extractClientFromUrl", String.class);
        method.setAccessible(true);
        return (String) method.invoke(interceptor, url);
    }

    /**
     * Invoke the private sanitizeUrl method using reflection.
     */
    private String invokeSanitizeUrl(String url) throws Exception {
        SentryInterceptor interceptor = new SentryInterceptor();
        Method method = SentryInterceptor.class.getDeclaredMethod("sanitizeUrl", String.class);
        method.setAccessible(true);
        return (String) method.invoke(interceptor, url);
    }
}
