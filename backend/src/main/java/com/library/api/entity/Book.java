package com.library.api.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * A book — a {@link LibraryItem} identified by an ISBN.
 *
 * @author stewicca
 * @version 1.0
 */
@Entity
@DiscriminatorValue("BOOK")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends LibraryItem {

    private String isbn;

    @Override
    public String describe() {
        return "Book: " + getTitle() + " by " + getAuthor() + " (ISBN " + isbn + ")";
    }

    @Override
    public String itemType() {
        return "BOOK";
    }
}
