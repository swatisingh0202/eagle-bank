package com.eaglebank.feature.transaction.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@Schema
public class TransactionResponse {
    private UUID transactionId;
    private BigDecimal amount;
    private TransactionType type;
    private ZonedDateTime timestamp;
}

