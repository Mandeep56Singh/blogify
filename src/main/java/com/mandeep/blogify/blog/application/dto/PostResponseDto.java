package com.mandeep.blogify.blog.application.dto;

import com.mandeep.blogify.user.UserView;

import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        UserView author,
        List<CategoryDto> categories
) {
}
