package com.mandeep.blogify.image;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class Image {

    @Id
    @Column(nullable = false, updatable = false, length = 255, columnDefinition = "VARCHAR")
    private String id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    public Image(String id, String fileName, String path, Long size, String contentType) {
        this.id = id;
        this.fileName = fileName;
        this.path = path;
        this.size = size;
        this.contentType = contentType;
    }

    @PrePersist
    public void beforeSave() {
        createdAt = Instant.now(Clock.systemUTC());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(getId(), image.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
