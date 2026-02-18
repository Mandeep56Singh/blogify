package com.mandeep.blogify.blog.application.dto.response;

import com.mandeep.blogify.shared.dto.AuthorView;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PostItemDto", description = "Represents a blog post item in Page of Posts")
public record PostItemDto (
        @Schema(description = "Unique ID of the post", example = "1")
        Long id,

        @Schema(description = "Title of the blog post", example = "Understanding Microservices Architecture")
        String title,

        @Schema(description = "Author of the post")
        AuthorView author,

        @ArraySchema(
                schema = @Schema(description = "List of categories this post belongs to"),
                arraySchema = @Schema(description = "Categories array")
        )
        List<CategoryDto> categories
) {
}
