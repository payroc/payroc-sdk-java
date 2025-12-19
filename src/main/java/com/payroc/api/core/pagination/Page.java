package com.payroc.api.core.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a single page of results from a paginated API response.
 * This class is immutable and provides convenient iteration over items.
 *
 * @param <T> The type of items in the page
 */
public final class Page<T> implements Iterable<T> {

    private static final Page<?> EMPTY = new Page<>(Collections.emptyList());

    private final List<T> items;

    /**
     * Creates a new page with the given items.
     *
     * @param items The items in this page (will be copied to ensure immutability)
     */
    public Page(List<T> items) {
        this.items = items != null ? Collections.unmodifiableList(new ArrayList<>(items)) : Collections.emptyList();
    }

    /**
     * Returns an empty page.
     *
     * @param <T> The type of items
     * @return An empty page instance
     */
    @SuppressWarnings("unchecked")
    public static <T> Page<T> empty() {
        return (Page<T>) EMPTY;
    }

    /**
     * Returns the items in this page.
     *
     * @return An unmodifiable list of items
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * Returns the number of items in this page.
     *
     * @return The number of items
     */
    public int size() {
        return items.size();
    }

    /**
     * Returns whether this page is empty.
     *
     * @return true if this page contains no items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Returns an iterator over the items in this page.
     *
     * @return An iterator
     */
    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public String toString() {
        return "Page{" + "items=" + items.size() + " items}";
    }
}
