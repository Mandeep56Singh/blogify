package com.mandeep.blogify.blog.infrastructure.persistence.entity;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.shared.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final CategoryEntity category;

        public Builder() {
            this.category = new CategoryEntity();
        }

        public Builder id(UUID id) {
            category.setId(id);
            return this;
        }

        public Builder title(String title) {
            category.setTitle(title);
            return this;
        }

        public Builder description(String description) {
            category.setDescription(description);
            return this;
        }

        public Builder status(CategoryStatus status) {
            category.setStatus(status);
            return this;
        }

        public CategoryEntity build() {

            if (category.id == null || category.title == null || category.status == null) {
                throw new IllegalArgumentException("Required fields are missing in category");
            }

            return category;
        }

    }
}
