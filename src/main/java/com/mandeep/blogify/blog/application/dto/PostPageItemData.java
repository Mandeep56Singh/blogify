package com.mandeep.blogify.blog.application.dto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PostPageItemData(
        UUID postId,
        String title,
        String slug,
        Set<CategoryResponse> categories,
        UUID authorId,
        Instant createdAt,
        Instant publishedAt
) {
}
