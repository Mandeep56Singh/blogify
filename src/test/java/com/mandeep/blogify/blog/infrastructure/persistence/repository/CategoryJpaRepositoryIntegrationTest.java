package com.mandeep.blogify.blog.infrastructure.persistence.repository;

import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CategoryJpaRepository Integration Tests")
class CategoryJpaRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryJpaRepository repository;

    private CategoryEntity persistCategory(String title, CategoryStatus status) {
        CategoryEntity category = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description("Desc for " + title)
                .status(status)
                .build();
        persist(category);
        return category;
    }

    @Test
    @DisplayName("existsByTitleAndStatus: returns true only on exact match")
    void should_ReturnCorrectExists_When_TitleAndStatusMatch() {
        persistCategory("Java", CategoryStatus.ACTIVE);

        assertThat(repository.existsByTitleAndStatus("Java", CategoryStatus.ACTIVE)).isTrue();
        assertThat(repository.existsByTitleAndStatus("Java", CategoryStatus.ARCHIVED)).isFalse();
        assertThat(repository.existsByTitleAndStatus("Kotlin", CategoryStatus.ACTIVE)).isFalse();
    }

    @Test
    @DisplayName("isCategoryExistsAndActive: returns true only if status is ACTIVE")
    void should_ReturnTrue_OnlyIfStatusIsActive() {
        persistCategory("Active", CategoryStatus.ACTIVE);
        persistCategory("Archived", CategoryStatus.ARCHIVED);

        assertThat(repository.isCategoryExistsAndActive("Active")).isTrue();
        assertThat(repository.isCategoryExistsAndActive("Archived")).isFalse();
        assertThat(repository.isCategoryExistsAndActive("NonExistent")).isFalse();
    }

    @Test
    @DisplayName("findExistingIds: returns only IDs that exist in DB")
    void should_ReturnOnlyExistingIds() {
        CategoryEntity c1 = persistCategory("C1", CategoryStatus.ACTIVE);
        CategoryEntity c2 = persistCategory("C2", CategoryStatus.ACTIVE);
        UUID randomId = UUID.randomUUID();

        Set<UUID> result = repository.findExistingIds(Set.of(c1.getId(), c2.getId(), randomId));

        assertThat(result).hasSize(2).contains(c1.getId(), c2.getId());
    }

    @Test
    @DisplayName("findCategoryById: returns projection for ACTIVE and empty for ARCHIVED")
    void should_ReturnProjection_OnlyForActive() {
        CategoryEntity active = persistCategory("Active", CategoryStatus.ACTIVE);
        CategoryEntity archived = persistCategory("Archived", CategoryStatus.ARCHIVED);

        Optional<CategoryResponse> activeResult = repository.findCategoryById(active.getId());
        Optional<CategoryResponse> archivedResult = repository.findCategoryById(archived.getId());

        assertThat(activeResult).isPresent();
        assertThat(activeResult.get().title()).isEqualTo("Active");
        assertThat(archivedResult).isEmpty();
    }

    @Test
    @DisplayName("findAllActiveCategories: returns paginated, sorted ACTIVE categories only")
    void should_ReturnPaginatedActiveCategoriesOnly() {
        persistCategory("B", CategoryStatus.ACTIVE); // B
        persistCategory("A", CategoryStatus.ACTIVE); // A
        persistCategory("C", CategoryStatus.ARCHIVED); // Filtered out

        // Request page 0, size 10, sorted by title ASC
        Page<CategoryResponse> page = repository.findAllActiveCategories(
                PageRequest.of(0, 10, Sort.by("title").ascending())
        );

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(CategoryResponse::title)
                .containsExactly("A", "B");
    }

    @Test
    @DisplayName("findAllActiveCategories: returns empty page when only archived exist")
    void should_ReturnEmptyPage_WhenOnlyArchivedExist() {
        persistCategory("OnlyArchived", CategoryStatus.ARCHIVED);

        Page<CategoryResponse> page = repository.findAllActiveCategories(PageRequest.of(0, 10));

        assertThat(page.isEmpty()).isTrue();
        assertThat(page.getTotalElements()).isEqualTo(0);
    }
}