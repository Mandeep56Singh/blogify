package com.mandeep.blogify.mapper;

import com.mandeep.blogify.dto.ImageDto;
import com.mandeep.blogify.entities.Image;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ImageMapper {
    ImageDto toDto(Image image);
}
