package com.mandeep.blogify.blog.infrastructure.persistence.entity;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.shared.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Table(name = "categories")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class CategoryEntity extends BaseEntity {

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CategoryEntity that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CategoryEntity{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getLastModifiedAt() +
                ", version=" + getVersion() +
                '}';
    }
}
