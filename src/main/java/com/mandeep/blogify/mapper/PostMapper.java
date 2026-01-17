package com.mandeep.blogify.mapper;

import com.mandeep.blogify.dto.post.AuthorDto;
import com.mandeep.blogify.dto.post.CategoryDto;
import com.mandeep.blogify.dto.post.PostResponseDto;
import com.mandeep.blogify.entities.Category;
import com.mandeep.blogify.entities.Post;
import com.mandeep.blogify.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {AuthorMapper.class, PostCategoryMapper.class}
)
public interface PostMapper {
    PostResponseDto toDto(Post post);

    List<PostResponseDto> toDtoList(List<Post> posts);
}

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
interface AuthorMapper {
    AuthorDto toDto(User user);
}

@Mapper(componentModel = "spring")
interface PostCategoryMapper {
    List<CategoryDto> toCategoryDtoList(Set<Category> categories);
}

