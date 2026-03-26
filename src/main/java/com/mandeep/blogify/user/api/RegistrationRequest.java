package com.mandeep.blogify.user.api;

public record RegistrationRequest(
        String email,
        String userName,
        String password
) {
}
