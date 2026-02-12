package com.mandeep.blogify.auth.application.dto.response;

public record AuthUserDto(
        Long id,
        String email,
        String name,
        String role
) {
}
