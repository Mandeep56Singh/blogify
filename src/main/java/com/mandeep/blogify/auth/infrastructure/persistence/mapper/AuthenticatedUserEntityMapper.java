package com.mandeep.blogify.auth.infrastructure.persistence.mapper;

import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.infrastructure.persistence.entity.AuthenticatedUserEntity;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserEntityMapper {

    public AuthenticatedUserEntity toEntity(AuthenticatedUser user) {

        return AuthenticatedUserEntity.builder()
                .id(user.getAuthUserId().value())
                .email(user.getEmail().value())
                .username(user.getUserName())
                .role(user.getRole())
                .password(user.getHashedPassword().value())
                .active(user.isActive())
                .build();

    }

    public AuthenticatedUser toDomain(AuthenticatedUserEntity userEntity) {
        return AuthenticatedUser.load(
                new AuthUserId(userEntity.getId()),
                userEntity.getUserName(),
                new Email(userEntity.getEmail()),
                new HashedPassword(userEntity.getPassword()),
                userEntity.getRole(),
                userEntity.isActive()
        );
    }
}
