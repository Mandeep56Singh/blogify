package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CategoryTitle Value Object")
class CategoryTitleUnitTest {

    @Test
    @DisplayName("Successfully creates instance and trims valid title")
    void should_CreateCategoryTitle_When_Valid() {
        String raw = "  Java Development  ";
        CategoryTitle title = new CategoryTitle(raw);

        assertThat(title.value()).isEqualTo("Java Development");
    }

    @ParameterizedTest(name = "rejects missing title: ''{0}''")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void should_ThrowException_When_NullOrBlank(String invalid) {
        assertThatThrownBy(() -> new CategoryTitle(invalid))
                .isInstanceOf(CategoryException.class)
                .extracting(ex -> ((CategoryException) ex).getError())
                .isEqualTo(CategoryException.categoryTitleNullOrBlank().getError());
    }

    @Test
    @DisplayName("Throws exception when title exceeds 120 characters")
    void should_ThrowException_When_TooLong() {
        String longTitle = "a".repeat(121);

        assertThatThrownBy(() -> new CategoryTitle(longTitle))
                .isInstanceOf(CategoryException.class)
                .extracting(ex -> ((CategoryException) ex).getError())
                .isEqualTo(CategoryException.categoryTitleInvalidLength().getError());
    }

    @Test
    @DisplayName("Throws exception if title becomes empty after trimming")
    void should_ThrowException_When_EmptyAfterTrim() {
        // This targets the specific 'trimmed.isEmpty()' check in your constructor
        String onlySpaces = "     ";

        assertThatThrownBy(() -> new CategoryTitle(onlySpaces))
                .isInstanceOf(CategoryException.class)
                .extracting(ex -> ((CategoryException) ex).getError())
                .isEqualTo(CategoryException.categoryTitleNullOrBlank().getError());
    }
}