package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PostTitle Value Object")
class PostTitleUnitTest {

    @Test
    @DisplayName("Successfully creates instance with valid title")
    void should_CreatePostTitle_When_Valid() {
        String validTitle = "How to write clean tests";
        PostTitle title = new PostTitle(validTitle);
        assertThat(title.value()).isEqualTo(validTitle);
    }

    @Test
    @DisplayName("Strips whitespace from both ends")
    void should_StripWhitespace_From_Value() {
        PostTitle title = new PostTitle("   Spaces are gone   ");
        assertThat(title.value()).isEqualTo("Spaces are gone");
    }

    @ParameterizedTest(name = "rejects missing title: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void should_ThrowException_When_NullOrBlank(String invalid) {
        assertThatThrownBy(() -> new PostTitle(invalid))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postTitleNullOrBlank().getError());
    }

    @ParameterizedTest(name = "rejects invalid length: {0} chars")
    @ValueSource(strings = {
            "Abcd", // 4 chars (Too short)
            "This title is way too long. It exceeds the one hundred and fifty character limit that we have set in our value object validation logic for blog post titles. This should fail." // 150+ chars
    })
    void should_ThrowException_When_LengthIsInvalid(String invalid) {
        assertThatThrownBy(() -> new PostTitle(invalid))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postTitleInvalidLength().getError());
    }
}