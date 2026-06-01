package com.library.api.service.impl;

import com.library.api.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RefreshTokenServiceImpl refreshTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties("secret-secret-secret-secret-secret", "issuer", 15, 168);
        refreshTokenService = new RefreshTokenServiceImpl(properties, redisTemplate);
    }

    @Test
    @DisplayName("issuing a token stores both the by-user and by-token mappings with a TTL")
    void issueStoresBothMappings() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String token = refreshTokenService.issue("user-1");

        assertThat(token).isNotBlank();
        verify(valueOperations).set(eq("refresh:user:user-1"), eq(token), any(Duration.class));
        verify(valueOperations).set(startsWith("refresh:token:"), eq("user-1"), any(Duration.class));
    }

    @Test
    @DisplayName("resolveUserId reads the owner from the by-token mapping")
    void resolveUserId() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:token:abc")).thenReturn("user-1");

        assertThat(refreshTokenService.resolveUserId("abc")).isEqualTo("user-1");
    }

    @Test
    @DisplayName("resolveUserId returns null for a null token without touching Redis")
    void resolveNullToken() {
        assertThat(refreshTokenService.resolveUserId(null)).isNull();
    }
}
