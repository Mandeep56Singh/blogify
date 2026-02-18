package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Image metadata returned by the API")
public record ImageDto(
        @Schema(description = "Unique identifier of the image", example = "abc123xyz")
        String id,
        @Schema(description = "Original filename of the uploaded image", example = "profile.jpg")
        String fileName,
        @Schema(description = "Size of the image in bytes", example = "204800")
        Long size,
        @Schema(description = "MIME type of the image", example = "image/jpeg")
        String contentType,
        @Schema(description = "Timestamp when the image was uploaded", example = "2026-02-13T11:44:41Z")
        Instant createdAt
) implements ResponsePayload { }
