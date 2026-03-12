package com.mandeep.blogify.user.application.dto;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String userName,
        String email,
        Role role,
        boolean isActive,
        Instant createdAt,
        Instant lastModifiedAt
) {
}
