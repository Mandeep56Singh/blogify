package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;

import java.time.Instant;

public record CategoryResponseDto(
        Long id,
        String title,
        String description,
        Instant createdAt,
        Instant lastModifiedAt
) implements ResponsePayload {
}
