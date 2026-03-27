package com.mandeep.blogify.user.api;

import com.mandeep.blogify.shared.domain.model.valueObject.Email;
import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.RegistrationRequest;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import com.mandeep.blogify.user.domain.model.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserCommandService userCommandService;
    private final UserQueryRepository queryRepository;
    private final UserViewMapper viewMapper;

    @Override
    @Transactional
    public UUID register(String email, String userName, String password, Role role) {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                email,
                userName,
                password,
                role
        );
        return userCommandService.register(registrationRequest);
    }


    @Override
    public boolean existsByEmail(String email) {
        return queryRepository.existsByEmail(new Email(email));
    }

    @Override
    public Optional<UserView> getByEmail(String email) {
        return queryRepository.findResponseByEmail(new Email(email)).map(
                viewMapper::toView
        );
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<UserView> getUserById(UUID id) {
        return queryRepository.findResponseById(new UserId(id)).map(
                viewMapper::toView
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, UserView> getUsersById(Set<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }

        List<UserResponse> userResponses = queryRepository.findUsersById(ids);

        return userResponses.stream()
                .map(viewMapper::toView)
                .collect(Collectors.toMap(
                        UserView::id,
                        view -> view,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public UUID createUser(String email, String userName, String password) {

        return userCommandService.register(new RegistrationRequest(
                email,
                userName,
                password,
                Role.USER
        ));
    }

    @Override
    public UUID createAdmin(String email, String userName, String password) {
        return userCommandService.register(new RegistrationRequest(
                email,
                userName,
                password,
                Role.ADMIN
        ));
    }

    @Override
    public void deactivateUser(UUID userId, UUID adminId) {
        userCommandService.deActiveUser(userId, adminId);
    }

}
