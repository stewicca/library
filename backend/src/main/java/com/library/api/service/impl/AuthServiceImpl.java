package com.library.api.service.impl;

import com.library.api.dto.request.LoginRequest;
import com.library.api.dto.response.AuthResponse;
import com.library.api.entity.UserAccount;
import com.library.api.repository.UserAccountRepository;
import com.library.api.service.AuthService;
import com.library.api.service.JwtService;
import com.library.api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserAccount user = (UserAccount) authentication.getPrincipal();

        String refreshToken = refreshTokenService.issue(user.getId());
        return new AuthResult(buildResponse(user), refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult refresh(String refreshToken) {
        String userId = refreshTokenService.resolveUserId(refreshToken);
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User no longer exists"));

        String rotated = refreshTokenService.rotate(refreshToken, userId);
        return new AuthResult(buildResponse(user), rotated);
    }

    @Override
    public void logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring("Bearer ".length());
            String userId = jwtService.extractUserId(token);
            if (userId != null) {
                refreshTokenService.revoke(userId);
            }
            jwtService.blacklist(token);
        }
    }

    @Override
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenService.getExpirationSeconds();
    }

    private AuthResponse buildResponse(UserAccount user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getAccessTokenExpirationSeconds())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}
