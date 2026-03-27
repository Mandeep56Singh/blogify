package com.mandeep.blogify.blog.application.command;

import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.domain.exceptions.AccountException;
import com.mandeep.blogify.blog.domain.exceptions.CategoryException;
import com.mandeep.blogify.blog.domain.model.entity.Category;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryId;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
import com.mandeep.blogify.blog.domain.repository.CategoryRepository;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.user.UserFacade;
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

    @Autowired
    private UserFacade userFacade;


    private static final String title = "C language";
    private static final String description = "C is a programming language most often used for operating system.";

    private UUID persistUser() {
        return userFacade.createUser("user@blogify.com", "user123", "Strong@123");
    }

    private UUID persistAdmin() {
        return userFacade.createAdmin("admin@blogify.com", "admin", "Strong@123");
    }

    private UUID persistCategory(UUID adminId) {
        return categoryCommandService.createCategory(new CategoryRequest(
                title,
                description,
                adminId
        ));
    }

    @Nested
    @DisplayName("Create category Method Integration tests")
    class CreateCategory {

        @Test
        @DisplayName("Return Id when category created by admin")
        void should_CreateCategory_When_CreatedByAdmin() {

            UUID userId = persistAdmin();

            CategoryRequest categoryRequest = new CategoryRequest(title, description, userId);

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

            UUID userId = persistUser();
            CategoryRequest categoryRequest = new CategoryRequest(title, description, userId);

            assertThatThrownBy(
                    () -> categoryCommandService.createCategory(categoryRequest)
            ).isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied().getError());
        }

        @Test
        @DisplayName("Throw Exception when category is being created by non-existent user")
        void should_ThrowException_When_UserNotFound() {

            // no need to persist user here, run query against empty db

            UUID userId = UUID.randomUUID();
            CategoryRequest categoryRequest = new CategoryRequest(title, description, userId);

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

            UUID userId = persistAdmin();
            persistCategory(userId);

            CategoryRequest categoryRequest = new CategoryRequest(title, description, userId);
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
            UUID userId = persistAdmin();
            UUID categoryId = persistCategory(userId);

            // delete category
            categoryCommandService.deleteCategory(categoryId, userId);

            CategoryRequest categoryRequest = new CategoryRequest(title, description, userId);

            // Act
            UUID newCategoryId = categoryCommandService.createCategory(categoryRequest);

            // Assert
            Optional<Category> newCategory = categoryRepository.findById(new CategoryId(newCategoryId));

            assertThat(newCategory).hasValueSatisfying(c -> assertAll(
                    () -> assertThat(c.getCategoryId().value()).isNotEqualTo(categoryId),
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
            UUID userId = persistAdmin();
            UUID categoryId = persistCategory(userId);

            String newTitle = "new title";
            CategoryRequest categoryRequest = new CategoryRequest(newTitle, description, userId);
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
            UUID userId = persistUser();
            CategoryRequest categoryRequest = new CategoryRequest("Some title", description, userId);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(UUID.randomUUID(), categoryRequest))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied().getError());
        }


        @Test
        @DisplayName("Throw Exception when updating category by non-existent user")
        void should_ThrowException_When_UserNotFound() {
            CategoryRequest categoryRequest = new CategoryRequest("Some title", description, UUID.randomUUID());

            assertThatThrownBy(() -> categoryCommandService.updateCategory(UUID.randomUUID(), categoryRequest))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }
        //endregion

        @Test
        @DisplayName("Throw Exception when category to update is not found")
        void should_ThrowException_When_CategoryNotFound() {
            UUID userId = persistAdmin();
            UUID nonExistentCategoryId = UUID.randomUUID();
            CategoryRequest categoryRequest = new CategoryRequest("New Title", description, userId);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(nonExistentCategoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(nonExistentCategoryId)).getError());
        }

        @Test
        @DisplayName("Throw Exception when updating to a title that already exists")
        void should_ThrowException_When_NewTitleAlreadyExists() {
            UUID userId = persistAdmin();

            // Create target category
            UUID targetCategoryId = persistCategory(userId);

            // Create another category holding the title we want to change to
            CategoryRequest categoryRequest = new CategoryRequest("Existing Title", description, userId);
            categoryCommandService.createCategory(categoryRequest);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(targetCategoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryAlreadyExists(new CategoryTitle("Existing Title")).getError());
        }

        @Test
        @DisplayName("Throw Exception when trying to update an archived category")
        void should_ThrowException_When_CategoryIsArchived() {
            UUID userId = persistAdmin();
            UUID categoryId = persistCategory(userId);

            // delete category
            categoryCommandService.deleteCategory(categoryId, userId);

            CategoryRequest categoryRequest = new CategoryRequest("New Title", description, userId);

            assertThatThrownBy(() -> categoryCommandService.updateCategory(categoryId, categoryRequest))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryArchived(new CategoryId(categoryId)).getError()); // Adjust specific exception if you have a custom error for this
        }
    }

    @Nested
    @DisplayName("Delete category Method Integration tests")
    class DeleteCategory {

        @Test
        @DisplayName("Successfully archives category when category exists and user is admin")
        void should_DeleteCategory_When_ValidRequest() {
            UUID userId = persistAdmin();
            UUID categoryId = persistCategory(userId);

            categoryCommandService.deleteCategory(categoryId, userId);

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
            UUID userId = persistUser();

            assertThatThrownBy(() -> categoryCommandService.deleteCategory(UUID.randomUUID(), userId))
                    .isInstanceOf(CommonException.class)
                    .extracting(ex -> ((CommonException) ex).getError())
                    .isEqualTo(CommonException.accessDenied().getError());
        }


        @Test
        @DisplayName("Throw Exception when deleting category by non-existent user")
        void should_ThrowException_When_UserNotFound() {
            assertThatThrownBy(() -> categoryCommandService.deleteCategory(UUID.randomUUID(), UUID.randomUUID()))
                    .isInstanceOf(AccountException.class)
                    .extracting(ex -> ((AccountException) ex).getError())
                    .isEqualTo(AccountException.accountNotFound().getError());
        }
        //endregion

        @Test
        @DisplayName("Throw Exception when category to delete is not found")
        void should_ThrowException_When_CategoryNotFound() {
            UUID userId = persistAdmin();
            UUID nonExistentCategoryId = UUID.randomUUID();

            assertThatThrownBy(() -> categoryCommandService.deleteCategory(nonExistentCategoryId, userId))
                    .isInstanceOf(CategoryException.class)
                    .extracting(ex -> ((CategoryException) ex).getError())
                    .isEqualTo(CategoryException.categoryNotFound(new CategoryId(nonExistentCategoryId)).getError());
        }

        @Test
        @DisplayName("Do nothing when category is already archived (Idempotency)")
        void should_Succeed_When_CategoryAlreadyArchived() {
            UUID userId = persistAdmin();
            UUID categoryId = persistCategory(userId);

            // delete category
            categoryCommandService.deleteCategory(categoryId, userId);

            // Since your Entity checks if it's already archived and returns, this should not throw an exception.
            categoryCommandService.deleteCategory(categoryId, userId);

            Optional<Category> category = categoryRepository.findById(new CategoryId(categoryId));

            assertThat(category).isPresent();
            assertThat(category.get().getCategoryStatus().isArchived()).isTrue();
        }
    }

}