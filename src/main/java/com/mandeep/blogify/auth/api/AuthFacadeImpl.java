package com.mandeep.blogify.auth.api;

import com.mandeep.blogify.auth.AuthFacade;
import com.mandeep.blogify.auth.AuthenticatedUserView;
import com.mandeep.blogify.auth.domain.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthenticatedUserMapper authenticatedUserMapper;

    @Override
    public Optional<AuthenticatedUserView> getAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof AuthUser authUser) {
            return Optional.of(authenticatedUserMapper.toUser(authUser));
        }

        return Optional.empty();

    }
}
