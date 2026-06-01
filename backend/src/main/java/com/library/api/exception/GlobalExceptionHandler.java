package com.library.api.exception;

import com.library.api.dto.response.WebResponse;
import com.library.api.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Maps exceptions to the uniform {@link com.library.api.dto.response.WebResponse} envelope with the right HTTP status.
 *
 * @author stewicca
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<WebResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();
        return ResponseUtil.build(HttpStatus.BAD_REQUEST, "Validation failed", null, errors);
    }

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class})
    public ResponseEntity<WebResponse<Object>> handleBadCredentials(RuntimeException ex) {
        return ResponseUtil.build(HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return ResponseUtil.build(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<WebResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        // @PreAuthorize denials surface here (thrown during controller invocation, so this
        // advice sees them before Security's filter-level handler would). Authenticated but
        // not permitted -> 403.
        return ResponseUtil.build(HttpStatus.FORBIDDEN, "Access denied", null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<WebResponse<Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseUtil.build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<WebResponse<Object>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseUtil.build(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> handleUnexpected(Exception ex) {
        return ResponseUtil.build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", null);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
