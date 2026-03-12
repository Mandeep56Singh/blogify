package com.mandeep.blogify.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginWebRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password
) {
}
