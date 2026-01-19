package com.mandeep.blogify.post;

import com.mandeep.blogify.post.dto.AuthorDto;
import com.mandeep.blogify.post.dto.CategoryDto;
import com.mandeep.blogify.post.dto.PostResponseDto;
import com.mandeep.blogify.category.Category;
import com.mandeep.blogify.user.User;
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

