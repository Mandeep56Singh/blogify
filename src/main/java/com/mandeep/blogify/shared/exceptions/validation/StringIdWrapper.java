package com.mandeep.blogify.shared.exceptions.validation;

import jakarta.validation.constraints.NotBlank;

public record StringIdWrapper(
        @NotBlank String id
) {
}
