package com.mandeep.blogify.user.api;

import com.mandeep.blogify.shared.domain.model.valueObject.Role;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.UserRegistrationRequest;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.application.query.UserQueryRepository;
import com.mandeep.blogify.user.domain.model.valueobjects.Email;
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
    public void register(String email, String userName, String password, Role role) {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                email,
                userName,
                password,
                role
        );
        userCommandService.register(userRegistrationRequest);
    }



    @Override
    public boolean existsByEmail(String email) {
        return queryRepository.existsByEmail(new Email(email));
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

}
