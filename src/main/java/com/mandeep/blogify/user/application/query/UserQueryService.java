package com.mandeep.blogify.user.application.query;

import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.domain.exceptions.UserDomainException;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import com.mandeep.blogify.user.domain.model.valueobjects.UserName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;


    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID id) {
        UserId userId = new UserId(id);
        return userQueryRepository.findResponseById(userId).orElseThrow(
                () -> UserDomainException.userNotFound(userId)
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        Email userEmail = new Email(email);
        return userQueryRepository.findResponseByEmail(userEmail).orElseThrow(
                () -> UserDomainException.emailNotFound(userEmail)
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUserName(String name) {

        UserName userName = new UserName(name);
        return userQueryRepository.findResponseByUserName(userName).orElseThrow(
                () -> UserDomainException.usernameNotFound(userName)
        );
    }

}
