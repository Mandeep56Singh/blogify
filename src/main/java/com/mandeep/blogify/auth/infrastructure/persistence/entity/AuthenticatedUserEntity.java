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
}
