package com.mandeep.blogify.auth.application;

public final class AuthConstants {

    private AuthConstants() {
        throw new UnsupportedOperationException("Can't instantiate");
    }

    public static final String[] PUBLIC_APIS = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    public static final String[] PUBLIC_GET_APIS = {
            "/api/v1/posts/**",
            "/api/v1/categories/**",
            "/api/v1/users/**",
    };
}
