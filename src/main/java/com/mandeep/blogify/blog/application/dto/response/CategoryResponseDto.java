package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Category response payload returned by the API")
public record CategoryResponseDto(
        @Schema(description = "Unique identifier of the category", example = "1")
        Long id,
        @Schema(description = "Title of the category", example = "Technology")
        String title,
        @Schema(description = "Description of the category", example = "Articles about technology trends")
        String description,
        @Schema(description = "Timestamp when category was created", example = "2026-02-13T11:44:41Z")
        Instant createdAt,
        @Schema(description = "Timestamp when category was last updated", example = "2026-02-14T08:12:00Z")
        Instant lastModifiedAt
) implements ResponsePayload { }

