package com.library.api.service;

import com.library.api.entity.UserAccount;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtService {
    String generateAccessToken(UserAccount userAccount);

    /** @return the subject (user id) if the token is valid, otherwise {@code null}. */
    String extractUserId(String token);

    String resolveToken(HttpServletRequest request);

    void blacklist(String token);

    boolean isBlacklisted(String token);

    long getAccessTokenExpirationSeconds();
}
