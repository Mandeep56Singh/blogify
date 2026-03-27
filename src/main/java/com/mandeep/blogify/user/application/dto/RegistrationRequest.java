package com.mandeep.blogify.user.application.dto;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;

public record RegistrationRequest(
        String email,
        String userName,
        String password,
        Role role
) {
}
