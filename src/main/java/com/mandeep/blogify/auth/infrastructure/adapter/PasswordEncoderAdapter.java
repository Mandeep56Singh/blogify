package com.mandeep.blogify.auth.infrastructure.adapter;

import com.mandeep.blogify.auth.application.command.PasswordHasher;
import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordVerifier, PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
