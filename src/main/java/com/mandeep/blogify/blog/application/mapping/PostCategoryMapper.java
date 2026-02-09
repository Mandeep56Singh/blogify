package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.blog.application.dto.CategoryDto;
import com.mandeep.blogify.blog.domain.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostCategoryMapper {
    List<CategoryDto> toCategoryDtoList(Set<Category> categories);
}
