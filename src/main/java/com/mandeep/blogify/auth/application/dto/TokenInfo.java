package com.mandeep.blogify.auth.application.dto;

public record TokenInfo(
        String token,
        Long expiresIn
) {
}
