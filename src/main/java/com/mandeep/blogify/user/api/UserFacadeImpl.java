package com.mandeep.blogify.user.api;

import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserViewMapper viewMapper;

    @Override
    public Optional<UserView> createUser(@NotNull @Email String email, @NotBlank String name, @NotBlank String password) {
        return userService.
                createUser(email, name, password)
                .map(
                        viewMapper::toView
                );

    }

    @Override
    public Optional<UserView> getUserById(@NotNull Long id) {
        return userService.getById(id).map(viewMapper::toView);
    }

    @Override
    public Optional<UserView> getUserByEmail(String email) {
        return userService.getByEmail(email).map(viewMapper::toView);
    }


}
