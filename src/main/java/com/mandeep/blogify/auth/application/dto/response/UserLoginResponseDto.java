package com.mandeep.blogify.auth.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO returned after a successful user login.
 */
@Schema(name = "UserLoginResponseDto", description = "Response payload after successful login, containing JWT token and user details")
public record UserLoginResponseDto(

        @Schema(
                description = "JWT authentication token",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        )
        String token,

        @Schema(
                description = "Type of the token, typically 'Bearer'",
                example = "Bearer"
        )
        String tokenType,

        @Schema(
                description = "Time in seconds until the token expires",
                example = "3600"
        )
        Long expiresIn,

        @Schema(
                description = "Information about the authenticated user"
        )
        AuthUserDto user

) implements ResponsePayload {}
