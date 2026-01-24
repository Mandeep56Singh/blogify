package com.mandeep.blogify.auth;

import com.mandeep.blogify.constants.ApiError;
import com.mandeep.blogify.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findUserByEmail(email)
                .map(user -> new AuthUser(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getName(),
                        List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                ))
                .orElseThrow(
                        () -> new UsernameNotFoundException(ApiError.USER_NOT_FOUND.getTitle())
                );
    }
}
