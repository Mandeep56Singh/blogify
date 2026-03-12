package com.mandeep.blogify.auth;

import java.util.Optional;

public interface AuthFacade {
    Optional<AuthView> getCurrentUserId();
}
