package com.mandeep.blogify.blog.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload for creating or updating a category.
 */
@Schema(name = "CategoryRequestDto", description = "Request payload to create or update a blog category")
public record CategoryRequestDto(

        @Schema(
                description = "Title of the category",
                example = "Technology",
                maxLength = 120
        )
        @NotBlank
        @Size(max = 120)
        String title,

        @Schema(
                description = "Optional description of the category",
                example = "Posts related to technology, gadgets, and innovation",
                maxLength = 1000
        )
        @Size(max = 1000)
        String description

) {}
