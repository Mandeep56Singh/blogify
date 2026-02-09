package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.blog.application.dto.CategoryRequestDto;
import com.mandeep.blogify.blog.application.dto.CategoryResponseDto;
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
