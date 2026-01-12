package com.mandeep.blogify.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public sealed class Base permits User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
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

}
