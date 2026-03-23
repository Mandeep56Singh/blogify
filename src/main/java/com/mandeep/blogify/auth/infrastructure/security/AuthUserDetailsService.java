package com.mandeep.blogify.auth.infrastructure.security;

import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.repository.AuthRepository;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public AuthenticatedUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Email userEmail = new Email(email);
        AuthenticatedUser user = authRepository.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("User didn't exits")
        );

        return new AuthenticatedUserDetails(
                user.getAuthUserId().value(),
                user.getUserName(),
                user.getHashedPassword().value(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

}
