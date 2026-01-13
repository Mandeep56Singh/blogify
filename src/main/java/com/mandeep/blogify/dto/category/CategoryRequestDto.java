package com.mandeep.blogify.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDto(

        @NotBlank
        @Size(max = 120)
        String title,

        @Size(max = 1000)
        String description
) {
}
