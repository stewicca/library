package com.library.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload for registering a new library member.
 *
 * @author stewicca
 * @version 1.0
 */
public record CreateMemberRequest(
        @NotBlank(message = "memberNumber is required")
        String memberNumber,

        @NotBlank(message = "name is required")
        String name,

        @Email(message = "email must be valid")
        String email
) {
}
