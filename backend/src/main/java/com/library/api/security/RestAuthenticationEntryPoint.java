package com.library.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.api.dto.response.WebResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a JSON 401 {@link com.library.api.dto.response.WebResponse} instead of the default
 * redirect when authentication is missing or invalid.
 *
 * @author stewicca
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        writeJson(response, HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    static void writeJson(HttpServletResponse response, HttpStatus status, String message,
                          ObjectMapper objectMapper) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        WebResponse<Object> body = WebResponse.builder()
                .status(status.value())
                .message(message)
                .build();
        objectMapper.writeValue(response.getWriter(), body);
    }

    private void writeJson(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        writeJson(response, status, message, objectMapper);
    }
}
