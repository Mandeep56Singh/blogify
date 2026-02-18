package com.mandeep.blogify.auth;

import com.mandeep.blogify.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthenticatedUserView(
        @Schema(description = "Unique identifier of the Authenticated User", example = "1")
        Long id,

        @Schema(description = "Authenticated User's email address", example = "mandeep@example.com")
        String email,

        @Schema(description = "User name of the Authenticated User", example = "Mandeep Singh")
        String name,

        @Schema(description = "role of Authenticated User", example = "USER")
        Role role

) {
}
