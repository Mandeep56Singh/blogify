package com.mandeep.blogify.auth.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;

public record UserLoginResponseDto(

        String token,
        String tokenType,
        Long expiresIn,
        AuthUserDto user
) implements ResponsePayload {
}
