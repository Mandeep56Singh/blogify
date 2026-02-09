package com.mandeep.blogify.auth.web;

import com.mandeep.blogify.auth.application.dto.UserLoginRequestDto;
import com.mandeep.blogify.auth.application.dto.UserLoginResponseDto;
import com.mandeep.blogify.auth.application.dto.UserSignUpDto;
import com.mandeep.blogify.auth.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth/")
@Slf4j
class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(
            @RequestBody @Valid UserLoginRequestDto userDto
    ) {
        log.info("recived values from user, {}", userDto);
        UserLoginResponseDto responseDto = authService.login(userDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<UserLoginResponseDto> signUp(
            @RequestBody @Valid UserSignUpDto userDto
    ) {
        UserLoginResponseDto responseDto = authService.signUp(userDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
