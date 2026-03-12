package com.mandeep.blogify.auth.infrastructure.adapter;


import com.mandeep.blogify.auth.domain.model.entity.AuthenticatedUser;
import com.mandeep.blogify.auth.domain.model.valueObject.Email;
import com.mandeep.blogify.auth.domain.repository.AuthRepository;
import com.mandeep.blogify.auth.infrastructure.persistence.mapper.AuthenticatedUserEntityMapper;
import com.mandeep.blogify.auth.infrastructure.persistence.repository.AuthenticatedUserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthCommandServiceAdapter implements AuthRepository {

    private final AuthenticatedUserJpaRepository authenticatedUserJpaRepository;
    private final AuthenticatedUserEntityMapper authenticatedUserEntityMapper;

    @Override
    public Optional<AuthenticatedUser> findByEmail(Email email) {
        return authenticatedUserJpaRepository.findByEmail(email.value()).map(authenticatedUserEntityMapper::toDomain);
    }
}
