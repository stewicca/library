package com.library.api.service.impl;

import com.library.api.dto.request.LoginRequest;
import com.library.api.entity.UserAccount;
import com.library.api.constant.UserRole;
import com.library.api.repository.UserAccountRepository;
import com.library.api.service.AuthService;
import com.library.api.service.JwtService;
import com.library.api.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserAccount user;

    @BeforeEach
    void setUp() {
        user = UserAccount.builder()
                .id("user-1")
                .username("alice")
                .password("encoded")
                .role(UserRole.ADMIN)
                .build();
    }

    @Test
    @DisplayName("login authenticates and returns an access token plus a fresh refresh token")
    void loginSucceeds() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(refreshTokenService.issue("user-1")).thenReturn("refresh-token");

        AuthService.AuthResult result = authService.login(new LoginRequest("alice", "secret"));

        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.response().accessToken()).isEqualTo("access-token");
        assertThat(result.response().role()).isEqualTo("ADMIN");
        assertThat(result.response().username()).isEqualTo("alice");
    }

    @Test
    @DisplayName("refresh with an unknown token is rejected as 401")
    void refreshWithUnknownTokenFails() {
        when(refreshTokenService.resolveUserId("bad-token")).thenReturn(null);

        assertThatThrownBy(() -> authService.refresh("bad-token"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("401");
    }

    @Test
    @DisplayName("refresh rotates the token and issues a new access token for a valid session")
    void refreshRotates() {
        when(refreshTokenService.resolveUserId("good-token")).thenReturn("user-1");
        when(userAccountRepository.findById("user-1")).thenReturn(Optional.of(user));
        when(refreshTokenService.rotate("good-token", "user-1")).thenReturn("rotated-token");
        when(jwtService.generateAccessToken(user)).thenReturn("new-access");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);

        AuthService.AuthResult result = authService.refresh("good-token");

        assertThat(result.refreshToken()).isEqualTo("rotated-token");
        assertThat(result.response().accessToken()).isEqualTo("new-access");
    }
}
