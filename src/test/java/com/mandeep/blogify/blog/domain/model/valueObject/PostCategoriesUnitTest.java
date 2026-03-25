package com.mandeep.blogify.blog.domain.model.valueObject;

import com.mandeep.blogify.blog.domain.exceptions.PostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PostCategories Value Object")
class PostCategoriesUnitTest {

    @Test
    @DisplayName("Successfully creates instance with valid category IDs")
    void should_CreatePostCategories_When_Valid() {
        Set<CategoryId> categories = Set.of(new CategoryId(UUID.randomUUID()));

        PostCategories postCategories = new PostCategories(categories);

        assertThat(postCategories.value()).hasSize(1);
    }

    @Test
    @DisplayName("Throws exception when category set is null")
    void should_ThrowException_When_Null() {
        assertThatThrownBy(() -> new PostCategories(null))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postCategoriesNullOrEmpty().getError());
    }

    @Test
    @DisplayName("Throws exception when category set is empty")
    void should_ThrowException_When_Empty() {
        assertThatThrownBy(() -> new PostCategories(Set.of()))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postCategoriesNullOrEmpty().getError());
    }

    @Test
    @DisplayName("Throws exception when set contains a null element")
    void should_ThrowException_When_ContainsNull() {
        // Using HashSet because Set.of() doesn't allow nulls anyway
        Set<CategoryId> categoriesWithNull = new HashSet<>();
        categoriesWithNull.add(null);

        assertThatThrownBy(() -> new PostCategories(categoriesWithNull))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postCategoriesNullOrEmpty().getError());
    }

    @Test
    @DisplayName("Ensures the internal set is an immutable copy")
    void should_BeImmutable_When_Created() {
        Set<CategoryId> original = new HashSet<>();
        CategoryId id = new CategoryId(UUID.randomUUID());
        original.add(id);

        PostCategories postCategories = new PostCategories(original);

        // Attempting to modify the original set should not affect the Value Object
        original.add(new CategoryId(UUID.randomUUID()));

        assertThat(postCategories.value()).hasSize(1);
        assertThatThrownBy(() -> postCategories.value().add(new CategoryId(UUID.randomUUID())))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("Successfully creates instance with multiple valid category IDs")
    void should_CreatePostCategories_When_MultipleValid() {
        Set<CategoryId> categories = Set.of(
                new CategoryId(UUID.randomUUID()),
                new CategoryId(UUID.randomUUID())
        );
        PostCategories postCategories = new PostCategories(categories);
        assertThat(postCategories.value()).hasSize(2);
    }

    @Test
    @DisplayName("Throws exception when set contains mix of valid and null elements")
    void should_ThrowException_When_ContainsMixedNullAndValid() {
        Set<CategoryId> mixed = new HashSet<>();
        mixed.add(new CategoryId(UUID.randomUUID()));
        mixed.add(null);

        assertThatThrownBy(() -> new PostCategories(mixed))
                .isInstanceOf(PostException.class)
                .extracting(ex -> ((PostException) ex).getError())
                .isEqualTo(PostException.postCategoriesNullOrEmpty().getError());
    }

    @Test
    @DisplayName("Handles TreeSet without NPE during null element check")
    void should_HandleTreeSet_Without_NPE() {
        Set<CategoryId> treeSet = new TreeSet<>(Comparator.comparing(c -> c.value().toString()));
        treeSet.add(new CategoryId(UUID.randomUUID()));

        assertThatCode(() -> new PostCategories(treeSet))
                .doesNotThrowAnyException();
    }
}