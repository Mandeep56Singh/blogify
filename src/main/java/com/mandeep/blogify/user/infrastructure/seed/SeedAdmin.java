package com.mandeep.blogify.user.infrastructure.seed;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
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
@Order(1)
@Slf4j
public final class SeedAdmin implements ApplicationRunner {

    private final UserCommandService commandService;
    private final UserQueryRepository queryRepository;
    private final PasswordEncoder passwordEncoder;


    @Value("${admin.email:admin@default.com}")
    private String email;

    @Value("${admin.password:Blogify@1234}")
    private String password;

    @Value("${admin.user-name:admin}")
    private String userName;

    @Override
    public void run(ApplicationArguments args) {

        log.debug("admin.signup.attempt email={}", email);

        if (queryRepository.existsByEmail(new Email(email))) {
            log.info("admin.signup.skipped reason='ADMIN ALREADY SIGNED UP, SKIPPING CREATING ADMIN' email={}", email);
            return;
        }

        String hashedPassword = passwordEncoder.encode(password);

        try {
            commandService.register(new UserRegistrationRequest(
                    email, userName, hashedPassword, Role.ADMIN
            ));
            log.info("admin.signup.success email={}", email);
        } catch (Exception ex) {
            log.warn("admin.signup.failed reason={}", ex.getMessage());
        }


        log.info("admin.signup.success email={}", email);
    }
}
