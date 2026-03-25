package com.mandeep.blogify.blog.domain.model.entity;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryDescription;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Category {

    // Getters
    private final CategoryId categoryId;
    private CategoryTitle title;
    private CategoryDescription description;
    private CategoryStatus categoryStatus;

    private Category(
            CategoryId categoryId,
            CategoryTitle title,
            CategoryDescription description,
            CategoryStatus status
    ) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.categoryStatus = status;
    }

    public static Category create(
            CategoryId categoryId,
            CategoryTitle title,
            CategoryDescription description) {

        return new Category(
                categoryId,
                title,
                description,
                CategoryStatus.ACTIVE
        );
    }

    public static Category reconstitute(
            CategoryId categoryId,
            CategoryTitle title,
            CategoryDescription description,
            CategoryStatus status
    ) {
        return new Category(categoryId, title, description, status);
    }

    public void update(CategoryTitle newTitle, CategoryDescription newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }

    public void delete() {
        if (categoryStatus.isArchived()) {
            return;
        }

        this.categoryStatus = CategoryStatus.ARCHIVED;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryId, category.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(categoryId);
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", title=" + title +
                ", description=" + description +
                ", categoryStatus=" + categoryStatus +
                '}';
    }
}