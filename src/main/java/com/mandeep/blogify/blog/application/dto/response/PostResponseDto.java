package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.ResponsePayload;
import com.mandeep.blogify.user.UserView;

import java.util.List;

public record PostResponseDto(
        Long id,
        String title,
        String content,
        UserView author,
        List<CategoryDto> categories
) implements ResponsePayload {
}
