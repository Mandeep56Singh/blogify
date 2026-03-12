package com.mandeep.blogify.blog.application.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PostData (
        UUID postId,
        String title,
        String slug,
        String content,
        Set<CategoryResponse> categories,
        UUID authorId,
        String status,
        Instant createdAt,
        Instant publishedAt,
        Instant lastModifiedAt
){
}
