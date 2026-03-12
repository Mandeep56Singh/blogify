package com.mandeep.blogify.auth.application.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long expiresIn,
        AuthUserResponse user
) {
}
