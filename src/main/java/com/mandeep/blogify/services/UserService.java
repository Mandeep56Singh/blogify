package com.mandeep.blogify.services;

import com.mandeep.blogify.dto.user.UserRequestDto;
import com.mandeep.blogify.dto.user.UserResponseDto;
import com.mandeep.blogify.entities.User;
import com.mandeep.blogify.mapper.user.UserMapper;
import com.mandeep.blogify.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        List<User> users = userRepository.findAll();
        return mapper.userListToDtoList(users);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(@NotNull UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user doesn't exists")
        );
        return mapper.userToDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(@Email String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("user with email not found")
        );
        return mapper.userToDto(user);
    }

    @Transactional
    public UserResponseDto createUser(@Valid UserRequestDto requestDto) {
        User user = mapper.dtoToUser(requestDto);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User newUser = userRepository.save(user);
        return mapper.userToDto(newUser);
    }
}
