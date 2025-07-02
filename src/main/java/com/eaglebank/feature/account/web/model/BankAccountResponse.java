package com.eaglebank.feature.account.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@Schema
public class BankAccountResponse {
    private UUID accountId;
    private UUID userId;
    private String name;
    private AccountType accountType;
    private String accountNumber;
    private String sortCode;
    private BigDecimal balance;
    private String currency;
    private ZonedDateTime createdTimestamp;
    private ZonedDateTime updatedTimestamp;
}
