package com.mandeep.blogify.auth.application.dto;

public record AuthUserDto(
        Long id,
        String email,
        String name,
        String role
) {
}
