package com.mandeep.blogify.auth.domain.model.entity;

import com.mandeep.blogify.auth.domain.model.valueObject.AuthUserId;
import com.mandeep.blogify.auth.domain.model.valueObject.HashedPassword;
import com.mandeep.blogify.auth.domain.repository.PasswordVerifier;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import lombok.Getter;

import java.util.Objects;

@Getter
public class AuthenticatedUser {

    private final AuthUserId authUserId;
    private final String userName;
    private final Email email;
    private final Role role;
    private final HashedPassword hashedPassword;
    private final boolean active;


    private AuthenticatedUser(AuthUserId authUserId, String userName, Email email, HashedPassword hashedPassword, Role role, boolean active) {
        this.authUserId = authUserId;
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.active = active;
    }

    public static AuthenticatedUser load(
            AuthUserId authUserId, String userName, Email email, HashedPassword hashedPassword, Role role, boolean active
    ) {
        return new AuthenticatedUser(authUserId, userName, email, hashedPassword, role, active);
    }


    public void authenticate(String rawPassword, PasswordVerifier encoder) {
        if (!this.active) {
            throw CommonException.accountBlocked(this.email.value());
        }

        if (!this.hashedPassword.matches(rawPassword, encoder)) {
            throw CommonException.invalidCredentials();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticatedUser that = (AuthenticatedUser) o;
        return Objects.equals(authUserId, that.authUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authUserId);
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "authUserId=" + authUserId +
                ", email=" + email +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}
