package com.mandeep.blogify.auth.application.dto;

public record LoginRequest(
        String email,
        String password
) {
}
