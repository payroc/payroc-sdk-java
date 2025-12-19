/**
 * Sentry integration for Payroc SDK with opt-out mechanism.
 *
 * <p>This integration provides comprehensive error tracking and monitoring capabilities,
 * including exception capture, breadcrumbs, and rich context for debugging.
 */
package com.payroc.api.core;

import io.sentry.Breadcrumb;
import io.sentry.Hint;
import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Sentry integration for error tracking and monitoring.
 *
 * <p>Users can opt-out by setting the PAYROC_DISABLE_SENTRY environment variable to 'true', '1', or 'yes'.
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic exception capture with rich context</li>
 *   <li>Breadcrumb tracking for operation history</li>
 *   <li>Sensitive data redaction (API keys, tokens, etc.)</li>
 *   <li>HTTP status code and response body context</li>
 *   <li>Fingerprinting for error grouping</li>
 * </ul>
 */
public final class SentryIntegration {

    private static volatile boolean initialized = false;
    private static volatile boolean telemetryEnabled = true;

    private static final String DSN =
            "https://e183f1b8f71281a42389b47e1c064931@o4505201678483456.ingest.us.sentry.io/4510516984872960";

    private static final String SDK_NAME = "payroc-java-sdk";
    private static final String SDK_VERSION = "1.0.0";

    private static final Set<String> SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
            "Authorization",
            "authorization",
            "X-API-Key",
            "x-api-key",
            "API-Key",
            "api-key",
            "Token",
            "token",
            "Auth-Token",
            "auth-token",
            "Access-Token",
            "access-token",
            "Client-Secret",
            "client-secret",
            "Secret",
            "secret"));

    private static final List<String> SENSITIVE_EXTRA_KEYWORDS =
            Arrays.asList("token", "key", "secret", "auth", "password");

    private static final Pattern BEARER_PATTERN =
            Pattern.compile("(Authorization['\"]?\\s*:\\s*['\"]?Bearer\\s+)[^\\s'\"]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern X_API_KEY_PATTERN =
            Pattern.compile("(x-api-key['\"]?\\s*:\\s*['\"]?)[^\\s'\"]+", Pattern.CASE_INSENSITIVE);

    private static final Pattern API_KEY_PATTERN =
            Pattern.compile("(api[_-]?key['\"]?\\s*[:=]\\s*['\"]?)[^\\s'\"]+", Pattern.CASE_INSENSITIVE);

    private static final String REDACTED = "[REDACTED]";

    private SentryIntegration() {}

    /**
     * Initialize Sentry for error tracking and monitoring.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        String disableSentry = System.getenv("PAYROC_DISABLE_SENTRY");
        if (disableSentry != null) {
            String lower = disableSentry.toLowerCase();
            if (lower.equals("true") || lower.equals("1") || lower.equals("yes")) {
                return;
            }
        }

        try {
            Sentry.init(options -> {
                options.setDsn(DSN);
                options.setSendDefaultPii(false);
                options.setTracesSampleRate(1.0);
                options.setProfilesSampleRate(1.0);
                options.setRelease(getSDKVersion());
                String environment = System.getenv("PAYROC_ENVIRONMENT");
                options.setEnvironment(environment != null ? environment : "production");
                options.setBeforeSend(SentryIntegration::beforeSend);
            });

            initialized = true;
        } catch (NoClassDefFoundError | ExceptionInInitializerError e) {
            // Sentry not available on classpath
        } catch (Exception e) {
            // Initialization error - don't break SDK
        }
    }

    private static SentryEvent beforeSend(SentryEvent event, Hint hint) {
        // Redact sensitive headers
        Request request = event.getRequest();
        if (request != null && request.getHeaders() != null) {
            Map<String, String> headers = new HashMap<>(request.getHeaders());
            for (String key : SENSITIVE_HEADERS) {
                if (headers.containsKey(key)) {
                    headers.put(key, REDACTED);
                }
            }
            request.setHeaders(headers);
        }

        // Redact sensitive data from exception messages
        List<SentryException> exceptions = event.getExceptions();
        if (exceptions != null) {
            for (SentryException exception : exceptions) {
                String value = exception.getValue();
                if (value != null) {
                    value = BEARER_PATTERN.matcher(value).replaceAll("$1" + REDACTED);
                    value = X_API_KEY_PATTERN.matcher(value).replaceAll("$1" + REDACTED);
                    value = API_KEY_PATTERN.matcher(value).replaceAll("$1" + REDACTED);
                    exception.setValue(value);
                }
            }
        }

        // Redact sensitive data from extra context
        Map<String, Object> extras = event.getExtras();
        if (extras != null) {
            for (String key : new ArrayList<>(extras.keySet())) {
                String lowerKey = key.toLowerCase();
                for (String sensitive : SENSITIVE_EXTRA_KEYWORDS) {
                    if (lowerKey.contains(sensitive)) {
                        extras.put(key, REDACTED);
                        break;
                    }
                }
            }
        }

        return event;
    }

    private static String getSDKVersion() {
        return SDK_NAME + "@" + SDK_VERSION;
    }

    /**
     * Check if telemetry is enabled.
     *
     * @return true if telemetry is enabled
     */
    public static boolean isTelemetryEnabled() {
        return telemetryEnabled && initialized;
    }

    /**
     * Enable or disable telemetry at runtime.
     *
     * @param enabled true to enable, false to disable
     */
    public static void setTelemetryEnabled(boolean enabled) {
        telemetryEnabled = enabled;
    }

    /**
     * Add a breadcrumb to track SDK operations.
     *
     * @param message the breadcrumb message (e.g., "PaymentsClient.create")
     * @param data optional additional data
     */
    public static void addBreadcrumb(String message, Map<String, Object> data) {
        if (!isTelemetryEnabled()) {
            return;
        }

        try {
            Breadcrumb breadcrumb = new Breadcrumb();
            breadcrumb.setCategory("sdk.operation");
            breadcrumb.setMessage(message);
            breadcrumb.setLevel(SentryLevel.INFO);
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    breadcrumb.setData(entry.getKey(), entry.getValue());
                }
            }
            Sentry.addBreadcrumb(breadcrumb);
        } catch (Exception e) {
            // Silent fail - don't break SDK
        }
    }

    /**
     * Add a breadcrumb without additional data.
     *
     * @param message the breadcrumb message
     */
    public static void addBreadcrumb(String message) {
        addBreadcrumb(message, null);
    }

    /**
     * Capture an exception and send it to Sentry with rich context.
     *
     * @param throwable the exception to capture
     * @param clientName the client class name (e.g., "PaymentsClient")
     * @param methodName the method name (e.g., "create")
     */
    public static void captureException(Throwable throwable, String clientName, String methodName) {
        if (!isTelemetryEnabled()) {
            return;
        }

        try {
            Sentry.withScope(scope -> {
                scope.setTag("sdk", SDK_NAME);
                scope.setTag("sdk_version", SDK_VERSION);
                scope.setTag("runtime", "java");
                scope.setTag("runtime_version", System.getProperty("java.version"));

                if (clientName != null) {
                    scope.setTag("client", clientName);
                }
                if (methodName != null) {
                    scope.setTag("method", methodName);
                }

                Integer statusCode = extractStatusCode(throwable);
                if (statusCode != null) {
                    scope.setTag("http_status", statusCode.toString());
                }

                Map<String, Object> errorContext = new HashMap<>();
                errorContext.put("error_type", throwable.getClass().getSimpleName());
                errorContext.put("error_message", throwable.getMessage());
                if (statusCode != null) {
                    errorContext.put("http_status_code", statusCode);
                }
                Object responseBody = extractResponseBody(throwable);
                if (responseBody != null) {
                    errorContext.put("response_body", responseBody);
                }
                scope.setContexts("payroc_error", errorContext);

                Map<String, Object> sdkInfo = new HashMap<>();
                sdkInfo.put("name", SDK_NAME);
                sdkInfo.put("version", SDK_VERSION);
                sdkInfo.put("runtime", "java");
                sdkInfo.put("runtime_version", System.getProperty("java.version"));
                scope.setContexts("sdk_info", sdkInfo);

                List<String> fingerprint = new ArrayList<>();
                fingerprint.add("{{ default }}");
                fingerprint.add(methodName != null ? methodName : "unknown");
                fingerprint.add(statusCode != null ? statusCode.toString() : "no-status");
                scope.setFingerprint(fingerprint);

                if (clientName != null && methodName != null) {
                    scope.setTransaction(clientName + "." + methodName);
                }

                Sentry.captureException(throwable);
            });
        } catch (Exception e) {
            // Silent fail - don't break SDK
        }
    }

    /**
     * Capture an exception without method context.
     *
     * @param throwable the exception to capture
     */
    public static void captureException(Throwable throwable) {
        captureException(throwable, null, null);
    }

    /**
     * Extract HTTP status code from a PayrocApiApiException.
     */
    private static Integer extractStatusCode(Throwable throwable) {
        if (throwable instanceof PayrocApiApiException) {
            return ((PayrocApiApiException) throwable).statusCode();
        }
        return null;
    }

    /**
     * Extract response body from a PayrocApiApiException.
     */
    private static Object extractResponseBody(Throwable throwable) {
        if (throwable instanceof PayrocApiApiException) {
            return ((PayrocApiApiException) throwable).body();
        }
        return null;
    }
}
