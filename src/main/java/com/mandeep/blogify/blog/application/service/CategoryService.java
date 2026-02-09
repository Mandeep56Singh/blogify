package com.mandeep.blogify.blog.application.service;

import com.mandeep.blogify.blog.application.dto.CategoryRequestDto;
import com.mandeep.blogify.blog.application.dto.CategoryResponseDto;
import com.mandeep.blogify.blog.application.mapping.CategoryMapper;
import com.mandeep.blogify.blog.domain.entity.Category;
import com.mandeep.blogify.blog.domain.exceptions.category.CategoryError;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.exceptions.ApiException;
import com.mandeep.blogify.shared.exceptions.PageError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Transactional(readOnly = true)
    public PaginatedResponseDto<CategoryResponseDto> getAllCategories(Integer pageNumber, Integer pageSize) {

        AppUtils.validatePage(pageNumber-1, pageSize);

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Category> pageUser = categoryRepository.findAll(pageable);

        if (pageNumber - 1 > pageUser.getTotalPages()) {
            throw new ApiException(PageError.INVALID_PAGE_NUMBER);
        }

        List<Category> categories = pageUser.getContent();

        List<CategoryResponseDto> categoryResponseDtoList = mapper.toDtoList(categories);

        return new PaginatedResponseDto<>(
                categoryResponseDtoList,
                pageNumber,
                pageSize,
                pageUser.getTotalElements(),
                pageUser.getTotalPages(),
                pageUser.isLast()
        );

    }

    @Transactional
    public CategoryResponseDto createCategory(@Valid CategoryRequestDto requestDto) {

        if (categoryRepository.existsByTitle(requestDto.title())) {
            throw new ApiException(CategoryError.CATEGORY_ALREADY_EXITS);
        }
        Category category = new Category(requestDto.title());
        category.setDescription(requestDto.description());
        Category createdCategory = categoryRepository.save(category);
        return mapper.toDto(createdCategory);
    }

    @Transactional
    public CategoryResponseDto updateCategory(@Valid CategoryRequestDto requestDto, @NotNull Long id) {
        Category category = getCategory(id);
        category.setTitle(requestDto.title());
        category.setDescription(requestDto.description());

        return mapper.toDto(category);
    }

    @Transactional
    public void deleteCategory(@NotNull Long id) {
        Category category = getCategory(id);
        category.softDelete();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ApiException(CategoryError.CATEGORY_NOT_FOUND)
        );
    }

    public List<Category> getCategoriesById(List<Long> ids) {
        return categoryRepository.findAllById(ids);
    }
}
