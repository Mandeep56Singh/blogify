package com.mandeep.blogify.mapper;

import com.mandeep.blogify.dto.category.CategoryRequestDto;
import com.mandeep.blogify.dto.category.CategoryResponseDto;
import com.mandeep.blogify.entities.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryRequestDto requestDto);

    List<CategoryResponseDto> toDtoList(List<Category> categoryList);
}
