package com.mandeep.blogify.dto.user;

import com.mandeep.blogify.enums.Role;

import java.time.LocalDateTime;

public record UserResponseDto(
        String id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
