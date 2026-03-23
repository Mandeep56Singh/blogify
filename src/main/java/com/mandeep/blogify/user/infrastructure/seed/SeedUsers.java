package com.mandeep.blogify.user.infrastructure.seed;

import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
@Order(2)
public final class SeedUsers implements ApplicationRunner {

    private final UserCommandService userCommandService;
    private final UserQueryRepository queryRepository;
    private final PasswordEncoder passwordEncoder;


    //    constant variable
    @Value("${seed.user_count:10}")
    private int userCount;


    @Override
    public void run(ApplicationArguments args) {

        if (queryRepository.existsByEmail(new Email("user1@seed.blogify.com"))) {
            log.info("users already seeded, Skipping...");
            return;
        }
        log.info("Seeding {} users....", userCount);


        String password = "Blogify@1234";
        String hashedPassword = passwordEncoder.encode(password);

        for (int i = 1; i <= userCount; i++) {
            String userNamePrefix = "user";
            String emailSuffix = "@seed.blogify.com";
            String email = userNamePrefix + i + emailSuffix;
            String userName = userNamePrefix + i;
            try {
                userCommandService.register(new UserRegistrationRequest(
                        email, userName, hashedPassword, Role.USER
                ));
            } catch (Exception e) {
                log.warn("users.seed.item.failed index={} reason='{}'", i, e.getMessage());
            }
        }

        log.info("Seeding {} users successful.", userCount);

    }


}
