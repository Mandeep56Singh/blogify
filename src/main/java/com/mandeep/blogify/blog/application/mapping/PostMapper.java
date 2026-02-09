package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.blog.application.dto.PostResponseDto;
import com.mandeep.blogify.blog.domain.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    PostResponseDto toDto(Post post);

    List<PostResponseDto> toDtoList(List<Post> posts);
}

