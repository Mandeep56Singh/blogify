package com.mandeep.blogify.auth;

import com.mandeep.blogify.auth.dto.AuthUserDto;
import com.mandeep.blogify.auth.dto.UserLoginRequestDto;
import com.mandeep.blogify.auth.dto.UserLoginResponseDto;
import com.mandeep.blogify.auth.dto.UserSignUpDto;
import com.mandeep.blogify.common.exceptions.ApiException;
import com.mandeep.blogify.constants.ApiError;
import com.mandeep.blogify.user.Role;
import com.mandeep.blogify.user.User;
import com.mandeep.blogify.user.UserRepository;
import com.mandeep.blogify.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RsaKeyProperties rsaKeys;
    private final AuthMapper authMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserLoginResponseDto signUp(@Valid UserSignUpDto userDto) {

        if (userRepository.existsByEmail(userDto.email())) {
            throw new ApiException(ApiError.EMAIL_ALREADY_EXISTS);
        }

        String email = userDto.email();
        String password = userDto.password();
        String passwordHash = passwordEncoder.encode(userDto.password());

        User user = new User(email, passwordHash);
        user.setName(userDto.name());
        user.setRole(Role.USER);
        User newUser = userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        newUser.getEmail(),
                        password
                )
        );

        return buildLoginResponse(authentication);
    }


    @Transactional
    public UserLoginResponseDto login(@Valid UserLoginRequestDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        return buildLoginResponse(authentication);
    }

    public UserLoginResponseDto buildLoginResponse(Authentication auth) {

        AuthUser user = (AuthUser) auth.getPrincipal();
        String token = tokenService.generateToken(auth);
        Long expiresIn = rsaKeys.tokenExpireIn().getSeconds();

        AuthUserDto userDto = authMapper.toDto(user);

        return new UserLoginResponseDto(
                token,
                "Bearer",
                expiresIn,
                userDto
        );

    }


}
