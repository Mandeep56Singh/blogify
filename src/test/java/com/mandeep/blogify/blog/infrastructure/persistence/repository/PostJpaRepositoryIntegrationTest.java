package com.mandeep.blogify.blog.infrastructure.persistence.repository;

import com.mandeep.blogify.blog.domain.model.valueObject.CategoryStatus;
import com.mandeep.blogify.blog.domain.model.valueObject.PostStatus;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.CategoryEntity;
import com.mandeep.blogify.blog.infrastructure.persistence.entity.PostEntity;
import com.mandeep.blogify.integrationTest.base.BaseIntegrationTest;
import com.mandeep.blogify.user.UserFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostJpaRepository Integration Tests")
class PostJpaRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private PostJpaRepository postRepository;

    @Autowired
    private UserFacade userFacade;

    private UUID authorId;
    // Helper to persist data for repository tests

    @BeforeEach
    public void persistUser() {
        authorId = userFacade.createUser("user@blogify.com", "user123", "Strong@123");
    }

    private PostEntity createPost(UUID authorId, String slug, PostStatus status, Set<CategoryEntity> categories) {
        PostEntity post = PostEntity.builder()
                .id(UUID.randomUUID())
                .title("Valid Title Length")
                .slug(slug)
                .content("a".repeat(100))
                .authorId(authorId)
                .status(status)
                .categories(categories)
                .publishAt(status == PostStatus.PUBLISHED ? Instant.now() : null)
                .build();

        persist(post);
        return post;
    }

    @Test
    @DisplayName("existsBySlug: Should return true only for existing slugs")
    void should_ReturnCorrectSlugExistence() {

        createPost(authorId, "my-first-post", PostStatus.PUBLISHED, Set.of());

        assertThat(postRepository.existsBySlug("my-first-post")).isTrue();
        assertThat(postRepository.existsBySlug("non-existent")).isFalse();
    }

    @Test
    @DisplayName("findSlugsByPrefix: Should return matching slugs correctly")
    void should_ReturnMatchingSlugs() {
        createPost(authorId, "java-tutorial", PostStatus.DRAFT, Set.of());
        createPost(authorId, "java-guide", PostStatus.DRAFT, Set.of());
        createPost(authorId, "spring-boot", PostStatus.DRAFT, Set.of());

        Set<String> results = postRepository.findSlugsByPrefix("java%");

        assertThat(results).containsExactlyInAnyOrder("java-tutorial", "java-guide");
        assertThat(results).doesNotContain("spring-boot");
    }

    @Test
    @DisplayName("findIdsOfAllPublished: Should filter by status and paginate")
    void should_ReturnOnlyPublishedIds_WithPagination() {
        createPost(authorId, "pub-1", PostStatus.PUBLISHED, Set.of());
        createPost(authorId, "pub-2", PostStatus.PUBLISHED, Set.of());
        createPost(authorId, "draft-1", PostStatus.DRAFT, Set.of());

        Page<UUID> result = postRepository.findIdsOfAllPublished(PageRequest.of(0, 10), PostStatus.PUBLISHED);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("findPostsWithCategoriesByIds: Should fetch categories without N+1")
    void should_FetchPostsWithCategories() {
        CategoryEntity cat = CategoryEntity.builder()
                .id(UUID.randomUUID())
                .title("a".repeat(5))
                .description("a".repeat(10))
                .status(CategoryStatus.ACTIVE)
                .build();
        persist(cat);

        PostEntity post = createPost(authorId, "joined-post", PostStatus.PUBLISHED, Set.of(cat));

        List<PostEntity> results = postRepository.findPostsWithCategoriesByIds(List.of(post.getId()));

        assertThat(results).hasSize(1);
        // If this line does not throw LazyInitializationException, the FETCH join worked!
        assertThat(results.getFirst().getCategories()).hasSize(1);
    }
}