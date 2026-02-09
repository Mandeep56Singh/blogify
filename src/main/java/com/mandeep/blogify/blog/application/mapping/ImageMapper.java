package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.blog.application.dto.ImageDto;
import com.mandeep.blogify.blog.domain.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageMapper {
    ImageDto toDto(Image image);
}
