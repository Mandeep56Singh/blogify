package com.mandeep.blogify.user;

import com.mandeep.blogify.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(name = "UserView", description = "Information about a user returned from the API")
public record UserView(

        @Schema(description = "Unique identifier of the user", example = "1")
        Long id,

        @Schema(description = "User's email address", example = "mandeep@example.com")
        String email,

        @Schema(description = "Full name of the user", example = "Mandeep Singh")
        String name,

        @Schema(description = "Hashed password of the user. Will not be returned in production APIs", example = "$2a$12$...")
        String password,

        @Schema(description = "Role of the user", example = "USER")
        Role role

) {}
