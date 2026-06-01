package com.library.api.dto.response;

import lombok.Builder;

@Builder
public record UserResponse(
        String id,
        String username,
        String role
) {
}
