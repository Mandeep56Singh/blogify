package com.mandeep.blogify.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequestDto(
        @NotBlank String title,
        @NotBlank @Size(min = 1200, max = 10000) String content,
        @NotNull Long authorId,
        @NotNull List<Long> categoryIds
) {
}
