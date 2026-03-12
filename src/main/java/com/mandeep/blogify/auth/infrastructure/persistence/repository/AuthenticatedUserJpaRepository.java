package com.mandeep.blogify.auth.infrastructure.persistence.repository;

import com.mandeep.blogify.auth.infrastructure.persistence.entity.AuthenticatedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthenticatedUserJpaRepository extends JpaRepository<AuthenticatedUserEntity, UUID> {
    Optional<AuthenticatedUserEntity> findByEmail(String email);

}
