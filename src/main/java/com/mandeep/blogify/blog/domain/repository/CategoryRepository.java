package com.mandeep.blogify.blog.domain.repository;

import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository {
    void save(Category category);
    void update(Category category);
    Set<CategoryId> findExistingIds(Set<CategoryId> categoryIds);
    boolean existsByTitle(CategoryTitle categoryTitle);
    Optional<Category> findById(CategoryId id);
}
