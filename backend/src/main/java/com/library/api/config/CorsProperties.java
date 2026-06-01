package com.library.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * CORS settings bound from {@code library.cors.*}. Credentials are always allowed because the
 * refresh-token cookie must be sent cross-origin during local development (Vite dev server).
 *
 * @author stewicca
 * @version 1.0
 */
@ConfigurationProperties(prefix = "library.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
}
