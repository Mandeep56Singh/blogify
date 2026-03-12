package com.mandeep.blogify.auth.application.command;

public interface PasswordHasher {
    String hash(String rawPassword);
}
