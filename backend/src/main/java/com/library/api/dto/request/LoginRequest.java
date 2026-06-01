package com.library.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Credentials submitted to the login endpoint.
 *
 * @author stewicca
 * @version 1.0
 */
public record LoginRequest(
        @NotBlank(message = "username is required")
        String username,

        @NotBlank(message = "password is required")
        String password
) {
}
