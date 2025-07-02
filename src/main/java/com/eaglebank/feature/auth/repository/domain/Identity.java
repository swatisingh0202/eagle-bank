package com.eaglebank.feature.auth.repository.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Identity {
    private UUID identityId;
    private String email;
    private String password;
    private UUID userId;
}
