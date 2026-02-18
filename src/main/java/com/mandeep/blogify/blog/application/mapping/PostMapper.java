package com.mandeep.blogify.blog.application.mapping;

import com.mandeep.blogify.auth.AuthenticatedUserView;
import com.mandeep.blogify.blog.application.dto.response.PostItemDto;
import com.mandeep.blogify.blog.application.dto.response.PostResponseDto;
import com.mandeep.blogify.blog.domain.entity.Post;
import com.mandeep.blogify.shared.dto.AuthorView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "author", source = "author")
    PostResponseDto toPostDto(Post post, AuthorView author);

    @Mapping(target = "id", source = "post.id")
    @Mapping(target = "author", source = "author")
    PostItemDto toItemDto(Post post, AuthorView author);

    AuthorView toAuthor(AuthenticatedUserView userView);
}

