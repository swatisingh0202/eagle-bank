package com.eaglebank.feature.user.service;

import com.eaglebank.feature.user.repository.UserRepository;
import com.eaglebank.feature.user.web.model.UserResponse;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getUser(UUID userId) {
        return userResponse(userId);
    }

    private static UserResponse userResponse(UUID userId) {
        return UserResponse.builder()
                .userId(userId)
                .name("")
                .email("")
                .phone("")
                .createdTimestamp(ZonedDateTime.now())
                .updatedTimestamp(ZonedDateTime.now())
                .build();
    }
}
