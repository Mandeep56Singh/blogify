package com.mandeep.blogify.shared.exceptions.validation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailWrapper(
        @NotBlank @Email String email
) {
}
