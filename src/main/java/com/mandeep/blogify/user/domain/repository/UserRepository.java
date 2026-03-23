package com.mandeep.blogify.user.domain.repository;

import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(UserId id);

    boolean existsByUserName(UserName name);

    boolean existsByEmail(Email email);
}
