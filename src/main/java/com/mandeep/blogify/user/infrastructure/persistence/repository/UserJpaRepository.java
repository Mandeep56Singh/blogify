package com.mandeep.blogify.user.infrastructure.persistence.repository;

import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByUserName(String name);

    boolean existsByEmail(String email);


    @Query("""
                SELECT new com.mandeep.blogify.user.application.dto.UserResponse(
                    u.id,
                    u.userName,
                    u.email,
                    u.role,
                    u.isActive,
                    u.createdAt,
                    u.lastModifiedAt
                )
                FROM UserEntity u WHERE u.id = :id
            """)
    Optional<UserResponse> findUserResponseById(@Param("id") UUID id);

    @Query("""
                SELECT new com.mandeep.blogify.user.application.dto.UserResponse(
                    u.id,
                    u.userName,
                    u.email,
                    u.role,
                    u.isActive,
                    u.createdAt,
                    u.lastModifiedAt
                )
                FROM UserEntity u WHERE u.email = :email
            """)
    Optional<UserResponse> findUserResponseByEmail(@Param("email") String email);


    @Query("""
                SELECT new com.mandeep.blogify.user.application.dto.UserResponse(
                    u.id,
                    u.userName,
                    u.email,
                    u.role,
                    u.isActive,
                    u.createdAt,
                    u.lastModifiedAt
                )
                FROM UserEntity u WHERE u.userName = :userName
            """)
    Optional<UserResponse> findUserResponseByUserName(@Param("userName") String userName);


    @Query("""
            SELECT new com.mandeep.blogify.user.application.dto.UserResponse(
                    u.id,
                    u.userName,
                    u.email,
                    u.role,
                    u.isActive,
                    u.createdAt,
                    u.lastModifiedAt
                )
                FROM UserEntity u WHERE u.id IN :ids
            """)
    List<UserResponse> findUsersById(@Param("ids") List<UUID> ids);


}
