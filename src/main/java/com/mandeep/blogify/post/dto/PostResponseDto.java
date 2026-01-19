package com.mandeep.blogify.post.dto;

import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        AuthorDto author,
        List<CategoryDto> categories
) {
}
