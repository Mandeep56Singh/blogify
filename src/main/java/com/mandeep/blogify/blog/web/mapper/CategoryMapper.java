package com.mandeep.blogify.blog.web.mapper;

import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.web.dto.CategoryWebRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    CategoryRequest toRequest(CategoryWebRequest webRequest, UUID userId);
}
