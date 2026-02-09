package com.mandeep.blogify.auth.application.dto;

public record UserLoginResponseDto(

        String token,
        String tokenType,
        Long expiresIn,
        AuthUserDto user
) {
}
