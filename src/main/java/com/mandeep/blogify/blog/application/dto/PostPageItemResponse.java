package com.mandeep.blogify.blog.application.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PostPageItemResponse(
        UUID postId,
        String title,
        String slug,
        Set<CategoryResponse> categories,
        AuthorData authorData,
        Instant createdAt,
        Instant publishedAt
) {
}
