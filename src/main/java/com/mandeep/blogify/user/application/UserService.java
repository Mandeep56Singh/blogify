package com.mandeep.blogify.user.application;

import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.exceptions.ApiException;
import com.mandeep.blogify.user.application.dto.UserRequestDto;
import com.mandeep.blogify.user.application.dto.UserResponseDto;
import com.mandeep.blogify.user.domain.Role;
import com.mandeep.blogify.user.domain.User;
import com.mandeep.blogify.user.domain.UserRepository;
import com.mandeep.blogify.user.domain.exceptions.UserError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserResponseDto> getAll(Integer pageNumber, Integer pageSize) {

        AppUtils.validatePage(pageNumber - 1, pageSize);
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> pageUser = userRepository.findAll(pageable);

        List<User> users = pageUser.getContent();

        List<UserResponseDto> usersDtoList = mapper.toDtoList(users);

        return new PaginatedResponseDto<>(
                usersDtoList,
                pageNumber,
                pageSize,
                pageUser.getTotalElements(),
                pageUser.getTotalPages(),
                pageUser.isLast()
        );
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(@NotNull Long id) {
        User user = getById(id);
        return mapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(@NotNull @Email String email) {
        User user = getByEmail(email);
        return mapper.toDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(@Valid UserRequestDto requestDto, @NotNull Long id) {

        User user = getById(id);

        user.setEmail(requestDto.email());
        user.setName(requestDto.name());
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        User updatedUser = userRepository.save(user);

        return mapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(@NotNull Long id) {
        User user = getById(id);
        user.softDelete();
        userRepository.save(user);
    }

//    @Transactional(readOnly = true)
//    public UserResponseDto getCurrentUser(@NotNull Authentication auth) {
//        User user = (User) auth.getPrincipal();
//        User currentUser = getById(user.getId());
//        return mapper.toDto(currentUser);
//    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApiException(UserError.USER_NOT_FOUND)
        );
    }


    public User getByEmail(@NotNull @Email String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> new ApiException(UserError.EMAIL_NOT_FOUND)
        );
    }

    public User createUser(@NotNull @Email String email,  @NotBlank String name, @NotBlank String password) {

        if (existsByEmail(email)) {
            throw new ApiException(UserError.EMAIL_ALREADY_EXISTS);
        }

        User user = new User(email, password);
        user.setName(name);
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public boolean existsByEmail(@NotNull @Email  String email) {
        return userRepository.existsByEmail(email);
    }

}
