package com.mandeep.blogify.shared;
import java.util.Optional;

public interface AuthenticationContext {
    Optional<AuthView> getCurrentUserId();
}
