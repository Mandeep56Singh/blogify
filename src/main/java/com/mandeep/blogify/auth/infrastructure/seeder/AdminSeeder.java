package com.mandeep.blogify.auth.infrastructure.seeder;

import com.mandeep.blogify.auth.application.command.AuthCommandService;
import com.mandeep.blogify.auth.application.dto.SignUpRequest;
import com.mandeep.blogify.user.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final AuthCommandService authService;
    private final UserFacade userFacade;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        log.debug("admin.signup.attempt email={}", email);

        if (userFacade.existsByEmail(email)) {
            log.info("admin.signup.failed reason='ADMIN ALREADY SIGNED UP, SKIPPING CREATING ADMIN' email={}", email);
            return;
        }

        authService.signUpAdmin(new SignUpRequest(
                email,
                "admin",
                password
        ));

        log.info("admin.signup.success email={}", email);

    }
}
