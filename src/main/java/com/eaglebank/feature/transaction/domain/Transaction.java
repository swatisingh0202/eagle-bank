package com.eaglebank.feature.transaction.domain;

import com.eaglebank.feature.transaction.web.model.TransactionType;
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
public class Transaction {
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private TransactionType type;
    private ZonedDateTime timestamp;
}
