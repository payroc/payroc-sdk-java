package com.payroc.api.core.pagination;

import static org.junit.jupiter.api.Assertions.*;

import com.payroc.api.core.ClientOptions;
import com.payroc.api.types.Link;
import com.payroc.api.types.PaymentPaginatedListForRead;
import com.payroc.api.types.RetrievedPayment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for PayrocPager.
 */
public class PayrocPagerTest {

    private MockWebServer mockServer;
    private OkHttpClient httpClient;
    private ClientOptions clientOptions;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        httpClient = new OkHttpClient();
        clientOptions = ClientOptions.builder().httpClient(httpClient).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Test
    void testPageEmpty() {
        Page<String> page = Page.empty();
        assertTrue(page.isEmpty());
        assertEquals(0, page.size());
        assertFalse(page.iterator().hasNext());
    }

    @Test
    void testPageWithItems() {
        List<String> items = Arrays.asList("a", "b", "c");
        Page<String> page = new Page<>(items);

        assertFalse(page.isEmpty());
        assertEquals(3, page.size());
        assertEquals(items, page.getItems());
    }

    @Test
    void testPageIsImmutable() {
        List<String> items = new ArrayList<>(Arrays.asList("a", "b"));
        Page<String> page = new Page<>(items);

        // Modify original list
        items.add("c");

        // Page should not be affected
        assertEquals(2, page.size());
    }

    @Test
    void testPageIteration() {
        List<String> items = Arrays.asList("x", "y", "z");
        Page<String> page = new Page<>(items);

        List<String> collected = new ArrayList<>();
        for (String item : page) {
            collected.add(item);
        }

        assertEquals(items, collected);
    }

    @Test
    void testCreatePagerWithNoLinks() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(2)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertFalse(pager.hasNext());
        assertFalse(pager.hasPrevious());
        assertTrue(pager.getItems().isEmpty());
    }

    @Test
    void testCreatePagerWithNextLink() throws IOException {
        String nextUrl = mockServer.url("/page2").toString();

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(1)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Collections.singletonList(nextLink))
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertTrue(pager.hasNext());
        assertFalse(pager.hasPrevious());
    }

    @Test
    void testCreatePagerWithPreviousLink() throws IOException {
        String prevUrl = mockServer.url("/page1").toString();

        Link prevLink =
                Link.builder().rel("previous").method("GET").href(prevUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(1)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.singletonList(prevLink))
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertFalse(pager.hasNext());
        assertTrue(pager.hasPrevious());
    }

    @Test
    void testCreatePagerWithBothLinks() throws IOException {
        String nextUrl = mockServer.url("/page3").toString();
        String prevUrl = mockServer.url("/page1").toString();

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();
        Link prevLink =
                Link.builder().rel("previous").method("GET").href(prevUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(1)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Arrays.asList(prevLink, nextLink))
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertTrue(pager.hasNext());
        assertTrue(pager.hasPrevious());
    }

    @Test
    void testNextPageThrowsWhenNoNextPage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertThrows(NoSuchElementException.class, () -> pager.nextPage());
    }

    @Test
    void testPreviousPageThrowsWhenNoPreviousPage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertThrows(NoSuchElementException.class, () -> pager.previousPage());
    }

    @Test
    void testFetchNextPage() throws IOException {
        String nextUrl = mockServer.url("/page2").toString();

        // Queue the response for the next page
        mockServer.enqueue(new MockResponse()
                .setBody("{\"limit\":10,\"count\":0,\"hasMore\":false,\"data\":[],\"links\":[]}")
                .addHeader("Content-Type", "application/json"));

        Link nextLink = Link.builder().rel("next").method("GET").href(nextUrl).build();

        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(1)
                .hasMore(true)
                .data(Collections.emptyList())
                .links(Collections.singletonList(nextLink))
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        assertTrue(pager.hasNext());

        pager.nextPage();

        assertFalse(pager.hasNext());
    }

    @Test
    void testIteratorWithSinglePageNoItems() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        Iterator<RetrievedPayment> iter = pager.iterator();
        assertFalse(iter.hasNext());
    }

    @Test
    void testIteratorThrowsNoSuchElementWhenExhausted() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        Iterator<RetrievedPayment> iter = pager.iterator();
        assertThrows(NoSuchElementException.class, () -> iter.next());
    }

    @Test
    void testStreamWithEmptyPage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        List<RetrievedPayment> collected = pager.stream().collect(Collectors.toList());

        assertTrue(collected.isEmpty());
    }

    @Test
    void testPagesIterableWithSinglePage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        int pageCount = 0;
        for (Page<RetrievedPayment> page : pager.pages()) {
            pageCount++;
        }

        // Empty page should still not be returned (pages() skips empty first page)
        assertEquals(0, pageCount);
    }

    @Test
    void testPreviousPagesIterableWithSinglePage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        int pageCount = 0;
        for (Page<RetrievedPayment> page : pager.previousPages()) {
            pageCount++;
        }

        // Empty page should still not be returned
        assertEquals(0, pageCount);
    }

    @Test
    void testGetResponseReturnsOriginalResponse() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        Optional<PaymentPaginatedListForRead> retrieved = pager.getResponse();

        assertTrue(retrieved.isPresent());
        assertEquals(10, retrieved.get().getLimit().orElse(0));
    }

    @Test
    void testGetCurrentPage() throws IOException {
        PaymentPaginatedListForRead response = PaymentPaginatedListForRead.builder()
                .limit(10)
                .count(0)
                .hasMore(false)
                .data(Collections.emptyList())
                .links(Collections.emptyList())
                .build();

        PayrocPager<RetrievedPayment> pager = PayrocPager.create(response, clientOptions, null);

        Page<RetrievedPayment> currentPage = pager.getCurrentPage();

        assertNotNull(currentPage);
        assertTrue(currentPage.isEmpty());
    }

    @Test
    void testCreateWithNonPaginatedResponseThrows() {
        Object invalidResponse = new Object();

        assertThrows(IllegalArgumentException.class, () -> PayrocPager.create(invalidResponse, clientOptions, null));
    }
}
