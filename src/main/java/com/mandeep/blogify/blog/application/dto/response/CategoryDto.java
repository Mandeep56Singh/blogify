package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Category object returned by the API")
public record CategoryDto(
        @Schema(description = "Unique identifier of the category", example = "1")
        Long id,
        @Schema(description = "Title of the category", example = "Technology")
        String title
) implements ResponsePayload { }

