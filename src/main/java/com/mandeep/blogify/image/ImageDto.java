package com.mandeep.blogify.image;

import java.time.Instant;

public record ImageDto(
        String id,
        String fileName,
        Long size,
        String contentType,
        Instant createdAt
) {
}
