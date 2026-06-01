package com.library.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bootstrap admin credentials bound from {@code library.admin.*}. Used once on startup to seed
 * the first ADMIN account so the system is never locked out.
 *
 * @author stewicca
 * @version 1.0
 */
@ConfigurationProperties(prefix = "library.admin")
public record AdminSeederProperties(
        boolean enabled,
        String username,
        String password
) {
}
