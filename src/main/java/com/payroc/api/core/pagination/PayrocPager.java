package com.payroc.api.core.pagination;

import com.fasterxml.jackson.databind.JsonNode;
import com.payroc.api.core.ClientOptions;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.core.RequestOptions;
import com.payroc.api.types.IPaginatedList;
import com.payroc.api.types.Link;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A paginator for Payroc API responses that supports bidirectional navigation,
 * automatic iteration across pages, and Java 8 Stream API.
 *
 * <p>This pager uses HATEOAS-style links from API responses to navigate between pages.
 * It extracts "next" and "previous" links from the response's links array.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Simple iteration (auto-paginates through all items):</h3>
 * <pre>{@code
 * PayrocPager<Payment> pager = client.payments().list();
 * for (Payment payment : pager) {
 *     System.out.println(payment.getId());
 * }
 * }</pre>
 *
 * <h3>Stream API:</h3>
 * <pre>{@code
 * List<Payment> completed = client.payments().list()
 *     .stream()
 *     .filter(p -> "completed".equals(p.getStatus()))
 *     .collect(Collectors.toList());
 * }</pre>
 *
 * <h3>Page-by-page iteration:</h3>
 * <pre>{@code
 * PayrocPager<Payment> pager = client.payments().list();
 * for (Page<Payment> page : pager.pages()) {
 *     System.out.println("Processing " + page.size() + " items");
 *     batchProcess(page.getItems());
 * }
 * }</pre>
 *
 * <h3>Manual navigation:</h3>
 * <pre>{@code
 * PayrocPager<Payment> pager = client.payments().list();
 * while (pager.hasNext()) {
 *     pager.nextPage();
 *     process(pager.getItems());
 * }
 * }</pre>
 *
 * @param <T> The type of items in each page
 */
public class PayrocPager<T> implements BiDirectionalPage<T>, Iterable<T> {

    private final ClientOptions clientOptions;
    private final Function<String, List<T>> itemsParser;
    private final RequestOptions requestOptions;

    private Page<T> currentPage;
    private String nextUrl;
    private String previousUrl;
    private Object fullResponse;

    /**
     * Private constructor - use the static create() method.
     */
    private PayrocPager(
            ClientOptions clientOptions,
            Function<String, List<T>> itemsParser,
            RequestOptions requestOptions,
            Page<T> currentPage,
            String nextUrl,
            String previousUrl,
            Object fullResponse) {
        this.clientOptions = clientOptions;
        this.itemsParser = itemsParser;
        this.requestOptions = requestOptions;
        this.currentPage = currentPage;
        this.nextUrl = nextUrl;
        this.previousUrl = previousUrl;
        this.fullResponse = fullResponse;
    }

    /**
     * Creates a PayrocPager from an initial API response.
     *
     * <p>This method is called by the generated client code when a paginated
     * endpoint returns its first page of results.
     *
     * @param <T> The type of items in the paginated list
     * @param <R> The response type, which must implement IPaginatedList
     * @param initialResponse The first page response from the API
     * @param httpClient The OkHttpClient to use for fetching additional pages
     * @param requestOptions Request options containing headers, timeout, etc.
     * @param dataExtractor Function to extract the list of items from the response
     * @param responseClass The class of the response for deserialization
     * @return A new PayrocPager instance
     * @throws IOException if parsing the response fails
     */
    public static <T, R extends IPaginatedList> PayrocPager<T> create(
            R initialResponse,
            ClientOptions clientOptions,
            RequestOptions requestOptions,
            Function<R, List<T>> dataExtractor,
            Class<R> responseClass)
            throws IOException {

        List<T> items = dataExtractor.apply(initialResponse);
        if (items == null) {
            items = Collections.emptyList();
        }

        String nextUrl = null;
        String previousUrl = null;
        Optional<List<Link>> linksOpt = initialResponse.getLinks();
        if (linksOpt.isPresent()) {
            for (Link link : linksOpt.get()) {
                if ("next".equals(link.getRel())) {
                    nextUrl = link.getHref();
                } else if ("previous".equals(link.getRel())) {
                    previousUrl = link.getHref();
                }
            }
        }

        Function<String, List<T>> itemsParser = (responseBody) -> {
            try {
                R parsed = ObjectMappers.JSON_MAPPER.readValue(responseBody, responseClass);
                List<T> parsedItems = dataExtractor.apply(parsed);
                return parsedItems != null ? parsedItems : Collections.emptyList();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to parse paginated response", e);
            }
        };

        return new PayrocPager<>(
                clientOptions, itemsParser, requestOptions, new Page<>(items), nextUrl, previousUrl, initialResponse);
    }

    /**
     * Creates a PayrocPager from an initial API response.
     * This is the primary method called by generated client code.
     *
     * @param <T> The type of items (inferred)
     * @param initialResponse The initial paginated response
     * @param httpClient The HTTP client
     * @param requestOptions Request options
     * @return A new PayrocPager
     * @throws IOException if response parsing fails
     */
    @SuppressWarnings("unchecked")
    public static <T> PayrocPager<T> create(
            Object initialResponse, ClientOptions clientOptions, RequestOptions requestOptions) throws IOException {

        if (!(initialResponse instanceof IPaginatedList)) {
            throw new IllegalArgumentException("Response must implement IPaginatedList, got: "
                    + initialResponse.getClass().getName());
        }

        IPaginatedList paginatedResponse = (IPaginatedList) initialResponse;
        List<T> items = extractDataField(initialResponse);

        String nextUrl = null;
        String previousUrl = null;
        Optional<List<Link>> linksOpt = paginatedResponse.getLinks();
        if (linksOpt.isPresent()) {
            for (Link link : linksOpt.get()) {
                if ("next".equals(link.getRel())) {
                    nextUrl = link.getHref();
                } else if ("previous".equals(link.getRel())) {
                    previousUrl = link.getHref();
                }
            }
        }

        Class<?> responseClass = initialResponse.getClass();
        Function<String, List<T>> itemsParser = (responseBody) -> {
            try {
                Object parsed = ObjectMappers.JSON_MAPPER.readValue(responseBody, responseClass);
                return extractDataField(parsed);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to parse paginated response", e);
            }
        };

        return new PayrocPager<>(
                clientOptions, itemsParser, requestOptions, new Page<>(items), nextUrl, previousUrl, initialResponse);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> extractDataField(Object response) {
        try {
            java.lang.reflect.Method getDataMethod = response.getClass().getMethod("getData");
            Object result = getDataMethod.invoke(response);

            if (result instanceof Optional) {
                Optional<?> optResult = (Optional<?>) result;
                if (optResult.isPresent() && optResult.get() instanceof List) {
                    return (List<T>) optResult.get();
                }
                return Collections.emptyList();
            } else if (result instanceof List) {
                return (List<T>) result;
            }
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        }

        return Collections.emptyList();
    }

    @Override
    public boolean hasNext() {
        return nextUrl != null && !nextUrl.isEmpty();
    }

    @Override
    public boolean hasPrevious() {
        return previousUrl != null && !previousUrl.isEmpty();
    }

    @Override
    public BiDirectionalPage<T> nextPage() throws IOException {
        if (!hasNext()) {
            throw new NoSuchElementException("No next page available");
        }
        fetchPage(nextUrl);
        return this;
    }

    @Override
    public BiDirectionalPage<T> previousPage() throws IOException {
        if (!hasPrevious()) {
            throw new NoSuchElementException("No previous page available");
        }
        fetchPage(previousUrl);
        return this;
    }

    @Override
    public List<T> getItems() {
        return currentPage.getItems();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Optional<R> getResponse() {
        return Optional.ofNullable((R) fullResponse);
    }

    /**
     * Returns the current page.
     *
     * @return The current page of results
     */
    public Page<T> getCurrentPage() {
        return currentPage;
    }

    /**
     * Returns an iterable over all pages (not items).
     * Use this when you want to process pages as batches.
     *
     * <pre>{@code
     * for (Page<Payment> page : pager.pages()) {
     *     batchProcess(page.getItems());
     * }
     * }</pre>
     *
     * @return An iterable of pages
     */
    public Iterable<Page<T>> pages() {
        return () -> new PageIterator();
    }

    /**
     * Returns an iterable over all previous pages (navigating backwards).
     * Use this when you need to traverse pages in reverse order.
     *
     * <pre>{@code
     * for (Page<Payment> page : pager.previousPages()) {
     *     processPreviousBatch(page.getItems());
     * }
     * }</pre>
     *
     * @return An iterable of previous pages
     */
    public Iterable<Page<T>> previousPages() {
        return () -> new PreviousPageIterator();
    }

    /**
     * Returns a Stream of all items across all pages.
     * This is a lazy stream that fetches pages on demand.
     *
     * <pre>{@code
     * List<Payment> filtered = pager.stream()
     *     .filter(p -> p.getAmount() > 100)
     *     .collect(Collectors.toList());
     * }</pre>
     *
     * @return A stream of all items
     */
    public Stream<T> stream() {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    /**
     * Returns an iterator that traverses ALL items across ALL pages.
     * Pages are fetched lazily as needed.
     *
     * @return An iterator over all items
     */
    @Override
    public Iterator<T> iterator() {
        return new AutoPaginatingIterator();
    }

    private void fetchPage(String url) throws IOException {
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method("GET", null)
                .headers(Headers.of(clientOptions.headers(requestOptions)))
                .addHeader("Accept", "application/json");

        Request request = requestBuilder.build();

        try (Response response = clientOptions.httpClient().newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to fetch page: HTTP " + response.code());
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }

            String responseBody = body.string();
            parseAndUpdateState(responseBody);
        }
    }

    private void parseAndUpdateState(String responseBody) throws IOException {
        List<T> items = itemsParser.apply(responseBody);
        currentPage = new Page<>(items);

        JsonNode root = ObjectMappers.JSON_MAPPER.readTree(responseBody);
        nextUrl = null;
        previousUrl = null;

        JsonNode linksNode = root.get("links");
        if (linksNode != null && linksNode.isArray()) {
            for (JsonNode linkNode : linksNode) {
                String rel = linkNode.has("rel") ? linkNode.get("rel").asText() : null;
                String href = linkNode.has("href") ? linkNode.get("href").asText() : null;

                if ("next".equals(rel) && href != null) {
                    nextUrl = href;
                } else if ("previous".equals(rel) && href != null) {
                    previousUrl = href;
                }
            }
        }

        try {
            if (fullResponse != null) {
                fullResponse = ObjectMappers.JSON_MAPPER.readValue(responseBody, fullResponse.getClass());
            }
        } catch (Exception ignored) {
        }
    }

    private class AutoPaginatingIterator implements Iterator<T> {
        private Iterator<T> currentIterator;
        private boolean exhausted;

        AutoPaginatingIterator() {
            this.currentIterator = currentPage.iterator();
            this.exhausted = false;
        }

        @Override
        public boolean hasNext() {
            if (currentIterator.hasNext()) {
                return true;
            }

            if (exhausted || !PayrocPager.this.hasNext()) {
                return false;
            }

            try {
                PayrocPager.this.nextPage();
                currentIterator = currentPage.iterator();
                return currentIterator.hasNext();
            } catch (IOException e) {
                exhausted = true;
                throw new UncheckedIOException("Failed to fetch next page", e);
            }
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return currentIterator.next();
        }
    }

    private class PageIterator implements Iterator<Page<T>> {
        private boolean returnedFirst;

        PageIterator() {
            this.returnedFirst = false;
        }

        @Override
        public boolean hasNext() {
            if (!returnedFirst) {
                return !currentPage.isEmpty();
            }
            return PayrocPager.this.hasNext();
        }

        @Override
        public Page<T> next() {
            if (!returnedFirst) {
                returnedFirst = true;
                return currentPage;
            }

            if (!PayrocPager.this.hasNext()) {
                throw new NoSuchElementException();
            }

            try {
                PayrocPager.this.nextPage();
                return currentPage;
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to fetch next page", e);
            }
        }
    }

    private class PreviousPageIterator implements Iterator<Page<T>> {
        private boolean returnedFirst;

        PreviousPageIterator() {
            this.returnedFirst = false;
        }

        @Override
        public boolean hasNext() {
            if (!returnedFirst) {
                return !currentPage.isEmpty();
            }
            return PayrocPager.this.hasPrevious();
        }

        @Override
        public Page<T> next() {
            if (!returnedFirst) {
                returnedFirst = true;
                return currentPage;
            }

            if (!PayrocPager.this.hasPrevious()) {
                throw new NoSuchElementException();
            }

            try {
                PayrocPager.this.previousPage();
                return currentPage;
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to fetch previous page", e);
            }
        }
    }
}
