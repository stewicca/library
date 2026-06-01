package com.library.api.dto.response;

import lombok.Builder;

/**
 * Returned by login / refresh. The refresh token itself is NOT placed here —
 * it travels only in an HttpOnly cookie so JavaScript can never read it.
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        String role
) {
}
