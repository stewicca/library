package com.library.api.service;

import com.library.api.dto.request.CreateBookRequest;
import com.library.api.dto.request.CreateMagazineRequest;
import com.library.api.dto.response.LibraryItemResponse;

import java.util.List;

/**
 * Catalog use cases: browsing, searching and adding items.
 *
 * <p>{@link #search(String)} and {@link #search(String, String)} demonstrate method
 * <b>overloading</b> required by the assessment.
 *
 * @author stewicca
 * @version 1.0
 */
public interface CatalogService {

    /** @return every item in the catalog. */
    List<LibraryItemResponse> listAll();

    /** @return only items that currently have copies available. */
    List<LibraryItemResponse> findAvailable();

    /**
     * Overload #1 — search by title.
     *
     * @param title partial, case-insensitive title
     * @return matching items
     */
    List<LibraryItemResponse> search(String title);

    /**
     * Overload #2 — search by title and restrict to a type.
     *
     * @param title partial, case-insensitive title
     * @param type  item type, e.g. {@code "BOOK"} or {@code "MAGAZINE"}
     * @return matching items of the given type
     */
    List<LibraryItemResponse> search(String title, String type);

    /**
     * @return all catalog titles sorted alphabetically, as a primitive array
     *         (demonstrates array handling with {@code Arrays.sort}).
     */
    String[] sortedTitles();

    /** Add a new book and return its stored form. */
    LibraryItemResponse addBook(CreateBookRequest request);

    /** Add a new magazine and return its stored form. */
    LibraryItemResponse addMagazine(CreateMagazineRequest request);
}
