package com.library.api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.library.api.config.JwtProperties;
import com.library.api.entity.UserAccount;
import com.library.api.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String BLACKLISTED = "1";

    private final JwtProperties jwtProperties;
    private final StringRedisTemplate redisTemplate;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtServiceImpl(JwtProperties jwtProperties, StringRedisTemplate redisTemplate) {
        this.jwtProperties = jwtProperties;
        this.redisTemplate = redisTemplate;
        this.algorithm = Algorithm.HMAC256(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
        this.verifier = JWT.require(algorithm).withIssuer(jwtProperties.issuer()).build();
    }

    @Override
    public String generateAccessToken(UserAccount userAccount) {
        try {
            Instant now = Instant.now();
            return JWT.create()
                    .withIssuer(jwtProperties.issuer())
                    .withSubject(userAccount.getId())
                    .withClaim("username", userAccount.getUsername())
                    .withClaim("role", userAccount.getRole().name())
                    .withIssuedAt(now)
                    .withExpiresAt(now.plus(jwtProperties.accessTokenExpirationMinutes(), ChronoUnit.MINUTES))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            log.error("Failed to create JWT: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create token");
        }
    }

    @Override
    public String extractUserId(String token) {
        DecodedJWT decoded = verify(token);
        return decoded != null ? decoded.getSubject() : null;
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    public void blacklist(String token) {
        DecodedJWT decoded = verify(token);
        if (decoded == null) {
            return;
        }
        Date expiresAt = decoded.getExpiresAt();
        long ttlMillis = expiresAt.getTime() - System.currentTimeMillis();
        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, BLACKLISTED, Duration.ofMillis(ttlMillis));
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    @Override
    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.accessTokenExpirationMinutes() * 60;
    }

    private DecodedJWT verify(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            log.debug("JWT verification failed: {}", e.getMessage());
            return null;
        }
    }
}
