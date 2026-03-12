package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.query.repository.CategoryQueryRepository;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryQueryService {

    private final CategoryQueryRepository categoryQueryRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<CategoryResponse> getAllCategories(int pageNumber, int pageSize) {
        log.debug("category.fetch.all.attempt pageNumber={} pageSize={}", pageNumber, pageSize);

        return categoryQueryRepository.getAllCategories(pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        log.debug("category.fetch.attempt id={}", id);

        return categoryQueryRepository.findById(id).orElseThrow(() -> CategoryException.categoryNotFound(new CategoryId(id))

        );
    }
}
