package com.library.api.constant;

import lombok.Getter;

/**
 * Roles for the Library application.
 *
 * <p>The {@code authority} ({@code ROLE_*}) is what Spring Security stores as a
 * {@code GrantedAuthority} and what {@code hasRole(...)} / {@code @PreAuthorize} match against.
 * The {@code name()} ({@code ADMIN}, {@code LIBRARIAN}, {@code MEMBER}) is the canonical value
 * exposed to clients in the JWT claim and the login response.</p>
 *
 * @author stewicca
 * @version 1.0
 */
@Getter
public enum UserRole {
    ADMIN("ROLE_ADMIN", "Administrator"),
    LIBRARIAN("ROLE_LIBRARIAN", "Librarian"),
    MEMBER("ROLE_MEMBER", "Member");

    private final String authority;
    private final String displayName;

    UserRole(String authority, String displayName) {
        this.authority = authority;
        this.displayName = displayName;
    }
}
