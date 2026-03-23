package com.mandeep.blogify.auth.infrastructure.persistence.entity;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.util.Objects;
import java.util.UUID;

@Entity
@Immutable // remove it when creating account lock feature
@Table(name = "auth_credentials")
@Getter
@Setter //`@Setter` for Hibernate to hydrate the object when reading
public class AuthenticatedUserEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AuthenticatedUserEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    //region Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final AuthenticatedUserEntity authenticatedUser;

        public Builder() {
            this.authenticatedUser = new AuthenticatedUserEntity();
        }

        public Builder id(UUID id) {
            authenticatedUser.setId(id);
            return this;
        }

        public Builder email(String email) {
            authenticatedUser.setEmail(email);
            return this;
        }

        public Builder username(String username) {
            authenticatedUser.setUserName(username);
            return this;
        }

        public Builder password(String password) {
            authenticatedUser.setPassword(password);
            return this;
        }

        public Builder active(boolean active) {
            authenticatedUser.setActive(active);
            return this;
        }

        public Builder role(Role role) {
            authenticatedUser.setRole(role);
            return this;
        }

        public AuthenticatedUserEntity build() {
            if (
                    authenticatedUser.id == null ||
                            authenticatedUser.userName == null ||
                            authenticatedUser.email == null || authenticatedUser.role == null
            ) {
                throw new IllegalArgumentException("Required fields are missing");
            }

            return authenticatedUser;
        }
    }
    //endregion
}
