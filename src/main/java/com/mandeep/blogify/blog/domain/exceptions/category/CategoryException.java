package com.mandeep.blogify.blog.domain.exceptions.category;

import lombok.Getter;

@Getter
public class CategoryException extends RuntimeException {

    private final CategoryError categoryError;

    public CategoryException(CategoryError categoryError) {
        this.categoryError = categoryError;
    }
}
