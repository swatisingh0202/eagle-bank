package com.eaglebank.feature.user.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema
public class CreateUserRequest {
    @NotBlank(message = "Name is required.")
    private String name;
    @Email
    private String email;
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone number must be valid")
    private String phone;
    @NotBlank(message = "Password is required.")
    private String password;
}
