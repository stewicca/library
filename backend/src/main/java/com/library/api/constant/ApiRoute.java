package com.library.api.constant;

/**
 * Centralised API path segments so controllers and the security config never drift apart.
 *
 * @author stewicca
 * @version 1.0
 */
public final class ApiRoute {
    private ApiRoute() {
    }

    public static final String API = "/api/v1";
    public static final String AUTH = API + "/auth";

    public static final String LOGIN = "/login";
    public static final String REFRESH_TOKEN = "/refresh-token";
    public static final String LOGOUT = "/logout";
    public static final String ME = "/me";

    // Domain modules
    public static final String BOOKS = API + "/books";
    public static final String MEMBERS = API + "/members";
    public static final String LOANS = API + "/loans";
}
