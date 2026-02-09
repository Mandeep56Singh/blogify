package com.mandeep.blogify.user.ui;

import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.user.application.UserService;
import com.mandeep.blogify.user.application.dto.UserRequestDto;
import com.mandeep.blogify.user.application.dto.UserResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
class UserController {

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


    @PostMapping(value = "{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Valid @RequestBody UserRequestDto requestDto,
            @PathVariable @NotNull Long id
    ) {
        UserResponseDto responseDto = userService.updateUser(requestDto, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

//    @GetMapping(value = "/me")
//    public ResponseEntity<UserResponseDto> getCurrentUser(@NotNull Authentication auth) {
//        UserResponseDto responseDto = userService.getCurrentUser(auth);
//        return new ResponseEntity<>(responseDto, HttpStatus.OK);
//    }


}
