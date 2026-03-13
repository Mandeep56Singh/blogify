package com.mandeep.blogify.user.infrastructure.seed;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public final class UserSeeder implements ApplicationRunner {

    private final UserCommandService userCommandService;
    private final UserQueryRepository queryRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository jpaRepository;


//    constant variable
    @Value("${seed.user_count:10}")
    private int userCount;

    private final String password = "Blogify@1234";
    private final String userNamePrefix = "user";
    private final String emailSuffix = "@seed.blogify.com";


    @Override
    public void run(ApplicationArguments args) {

        if (queryRepository.existsByEmail(new Email("user1@seed.blogify.com"))) {
            log.info("users already seeded, Skipping...");
            return;
        }
        log.info("Seeding {} users....", userCount);


        String hashedPassword = passwordEncoder.encode(password);

        for (int i = 1; i <= userCount; i++) {
            String email = userNamePrefix + i + emailSuffix;
            String userName = userNamePrefix + i;

            userCommandService.register(new UserRegistrationRequest(
                email, userName, hashedPassword, Role.USER
            ));
        }

        log.info("Seeding {} users successful.", userCount);

    }


}
