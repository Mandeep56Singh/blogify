package com.mandeep.blogify.blog.application.query.repository;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.shared.dto.PaginatedResponse;

import java.util.Optional;
import java.util.UUID;

public interface CategoryQueryRepository {
    Optional<CategoryResponse> findById(UUID id);

    PaginatedResponse<CategoryResponse> getAllCategories(int pageNumber, int pageSize);

    boolean isCategoryExistsAndActive(CategoryTitle title);
}
