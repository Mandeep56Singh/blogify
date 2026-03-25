package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;

import java.util.Objects;
import java.util.Set;

public record PostCategories(Set<CategoryId> value) {

    public PostCategories {
        if (value == null || value.isEmpty()) {
            throw PostException.postCategoriesNullOrEmpty();
        }


        if (value.stream().anyMatch(Objects::isNull)) {
            throw PostException.postCategoriesNullOrEmpty();
        }

        // Defensive copy to ensure immutability
        value = Set.copyOf(value);
    }
}
