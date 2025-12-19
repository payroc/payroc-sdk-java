package com.payroc.api.integration.cardpayments.refunds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.payroc.api.integration.GlobalFixture;
import com.payroc.api.integration.TestDataLoader;
import com.payroc.api.resources.cardpayments.refunds.requests.UnreferencedRefund;
import com.payroc.api.types.RetrievedRefund;
import com.payroc.api.types.TransactionResultStatus;
import java.util.UUID;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for Card Payments Refunds Create operations.
 *
 * These tests run against the Payroc UAT environment and require the following
 * environment variables to be set:
 * - PAYROC_API_KEY_PAYMENTS: API key for payments operations
 * - PAYROC_API_KEY_GENERIC: API key for generic operations
 * - TERMINAL_ID_AVS: Terminal ID with AVS enabled
 * - TERMINAL_ID_NO_AVS: Terminal ID without AVS
 */
@Tag("integration")
@Tag("CardPayments.Refunds")
public class CreateTests {

    /**
     * Smoke test for creating an unreferenced refund.
     * Verifies that a refund can be created successfully and returns a Ready status.
     *
     * Note: This test uses a helper method to build the request with proper test values.
     * The JSON test data file serves as a reference for the request structure.
     */
    @Test
    public void smokeTest() {
        // Build the refund request with test values
        UnreferencedRefund refundRequest =
                buildUnreferencedRefundRequest(UUID.randomUUID().toString(), GlobalFixture.TERMINAL_ID_AVS);

        // Execute the refund creation
        RetrievedRefund createdRefund =
                GlobalFixture.PAYMENTS_CLIENT.cardPayments().refunds().createUnreferencedRefund(refundRequest);

        // Verify the response
        assertNotNull(createdRefund, "Created refund should not be null");
        assertNotNull(createdRefund.getTransactionResult(), "Transaction result should not be null");
        assertEquals(
                TransactionResultStatus.READY,
                createdRefund.getTransactionResult().getStatus(),
                "Transaction status should be READY");
    }

    /**
     * Helper method to build an UnreferencedRefund request with test values.
     * This mirrors the structure from UnreferencedRefund.json test data.
     */
    private UnreferencedRefund buildUnreferencedRefundRequest(String idempotencyKey, String terminalId) {
        // Load the base structure from JSON to get proper types
        UnreferencedRefund template = TestDataLoader.load("UnreferencedRefund.json", UnreferencedRefund.class);

        // Build a new request using the staged builder pattern
        return UnreferencedRefund.builder()
                .idempotencyKey(idempotencyKey)
                .channel(template.getChannel())
                .processingTerminalId(terminalId)
                .order(template.getOrder())
                .refundMethod(template.getRefundMethod())
                .build();
    }
}
