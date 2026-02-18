package com.mandeep.blogify.user.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "User update or creation request payload")
public record UserRequestDto(

        @Schema(description = "Full name of the user", example = "Mandeep Singh")
        String name,

        @NotBlank
        @Email
        @Schema(
                description = "Valid email address of the user",
                example = "mandeep@example.com"
        )
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
                message = "must be at least 8 characters long and include uppercase, lowercase, number, and special character"
        )
        @Schema(
                description = "Password must contain at least 8 characters, including uppercase, lowercase, number, and special character",
                example = "StrongPass@123"
        )
        String password
) {}

