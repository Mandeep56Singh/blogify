package com.mandeep.blogify.auth.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for user login requests.
 */
@Schema(name = "UserLoginRequestDto", description = "Payload for user login containing email and password")
public record UserLoginRequestDto(

        @NotBlank
        @Email
        @Schema(
                description = "Registered email of the user",
                example = "user@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,

        @NotBlank
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$",
                message = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
        )
        @Schema(
                description = "Password of the user. Must include uppercase, lowercase, number, and special character, minimum 8 characters",
                example = "Password@123",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String password

) {}
