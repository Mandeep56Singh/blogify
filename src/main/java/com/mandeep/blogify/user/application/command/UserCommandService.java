package com.mandeep.blogify.user.application.command;


import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.user.application.dto.RegistrationRequestWithRole;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
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
    public UUID register(RegistrationRequestWithRole registrationRequestWithRole) {

        log.debug("register.attempt email='{}' username='{}' role='{}'",
                registrationRequestWithRole.email(),
                registrationRequestWithRole.userName(),
                registrationRequestWithRole.role());

        Email userEmail = new Email(registrationRequestWithRole.email());

        if (userRepository.existsByEmail(userEmail)) {
            throw CommonException.emailAlreadyExists(userEmail);
        }

        UserName name = new UserName(registrationRequestWithRole.userName());

        if (userRepository.existsByUserName(name)) {
            throw CommonException.usernameAlreadyExists(name.value());
        }


        UserId id = userIdentityGenerator.nextUserId();

        String password = registrationRequestWithRole.password();

        User user = User.register(
                id,
                name,
                userEmail,
                password,
                registrationRequestWithRole.role(),
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

    public void deActiveUser(UUID actorId, UUID targetId) {

        log.debug("user.deactivate.attempt id={} requestedBy={}", targetId, actorId);

        UserId actorIdVO = new UserId(actorId);
        User actor = userRepository.findById(actorIdVO).orElseThrow(
                () -> UserDomainException.userNotFound(actorIdVO)
        );

        UserId targetUserId = new UserId(targetId);
        User targetUser = userRepository.findById(targetUserId).orElseThrow(
                () -> UserDomainException.userNotFound(targetUserId)
        );

        targetUser.deActivate(actor.getRole());

        userRepository.save(targetUser);

        log.info("user.deactivated id={} deActivatedBy={}", targetId, actorId);
    }

}
