package com.mandeep.blogify.user;

import com.mandeep.blogify.shared.dto.AuthorView;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserFacade {
    Optional<UserView> createUser(String email, String name, String password);
    Optional<UserView> getUserById(Long Id);
    Optional<UserView> getUserByEmail(String Email);
    Optional<AuthorView> getPostAuthor(Long id);
    Map<Long, AuthorView> getAuthors(Set<Long> ids);
}
