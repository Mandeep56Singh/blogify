package com.mandeep.blogify.user;

public interface UserFacade {
    UserView createUser(String email, String name, String password);
    UserView getUserById(Long Id);
    UserView getUserByEmail(String Email);

}
