package com.mandeep.blogify.auth.infrastructure.persistence.mapper;

import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.domain.model.valueObject.Email;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.infrastructure.persistence.entity.AuthenticatedUserEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserEntityMapper {

    public AuthenticatedUserEntity toEntity(AuthenticatedUser user) {
        AuthenticatedUserEntity userEntity = new AuthenticatedUserEntity();
        userEntity.setId(user.getAuthUserId().value());
        userEntity.setEmail(user.getEmail().value());
        userEntity.setUserName(user.getUserName());
        userEntity.setRole(user.getRole());
        userEntity.setPassword(user.getHashedPassword().value());
        userEntity.setActive(user.isActive());

        return userEntity;
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
