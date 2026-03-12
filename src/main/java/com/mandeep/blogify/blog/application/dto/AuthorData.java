package com.mandeep.blogify.blog.application.dto;

import java.util.UUID;

public record AuthorData(
        UUID id,
        String userName
) {
}
