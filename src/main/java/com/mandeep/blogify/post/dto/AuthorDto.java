package com.mandeep.blogify.post.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthorDto(
        @NotNull Long id,
        String name,
        @NotNull @Email String email
) {
}
