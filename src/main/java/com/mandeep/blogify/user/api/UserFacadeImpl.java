package com.mandeep.blogify.user.api;

import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.UserService;
import com.mandeep.blogify.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserViewMapper viewMapper;

    @Override
    public UserView createUser(@NotNull @Email String email, @NotBlank String name, @NotBlank String password) {
        User user = userService.createUser(email, name, password);
        return viewMapper.toView(user);
    }

    @Override
    public UserView getUserById(@NotNull Long id) {
        User user = userService.getById(id);
        return viewMapper.toView(user);
    }

    @Override
    public UserView getUserByEmail(String email) {
        User user = userService.getByEmail(email);
        return viewMapper.toView(user);
    }


}
