package com.mandeep.blogify.user.infrastructure.persistence.mapper;

import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    // Domain -> DB
    public UserEntity toEntity(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getUserId().value());
        userEntity.setUserName(user.getUserName().value());
        userEntity.setEmail(user.getEmail().value());
        userEntity.setPassword(user.getPassword());
        userEntity.setRole(user.getRole());
        userEntity.setActive(user.isActive());
        return userEntity;
    }

    // DB -> Domain
    public User toDomain(UserEntity entity) {
        return User.reconstitute(
                new UserId(entity.getId()),
                new UserName(entity.getUserName()),
                new Email(entity.getEmail()),
                entity.getPassword(), // Safe because it comes from DB
                entity.isActive(),
                entity.getRole(),
                entity.getCreatedAt()
        );
    }
}