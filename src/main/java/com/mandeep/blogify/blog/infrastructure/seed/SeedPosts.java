package com.mandeep.blogify.blog.infrastructure.seed;

import com.mandeep.blogify.blog.application.command.PostCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryResponse;
import com.mandeep.blogify.blog.application.dto.PostRequest;
import com.mandeep.blogify.blog.application.query.repository.CategoryQueryRepository;
import com.mandeep.blogify.blog.application.query.repository.PostQueryRepository;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("dev")
@Order(4)
@RequiredArgsConstructor
@Slf4j
public final class SeedPosts implements ApplicationRunner {

    private final PostCommandService postCommandService;
    private final PostQueryRepository postQueryRepository;
    private final CategoryQueryRepository categoryQueryRepository;
    private final UserFacade userFacade;

    @Value("${seed.post_count:100}")
    private int postCount;

    @Value("${seed.user_count:10}")
    private int userCount;

    // Long enough to always clear the PostContent minimum of 100 chars.
    private static final String CONTENT = """
            Seed content for this post. This is placeholder content used for \
            development and testing purposes only. It is long enough to satisfy \
            the minimum content length enforced by the PostContent value object.\
            """;

    @Override
    public void run(ApplicationArguments args) {

        log.debug("posts.seed.attempt count={}", postCount);

        if (postQueryRepository.existsBySlug("post-1")) {
            log.info("posts.seed.skipped reason='posts already seeded'");
            return;
        }

        List<UUID> categoryIds = loadCategoryIds();
        if (categoryIds.isEmpty()) {
            log.warn("posts.seed.aborted reason='no active categories found — ensure SeedCategories (Order 3) ran first'");
            return;
        }

        List<UUID> authorIds = loadAuthorIds();
        if (authorIds.isEmpty()) {
            log.warn("posts.seed.aborted reason='no seeded users found — ensure SeedUsers (Order 2) ran first'");
            return;
        }

        log.info("posts.seed.start count={} authors={} categories={}",
                postCount, authorIds.size(), categoryIds.size());

        Random random = new Random(42L);
        int created = 0;

        for (int i = 1; i <= postCount; i++) {
            try {
                UUID authorId = authorIds.get(i % authorIds.size()); // round-robin
                List<UUID> postCategoryIds = pickCategories(categoryIds, random);

                UUID postId = postCommandService.createPost(
                        new PostRequest("Post " + i, CONTENT, postCategoryIds, authorId)
                );

                // publish() sets publishedAt in the domain — never null.
                // Without this the post stays DRAFT and never appears in the public feed.
                postCommandService.publishPost(postId, authorId);

                created++;

                if (i % 100 == 0) {
                    log.info("posts.seed.progress created={}/{}", created, postCount);
                }

            } catch (Exception e) {
                log.warn("posts.seed.item.failed index={} reason='{}'", i, e.getMessage());
            }
        }

        log.info("posts.seed.complete created={}/{}", created, postCount);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Picks 1–3 distinct category IDs at random.
     */
    private List<UUID> pickCategories(List<UUID> all, Random random) {
        int count = 1 + random.nextInt(Math.min(3, all.size()));
        List<UUID> shuffled = new ArrayList<>(all);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, count);
    }

    /**
     * Loads all active category IDs. Page size 2000 covers any realistic seed count.
     */
    private List<UUID> loadCategoryIds() {
        return categoryQueryRepository.getAllCategories(0, 2000)
                .items()
                .stream()
                .map(CategoryResponse::id)
                .toList();
    }

    /**
     * Loads seeded user IDs by their known email pattern.
     * Caps at 50 to bound startup time — 50 rotating authors is enough variety.
     * <p>
     * Requires UserFacade.findByEmail(String email).
     */
    private List<UUID> loadAuthorIds() {
        int sample = Math.min(userCount, 50);
        List<UUID> ids = new ArrayList<>(sample);

        for (int i = 1; i <= sample; i++) {
            userFacade.getByEmail("user" + i + "@seed.blogify.com")
                    .map(UserView::id)
                    .ifPresent(ids::add);
        }


        return ids;
    }
}