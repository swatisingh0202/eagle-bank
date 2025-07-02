package com.eaglebank.feature.account.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema
public class CreateBankAccountRequest {
    @NotBlank(message = "Name is required.")
    private String name;
    @NotNull(message = "Account type is required")
    private AccountType accountType;
}
