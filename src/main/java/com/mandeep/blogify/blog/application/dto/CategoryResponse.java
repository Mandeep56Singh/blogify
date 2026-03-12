package com.mandeep.blogify.blog.application.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String title
) {
}
