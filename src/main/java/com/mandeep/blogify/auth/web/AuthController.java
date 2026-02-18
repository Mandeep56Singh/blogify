package com.mandeep.blogify.auth.web;

import com.mandeep.blogify.auth.application.dto.request.UserLoginRequestDto;
import com.mandeep.blogify.auth.application.dto.request.UserSignUpDto;
import com.mandeep.blogify.auth.application.dto.response.UserLoginResponseDto;
import com.mandeep.blogify.auth.application.service.AuthService;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.validation.RequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for login and user registration")
class AuthController {

    private final AuthService authService;
    private final RequestValidator validator;

    @Operation(summary = "Login user", description = "Authenticate a user and return JWT token with user info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<UserLoginResponseDto>> loginUser(
            @RequestBody UserLoginRequestDto requestDto
    ) {
        Optional<ResponseDto<UserLoginResponseDto>> violatedResponse = validator.validate(requestDto);

        if (violatedResponse.isPresent()) {
            log.info("validation failed");
            return new ResponseEntity<>(violatedResponse.get(), violatedResponse.get().error().status());
        }

        log.info("validated successfully");

        ResponseDto<UserLoginResponseDto> responseDto = authService.login(requestDto);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Sign up user", description = "Register a new user and return JWT token with user info")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<UserLoginResponseDto>> signUp(
            @RequestBody UserSignUpDto userDto
    ) {
        Optional<ResponseDto<UserLoginResponseDto>> violatedResponse = validator.validate(userDto);
        if (violatedResponse.isPresent()) {
            return new ResponseEntity<>(violatedResponse.get(), violatedResponse.get().error().status());
        }

        ResponseDto<UserLoginResponseDto> responseDto = authService.signUp(userDto);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
}
