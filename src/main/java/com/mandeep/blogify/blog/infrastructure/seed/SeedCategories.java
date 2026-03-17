package com.mandeep.blogify.blog.infrastructure.seed;


import com.mandeep.blogify.blog.application.command.CategoryCommandService;
import com.mandeep.blogify.blog.application.dto.CategoryRequest;
import com.mandeep.blogify.blog.application.query.repository.CategoryQueryRepository;
import com.mandeep.blogify.blog.domain.model.valueObject.CategoryTitle;
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

import java.util.Optional;

@Component
@Profile("dev")
@Order(3)
@RequiredArgsConstructor
@Slf4j
public final class SeedCategories implements ApplicationRunner {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryRepository categoryQueryRepository;
    private final UserFacade userFacade;


    @Value("${seed.category_count:10}")
    private int categoryCount;

    @Value("${admin.email:admin@default.com}")
    private String adminEmail;


    @Override
    public void run(ApplicationArguments args) {

        log.debug("categories.seed.attempt count={}", categoryCount);

        if (categoryQueryRepository.isCategoryExistsAndActive(new CategoryTitle("Category1"))) {
            log.info("categories.seed.skipped reason='CATEGORY ALREADY SEEDED'");
            return;
        }

        Optional<UserView> adminView = userFacade.getByEmail(adminEmail);

        if (adminView.isEmpty()) {
            log.warn("categories.seed.skipped reason='ADMIN IS NOT REGISTERED, PLEASE CREATE ADMIN FIRST.'");
            return;
        }

        for (int i = 1; i <= categoryCount; i++) {
            String title = "Category" + i;
            String description = "Lorem ipsum dolor sit amet consectetur adipiscing elit.";

            try {
                categoryCommandService.createCategory(
                        new CategoryRequest(title, description, adminView.get().id())
                );
            } catch (Exception ex) {
                log.warn("categories.seed.item.failed index={} reason={}", i, ex.getMessage());
            }
        }

        log.info("categories.seeded count={}", categoryCount);

    }
}
