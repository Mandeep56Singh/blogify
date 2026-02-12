package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;

import java.time.Instant;

public record ImageDto(
        String id,
        String fileName,
        Long size,
        String contentType,
        Instant createdAt
) implements ResponsePayload {
}
