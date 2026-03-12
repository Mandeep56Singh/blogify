package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;

import java.util.Set;

public record PostCategories(Set<CategoryId> value) {

    public PostCategories {
        if (value == null || value.isEmpty() || value.contains(null)) {
            throw PostException.postCategoriesNullOrEmpty();
        }

        value = Set.copyOf(value);
     }
}
