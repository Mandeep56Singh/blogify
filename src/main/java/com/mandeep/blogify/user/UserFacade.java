package com.mandeep.blogify.user;

import java.util.Optional;

public interface UserFacade {
    Optional<UserView> createUser(String email, String name, String password);
    Optional<UserView> getUserById(Long Id);
    Optional<UserView> getUserByEmail(String Email);

}
