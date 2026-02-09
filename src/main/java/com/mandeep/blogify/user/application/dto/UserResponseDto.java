package com.mandeep.blogify.user.application.dto;

import com.mandeep.blogify.user.domain.Role;

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
