package com.library.api.service.impl;

import com.library.api.config.JwtProperties;
import com.library.api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * Opaque (non-JWT) refresh tokens stored in Redis. Two keys are kept so we can look up
 * by user (to revoke/rotate) and by token (to resolve the owner on refresh):
 * <ul>
 *     <li>{@code refresh:user:{userId}}  -> token</li>
 *     <li>{@code refresh:token:{token}}  -> userId</li>
 * </ul>
 *
 * @author stewicca
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final String BY_USER = "refresh:user:";
    private static final String BY_TOKEN = "refresh:token:";

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;

    @Override
    public String issue(String userId) {
        revoke(userId);
        String token = UUID.randomUUID().toString();
        Duration ttl = Duration.ofHours(jwtProperties.refreshTokenExpirationHours());
        redisTemplate.opsForValue().set(BY_USER + userId, token, ttl);
        redisTemplate.opsForValue().set(BY_TOKEN + token, userId, ttl);
        return token;
    }

    @Override
    public String resolveUserId(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        return redisTemplate.opsForValue().get(BY_TOKEN + refreshToken);
    }

    @Override
    public String rotate(String oldRefreshToken, String userId) {
        if (oldRefreshToken != null) {
            redisTemplate.delete(BY_TOKEN + oldRefreshToken);
        }
        return issue(userId);
    }

    @Override
    public void revoke(String userId) {
        String existing = redisTemplate.opsForValue().get(BY_USER + userId);
        if (existing != null) {
            redisTemplate.delete(BY_TOKEN + existing);
        }
        redisTemplate.delete(BY_USER + userId);
    }

    @Override
    public long getExpirationSeconds() {
        return jwtProperties.refreshTokenExpirationHours() * 3600;
    }
}
