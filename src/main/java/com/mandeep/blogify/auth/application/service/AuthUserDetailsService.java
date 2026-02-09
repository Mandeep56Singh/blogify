package com.mandeep.blogify.auth.application.service;

import com.mandeep.blogify.auth.domain.AuthUser;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserDetailsService implements UserDetailsService {
    private final UserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserView user = userFacade.getUserByEmail(email);

        return new AuthUser(
                user.id(),
                user.email(),
                user.password(),
                user.name(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
        );

    }
}
