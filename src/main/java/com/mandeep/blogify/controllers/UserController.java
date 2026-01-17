package com.mandeep.blogify.controllers;

import com.mandeep.blogify.dto.PaginatedResponseDto;
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

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @GetMapping(value = "/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable @NotNull Long id
    ) {
        UserResponseDto responseDto = userService.getUserById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping(value = "/by-email")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @RequestParam @Email String email
    ) {
        UserResponseDto responseDto = userService.getUserByEmail(email);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<UserResponseDto>> getUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize
    ) {
        PaginatedResponseDto<UserResponseDto> responseDto = userService.getAll(pageNumber, pageSize);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto requestDto
    ) {
        UserResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Valid @RequestBody UserRequestDto requestDto,
            @PathVariable @NotNull Long id
    ) {
        UserResponseDto responseDto = userService.updateUser(requestDto, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @NotNull Long id
    ) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
