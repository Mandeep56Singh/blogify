package com.mandeep.blogify.common;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Clock;
import java.time.Instant;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastModifiedAt;

    private boolean isDeleted = false;

    private Instant deletedAt = null;

    @Version
    @Column(nullable = false)
    private Long version;

    public void softDelete() {
        if (!isDeleted && deletedAt == null) {
            this.isDeleted = true;
            this.deletedAt = Instant.now(Clock.systemUTC());
        }
    }

    @PrePersist
    public void beforeSave() {
        createdAt = Instant.now(Clock.systemUTC());
        lastModifiedAt = Instant.now(Clock.systemUTC());
    }

    @PreUpdate
    public void beforeUpdate() {
        lastModifiedAt = Instant.now(Clock.systemUTC());
    }

}
