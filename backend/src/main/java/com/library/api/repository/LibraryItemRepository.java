package com.library.api.repository;

import com.library.api.entity.LibraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Data-access for the catalog. Works across the whole {@link LibraryItem} hierarchy
 * (books and magazines alike) thanks to single-table inheritance.
 *
 * @author stewicca
 * @version 1.0
 */
public interface LibraryItemRepository extends JpaRepository<LibraryItem, String> {

    /** Derived query — case-insensitive title search. */
    List<LibraryItem> findByTitleContainingIgnoreCase(String title);

    /** JPQL query — only items that currently have copies on the shelf. */
    @Query("SELECT i FROM LibraryItem i WHERE i.availableCopies > 0")
    List<LibraryItem> findAvailable();
}
