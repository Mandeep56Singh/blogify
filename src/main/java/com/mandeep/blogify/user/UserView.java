package com.mandeep.blogify.user;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;


@Schema(name = "UserView", description = "Information about a user returned from the API")
public record UserView(

        @Schema(description = "Unique identifier of the user", example = "1")
        UUID id,

        @Schema(description = "User's value address", example = "mandeep@example.com")
        String email,

        @Schema(description = "User Name of the user", example = "Mandeep Singh")
        String userName,

        boolean isActive,

        @Schema(description = "Role of the user", example = "USER")
        Role role

) {}
