package com.mandeep.blogify.category.dto;

import java.time.Instant;

public record CategoryResponseDto(
        Long id,
        String title,
        String description,
        Instant createdAt,
        Instant lastModifiedAt
) {
}
