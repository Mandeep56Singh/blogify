package com.mandeep.blogify.user.application.query;

import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserQueryRepository {
    Optional<UserResponse> findResponseById(UserId userId);
    Optional<UserResponse> findResponseByEmail(Email email);
    Optional<UserResponse> findResponseByUserName(UserName name);
    List<UserResponse> findUsersById(Set<UUID> ids);
    boolean existsByEmail(Email email);

}
