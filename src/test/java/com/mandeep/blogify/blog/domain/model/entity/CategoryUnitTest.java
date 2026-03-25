package com.mandeep.blogify.blog.domain.model.entity;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryDescription;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category Domain Model Unit test")
class CategoryUnitTest {

    //region Test Data
    private static final CategoryId A_CATEGORY_ID = new CategoryId(UUID.fromString("019ce66a-7a58-7ebd-b78c-ac88bd154378"));
    private static final CategoryId B_CATEGORY_ID = new CategoryId(UUID.fromString("019ce847-d388-79c9-a6cf-7242cc48e6d8"));
    private static final CategoryTitle A_TITLE = new CategoryTitle("Technology");
    private static final CategoryDescription A_DESCRIPTION = new CategoryDescription("All things tech");
    //endregion

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Creates a new category with ACTIVE status by default")
        void should_CreateCategory_When_ValidDataProvided() {
            // Act
            Category category = Category.create(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION);

            // Assert
            assertAll(
                    () -> assertThat(category.getCategoryId()).isEqualTo(A_CATEGORY_ID),
                    () -> assertThat(category.getTitle()).isEqualTo(A_TITLE),
                    () -> assertThat(category.getDescription()).isEqualTo(A_DESCRIPTION),
                    () -> assertThat(category.getCategoryStatus()).isEqualTo(CategoryStatus.ACTIVE)
            );
        }
    }

    @Nested
    @DisplayName("reconstitute()")
    class Reconstitute {

        @Test
        @DisplayName("Restores category state exactly as provided from persistence")
        void should_RestoreCategory_When_AllDataProvided() {
            // Act
            Category category = Category.reconstitute(
                    A_CATEGORY_ID, A_TITLE, A_DESCRIPTION, CategoryStatus.ARCHIVED
            );

            // Assert
            assertAll(
                    () -> assertThat(category.getCategoryId()).isEqualTo(A_CATEGORY_ID),
                    () -> assertThat(category.getCategoryStatus()).isEqualTo(CategoryStatus.ARCHIVED)
            );
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Updates title and description successfully")
        void should_UpdateCategoryDetails_When_NewValuesProvided() {
            // Arrange
            Category category = Category.create(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION);
            CategoryTitle newTitle = new CategoryTitle("New Title");
            CategoryDescription newDesc = new CategoryDescription("New Description");

            // Act
            category.update(newTitle, newDesc);

            // Assert
            assertAll(
                    () -> assertThat(category.getTitle()).isEqualTo(newTitle),
                    () -> assertThat(category.getDescription()).isEqualTo(newDesc)
            );
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Changes status to ARCHIVED when delete is called")
        void should_ChangeStatusToArchived_When_Deleted() {
            // Arrange
            Category category = Category.create(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION);

            // Act
            category.delete();

            // Assert
            assertThat(category.getCategoryStatus()).isEqualTo(CategoryStatus.ARCHIVED);
        }

        @Test
        @DisplayName("Does nothing if category is already ARCHIVED")
        void should_RemainArchived_When_AlreadyArchived() {
            // Arrange
            Category category = Category.reconstitute(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION, CategoryStatus.ARCHIVED);

            // Act
            category.delete();

            // Assert
            assertThat(category.getCategoryStatus()).isEqualTo(CategoryStatus.ARCHIVED);
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class Equality {

        @Test
        @DisplayName("Equality is based solely on CategoryId")
        void should_BeEqual_When_CategoryIdIsIdentical() {
            // Arrange
            Category first = Category.create(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION);
            Category second = Category.reconstitute(A_CATEGORY_ID, new CategoryTitle("Different"), new CategoryDescription("Diff"), CategoryStatus.ARCHIVED);

            // Assert
            assertAll(
                    () -> assertThat(first).isEqualTo(second),
                    () -> assertThat(first.hashCode()).isEqualTo(second.hashCode())
            );
        }

        @Test
        @DisplayName("Inequality when CategoryIds differ")
        void should_NotBeEqual_When_CategoryIdsDiffer() {
            // Arrange
            Category first = Category.create(A_CATEGORY_ID, A_TITLE, A_DESCRIPTION);
            Category second = Category.create(B_CATEGORY_ID, A_TITLE, A_DESCRIPTION);

            // Assert
            assertThat(first).isNotEqualTo(second);
        }
    }
}