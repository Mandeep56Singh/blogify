package com.mandeep.blogify.user.ui;

import com.mandeep.blogify.shared.AppUtils;
import com.mandeep.blogify.shared.dto.PaginatedResponseDto;
import com.mandeep.blogify.shared.dto.ResponseDto;
import com.mandeep.blogify.shared.exceptions.PageError;
import com.mandeep.blogify.shared.exceptions.validation.EmailWrapper;
import com.mandeep.blogify.shared.exceptions.validation.RequestValidator;
import com.mandeep.blogify.user.application.UserService;
import com.mandeep.blogify.user.application.dto.UserRequestDto;
import com.mandeep.blogify.user.application.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
@Validated
class UserController {

    private final UserService userService;
    private final RequestValidator validator;


    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserById(
            @PathVariable Long id
    ) {
        ResponseDto<UserResponseDto> responseDto = userService.getUserById(id);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @GetMapping(value = "/by-email")
    public ResponseEntity<ResponseDto<UserResponseDto>> getUserByEmail(
            @RequestParam  String email
    ) {
        Optional<ResponseDto<UserResponseDto>> violatedResponse = validator.validate(new EmailWrapper(email));

        if (violatedResponse.isPresent()) {
            return new ResponseEntity<>(violatedResponse.get(), violatedResponse.get().error().status());
        }

        ResponseDto<UserResponseDto> responseDto = userService.getUserByEmail(email);

        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<PaginatedResponseDto<UserResponseDto>>> getUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize
    ) {
        Optional<PageError> pageError = AppUtils.validatePage(pageNumber - 1, pageSize);

        if (pageError.isPresent()) {
            return new ResponseEntity<>(ResponseDto.failure(pageError.get()), pageError.get().status());
        }

        PaginatedResponseDto<UserResponseDto> pageDataDto = userService.getAll(pageNumber, pageSize);
        ResponseDto<PaginatedResponseDto<UserResponseDto>> responseDto = ResponseDto.success(pageDataDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


    @PostMapping(value = "{id}")
    public ResponseEntity<ResponseDto<UserResponseDto>> updateUser(
            @RequestBody UserRequestDto requestDto,
            @PathVariable Long id
    ) {
        Optional<ResponseDto<UserResponseDto>> violatedResponse = validator.validate(requestDto);

        if (violatedResponse.isPresent()) {
            return new ResponseEntity<>(violatedResponse.get(), violatedResponse.get().error().status());
        }

        ResponseDto<UserResponseDto> responseDto = userService.updateUser(requestDto, id);
        if (!responseDto.success()) {
            return new ResponseEntity<>(responseDto, responseDto.error().status());
        }
        return ResponseEntity.ok(responseDto);
    }

}
