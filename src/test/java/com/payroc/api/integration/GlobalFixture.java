package com.payroc.api.integration;

import com.payroc.api.PayrocApiClient;
import com.payroc.api.core.Environment;

/**
 * Global fixture for integration tests.
 * Provides configured clients and test configuration loaded from environment variables.
 */
public class GlobalFixture {
    public static final PayrocApiClient PAYMENTS_CLIENT;
    public static final PayrocApiClient GENERIC_CLIENT;
    public static final String TERMINAL_ID_AVS;
    public static final String TERMINAL_ID_NO_AVS;

    static {
        GENERIC_CLIENT = createClient(getEnvWithFallback("PAYROC_API_KEY_GENERIC", "PAYROC_API_KEY"));
        PAYMENTS_CLIENT = createClient(getEnvWithFallback("PAYROC_API_KEY_PAYMENTS", "PAYROC_API_KEY"));
        TERMINAL_ID_AVS = getEnv("TERMINAL_ID_AVS");
        TERMINAL_ID_NO_AVS = getEnv("TERMINAL_ID_NO_AVS");
    }

    private static PayrocApiClient createClient(String apiKey) {
        Environment environment = getCustomEnvironment();
        return PayrocApiClient.builder()
                .apiKey(apiKey)
                .environment(environment)
                .build();
    }

    private static Environment getCustomEnvironment() {
        String apiBaseUrl = System.getenv("PAYROC_API_BASE_URL");
        String identityBaseUrl = System.getenv("PAYROC_IDENTITY_BASE_URL");

        // If custom URLs are provided, use them
        if (apiBaseUrl != null && !apiBaseUrl.isEmpty() && identityBaseUrl != null && !identityBaseUrl.isEmpty()) {
            return Environment.custom()
                    .api(apiBaseUrl)
                    .identity(identityBaseUrl)
                    .build();
        }

        // Otherwise, fall back to UAT
        return Environment.UAT;
    }

    private static String getEnvWithFallback(String name, String fallbackName) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            value = System.getenv(fallbackName);
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable '" + name + "' or '" + fallbackName + "' is not set. "
                    + "Please set one of them before running integration tests.");
        }
        return value;
    }

    private static String getEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Environment variable '" + name + "' is not set. "
                    + "Please set it before running integration tests.");
        }
        return value;
    }
}
