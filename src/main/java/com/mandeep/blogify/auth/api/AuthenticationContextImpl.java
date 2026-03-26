package com.mandeep.blogify.auth.api;

import com.mandeep.blogify.auth.infrastructure.security.AuthenticatedUserDetails;
import com.mandeep.blogify.shared.AuthView;
import com.mandeep.blogify.shared.AuthenticationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationContextImpl implements AuthenticationContext {

    @Override
    public Optional<AuthView> getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        if (auth.getPrincipal() instanceof AuthenticatedUserDetails details) {
            return Optional.of(new AuthView(details.id()));
        }

        return Optional.empty();
    }
}
