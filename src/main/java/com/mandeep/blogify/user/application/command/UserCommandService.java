package com.mandeep.blogify.user.application.command;


import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.entity.User;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import com.mandeep.blogify.user.domain.repository.UserIdentityGenerator;
import com.mandeep.blogify.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final UserRepository userRepository;
    private final UserIdentityGenerator userIdentityGenerator;


    @Transactional
    public void register(UserRegistrationRequest userRegistrationRequest) {

        Email email = new Email(userRegistrationRequest.email());

        if (userRepository.existsByEmail(email)) {
            throw UserDomainException.emailAlreadyExists(email);
        }

        UserName name = new UserName(userRegistrationRequest.userName());

        if (userRepository.existsByUserName(name)) {
            throw UserDomainException.usernameAlreadyExists(name);
        }


        UserId id = userIdentityGenerator.nextUserId();

        String password = userRegistrationRequest.password();

        User user = User.register(
                id,
                name,
                email,
                password,
                userRegistrationRequest.role()
        );
        userRepository.save(user);

    }

    @Transactional
    public void updateUserName(UUID id, String name) {

        UserId userId = new UserId(id);

        User user = userRepository.findById(userId).orElseThrow(
                () -> UserDomainException.userNotFound(userId)
        );

        user.updateName(new UserName(name));

        userRepository.save(user);

    }

    @Transactional
    public void updateEmail(UUID id, String email) {

        UserId userId = new UserId(id);

        User user = userRepository.findById(userId).orElseThrow(
                () -> UserDomainException.userNotFound(userId)
        );

        user.updateEmail(new Email(email));

        userRepository.save(user);
    }

}
