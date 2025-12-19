package com.payroc.api.core.pagination;

import com.fasterxml.jackson.databind.JsonNode;
import com.payroc.api.core.ClientOptions;
import com.payroc.api.core.ObjectMappers;
import com.payroc.api.core.RequestOptions;
import com.payroc.api.types.IPaginatedList;
import com.payroc.api.types.Link;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.function.Function;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * An asynchronous paginator for Payroc API responses that supports bidirectional navigation
 * and CompletableFuture-based async operations.
 *
 * <p>This pager uses HATEOAS-style links from API responses to navigate between pages.
 * All page fetching operations are non-blocking and return CompletableFuture.
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Fetch next page asynchronously:</h3>
 * <pre>{@code
 * AsyncPayrocPager<Payment> pager = client.payments().listAsync().get();
 * if (pager.hasNext()) {
 *     pager.nextPageAsync()
 *         .thenAccept(nextPager -> {
 *             for (Payment p : nextPager.getItems()) {
 *                 System.out.println(p.getId());
 *             }
 *         });
 * }
 * }</pre>
 *
 * <h3>Get all items across all pages:</h3>
 * <pre>{@code
 * pager.getAllItemsAsync()
 *     .thenAccept(allPayments -> {
 *         System.out.println("Total payments: " + allPayments.size());
 *     });
 * }</pre>
 *
 * <h3>Process pages as they arrive:</h3>
 * <pre>{@code
 * pager.forEachPageAsync(items -> {
 *     System.out.println("Processing batch of " + items.size());
 *     batchProcess(items);
 *     return CompletableFuture.completedFuture(null);
 * }).join();
 * }</pre>
 *
 * @param <T> The type of items in each page
 */
public class AsyncPayrocPager<T> implements BiDirectionalPage<T> {

    private final ClientOptions clientOptions;
    private final Function<String, List<T>> itemsParser;
    private final Class<?> responseClass;
    private final RequestOptions requestOptions;

    private Page<T> currentPage;
    private String nextUrl;
    private String previousUrl;
    private Object fullResponse;

    /**
     * Private constructor - use the static createAsync() method.
     */
    private AsyncPayrocPager(
            ClientOptions clientOptions,
            Function<String, List<T>> itemsParser,
            Class<?> responseClass,
            RequestOptions requestOptions,
            Page<T> currentPage,
            String nextUrl,
            String previousUrl,
            Object fullResponse) {
        this.clientOptions = clientOptions;
        this.itemsParser = itemsParser;
        this.responseClass = responseClass;
        this.requestOptions = requestOptions;
        this.currentPage = currentPage;
        this.nextUrl = nextUrl;
        this.previousUrl = previousUrl;
        this.fullResponse = fullResponse;
    }

    /**
     * Creates an AsyncPayrocPager from an initial API response.
     * This method returns a CompletableFuture that completes immediately with the pager.
     *
     * @param <T> The type of items (inferred)
     * @param initialResponse The initial paginated response
     * @param httpClient The HTTP client
     * @param requestOptions Request options
     * @return A CompletableFuture containing the new AsyncPayrocPager
     */
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<AsyncPayrocPager<T>> createAsync(
            Object initialResponse, ClientOptions clientOptions, RequestOptions requestOptions) {

        return CompletableFuture.supplyAsync(() -> {
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
                    throw new RuntimeException("Failed to parse paginated response", e);
                }
            };

            return new AsyncPayrocPager<>(
                    clientOptions,
                    itemsParser,
                    responseClass,
                    requestOptions,
                    new Page<>(items),
                    nextUrl,
                    previousUrl,
                    initialResponse);
        });
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
        try {
            return nextPageAsync().get();
        } catch (Exception e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("Failed to fetch next page", e);
        }
    }

    @Override
    public BiDirectionalPage<T> previousPage() throws IOException {
        if (!hasPrevious()) {
            throw new NoSuchElementException("No previous page available");
        }
        try {
            return previousPageAsync().get();
        } catch (Exception e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new IOException("Failed to fetch previous page", e);
        }
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
     * Asynchronously fetches the next page.
     *
     * @return A CompletableFuture that completes with this pager (with updated state)
     * @throws NoSuchElementException (via future) if no next page exists
     */
    public CompletableFuture<BiDirectionalPage<T>> nextPageAsync() {
        if (!hasNext()) {
            CompletableFuture<BiDirectionalPage<T>> future = new CompletableFuture<>();
            future.completeExceptionally(new NoSuchElementException("No next page available"));
            return future;
        }
        return fetchPageAsync(nextUrl);
    }

    /**
     * Asynchronously fetches the previous page.
     *
     * @return A CompletableFuture that completes with this pager (with updated state)
     * @throws NoSuchElementException (via future) if no previous page exists
     */
    public CompletableFuture<BiDirectionalPage<T>> previousPageAsync() {
        if (!hasPrevious()) {
            CompletableFuture<BiDirectionalPage<T>> future = new CompletableFuture<>();
            future.completeExceptionally(new NoSuchElementException("No previous page available"));
            return future;
        }
        return fetchPageAsync(previousUrl);
    }

    /**
     * Asynchronously fetches all items across all remaining pages.
     * Starts from the current page and continues until no more pages are available.
     *
     * @return A CompletableFuture containing all items
     */
    public CompletableFuture<List<T>> getAllItemsAsync() {
        List<T> allItems = new ArrayList<>(currentPage.getItems());
        return collectAllItemsAsync(allItems);
    }

    private CompletableFuture<List<T>> collectAllItemsAsync(List<T> accumulator) {
        if (!hasNext()) {
            return CompletableFuture.completedFuture(accumulator);
        }

        return nextPageAsync().thenCompose(page -> {
            accumulator.addAll(page.getItems());
            return collectAllItemsAsync(accumulator);
        });
    }

    /**
     * Processes each page asynchronously as it arrives.
     * The processor function is called for each page's items.
     *
     * @param pageProcessor Function that processes a page's items and returns a CompletionStage
     * @return A CompletableFuture that completes when all pages have been processed
     */
    public CompletableFuture<Void> forEachPageAsync(Function<List<T>, CompletionStage<Void>> pageProcessor) {
        return pageProcessor.apply(currentPage.getItems()).toCompletableFuture().thenCompose(v -> {
            if (!hasNext()) {
                return CompletableFuture.completedFuture(null);
            }
            return nextPageAsync().thenCompose(page -> {
                AsyncPayrocPager<T> nextPager = (AsyncPayrocPager<T>) page;
                return nextPager.forEachPageAsync(pageProcessor);
            });
        });
    }

    /**
     * Processes each item asynchronously.
     * This is a convenience method that iterates through all items across all pages.
     *
     * @param itemConsumer Consumer that processes each item
     * @return A CompletableFuture that completes when all items have been processed
     */
    public CompletableFuture<Void> forEachItemAsync(Consumer<T> itemConsumer) {
        return forEachPageAsync(items -> {
            items.forEach(itemConsumer);
            return CompletableFuture.completedFuture(null);
        });
    }

    /**
     * Returns the current page.
     *
     * @return The current page
     */
    public Page<T> getCurrentPage() {
        return currentPage;
    }

    private CompletableFuture<BiDirectionalPage<T>> fetchPageAsync(String url) {
        CompletableFuture<BiDirectionalPage<T>> future = new CompletableFuture<>();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .method("GET", null)
                .headers(Headers.of(clientOptions.headers(requestOptions)))
                .addHeader("Accept", "application/json");

        Request request = requestBuilder.build();

        clientOptions.httpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (!response.isSuccessful()) {
                        future.completeExceptionally(new IOException("Failed to fetch page: HTTP " + response.code()));
                        return;
                    }

                    ResponseBody body = response.body();
                    if (body == null) {
                        future.completeExceptionally(new IOException("Empty response body"));
                        return;
                    }

                    String responseBody = body.string();
                    parseAndUpdateState(responseBody);
                    future.complete(AsyncPayrocPager.this);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                } finally {
                    response.close();
                }
            }
        });

        return future;
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
                fullResponse = ObjectMappers.JSON_MAPPER.readValue(responseBody, responseClass);
            }
        } catch (Exception ignored) {
        }
    }
}
