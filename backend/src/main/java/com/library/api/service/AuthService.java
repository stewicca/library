package com.library.api.service;

import com.library.api.dto.request.LoginRequest;

/**
 * Authentication use cases: login, token refresh, logout.
 *
 * @author stewicca
 * @version 1.0
 */
public interface AuthService {
    /** A login/refresh result bundling the client-facing response with the cookie-only refresh token. */
    record AuthResult(com.library.api.dto.response.AuthResponse response, String refreshToken) {
    }

    AuthResult login(LoginRequest request);

    AuthResult refresh(String refreshToken);

    void logout(String bearerToken);

    long getRefreshTokenExpirationSeconds();
}
