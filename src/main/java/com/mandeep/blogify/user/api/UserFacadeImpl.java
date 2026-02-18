package com.mandeep.blogify.user.api;

import com.mandeep.blogify.shared.dto.AuthorView;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
import com.mandeep.blogify.user.application.UserService;
import com.mandeep.blogify.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserViewMapper viewMapper;
    private final AuthorViewMapper authorViewMapper;

    @Override
    public Optional<UserView> createUser(@NotNull @Email String email, @NotBlank String name, @NotBlank String password) {
        return userService.
                createUser(email, name, password)
                .map(viewMapper::toView);
    }

    @Override
    public Optional<UserView> getUserById(@NotNull Long id) {
        return userService.getById(id).map(viewMapper::toView);
    }

    @Override
    public Optional<UserView> getUserByEmail(String email) {
        return userService.getByEmail(email).map(viewMapper::toView);
    }

    @Override
    public Optional<AuthorView> getPostAuthor(Long id) {
        return userService.getById(id).map(authorViewMapper::toView);
    }

    @Override
    public Map<Long, AuthorView> getAuthors(Set<Long> ids) {
        List<User> users = userService.getAllIds(ids);
        Map<Long, AuthorView> authorMap = new HashMap<>();
        users.forEach(user -> authorMap.put(user.getId(), authorViewMapper.toView(user)));
        return authorMap;
    }

}
