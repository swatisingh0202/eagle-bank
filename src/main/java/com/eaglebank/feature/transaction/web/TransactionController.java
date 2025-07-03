package com.eaglebank.feature.transaction.web;

import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.common.web.BaseController;
import com.eaglebank.feature.transaction.service.TransactionService;
import com.eaglebank.feature.transaction.web.model.TransactionRequest;
import com.eaglebank.feature.transaction.web.model.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/accounts/{accountId}/transactions")
@Tag(name = "Transactions", description = "Apis related to transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController extends BaseController {
    private final TransactionService transactionService;

    public TransactionController(JwtProvider jwtProvider, TransactionService transactionService) {
        super(jwtProvider);
        this.transactionService = transactionService;
    }

    @PostMapping()
    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction for the specified account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Operation not allowed"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "422", description = "Insufficient funds to process transaction"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<?> createTransaction(@PathVariable UUID accountId,
                                               @RequestBody @Valid TransactionRequest request,
                                               @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        UUID userId = getAuthenticatedUserId(authHeader);
        transactionService.createTransaction(accountId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping()
    @Operation(
            summary = "List all transactions for an account",
            description = "Retrieves all transactions for the specified account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public List<TransactionResponse> listTransactions(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        UUID userId = getAuthenticatedUserId(authHeader);
        return transactionService.getTransactions(accountId, userId);
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get transaction by ID",
            description = "Retrieves a transaction by its unique transaction ID for the specified account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public TransactionResponse getTransactionById(
            @Parameter(description = "Account ID", required = true)
            @PathVariable UUID accountId,
            @Parameter(description = "Transaction ID", required = true)
            @PathVariable UUID transactionId,
            @Parameter(description = "Bearer token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        UUID userId = getAuthenticatedUserId(authHeader);
        return transactionService.getTransaction(transactionId, accountId, userId);
    }
}
