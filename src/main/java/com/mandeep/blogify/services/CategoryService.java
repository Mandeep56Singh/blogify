package com.mandeep.blogify.services;

import com.mandeep.blogify.dto.PaginatedResponseDto;
import com.mandeep.blogify.dto.category.CategoryRequestDto;
import com.mandeep.blogify.dto.category.CategoryResponseDto;
import com.mandeep.blogify.entities.Category;
import com.mandeep.blogify.enums.Constants;
import com.mandeep.blogify.exceptions.ApiError;
import com.mandeep.blogify.exceptions.ApiException;
import com.mandeep.blogify.mapper.CategoryMapper;
import com.mandeep.blogify.repositories.CategoryRepository;
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

        if (pageNumber - 1 < 0) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
        }

        if (pageSize <= 0 || pageSize > Constants.MAX_PAGE_SIZE.getVal()) {
            throw new ApiException(ApiError.INVALID_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<Category> pageUser = categoryRepository.findAll(pageable);

        if (pageNumber - 1 > pageUser.getTotalPages()) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
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
            throw new ApiException(ApiError.CATEGORY_ALREADY_EXITS);
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

    private Category getCategory(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.CATEGORY_NOT_FOUND)
        );
    }
}
