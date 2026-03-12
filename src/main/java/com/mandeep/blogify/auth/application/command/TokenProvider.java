package com.mandeep.blogify.auth.application.command;

import com.mandeep.blogify.auth.application.dto.TokenInfo;
import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;

public interface TokenProvider {
    TokenInfo generateToken(AuthUserId id, Role role);
}
