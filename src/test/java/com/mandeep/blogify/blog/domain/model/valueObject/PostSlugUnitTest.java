package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PostSlug Value Object")
class PostSlugUnitTest {


    @ParameterizedTest(name = "accepts valid slug: {0}")
    @ValueSource(strings = {"my-first-post", "post-123", "simple-slug", "a-b-c"})
    void should_CreatePostSlug_When_Valid(String value) {
        PostSlug slug = new PostSlug(value);
        assertThat(slug.value()).isEqualTo(value);
    }

    @ParameterizedTest(name = "rejects invalid format: {0}")
    @ValueSource(strings = {
            "My-Post",        // Uppercase
            "my--post",      // Double hyphen
            "-my-post",      // Leading hyphen
            "my-post-",      // Trailing hyphen
            "my_post",       // Underscore
            "post!@#"        // Special chars
    })
    void should_ThrowException_When_FormatIsInvalid(String invalid) {


        assertThatThrownBy(() -> new PostSlug(invalid))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postSlugInvalidFormat().getError());
    }

    @Test
    void should_ThrowException_When_TooShort() {
        assertThatThrownBy(() -> new PostSlug("abc"))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postSlugInvalidLength().getError());
    }
}