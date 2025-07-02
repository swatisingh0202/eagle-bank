package com.eaglebank.feature.account.repository.domain;

import com.eaglebank.feature.account.web.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
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

