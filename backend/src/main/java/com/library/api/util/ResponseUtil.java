package com.library.api.util;

import com.library.api.dto.response.WebResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public final class ResponseUtil {
    private ResponseUtil() {
    }

    public static <T> ResponseEntity<WebResponse<T>> ok(String message, T data) {
        return build(HttpStatus.OK, message, data, null);
    }

    public static <T> ResponseEntity<WebResponse<T>> build(HttpStatus status, String message, T data) {
        return build(status, message, data, null);
    }

    public static <T> ResponseEntity<WebResponse<T>> build(
            HttpStatus status, String message, T data, List<String> errors) {
        WebResponse<T> body = WebResponse.<T>builder()
                .status(status.value())
                .message(message)
                .data(data)
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
