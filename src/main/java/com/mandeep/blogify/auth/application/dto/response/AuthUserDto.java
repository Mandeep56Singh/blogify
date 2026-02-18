package com.mandeep.blogify.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a user object returned after authentication.
 */
@Schema(name = "AuthUserDto", description = "Details of the authenticated user")
public record AuthUserDto(

        @Schema(
                description = "Unique identifier of the user",
                example = "123"
        )
        Long id,

        @Schema(
                description = "Email address of the user",
                example = "user@example.com"
        )
        String email,

        @Schema(
                description = "Full name of the user",
                example = "Mandeep Singh"
        )
        String name,

        @Schema(
                description = "Role of the user, e.g., USER or ADMIN",
                example = "USER"
        )
        String role

) {}
