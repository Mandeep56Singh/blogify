package com.mandeep.blogify.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    protected UUID id;

    @Column(name = "created_at", nullable = false, updatable = false)
    protected Instant createdAt;

    @Column(name = "last_modified_at")
    protected Instant lastModifiedAt;

    @Version
    protected Long version;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now(Clock.systemUTC());
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.lastModifiedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = Instant.now(Clock.systemUTC());
    }

}