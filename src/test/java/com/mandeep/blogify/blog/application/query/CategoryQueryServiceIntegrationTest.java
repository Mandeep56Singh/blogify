package com.mandeep.blogify.blog.application.query;

import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.dto.PaginatedResponse;
import com.mandeep.blogify.user.UserFacade;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private CategoryCommandService categoryCommandService;

    @Autowired
    private UserFacade userFacade;

    private UUID adminId;

    //region Helper Methods

    @BeforeEach
    public void persistAdmin() {
        adminId = userFacade.createAdmin("admin@blogify.com", "admin", "Strong@123");
    }

    private UUID persistCategory(String title, UUID adminId) {
        return categoryCommandService.createCategory(new CategoryRequest(
                title,
                "description",
                adminId
        ));
    }
    //endregion

    @Nested
    @DisplayName("getAllCategories()")
    class GetAllCategories {

        @Test
        @DisplayName("Successfully returns paginated list of all active categories")
        void should_ReturnAllActiveCategories() {
            persistCategory("Java", adminId);
            persistCategory("Spring", adminId);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);

            assertThat(response.items()).hasSize(2);
            assertThat(response.totalItems()).isEqualTo(2);
        }

        @Test
        @DisplayName("Filters out ARCHIVED categories and only returns ACTIVE ones")
        void should_FilterOutArchivedCategories() {
            persistCategory("Active Cat", adminId);

            UUID categoryId = persistCategory("Archived Cat", adminId);
            categoryCommandService.deleteCategory(categoryId, adminId);

            PaginatedResponse<CategoryResponse> response = categoryQueryService.getAllCategories(0, 10);

            assertThat(response.items()).hasSize(1);
            assertThat(response.items().getFirst().title()).isEqualTo("Active Cat");
        }

        @Test
        @DisplayName("Returns empty response when all categories are archived")
        void should_ReturnEmpty_WhenAllAreArchived() {
            UUID categoryId1 = persistCategory("Archived 1", adminId);
            UUID categoryId2 = persistCategory("Archived 2", adminId);

            categoryCommandService.deleteCategory(categoryId1, adminId);
            categoryCommandService.deleteCategory(categoryId2, adminId);


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
            persistCategory("Cat 1", adminId);
            persistCategory("Cat 2", adminId);

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
            persistCategory("Cat 1", adminId);

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
            UUID categoryId = persistCategory("Java", adminId);

            CategoryResponse response = categoryQueryService.getCategoryById(categoryId);

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
            UUID categoryId = persistCategory("Hidden", adminId);
            categoryCommandService.deleteCategory(categoryId, adminId);

            // Note: This test assumes your CategoryQueryRepository filters by status = ACTIVE
            assertThatThrownBy(() -> categoryQueryService.getCategoryById(categoryId))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(categoryId)).getError());
        }
    }
}