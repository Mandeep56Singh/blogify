package com.mandeep.blogify.auth.web;

import com.mandeep.blogify.auth.application.command.AuthCommandService;
import com.mandeep.blogify.auth.application.dto.LoginResponse;
import com.mandeep.blogify.auth.web.dto.LoginWebRequest;
import com.mandeep.blogify.auth.web.dto.LoginWebResponse;
import com.mandeep.blogify.auth.web.dto.SignUpWebRequest;
import com.mandeep.blogify.auth.web.mapper.AuthWebMapper;
import com.mandeep.blogify.shared.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Validated
@Tag(name = "Authentication", description = "Endpoints for login and user registration")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final AuthWebMapper authWebMapper;

    @Operation(summary = "Login user", description = "Authenticate a user and return JWT token with user info")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<Response<LoginWebResponse>> loginUser(
            @RequestBody @Valid LoginWebRequest loginWebRequest
    ) {

        LoginResponse loginResponse = authCommandService.login(authWebMapper.toLoginRequest(loginWebRequest));
        LoginWebResponse loginWebResponse = authWebMapper.toWebResponse(loginResponse);
        return ResponseEntity.ok(Response.success(loginWebResponse));
    }

    @Operation(summary = "Sign up user", description = "Register a new user and return JWT token with user info")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "User with this value already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<Response<LoginWebResponse>> signUp(
            @RequestBody @Valid SignUpWebRequest signUpWebRequest
    ) {
        LoginResponse loginResponse = authCommandService.signUp(authWebMapper.toSignUpRequest(signUpWebRequest));
        LoginWebResponse loginWebResponse = authWebMapper.toWebResponse(loginResponse);
        var responseDto = Response.success(loginWebResponse);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


}
