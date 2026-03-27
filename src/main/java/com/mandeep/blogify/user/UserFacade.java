package com.mandeep.blogify.user;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserFacade {
    UUID register(String email, String userName, String password, Role role);

    boolean existsByEmail(String email);

    Optional<UserView> getByEmail(String email);

    Optional<UserView> getUserById(UUID id);

    Map<UUID, UserView> getUsersById(Set<UUID> ids);

    UUID createUser(String email, String userName, String password);

    UUID createAdmin(String email, String userName, String password);

    void deactivateUser(UUID userId, UUID adminId);
}
