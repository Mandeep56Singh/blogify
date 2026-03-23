package com.mandeep.blogify.user.infrastructure.persistence.entity;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.shared.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {

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
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + getId() +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", isActive=" + isActive +
                ", role='" + role + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", lastModifiedAt=" + getLastModifiedAt() +
                ", version=" + getVersion() +
                '}';
    }

    //region Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final UserEntity user;

        public Builder() {
            this.user = new UserEntity();
        }

        public Builder id(UUID id) {
            user.setId(id);
            return this;
        }

        public Builder email(String email) {
            user.setEmail(email);
            return this;
        }

        public Builder userName(String userName) {
            user.setUserName(userName);
            return this;
        }

        public Builder password(String password) {
            user.setPassword(password);
            return this;
        }

        public Builder isActive(boolean isActive) {
            user.setActive(isActive);
            return this;
        }

        public Builder role(Role role) {
            user.setRole(role);
            return this;
        }

        public UserEntity build() {
            if (user.email == null || user.userName == null || user.password == null || user.role == null) {
                throw new IllegalStateException("Required fields are missing");
            }

            return user;
        }

    }
    //endregion

}
