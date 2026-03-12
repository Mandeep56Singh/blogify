package com.mandeep.blogify.user.domain.repository;

import com.mandeep.blogify.user.domain.model.valueobjects.UserId;

public interface UserIdentityGenerator {
    UserId nextUserId();
}
