package com.library.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Payload for recording a borrowing transaction: which member borrows which items.
 *
 * @author stewicca
 * @version 1.0
 */
public record CreateLoanRequest(
        @NotBlank(message = "memberId is required")
        String memberId,

        @NotEmpty(message = "at least one itemId is required")
        List<String> itemIds
) {
}
