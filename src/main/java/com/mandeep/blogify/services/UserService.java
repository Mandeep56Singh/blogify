package com.mandeep.blogify.services;

import com.mandeep.blogify.dto.PaginatedResponseDto;
import com.mandeep.blogify.dto.user.UserRequestDto;
import com.mandeep.blogify.dto.user.UserResponseDto;
import com.mandeep.blogify.entities.User;
import com.mandeep.blogify.enums.Constants;
import com.mandeep.blogify.exceptions.ApiError;
import com.mandeep.blogify.exceptions.ApiException;
import com.mandeep.blogify.mapper.UserMapper;
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

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserResponseDto> getAll(Integer pageNumber, Integer pageSize) {

        if (pageNumber - 1 < 0) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
        }

        if (pageSize <= 0 || pageSize > Constants.MAX_PAGE_SIZE.getVal()) {
            throw new ApiException(ApiError.INVALID_PAGE_SIZE);
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> pageUser = userRepository.findAll(pageable);

        if (pageNumber - 1 > pageUser.getTotalPages()) {
            throw new ApiException(ApiError.INVALID_PAGE_NUMBER);
        }

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
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.USER_NOT_FOUND)
        );
        return mapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(@NotNull @Email String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new ApiException(ApiError.EMAIL_NOT_FOUND)
        );
        return mapper.toDto(user);
    }

    @Transactional
    public UserResponseDto createUser(@Valid UserRequestDto requestDto) {
        User user = mapper.toEntity(requestDto);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ApiException(ApiError.EMAIL_ALREADY_EXISTS);
        }
        User newUser = userRepository.save(user);
        return mapper.toDto(newUser);
    }

    @Transactional
    public UserResponseDto updateUser(@Valid UserRequestDto requestDto, @NotNull Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.USER_NOT_FOUND)
        );

        user.setEmail(requestDto.email());
        user.setName(requestDto.name());
        user.setPassword(requestDto.password());

        User updatedUser = userRepository.save(user);
        return mapper.toDto(updatedUser);
    }


    @Transactional
    public void deleteUser(@NotNull Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.USER_NOT_FOUND)
        );
        user.softDelete();
        userRepository.save(user);
    }
}
