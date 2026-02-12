package com.mandeep.blogify.user.application;

import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.AppProblem;
import com.mandeep.blogify.user.application.dto.UserRequestDto;
import com.mandeep.blogify.user.application.dto.UserResponseDto;
import com.mandeep.blogify.user.domain.Role;
import com.mandeep.blogify.user.domain.User;
import com.mandeep.blogify.user.domain.UserRepository;
import com.mandeep.blogify.user.domain.exceptions.UserError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PaginatedResponseDto<UserResponseDto> getAll(Integer pageNumber, Integer pageSize) {

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
    public ResponseDto<UserResponseDto> getUserById(Long id) {
        return getById(id).map(
                user -> ResponseDto.success(mapper.toDto(user))
        ).orElseGet(
                () -> ResponseDto.failure(AppProblem.getDetail(UserError.EMAIL_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<UserResponseDto> getUserByEmail(String email) {
        return getByEmail(email).map(
                user -> ResponseDto.success(mapper.toDto(user))
        ).orElseGet(
                () -> ResponseDto.failure(AppProblem.getDetail(UserError.USER_NOT_FOUND))
        );
    }

    @Transactional
    public ResponseDto<UserResponseDto> updateUser(UserRequestDto requestDto, Long id) {

        return getById(id).map(
                user -> {
                    user.setEmail(requestDto.email());
                    user.setName(requestDto.name());
                    user.setPassword(passwordEncoder.encode(requestDto.password()));
                    User updatedUser = userRepository.save(user);

                    return ResponseDto.success(mapper.toDto(updatedUser));
                }
        ).orElseGet(
                () -> ResponseDto.failure(UserError.USER_NOT_FOUND)
        );
    }

    @Transactional
    public Optional<Void> deleteUser(Long id) {
        getById(id).ifPresent(
                user -> {
                    user.softDelete();
                    userRepository.save(user);

                }
        );
        return Optional.empty();
    }

//    @Transactional(readOnly = true)
//    public UserResponseDto getCurrentUser(@NotNull Authentication auth) {
//        User user = (User) auth.getPrincipal();
//        User currentUser = getById(user.getId());
//        return mapper.toDto(currentUser);
//    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }


    public Optional<User> getByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> createUser( String email, String name, String password) {

        if (existsByEmail(email)) {
            return Optional.empty();
        }

        User user = new User(email, password);
        user.setName(name);
        user.setRole(Role.USER);
        return Optional.of(userRepository.save(user));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
