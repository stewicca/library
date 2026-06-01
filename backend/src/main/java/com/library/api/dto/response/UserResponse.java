package com.library.api.dto.response;

import lombok.Builder;

/**
 * Client-facing view of the authenticated user.
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
public record UserResponse(
        String id,
        String username,
        String role
) {
}
