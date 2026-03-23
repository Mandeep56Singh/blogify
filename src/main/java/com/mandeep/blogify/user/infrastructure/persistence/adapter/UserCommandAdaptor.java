package com.mandeep.blogify.user.infrastructure.persistence.adapter;

import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import com.mandeep.blogify.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.mandeep.blogify.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCommandAdaptor implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public void save(User user) {

        userJpaRepository.save(userEntityMapper.toEntity(user));
    }

    @Override
    public Optional<User> findById(UserId id) {
        return userJpaRepository.findById(id.value()).map(userEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUserName(UserName name) {
        return userJpaRepository.existsByUserName(name.value());
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.value());
    }
}
