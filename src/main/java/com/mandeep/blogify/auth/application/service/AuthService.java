package com.mandeep.blogify.auth.application.service;

import com.mandeep.blogify.auth.application.dto.request.UserLoginRequestDto;
import com.mandeep.blogify.auth.application.dto.request.UserSignUpDto;
import com.mandeep.blogify.auth.application.dto.response.AuthUserDto;
import com.mandeep.blogify.auth.application.dto.response.UserLoginResponseDto;
import com.mandeep.blogify.auth.application.mapping.AuthMapper;
import com.mandeep.blogify.auth.config.RsaKeyProperties;
import com.mandeep.blogify.auth.domain.AuthUser;
import com.mandeep.blogify.auth.domain.exception.AuthError;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.AppProblem;
import com.mandeep.blogify.user.UserFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserFacade userFacade;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RsaKeyProperties rsaKeys;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseDto<UserLoginResponseDto> signUp( UserSignUpDto userDto) {

        String email = userDto.email();
        String password = userDto.password();
        String passwordHash = passwordEncoder.encode(userDto.password());

        // save user credentials to db, if email already present, then return error
        if (userFacade.createUser(email, userDto.name(), passwordHash).isEmpty()) {
            return ResponseDto.failure(AppProblem.getDetail(AuthError.EMAIL_ALREADY_EXISTS));
        }

        // needed to generate authentication token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        return ResponseDto.success(buildLoginResponse(authentication));
    }


    @Transactional
    public ResponseDto<UserLoginResponseDto> login(UserLoginRequestDto loginDto) {
        String email = loginDto.email();
        String password = loginDto.password();

        log.info("request data {}", loginDto);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                )
        );

        return ResponseDto.success(buildLoginResponse(authentication));
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
