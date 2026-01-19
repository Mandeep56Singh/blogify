package com.mandeep.blogify.user.dto;

import com.mandeep.blogify.user.Role;

import java.time.Instant;

public record UserResponseDto(
        Long id,
        String name,
        String email,
        Role role,
        Instant createdAt,
        Instant lastModifiedAt

) {
}
