package com.mandeep.blogify.blog.application.service;

import com.mandeep.blogify.blog.application.dto.request.CategoryRequestDto;
import com.mandeep.blogify.blog.application.dto.response.CategoryResponseDto;
import com.mandeep.blogify.blog.application.mapping.CategoryMapper;
import com.mandeep.blogify.blog.domain.entity.Category;
import com.mandeep.blogify.blog.domain.exceptions.CategoryError;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.AppError;
import com.mandeep.blogify.shared.exceptions.AppProblem;
import com.mandeep.blogify.shared.exceptions.PageError;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Transactional(readOnly = true)
    public ResponseDto<PaginatedResponseDto<CategoryResponseDto>> getAllCategories(
            Integer pageNumber,
            Integer pageSize
    ) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Category> pageUser = categoryRepository.findAll(pageable);

        if (pageNumber - 1 > pageUser.getTotalPages()) {
            return ResponseDto.failure(AppProblem.getDetail(PageError.INVALID_PAGE_NUMBER));
        }

        List<Category> categories = pageUser.getContent();

        List<CategoryResponseDto> categoryResponseDtoList = mapper.toDtoList(categories);

        return ResponseDto.success(
                new PaginatedResponseDto<>(
                        categoryResponseDtoList,
                        pageNumber,
                        pageSize,
                        pageUser.getTotalElements(),
                        pageUser.getTotalPages(),
                        pageUser.isLast()
                )
        );

    }

    @Transactional
    public ResponseDto<CategoryResponseDto> createCategory(CategoryRequestDto requestDto) {

        if (categoryRepository.existsByTitle(requestDto.title())) {
            return ResponseDto.failure(AppProblem.getDetail(CategoryError.CATEGORY_ALREADY_EXITS));
        }

        Category category = new Category(requestDto.title());
        category.setDescription(requestDto.description());
        Category createdCategory = categoryRepository.save(category);
        return ResponseDto.success(mapper.toDto(createdCategory));
    }

    @Transactional
    public ResponseDto<CategoryResponseDto> updateCategory(
             CategoryRequestDto requestDto,
            @NotNull Long id
    ) {
        return getCategory(id).map(
                category -> {
                    category.setTitle(requestDto.title());
                    category.setDescription(requestDto.description());

                    return ResponseDto.success(mapper.toDto(category));
                }
        ).orElseGet(
                () -> ResponseDto.failure(AppProblem.getDetail(CategoryError.CATEGORY_NOT_FOUND))
        );

    }

    @Transactional
    public Optional<AppError> deleteCategory(Long id) {

        return getCategory(id)
                .map(category -> {
                    category.softDelete();
                    return Optional.<AppError>empty();
                })
                .orElse(Optional.of(CategoryError.CATEGORY_NOT_FOUND));
    }


    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> getCategoriesById(List<Long> ids) {
        return categoryRepository.findAllById(ids);
    }
}
