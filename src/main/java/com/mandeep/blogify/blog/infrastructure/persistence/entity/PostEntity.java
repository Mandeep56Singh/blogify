package com.mandeep.blogify.blog.infrastructure.persistence.entity;

import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.shared.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Table(name = "posts")
@Entity
@Getter
@Setter
@NoArgsConstructor()
public class PostEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 150)
    private String slug;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "author_id", nullable = false, updatable = false)
    private UUID authorId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_categories",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<CategoryEntity> categories = new HashSet<>();

    @Column(name = "published_at")
    private Instant publishedAt;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PostEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PostEntity{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", authorId=" + authorId +
                ", status='" + status + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getLastModifiedAt() +
                ", version=" + getVersion() +
                '}';
    }
}
