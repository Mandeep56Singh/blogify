package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Category Command Service Integration tests")
class CategoryCommandServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryCommandService categoryCommandService;


    private static final String title = "C language";
    private static final String description = "C is a programming language most often used for operating system.";
    private static final UUID id = UUID.fromString("019d0253-ef34-7361-ba13-0516d43c4ab8");


    private void persistUser(boolean isActive, Role role) {

        String sqlInsertAdmin = """
                INSERT INTO users (id, email, user_name, password, is_active, role, created_at, last_modified_at, version)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        var now = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC);

        jdbcTemplate.update(sqlInsertAdmin,
                id,
                "admin@blogify.com",
                "admin_user",
                "StrongPass@123",
                isActive,
                role.name(),
                now,
                now,
                0L
        );
    }

    private void persistCategory(UUID categoryId, String title) {
        persistCategory(categoryId, title, description, CategoryStatus.ACTIVE);
    }


    private void persistCategory(UUID categoryId, String title, String description, CategoryStatus status) {

        CategoryEntity category = CategoryEntity.builder()
                .id(categoryId)
                .title(title)
                .description(description)
                .status(status)
                .build();

        persist(category);
    }

    @Nested
    @DisplayName("Create category Method Integration tests")
    class CreateCategory {

        @Test
        @DisplayName("Return Id when category created by admin")
        void should_CreateCategory_When_CreatedByAdmin() {

            persistUser(true, Role.ADMIN);

            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);

            UUID categoryId = categoryCommandService.createCategory(categoryRequest);

            Optional<Category> category = categoryRepository.findById(new CategoryId(categoryId));


            assertThat(category).hasValueSatisfying(c -> assertAll(
                    () -> assertThat(c.getCategoryId().value()).isEqualTo(categoryId),
                    () -> assertThat(c.getTitle().value()).isEqualTo(title),
                    () -> assertThat(c.getCategoryStatus().isActive()).isTrue(),
                    () -> assertThat(c.getCategoryStatus().isArchived()).isFalse()
            ));

        }

        //region Admin Fail Test
        @Test
        @DisplayName("Throw Exception when category is being created by non-admin")
        void should_ThrowException_When_NonAdminUser() {

            persistUser(true, Role.USER);
            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);

            assertThatThrownBy(
                    () -> categoryCommandService.createCategory(categoryRequest)
            ).isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.unauthorized().getError());

        }

        @Test
        @DisplayName("Throw Exception when category is being created by in-active user")
        void should_ThrowException_When_InActiveUser() {

            persistUser(false, Role.ADMIN);
            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);

            assertThatThrownBy(
                    () -> categoryCommandService.createCategory(categoryRequest)
            ).isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());

        }

        @Test
        @DisplayName("Throw Exception when category is being created by non-existent user")
        void should_ThrowException_When_UserNotFound() {

            // no need to persist user here, run query against empty db


            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);

            assertThatThrownBy(
                    () -> categoryCommandService.createCategory(categoryRequest)
            ).isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());

        }
        //endregion

        @Test
        @DisplayName("Throw Exception when category title already exists")
        void should_ThrowException_When_CategoryTitleAlreadyExists() {

            persistUser(true, Role.ADMIN);
            persistCategory(UUID.randomUUID(), title);

            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);
            assertThatThrownBy(
                    () -> categoryCommandService.createCategory(categoryRequest)
            ).isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryAlreadyExists(new CategoryTitle(title)).getError());

        }

        @Test
        @DisplayName("Successfully create category when a category with same title is ARCHIVED")
        void should_CreateCategory_When_SameTitleIsArchived() {
            // Arrange
            persistUser(true, Role.ADMIN);
            UUID archivedId = UUID.randomUUID();

            // Create an archived category with the same title we want to use
            persistCategory(archivedId, title, "Old Archived Category", CategoryStatus.ARCHIVED);

            CategoryRequest categoryRequest = new CategoryRequest(title, description, id);

            // Act
            UUID newCategoryId = categoryCommandService.createCategory(categoryRequest);

            // Assert
            Optional<Category> newCategory = categoryRepository.findById(new CategoryId(newCategoryId));

            assertThat(newCategory).hasValueSatisfying(c -> assertAll(
                    () -> assertThat(c.getCategoryId().value()).isNotEqualTo(archivedId),
                    () -> assertThat(c.getTitle().value()).isEqualTo(title),
                    () -> assertThat(c.getCategoryStatus().isActive()).isTrue(),
                    () -> assertThat(c.getCategoryStatus().isArchived()).isFalse()
            ));

        }
    }

    @Nested
    @DisplayName("Update category Integration tests")
    class UpdateCategory {

        @Test
        @DisplayName("Updates category when category exists")
        void should_UpdateCategory_When_CategoryExists() {
            persistUser(true, Role.ADMIN);
            UUID categoryId = UUID.randomUUID();
            persistCategory(categoryId, title);

            String newTitle = "new title";
            CategoryRequest categoryRequest = new CategoryRequest(newTitle, description, id);
            categoryCommandService.updateCategory(categoryId, categoryRequest);

            Optional<Category> category = categoryRepository.findById(new CategoryId(categoryId));

            assertThat(category).hasValueSatisfying(c -> assertAll(
                    () -> assertThat(c.getCategoryId().value()).isEqualTo(categoryId),
                    () -> assertThat(c.getTitle().value()).isEqualTo(newTitle),
                    () -> assertThat(c.getCategoryStatus().isActive()).isTrue(),
                    () -> assertThat(c.getCategoryStatus().isArchived()).isFalse()
            ));
        }

        //region Admin Fail Tests
        @Test
        @DisplayName("Throw Exception when updating category by non-admin")
        void should_ThrowException_When_NonAdminUser() {
            persistUser(true, Role.USER);
            CategoryRequest categoryRequest = new CategoryRequest("Some title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(UUID.randomUUID(), categoryRequest))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.unauthorized().getError());
        }

        @Test
        @DisplayName("Throw Exception when updating category by in-active user")
        void should_ThrowException_When_InActiveUser() {
            persistUser(false, Role.ADMIN);
            CategoryRequest categoryRequest = new CategoryRequest("Some title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(UUID.randomUUID(), categoryRequest))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throw Exception when updating category by non-existent user")
        void should_ThrowException_When_UserNotFound() {
            CategoryRequest categoryRequest = new CategoryRequest("Some title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(UUID.randomUUID(), categoryRequest))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }
        //endregion

        @Test
        @DisplayName("Throw Exception when category to update is not found")
        void should_ThrowException_When_CategoryNotFound() {
            persistUser(true, Role.ADMIN);
            UUID nonExistentCategoryId = UUID.randomUUID();
            CategoryRequest categoryRequest = new CategoryRequest("New Title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(nonExistentCategoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(nonExistentCategoryId)).getError());
        }

        @Test
        @DisplayName("Throw Exception when updating to a title that already exists")
        void should_ThrowException_When_NewTitleAlreadyExists() {
            persistUser(true, Role.ADMIN);

            // Create target category
            UUID targetCategoryId = UUID.randomUUID();
            persistCategory(targetCategoryId, "Original Title");

            // Create another category holding the title we want to change to
            persistCategory(UUID.randomUUID(), "Existing Title");

            CategoryRequest categoryRequest = new CategoryRequest("Existing Title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(targetCategoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryAlreadyExists(new CategoryTitle("Existing Title")).getError());
        }

        @Test
        @DisplayName("Throw Exception when trying to update an archived category")
        void should_ThrowException_When_CategoryIsArchived() {
            persistUser(true, Role.ADMIN);
            UUID archivedCategoryId = UUID.randomUUID();
            persistCategory(archivedCategoryId, title, description, CategoryStatus.ARCHIVED);

            CategoryRequest categoryRequest = new CategoryRequest("New Title", description, id);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(archivedCategoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryArchived(new CategoryId(archivedCategoryId)).getError()); // Adjust specific exception if you have a custom error for this
        }
    }

    @Nested
    @DisplayName("Delete category Method Integration tests")
    class DeleteCategory {

        @Test
        @DisplayName("Successfully archives category when category exists and user is admin")
        void should_DeleteCategory_When_ValidRequest() {
            persistUser(true, Role.ADMIN);
            UUID categoryId = UUID.randomUUID();
            persistCategory(categoryId, title);

            categoryCommandService.deleteCategory(categoryId, id);

            Optional<Category> category = categoryRepository.findById(new CategoryId(categoryId));

            assertThat(category).hasValueSatisfying(c -> assertAll(
                            () -> assertThat(c.getCategoryStatus().isActive()).isFalse(),
                            () -> assertThat(c.getCategoryStatus().isArchived()).isTrue()
                    )
            );
        }

        //region Admin Fail Tests
        @Test
        @DisplayName("Throw Exception when deleting category by non-admin")
        void should_ThrowException_When_NonAdminUser() {
            persistUser(true, Role.USER);

            assertThatThrownBy(() -> categoryCommandService.deleteCategory(UUID.randomUUID(), id))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.unauthorized().getError());
        }

        @Test
        @DisplayName("Throw Exception when deleting category by in-active user")
        void should_ThrowException_When_InActiveUser() {
            persistUser(false, Role.ADMIN);

            assertThatThrownBy(() -> categoryCommandService.deleteCategory(UUID.randomUUID(), id))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotActive().getError());
        }

        @Test
        @DisplayName("Throw Exception when deleting category by non-existent user")
        void should_ThrowException_When_UserNotFound() {
            assertThatThrownBy(() -> categoryCommandService.deleteCategory(UUID.randomUUID(), id))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }
        //endregion

        @Test
        @DisplayName("Throw Exception when category to delete is not found")
        void should_ThrowException_When_CategoryNotFound() {
            persistUser(true, Role.ADMIN);
            UUID nonExistentCategoryId = UUID.randomUUID();

            assertThatThrownBy(() -> categoryCommandService.deleteCategory(nonExistentCategoryId, id))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(nonExistentCategoryId)).getError());
        }

        @Test
        @DisplayName("Do nothing when category is already archived (Idempotency)")
        void should_Succeed_When_CategoryAlreadyArchived() {
            persistUser(true, Role.ADMIN);
            UUID archivedCategoryId = UUID.randomUUID();
            persistCategory(archivedCategoryId, title, description, CategoryStatus.ARCHIVED);

            // Since your Entity checks if it's already archived and returns, this should not throw an exception.
            categoryCommandService.deleteCategory(archivedCategoryId, id);

            Optional<Category> category = categoryRepository.findById(new CategoryId(archivedCategoryId));

            assertThat(category).isPresent();
            assertThat(category.get().getCategoryStatus().isArchived()).isTrue();
        }
    }

}