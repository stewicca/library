package com.library.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload for adding a magazine to the catalog.
 *
 * @author stewicca
 * @version 1.0
 */
public record CreateMagazineRequest(
        @NotBlank(message = "title is required")
        String title,

        @NotBlank(message = "author is required")
        String author,

        @Min(value = 0, message = "availableCopies must be zero or greater")
        int availableCopies,

        @Min(value = 1, message = "edition must be at least 1")
        int edition
) {
}
