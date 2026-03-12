package com.mandeep.blogify.user.infrastructure.persistence.adapter;

import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import com.mandeep.blogify.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.mandeep.blogify.user.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserCommandAdaptor implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;


    public UserCommandAdaptor(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityMapper = userEntityMapper;
    }


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
