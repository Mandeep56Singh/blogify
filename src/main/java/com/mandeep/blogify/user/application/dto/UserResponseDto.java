package com.mandeep.blogify.user.application.dto;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import com.mandeep.blogify.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "User response payload returned from API")
public record UserResponseDto(

        @Schema(description = "Unique user identifier", example = "1")
        Long id,

        @Schema(description = "Full name of the user", example = "Mandeep Singh")
        String name,

        @Schema(description = "User email address", example = "mandeep@example.com")
        String email,

        @Schema(description = "User role in the system", example = "USER")
        Role role,

        @Schema(description = "Timestamp when the user was created", example = "2025-01-01T10:15:30Z")
        Instant createdAt,

        @Schema(description = "Timestamp when the user was last modified", example = "2025-01-10T12:00:00Z")
        Instant lastModifiedAt

) implements ResponsePayload {}

