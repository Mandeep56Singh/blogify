package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.Resource;

@Schema(description = "Image file content returned by the API")
public record ImageResourceDto(
        @Schema(description = "Binary stream of the image", type = "string", format = "binary")
        Resource resource
) implements ResponsePayload { }
