package com.mandeep.blogify.blog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryWebRequest(

        @NotBlank @Size(min = 1, max = 120)
        String title,

        @NotBlank @Size(max = 1_000)
        String description
) {
}
