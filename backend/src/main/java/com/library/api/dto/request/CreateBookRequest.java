package com.library.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload for adding a book to the catalog.
 *
 * @author stewicca
 * @version 1.0
 */
public record CreateBookRequest(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "author is required")
        String author,

        @Min(value = 0, message = "availableCopies must be zero or greater")
        int availableCopies,

        @NotBlank(message = "isbn is required")
        String isbn
) {
}
