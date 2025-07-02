package com.eaglebank.feature.user.web;

import com.eaglebank.feature.user.service.UserService;
import com.eaglebank.feature.user.web.model.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "Users", description = "Apis related to users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Fetch user by user id")
    public UserResponse getUser(@PathVariable UUID userId) throws AccessDeniedException {
        return userService.getUser(userId);
    }
}
