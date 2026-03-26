package com.mandeep.blogify.user.domain.model.entity;

import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import lombok.Getter;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Getter
public class User {

    private final UserId userId;
    private final Email email;
    private final UserName userName;
    private final String password;
    private final Role role;
    private boolean active;
    private final Instant createdAt;


    private User(
            UserId userId,
            UserName userName,
            Email email,
            String password,
            boolean active,
            Role role,
            Instant createdAt
    ) {
        this.password = password;
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.active = active;
        this.role = role;
        this.createdAt = createdAt;
    }

    //region Factories
    // User Registration Factory
    public static User register(
            UserId id,
            UserName userName,
            Email email,
            String password,
            Role role,
            Clock clock
    ) {
        return new User(
                id,
                userName,
                email,
                password,
                true,
                role,
                Instant.now(clock)
        );
    }

    //  DB hydration Factory
    public static User reconstitute(
            UserId userId,
            UserName userName,
            Email userEmail,
            String password,
            boolean active,
            Role role,
            Instant createdAt
    ) {
        return new User(userId, userName, userEmail, password, active, role, createdAt);
    }
    //endregion

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public void deActivate(Role myRole) {

        if (myRole == Role.USER || isAdmin()) {
            throw UserDomainException.forbiddenToDeactivate();
        }

        if (!isActive()) {
            return;
        }

        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }


    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email=" + email +
                ", userName=" + userName +
                ", role=" + role +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
