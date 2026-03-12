package com.mandeep.blogify.blog.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PostWebRequest (

        @NotBlank
        @Size(min = 5, max = 150)
        String title,

        @Size(min = 100, max = 20_000)
        @NotBlank
        String content,

        @NotNull
        List<UUID> categoryIds

) {
}
