package com.eaglebank.feature.common.web;


import com.eaglebank.feature.auth.JwtProvider;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Slf4j
public class BaseController {
    private final JwtProvider jwtProvider;

    public BaseController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    protected void validateUserId(UUID userId, String authHeader) throws AccessDeniedException {
        String token = authHeader.replace("Bearer ", "");
        UUID authenticatedUserId = jwtProvider.getUserId(token);

        if (!userId.equals(authenticatedUserId)) {
            log.error("Not authorised to access user data for {}", authenticatedUserId);
            throw new AccessDeniedException("You are not authorized to access this user's data.");
        }
    }

    protected UUID getAuthenticatedUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtProvider.getUserId(token);
    }
}