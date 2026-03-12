package com.mandeep.blogify.auth.api;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.AuthView;
import com.mandeep.blogify.auth.infrastructure.security.AuthenticatedUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {


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
