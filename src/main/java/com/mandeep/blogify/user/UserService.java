package com.mandeep.blogify.user;

import com.mandeep.blogify.constants.ApiError;
import com.mandeep.blogify.constants.AppConstants;
import com.mandeep.blogify.common.PaginatedResponseDto;
import com.mandeep.blogify.user.dto.UserRequestDto;
import com.mandeep.blogify.user.dto.UserResponseDto;
import com.mandeep.blogify.common.exceptions.ApiException;
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

        if (pageSize <= 0 || pageSize > AppConstants.MAX_PAGE_SIZE) {
            throw new ApiException(ApiError.INVALID_PAGE_SIZE);
        }

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

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApiException(ApiError.USER_NOT_FOUND)
        );
    }

}
