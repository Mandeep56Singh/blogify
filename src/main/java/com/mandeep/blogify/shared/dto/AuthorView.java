package com.mandeep.blogify.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorView(
        @Schema(description = "Unique identifier of the Author", example = "1")
        Long id,

        @Schema(description = "Author's email address", example = "mandeep@example.com")
        String email,

        @Schema(description = "User name of the Author", example = "Mandeep Singh")
        String name

        ) {
}
