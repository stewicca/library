package com.library.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Refresh-token cookie attributes bound from {@code library.cookie.*}.
 *
 * <p>For local dev behind the Vite proxy (same origin) {@code secure=false} and
 * {@code sameSite=Lax} work. For a cross-site deployment use {@code secure=true} and
 * {@code sameSite=None}.</p>
 *
 * @author stewicca
 * @version 1.0
 */
@ConfigurationProperties(prefix = "library.cookie")
public record CookieProperties(
        String name,
        boolean secure,
        String sameSite,
        String path
) {
}
