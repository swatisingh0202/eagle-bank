package com.eaglebank.feature.auth.web;

import com.eaglebank.feature.auth.service.IdentityService;
import com.eaglebank.feature.auth.web.model.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "Auth", description = "Apis related to authentication")
public class AuthController {
    private final IdentityService identityService;

    public AuthController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = identityService.login(loginRequest);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

