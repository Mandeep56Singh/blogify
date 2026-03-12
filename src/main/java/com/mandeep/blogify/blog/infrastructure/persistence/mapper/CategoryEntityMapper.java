package com.mandeep.blogify.blog.infrastructure.persistence.mapper;

import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryDescription;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityMapper {

    public CategoryEntity toEntity(Category category) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(category.getCategoryId().value());
        categoryEntity.setTitle(category.getTitle().value());
        categoryEntity.setDescription(category.getDescription().value());
        categoryEntity.setStatus(category.getCategoryStatus());
        return categoryEntity;
    }

    public Category toDomain(CategoryEntity categoryEntity) {
        return Category.reconstitute(
                new CategoryId(categoryEntity.getId()),
                new CategoryTitle(categoryEntity.getTitle()),
                new CategoryDescription(categoryEntity.getDescription()),
                categoryEntity.getStatus()
        );
    }
}
