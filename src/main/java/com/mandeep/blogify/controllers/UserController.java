package com.mandeep.blogify.controllers;

import com.mandeep.blogify.dto.user.UserRequestDto;
import com.mandeep.blogify.dto.user.UserResponseDto;
import com.mandeep.blogify.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable("id") @NotNull UUID id
    ) {
        UserResponseDto responseDto = userService.getUserById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = "/by-email")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @RequestParam(required = true) @Email String email
    ) {
        UserResponseDto responseDto = userService.getUserByEmail(email);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getUsers() {
        List<UserResponseDto> responseDtos = userService.getAll();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }


}
