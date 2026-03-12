package com.mandeep.blogify.user.infrastructure.persistence.adapter;

import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserQueryAdapter implements UserQueryRepository {

    private final UserJpaRepository userJpaRepository;

    public UserQueryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }


    @Override
    public Optional<UserResponse> findResponseById(UserId userId) {
        return userJpaRepository.findUserResponseById(userId.value());
    }

    @Override
    public Optional<UserResponse> findResponseByEmail(Email email) {
        return userJpaRepository.findUserResponseByEmail(email.value());
    }

    @Override
    public Optional<UserResponse> findResponseByUserName(UserName name) {
        return userJpaRepository.findUserResponseByUserName(name.value());
    }

    @Override
    public List<UserResponse> findUsersById(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userJpaRepository.findUsersById(new ArrayList<>(ids));
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.value());
    }
}
