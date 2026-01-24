package com.mandeep.blogify.auth.dto;

public record AuthUserDto(
        Long id,
        String email,
        String name,
        String role
) {
}
