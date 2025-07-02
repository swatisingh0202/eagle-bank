package com.eaglebank.feature.user.web;

import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.common.web.BaseController;
import com.eaglebank.feature.user.service.UserService;
import com.eaglebank.feature.user.web.model.CreateUserRequest;
import com.eaglebank.feature.user.web.model.UpdateUserRequest;
import com.eaglebank.feature.user.web.model.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "Apis related to users")
public class UserController extends BaseController {
    private final UserService userService;

    public UserController(JwtProvider jwtProvider,
                          UserService userService) {
        super(jwtProvider);
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse response = userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Fetch user by user id", security = @SecurityRequirement(name = "bearerAuth"))
    public UserResponse getUser(@PathVariable UUID userId,
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        validateUserId(userId, authHeader);
        return userService.getUser(userId);
    }

    @PatchMapping("/{userId}")
    @Operation(summary = "Update user", security = @SecurityRequirement(name = "bearerAuth"))
    public void updateUser(@PathVariable UUID userId,
                           @Valid @RequestBody UpdateUserRequest updateUserRequest,
                           @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        validateUserId(userId, authHeader);
        userService.updateUser(userId, updateUserRequest);
    }
}
