package com.mandeep.blogify.category;

import com.mandeep.blogify.category.dto.CategoryRequestDto;
import com.mandeep.blogify.category.dto.CategoryResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponseDto toDto(Category category);

    Category toEntity(CategoryRequestDto requestDto);

    List<CategoryResponseDto> toDtoList(List<Category> categoryList);
}
