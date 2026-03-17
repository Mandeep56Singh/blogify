package com.mandeep.blogify.auth.application.dto;

import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String role
) {
}
