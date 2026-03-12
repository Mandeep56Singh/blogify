package com.mandeep.blogify.auth.domain.repository;


import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.Email;

import java.util.Optional;

public interface AuthRepository {
    Optional<AuthenticatedUser> findByEmail(Email email);
}
