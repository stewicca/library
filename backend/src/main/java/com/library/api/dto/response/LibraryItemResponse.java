package com.library.api.dto.response;

import com.library.api.entity.LibraryItem;
import lombok.Builder;

/**
 * Client-facing view of a catalog item. The {@code description} is produced polymorphically
 * by {@link LibraryItem#describe()}, so books and magazines render differently.
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
public record LibraryItemResponse(
        String id,
        String type,
        String title,
        String author,
        int availableCopies,
        String description
) {
    /**
     * Map any {@link LibraryItem} (book or magazine) to its response form.
     *
     * @param item the entity to convert
     * @return the response DTO
     */
    public static LibraryItemResponse from(LibraryItem item) {
        return LibraryItemResponse.builder()
                .id(item.getId())
                .type(item.itemType())
                .title(item.getTitle())
                .author(item.getAuthor())
                .availableCopies(item.getAvailableCopies())
                .description(item.describe())
                .build();
    }
}
