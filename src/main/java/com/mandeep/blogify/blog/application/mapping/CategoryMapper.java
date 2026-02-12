package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.blog.application.dto.request.CategoryRequestDto;
import com.mandeep.blogify.blog.application.dto.response.CategoryResponseDto;
import com.mandeep.blogify.blog.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryRequestDto requestDto);

    List<CategoryResponseDto> toDtoList(List<Category> categoryList);
}
