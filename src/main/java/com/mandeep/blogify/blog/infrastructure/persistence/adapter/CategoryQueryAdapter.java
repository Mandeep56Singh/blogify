package com.mandeep.blogify.blog.infrastructure.persistence.adapter;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.query.repository.CategoryQueryRepository;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.infrastructure.persistence.repository.CategoryJpaRepository;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryRepository {

    private final CategoryJpaRepository jpaRepository;

    @Override
    public Optional<CategoryResponse> findById(UUID id) {

        return jpaRepository.findCategoryById(id);
    }

    @Override
    public PaginatedResponse<CategoryResponse> getAllCategories(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<CategoryResponse> page = jpaRepository.findAllActiveCategories(pageable);

        List<CategoryResponse> pageData = page.getContent().stream().toList();

        return new PaginatedResponse<>(
                pageData,
                pageNumber,
                pageSize,
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public boolean isCategoryExistsAndActive(CategoryTitle title) {
        return jpaRepository.isCategoryExistsAndActive(title.value());
    }


}
