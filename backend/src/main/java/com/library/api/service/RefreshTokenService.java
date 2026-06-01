package com.library.api.service;

/**
 * Lifecycle of opaque refresh tokens stored in Redis.
 *
 * @author stewicca
 * @version 1.0
 */
public interface RefreshTokenService {
    /** Issue a brand new refresh token for the user, replacing any existing one. */
    String issue(String userId);

    /** @return the user id bound to this refresh token, or {@code null} if unknown/expired. */
    String resolveUserId(String refreshToken);

    /** Invalidate the old token and issue a fresh one (rotation on every refresh). */
    String rotate(String oldRefreshToken, String userId);

    void revoke(String userId);

    long getExpirationSeconds();
}
