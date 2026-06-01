package com.library.api.controller;

import com.library.api.constant.ApiRoute;
import com.library.api.dto.request.LoginRequest;
import com.library.api.dto.response.AuthResponse;
import com.library.api.dto.response.UserResponse;
import com.library.api.dto.response.WebResponse;
import com.library.api.entity.UserAccount;
import com.library.api.service.AuthService;
import com.library.api.util.CookieUtil;
import com.library.api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ApiRoute.AUTH)
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login, token refresh, logout and current-user lookup")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @Operation(summary = "Log in with username + password. Sets an HttpOnly refresh-token cookie.")
    @PostMapping(ApiRoute.LOGIN)
    public ResponseEntity<WebResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthService.AuthResult result = authService.login(request);
        return withRefreshCookie(result, "Login successful");
    }

    @Operation(summary = "Exchange the refresh-token cookie for a new access token (rotates the refresh token).")
    @PostMapping(ApiRoute.REFRESH_TOKEN)
    public ResponseEntity<WebResponse<AuthResponse>> refresh(HttpServletRequest request) {
        String refreshToken = cookieUtil.read(request)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token missing"));
        AuthService.AuthResult result = authService.refresh(refreshToken);
        return withRefreshCookie(result, "Token refreshed");
    }

    @Operation(summary = "Log out: blacklist the access token and clear the refresh-token cookie.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(ApiRoute.LOGOUT)
    public ResponseEntity<WebResponse<Object>> logout(HttpServletRequest request) {
        authService.logout(request.getHeader(HttpHeaders.AUTHORIZATION));
        ResponseCookie expired = cookieUtil.expire();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, expired.toString())
                .body(WebResponse.builder().status(HttpStatus.OK.value()).message("Logout successful").build());
    }

    @Operation(summary = "Return the currently authenticated user.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping(ApiRoute.ME)
    public ResponseEntity<WebResponse<UserResponse>> me(@AuthenticationPrincipal UserAccount user) {
        UserResponse body = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
        return ResponseUtil.ok("Current user", body);
    }

    private ResponseEntity<WebResponse<AuthResponse>> withRefreshCookie(AuthService.AuthResult result, String message) {
        ResponseCookie cookie = cookieUtil.create(result.refreshToken(), authService.getRefreshTokenExpirationSeconds());
        WebResponse<AuthResponse> body = WebResponse.<AuthResponse>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(result.response())
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(body);
    }
}
