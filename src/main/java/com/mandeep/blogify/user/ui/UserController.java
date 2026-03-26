package com.mandeep.blogify.user.ui;

import com.mandeep.blogify.shared.AuthView;
import com.mandeep.blogify.shared.AuthenticationContext;
import com.mandeep.blogify.shared.domain.exception.CommonException;
import com.mandeep.blogify.shared.dto.Response;
import com.mandeep.blogify.user.application.command.UserCommandService;
import com.mandeep.blogify.user.application.dto.UserResponse;
import com.mandeep.blogify.user.application.query.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final AuthenticationContext authenticationContext;

    //region Queries By User
    @Operation(summary = "Get user by ID", description = "Fetch a user by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response<UserResponse>> getUserById(
            @PathVariable UUID id
    ) {
        UserResponse userResponse = userQueryService.getUserById(id);
        var responseDto = Response.success(userResponse);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @Operation(summary = "Get user by email", description = "Fetch a user using their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid value format"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/email")
    public ResponseEntity<Response<UserResponse>> getUserByEmail(
            @NotBlank @Email @RequestParam String email
    ) {

        UserResponse userResponse = userQueryService.getUserByEmail(email);
        var responseDto = Response.success(userResponse);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get user by username", description = "Fetch a user using their username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid value format"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/username")
    public ResponseEntity<Response<UserResponse>> getUserByUserName(
            @NotBlank @RequestParam String username
    ) {

        UserResponse userResponse = userQueryService.getUserByUserName(username);
        var responseDto = Response.success(userResponse);
        return ResponseEntity.ok(responseDto);
    }
    //endregion

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response<Void>> deactivateUser(@PathVariable UUID id) {

        UUID actorId = authenticationContext.getCurrentUserId()
                .map(AuthView::id)
                .orElseThrow(CommonException::unauthorizedAccess);

        userCommandService.deActiveUser(actorId, id);

        return ResponseEntity.ok(Response.success(null));
    }

}
