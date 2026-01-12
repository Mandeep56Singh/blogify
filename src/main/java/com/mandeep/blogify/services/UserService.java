package com.mandeep.blogify.services;

import com.mandeep.blogify.dto.PaginatedResponseDto;
import com.mandeep.blogify.dto.user.UserRequestDto;
import com.mandeep.blogify.dto.user.UserResponseDto;
import com.mandeep.blogify.entities.User;
import com.mandeep.blogify.mapper.user.UserMapper;
import com.mandeep.blogify.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public PaginatedResponseDto<UserResponseDto> getAll(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> pageUser = userRepository.findAll(pageable);
        List<User> users = pageUser.getContent();

        List<UserResponseDto> usersDtoList = mapper.toDtoList(users);

        return new PaginatedResponseDto<UserResponseDto>(
                usersDtoList,
                pageNumber,
                pageSize,
                pageUser.getTotalElements(),
                pageUser.getTotalPages(),
                pageUser.isLast()
        );

    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(@NotNull UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user doesn't exists")
        );
        return mapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(@NotNull @Email String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("user with email not found")
        );
        return mapper.toDto(user);
    }

    @Transactional
    public UserResponseDto createUser(@Valid UserRequestDto requestDto) {
        User user = mapper.toEntity(requestDto);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        User newUser = userRepository.save(user);
        return mapper.toDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(@Valid UserRequestDto requestDto, @NotNull UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user doesn't exits")
        );

        user.setEmail(requestDto.email());
        user.setName(requestDto.name());
        user.setPassword(requestDto.password());

        User updatedUser = userRepository.save(user);
        return mapper.toDto(updatedUser);
    }


    @Transactional
    public void deleteUser(@NotNull UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("user doesn't exits")
        );
        user.softDelete();
        userRepository.save(user);
    }
}
