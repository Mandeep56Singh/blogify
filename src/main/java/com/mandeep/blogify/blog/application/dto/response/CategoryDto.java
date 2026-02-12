package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;

public record CategoryDto(
        Long id,
        String title
) implements ResponsePayload {
}
