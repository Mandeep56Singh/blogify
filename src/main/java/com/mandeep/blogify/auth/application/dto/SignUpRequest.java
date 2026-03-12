package com.mandeep.blogify.auth.application.dto;

public record SignUpRequest(
        String email,
        String userName,
        String password
) {
}
