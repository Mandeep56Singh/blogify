package com.mandeep.blogify.auth.web.dto;

import com.mandeep.blogify.auth.application.dto.AuthUserResponse;

public record LoginWebResponse(
        String token,
        String tokenType,
        Long expiresIn,
        AuthUserResponse user
) {
}
