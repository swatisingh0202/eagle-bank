package com.eaglebank.feature.user.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private ZonedDateTime createdTimestamp;
    private ZonedDateTime updatedTimestamp;
}