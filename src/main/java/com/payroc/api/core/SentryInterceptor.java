/**
 * OkHttp interceptor that captures HTTP errors to Sentry.
 */
package com.payroc.api.core;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Interceptor that captures HTTP errors to Sentry for monitoring and debugging.
 *
 * <p>This interceptor:
 * <ul>
 *   <li>Adds breadcrumbs for all HTTP requests</li>
 *   <li>Captures non-2XX responses to Sentry with context</li>
 *   <li>Captures network errors (IOException) to Sentry</li>
 * </ul>
 */
public class SentryInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String method = request.method();
        String url = request.url().toString();

        SentryIntegration.addBreadcrumb(method + " " + sanitizeUrl(url));

        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            captureNetworkError(e, method, url);
            throw e;
        }

        if (!response.isSuccessful()) {
            captureHttpError(response, method, url);
        }

        return response;
    }

    /**
     * Capture a network error (IOException) to Sentry.
     */
    private void captureNetworkError(IOException error, String method, String url) {
        try {
            String clientName = extractClientFromUrl(url);
            SentryIntegration.captureException(error, clientName, method.toLowerCase());
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Capture an HTTP error response to Sentry.
     */
    private void captureHttpError(Response response, String method, String url) {
        try {
            int statusCode = response.code();
            String clientName = extractClientFromUrl(url);

            PayrocApiApiException exception = new PayrocApiApiException(
                    "HTTP " + statusCode + " " + response.message() + " for " + method + " " + sanitizeUrl(url),
                    statusCode,
                    null);

            SentryIntegration.captureException(exception, clientName, method.toLowerCase());
        } catch (Exception e) {
            // Silent fail
        }
    }

    /**
     * Extract a client name from the URL path for context.
     * E.g., "/api/v1/payments/123" -> "PaymentsClient"
     */
    private String extractClientFromUrl(String url) {
        try {
            String sanitizedUrl = sanitizeUrl(url);

            String path = sanitizedUrl.replaceFirst("https?://[^/]+", "");
            String[] segments = path.split("/");

            for (String segment : segments) {
                if (segment.isEmpty() || segment.equals("api") || segment.matches("v\\d+")) {
                    continue;
                }
                return capitalize(segment) + "Client";
            }
        } catch (Exception e) {
            // Silent fail
        }
        return "HttpClient";
    }

    /**
     * Sanitize URL by removing query parameters (may contain sensitive data).
     */
    private String sanitizeUrl(String url) {
        int queryIndex = url.indexOf('?');
        return queryIndex > 0 ? url.substring(0, queryIndex) : url;
    }

    /**
     * Capitalize the first letter of a string.
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
