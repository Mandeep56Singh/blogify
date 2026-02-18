package com.mandeep.blogify.blog.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Payload for creating or updating a blog post.
 */
@Schema(name = "PostRequestDto", description = "Request payload to create or update a blog post")
public record PostRequestDto(

        @Schema(
                description = "Title of the blog post",
                example = "Understanding Microservices Architecture"
        )
        @NotBlank
        String title,

        @Schema(
                description = "Content of the blog post. Must be between 1200 and 10000 characters",
                example = "This is a detailed blog post about microservices...",
                minLength = 1200,
                maxLength = 10000
        )
        @NotBlank
        @Size(min = 1200, max = 10000)
        String content,


        @Schema(
                description = "List of category IDs this post belongs to",
                example = "[1, 2, 5]"
        )
        @NotNull
        List<Long> categoryIds

) {}
