package com.mandeep.blogify.blog.domain.exceptions.post;

import lombok.Getter;

@Getter
public class PostException extends RuntimeException {
    private final PostError postError;

    public PostException(PostError postError) {
        this.postError = postError;
    }
}
