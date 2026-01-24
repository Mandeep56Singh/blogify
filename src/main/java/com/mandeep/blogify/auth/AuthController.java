package com.mandeep.blogify.auth;

import com.mandeep.blogify.auth.dto.UserLoginRequestDto;
import com.mandeep.blogify.auth.dto.UserLoginResponseDto;
import com.mandeep.blogify.auth.dto.UserSignUpDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<UserLoginResponseDto> loginUser(
            @RequestBody @Valid UserLoginRequestDto userDto
    ) {
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
