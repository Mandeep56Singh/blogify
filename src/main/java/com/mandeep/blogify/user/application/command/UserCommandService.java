package com.mandeep.blogify.user.application.command;


import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserIdentityGenerator;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserIdentityGenerator userIdentityGenerator;
    private final Clock clock;


    @Transactional
    public UUID register(UserRegistrationRequest userRegistrationRequest) {

        log.debug("register.attempt email='{}' username='{}' role='{}'",
                userRegistrationRequest.email(),
                userRegistrationRequest.userName(),
                userRegistrationRequest.role());

        Email userEmail = new Email(userRegistrationRequest.email());

        if (userRepository.existsByEmail(userEmail)) {
            throw CommonException.emailAlreadyExists(userEmail);
        }

        UserName name = new UserName(userRegistrationRequest.userName());

        if (userRepository.existsByUserName(name)) {
            throw CommonException.usernameAlreadyExists(name.value());
        }


        UserId id = userIdentityGenerator.nextUserId();

        String password = userRegistrationRequest.password();

        User user = User.register(
                id,
                name,
                userEmail,
                password,
                userRegistrationRequest.role(),
                this.clock
        );
        userRepository.save(user);

        log.info("register.success id={} email='{}' username='{}' role='{}'",
                user.getUserId().value(),
                user.getEmail().value(),
                user.getUserName().value(),
                user.getRole());

        return user.getUserId().value();
    }

}
