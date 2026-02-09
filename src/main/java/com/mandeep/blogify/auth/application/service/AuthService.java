package com.mandeep.blogify.auth.application.service;

import com.mandeep.blogify.auth.application.dto.AuthUserDto;
import com.mandeep.blogify.auth.application.dto.UserLoginRequestDto;
import com.mandeep.blogify.auth.application.dto.UserLoginResponseDto;
import com.mandeep.blogify.auth.application.dto.UserSignUpDto;
import com.mandeep.blogify.auth.application.mapping.AuthMapper;
import com.mandeep.blogify.auth.config.RsaKeyProperties;
import com.mandeep.blogify.auth.domain.AuthUser;
import com.mandeep.blogify.user.UserFacade;
import com.mandeep.blogify.user.UserView;
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

    private final UserFacade userFacade;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RsaKeyProperties rsaKeys;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserLoginResponseDto signUp(@Valid UserSignUpDto userDto) {

        String email = userDto.email();
        String password = userDto.password();
        String passwordHash = passwordEncoder.encode(userDto.password());

        UserView newUser = userFacade.createUser(email, userDto.name(), passwordHash);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        newUser.email(),
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
