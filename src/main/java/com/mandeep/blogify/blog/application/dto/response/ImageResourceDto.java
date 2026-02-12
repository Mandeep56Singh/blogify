package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import org.springframework.core.io.Resource;

public record ImageResourceDto(
        Resource resource
) implements ResponsePayload {
}
