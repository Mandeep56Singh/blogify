package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("CategoryDescription Value Object")
class CategoryDescriptionUnitTest {

    @Test
    @DisplayName("Successfully creates instance and strips valid description")
    void should_CreateCategoryDescription_When_Valid() {
        String raw = "  This is a valid category description.  ";
        CategoryDescription description = new CategoryDescription(raw);

        assertThat(description.value()).isEqualTo("This is a valid category description.");
    }

    @Test
    @DisplayName("Converts null input to an empty string")
    void should_ConvertNull_To_EmptyString() {
        CategoryDescription description = new CategoryDescription(null);

        assertThat(description.value()).isEqualTo("");
    }

    @Test
    @DisplayName("Throws exception when description exceeds 1000 characters")
    void should_ThrowException_When_TooLong() {
        String longDesc = "a".repeat(1001);

        assertThatThrownBy(() -> new CategoryDescription(longDesc))
                .isInstanceOf(CategoryException.class)
                .extracting(ex -> ((CategoryException) ex).getError())
                .isEqualTo(CategoryException.categoryDescriptionTooLong().getError());
    }

    @Test
    @DisplayName("Accepts description that is exactly 1000 characters")
    void should_Accept_When_LengthIsExactlyLimit() {
        String limitDesc = "a".repeat(1000);
        CategoryDescription description = new CategoryDescription(limitDesc);

        assertThat(description.value()).hasSize(1000);
    }
}