package com.mandeep.blogify.auth.web.dto;

import com.mandeep.blogify.shared.AppConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpWebRequest(
        @NotBlank @Email String email,

        @NotBlank
        @Size(min = AppConstants.USER_NAME_MIN_LENGTH, max = AppConstants.USER_NAME_MAX_LENGTH)
        String userName,

        @NotBlank
        @Size(min = 8, max = 16)
        String password
) {
}
