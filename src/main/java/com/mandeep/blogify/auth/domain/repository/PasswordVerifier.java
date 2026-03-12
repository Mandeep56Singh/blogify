package com.mandeep.blogify.auth.domain.repository;

public interface PasswordVerifier {
    boolean matches(String rawPassword, String hashedPassword);
}
