package com.mandeep.blogify.dto.user;

import com.mandeep.blogify.enums.Role;

import java.time.Instant;

public record UserResponseDto(
        String id,
        String name,
        String email,
        Role role,
        Instant createdAt,
        Instant updatedAt

) {
}
