package com.library.api.service.impl;

import com.library.api.config.JwtProperties;
import com.library.api.constant.UserRole;
import com.library.api.entity.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    private JwtServiceImpl jwtService;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-key-long-enough-for-hmac-sha256-0123456789-abcdef",
                "library-api-test",
                15,
                168);
        jwtService = new JwtServiceImpl(properties, redisTemplate);
        user = UserAccount.builder()
                .id("user-123")
                .username("alice")
                .password("encoded")
                .role(UserRole.LIBRARIAN)
                .build();
    }

    @Test
    @DisplayName("a generated token can be verified and yields the user id back")
    void generateAndExtractRoundTrip() {
        String token = jwtService.generateAccessToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUserId(token)).isEqualTo("user-123");
    }

    @Test
    @DisplayName("a tampered/invalid token resolves to null instead of throwing")
    void invalidTokenReturnsNull() {
        assertThat(jwtService.extractUserId("not-a-real-jwt")).isNull();
    }

    @Test
    @DisplayName("access-token expiry is reported in seconds")
    void expirationInSeconds() {
        assertThat(jwtService.getAccessTokenExpirationSeconds()).isEqualTo(15 * 60);
    }
}
