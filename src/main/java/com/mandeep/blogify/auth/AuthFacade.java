package com.mandeep.blogify.auth;

public interface AuthFacade {
    TokenView login(String email, String password);
}
