package com.mandeep.blogify.auth.dto;

public record UserLoginResponseDto(

        String token,
        String tokenType,
        Long expiresIn,
        AuthUserDto user
) {
}
