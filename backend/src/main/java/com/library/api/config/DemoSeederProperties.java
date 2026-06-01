package com.library.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Toggle for the demo-data seeder, bound from {@code library.demo.*}. When enabled and the
 * catalog is empty, a handful of books, magazines, members and a LIBRARIAN account are seeded
 * so the running app has something to show. Off by default; on in the {@code dev} profile.
 *
 * @author stewicca
 * @version 1.0
 */
@ConfigurationProperties(prefix = "library.demo")
public record DemoSeederProperties(
        boolean enabled,
        String librarianUsername,
        String librarianPassword,
        String memberUsername,
        String memberPassword
) {
}
