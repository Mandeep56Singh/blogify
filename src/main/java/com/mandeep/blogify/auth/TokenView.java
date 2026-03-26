package com.mandeep.blogify.auth;

public record TokenView(
        String token,
        Long expiresIn
) {
}
