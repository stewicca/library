package com.library.api.util;

import com.library.api.config.CookieProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtil {

    private final CookieProperties properties;

    public CookieUtil(CookieProperties properties) {
        this.properties = properties;
    }

    public ResponseCookie create(String refreshToken, long maxAgeSeconds) {
        return base(refreshToken).maxAge(Duration.ofSeconds(maxAgeSeconds)).build();
    }

    public ResponseCookie expire() {
        return base("").maxAge(0).build();
    }

    public Optional<String> read(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> properties.name().equals(cookie.getName()))
                .map(jakarta.servlet.http.Cookie::getValue)
                .findFirst();
    }

    private ResponseCookie.ResponseCookieBuilder base(String value) {
        return ResponseCookie.from(properties.name(), value)
                .httpOnly(true)
                .secure(properties.secure())
                .sameSite(properties.sameSite())
                .path(properties.path());
    }
}
