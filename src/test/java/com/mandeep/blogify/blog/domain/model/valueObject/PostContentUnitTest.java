package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PostContent Value Object")
class PostContentUnitTest {

    @Test
    @DisplayName("Successfully creates instance with valid content length")
    void should_CreatePostContent_When_Valid() {
        // A string exactly 100 characters long
        String validContent = "a".repeat(100);
        PostContent content = new PostContent(validContent);
        assertThat(content.value()).isEqualTo(validContent);
    }

    @Test
    @DisplayName("Strips whitespace from the content")
    void should_StripWhitespace_From_Value() {
        String raw = "   This is some content that needs to be long enough to pass the validation check for blog posts.   ";
        // Ensure the content is long enough even after stripping
        String padded = " ".repeat(10) + "a".repeat(100) + " ".repeat(10);

        PostContent content = new PostContent(padded);
        assertThat(content.value()).isEqualTo("a".repeat(100));
    }

    @ParameterizedTest(name = "rejects missing content: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void should_ThrowException_When_NullOrBlank(String invalid) {
        assertThatThrownBy(() -> new PostContent(invalid))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postContentNullOrEmpty().getError());
    }

    @Test
    @DisplayName("Throws exception when content is below 100 characters")
    void should_ThrowException_When_TooShort() {
        String shortContent = "Too short";

        assertThatThrownBy(() -> new PostContent(shortContent))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postContentInvalidLength().getError());
    }

    @Test
    @DisplayName("Throws exception when content exceeds 20,000 characters")
    void should_ThrowException_When_TooLong() {
        String giantContent = "a".repeat(20_001);

        assertThatThrownBy(() -> new PostContent(giantContent))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postContentInvalidLength().getError());
    }
}