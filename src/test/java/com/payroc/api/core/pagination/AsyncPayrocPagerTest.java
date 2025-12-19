package com.payroc.api.core.pagination;

import static org.junit.jupiter.api.Assertions.*;

import com.payroc.api.core.ClientOptions;
import com.payroc.api.types.Link;
import com.payroc.api.types.PaymentPaginatedListForRead;
import com.payroc.api.types.RetrievedPayment;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AsyncPayrocPager.
 */
public class AsyncPayrocPagerTest {

    private MockWebServer mockServer;
    private OkHttpClient httpClient;
    private ClientOptions clientOptions;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        clientOptions = ClientOptions.builder().httpClient(httpClient).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testCreateAsyncPagerWithNoLinks() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        CompletableFuture<AsyncPayrocPager<RetrievedPayment>> future =
                AsyncPayrocPager.createAsync(response, clientOptions, null);

        AsyncPayrocPager<RetrievedPayment> pager = future.get(5, TimeUnit.SECONDS);

        assertFalse(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertTrue(pager.getItems().isEmpty());
    }

    @Test
    void testCreateAsyncPagerWithNextLink() throws Exception {
        String nextUrl = mockServer.url("/page2").toString();

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(1.0)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Collections.singletonList(nextLink))
                .build();

        CompletableFuture<AsyncPayrocPager<RetrievedPayment>> future =
                AsyncPayrocPager.createAsync(response, clientOptions, null);

        AsyncPayrocPager<RetrievedPayment> pager = future.get(5, TimeUnit.SECONDS);

        assertTrue(pager.hasNext());
        assertFalse(pager.hasPrevious());
    }

    @Test
    void testCreateAsyncPagerWithBothLinks() throws Exception {
        String nextUrl = mockServer.url("/page3").toString();
        String prevUrl = mockServer.url("/page1").toString();

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();
        Link prevLink =
                Link.builder().rel("previous").method("GET").href(prevUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(1.0)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Arrays.asList(prevLink, nextLink))
                .build();

        CompletableFuture<AsyncPayrocPager<RetrievedPayment>> future =
                AsyncPayrocPager.createAsync(response, clientOptions, null);

        AsyncPayrocPager<RetrievedPayment> pager = future.get(5, TimeUnit.SECONDS);

        assertTrue(pager.hasNext());
        assertTrue(pager.hasPrevious());
    }

    @Test
    void testNextPageAsyncThrowsWhenNoNextPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        CompletableFuture<BiDirectionalPage<RetrievedPayment>> nextFuture = pager.nextPageAsync();

        ExecutionException ex = assertThrows(ExecutionException.class, () -> nextFuture.get(5, TimeUnit.SECONDS));
        assertTrue(ex.getCause() instanceof NoSuchElementException);
    }

    @Test
    void testPreviousPageAsyncThrowsWhenNoPreviousPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        CompletableFuture<BiDirectionalPage<RetrievedPayment>> prevFuture = pager.previousPageAsync();

        ExecutionException ex = assertThrows(ExecutionException.class, () -> prevFuture.get(5, TimeUnit.SECONDS));
        assertTrue(ex.getCause() instanceof NoSuchElementException);
    }

    @Test
    void testFetchNextPageAsync() throws Exception {
        String nextUrl = mockServer.url("/page2").toString();

        // Queue the response for the next page
        mockServer.enqueue(new MockResponse()
                .setBody("{\"limit\":10,\"count\":0,\"hasMore\":false,\"data\":[],\"links\":[]}")
                .addHeader("Content-Type", "application/json"));

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(1.0)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Collections.singletonList(nextLink))
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        assertTrue(pager.hasNext());

        BiDirectionalPage<RetrievedPayment> nextPage = pager.nextPageAsync().get(5, TimeUnit.SECONDS);

        assertFalse(nextPage.hasNext());
    }

    @Test
    void testGetAllItemsAsyncWithSinglePage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        List<RetrievedPayment> allItems = pager.getAllItemsAsync().get(5, TimeUnit.SECONDS);

        assertNotNull(allItems);
        assertTrue(allItems.isEmpty());
    }

    @Test
    void testForEachPageAsyncWithSinglePage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        int[] pageCount = {0};

        pager.forEachPageAsync(items -> {
                    pageCount[0]++;
                    return CompletableFuture.completedFuture(null);
                })
                .get(5, TimeUnit.SECONDS);

        assertEquals(1, pageCount[0]);
    }

    @Test
    void testForEachItemAsyncWithEmptyPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        int[] itemCount = {0};

        pager.forEachItemAsync(item -> itemCount[0]++).get(5, TimeUnit.SECONDS);

        assertEquals(0, itemCount[0]);
    }

    @Test
    void testGetCurrentPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        Page<RetrievedPayment> currentPage = pager.getCurrentPage();

        assertNotNull(currentPage);
        assertTrue(currentPage.isEmpty());
    }

    @Test
    void testCreateAsyncWithNonPaginatedResponseThrows() throws Exception {
        Object invalidResponse = new Object();

        CompletableFuture<AsyncPayrocPager<Object>> future =
                AsyncPayrocPager.createAsync(invalidResponse, clientOptions, null);

        ExecutionException ex = assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
        assertTrue(ex.getCause() instanceof IllegalArgumentException);
    }

    @Test
    void testBlockingNextPageThrowsWhenNoNextPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        assertThrows(NoSuchElementException.class, () -> pager.nextPage());
    }

    @Test
    void testBlockingPreviousPageThrowsWhenNoPreviousPage() throws Exception {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10.0)
                .count(0.0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        AsyncPayrocPager<RetrievedPayment> pager = AsyncPayrocPager.<RetrievedPayment>createAsync(
                        response, clientOptions, null)
                .get(5, TimeUnit.SECONDS);

        assertThrows(NoSuchElementException.class, () -> pager.previousPage());
    }
}
