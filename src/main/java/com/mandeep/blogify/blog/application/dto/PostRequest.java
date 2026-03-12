package com.mandeep.blogify.blog.application.dto;

import java.util.List;
import java.util.UUID;

public record PostRequest(
    String title,
    String content,
    List<UUID> categoryIds,
    UUID authorId
) {
}
