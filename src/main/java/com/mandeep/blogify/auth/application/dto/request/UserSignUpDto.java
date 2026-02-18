package com.mandeep.blogify.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for user signup requests.
 */
@Schema(name = "UserSignUpDto", description = "Payload for creating a new user account")
public record UserSignUpDto(

        @NotBlank
        @Schema(
                description = "Full name of the user",
                example = "Mandeep Singh",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String name,

        @NotBlank
        @Email
        @Schema(
                description = "Email address for the new user",
                example = "mandeep@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
                message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
        )
        @Schema(
                description = "Password for the new account. Must include uppercase, lowercase, number, and special character, minimum 8 characters",
                example = "Password@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String password

) {}
