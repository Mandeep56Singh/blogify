package com.mandeep.blogify.user;

import com.mandeep.blogify.user.domain.Role;

public record UserView(
        Long id,
        String email,
        String name,
        String password,
        Role role
) {
}
