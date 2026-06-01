package com.library.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT / token settings bound from the {@code library.jwt.*} properties.
 *
 * @author stewicca
 * @version 1.0
 */
@Validated
@ConfigurationProperties(prefix = "library.jwt")
public record JwtProperties(
        @NotBlank String secret,
        @NotBlank String issuer,
        @Positive long accessTokenExpirationMinutes,
        @Positive long refreshTokenExpirationHours
) {
}
