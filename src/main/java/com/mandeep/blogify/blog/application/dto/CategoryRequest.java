package com.mandeep.blogify.blog.application.dto;

import java.util.UUID;

public record CategoryRequest(
        String title,
        String description,
        UUID userId
) {
}
