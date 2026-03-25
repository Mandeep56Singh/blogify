package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CategoryQueryService Integration Tests")
class CategoryQueryServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryQueryService categoryQueryService;

    //region Helper Methods
    private CategoryEntity persistCategory(String title, CategoryStatus status) {
        CategoryEntity category = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description("Description for " + title)
                .status(status)
                .build();
        persist(category);
        return category;
    }
    //endregion

    @Nested
    @DisplayName("getAllCategories()")
    class GetAllCategories {

        @Test
        @DisplayName("Successfully returns paginated list of all active categories")
        void should_ReturnAllActiveCategories() {
            persistCategory("Java", CategoryStatus.ACTIVE);
            persistCategory("Spring", CategoryStatus.ACTIVE);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);

            assertThat(response.items()).hasSize(2);
            assertThat(response.totalItems()).isEqualTo(2);
        }

        @Test
        @DisplayName("Filters out ARCHIVED categories and only returns ACTIVE ones")
        void should_FilterOutArchivedCategories() {
            persistCategory("Active Cat", CategoryStatus.ACTIVE);
            persistCategory("Archived Cat", CategoryStatus.ARCHIVED);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);

            assertThat(response.items()).hasSize(1);
            assertThat(response.items().getFirst().title()).isEqualTo("Active Cat");
        }

        @Test
        @DisplayName("Returns empty response when all categories are archived")
        void should_ReturnEmpty_WhenAllAreArchived() {
            persistCategory("Archived 1", CategoryStatus.ARCHIVED);
            persistCategory("Archived 2", CategoryStatus.ARCHIVED);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);

            assertThat(response.items()).isEmpty();
            assertThat(response.totalItems()).isEqualTo(0);
        }

        @Test
        @DisplayName("Returns empty response when database is empty")
        void should_ReturnEmpty_WhenNoCategoriesExist() {
            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);
            assertThat(response.items()).isEmpty();
        }

        @Test
        @DisplayName("Verifies pagination metadata on boundary")
        void should_VerifyPaginationMetadata() {
            persistCategory("Cat 1", CategoryStatus.ACTIVE);
            persistCategory("Cat 2", CategoryStatus.ACTIVE);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 1);

            assertAll(
                    () -> assertThat(response.items()).hasSize(1),
                    () -> assertThat(response.totalItems()).isEqualTo(2),
                    () -> assertThat(response.totalPages()).isEqualTo(2),
                    () -> assertThat(response.lastPage()).isFalse()
            );
        }

        @Test
        @DisplayName("Returns empty items when requested page is out of bounds")
        void should_ReturnEmpty_WhenPageOutOfBounds() {
            persistCategory("Cat 1", CategoryStatus.ACTIVE);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(5, 10);

            assertThat(response.items()).isEmpty();
            assertThat(response.lastPage()).isTrue();
        }
    }

    @Nested
    @DisplayName("getCategoryById()")
    class GetCategoryById {

        @Test
        @DisplayName("Successfully returns category details for valid active category")
        void should_ReturnCategory_WhenIdIsValid() {
            CategoryEntity category = persistCategory("Java", CategoryStatus.ACTIVE);

            CategoryResponse response = categoryQueryService.getCategoryById(category.getId());

            assertThat(response.title()).isEqualTo("Java");
        }

        @Test
        @DisplayName("Throws CategoryException when category does not exist")
        void should_ThrowException_WhenCategoryDoesNotExist() {
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> categoryQueryService.getCategoryById(randomId))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(randomId)).getError());
        }

        @Test
        @DisplayName("Throws CategoryException when category is archived")
        void should_ThrowException_WhenCategoryIsArchived() {
            CategoryEntity archivedCat = persistCategory("Hidden", CategoryStatus.ARCHIVED);

            // Note: This test assumes your CategoryQueryRepository filters by status = ACTIVE
            assertThatThrownBy(() -> categoryQueryService.getCategoryById(archivedCat.getId()))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(archivedCat.getId())).getError());
        }
    }
}