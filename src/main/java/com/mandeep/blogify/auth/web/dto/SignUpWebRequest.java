package com.mandeep.blogify.auth.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpWebRequest(
        @NotBlank @Email String email,

        @NotBlank
        @Size(min = 2, max = 100)
        String userName,

        @NotBlank
        @Size(min = 8, max = 16)
        String password
) {}
