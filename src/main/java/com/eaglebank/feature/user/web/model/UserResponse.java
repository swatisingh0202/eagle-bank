package com.eaglebank.feature.user.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@Schema
public class UserResponse {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private ZonedDateTime createdTimestamp;
    private ZonedDateTime updatedTimestamp;
}
