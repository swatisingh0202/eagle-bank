package com.eaglebank.feature.account;

import com.eaglebank.feature.account.service.BankAccountService;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.account.web.model.CreateBankAccountRequest;
import com.eaglebank.feature.account.web.model.UpdateBankAccountRequest;
import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.common.web.BaseController;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/v1/accounts")
@Tag(name = "Bank Accounts", description = "Apis related to accounts")
@SecurityRequirement(name = "bearerAuth")
public class BankAccountController extends BaseController {
    private final BankAccountService bankAccountService;

    public BankAccountController(JwtProvider jwtProvider,
                                 BankAccountService bankAccountService) {
        super(jwtProvider);
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Create a new bank account for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Operation not allowed"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public ResponseEntity<BankAccountResponse> createAccount(@PathVariable UUID userId,
                                                             @Valid @RequestBody CreateBankAccountRequest account,
                                                             @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) throws AccessDeniedException {
        validateUserId(userId, authHeader);
        BankAccountResponse response = bankAccountService.createAccount(userId, account);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{accountId}")
    @Operation(
            summary = "Fetch bank account by account id",
            description = "Retrieves a bank account by its unique account ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bank account found",
                    content = @Content(schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public BankAccountResponse getAccount(@PathVariable UUID accountId,
                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        UUID userId = getAuthenticatedUserId(authHeader);
        return bankAccountService.getAccountForUser(accountId, userId);
    }

    @GetMapping
    @Operation(
            summary = "Fetch all bank accounts for the authenticated user",
            description = "Retrieves all bank accounts associated with the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of bank accounts",
                    content = @Content(schema = @Schema(implementation = BankAccountResponse.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public List<BankAccountResponse> getAccountsForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        UUID userId = getAuthenticatedUserId(authHeader);
        return bankAccountService.getAccountsByUserId(userId);
    }

    @PatchMapping("/{accountId}")
    @Operation(
            summary = "Update bank account",
            description = "Updates details of an existing bank account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bank account updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public void updateAccount(@PathVariable UUID accountId,
                              @Valid @RequestBody UpdateBankAccountRequest account) {
        bankAccountService.updateAccount(accountId, account);
    }

    @DeleteMapping("/{accountId}")
    @Operation(
            summary = "Delete bank account",
            description = "Deletes a bank account by its unique account ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bank account deleted"),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected error")
    })
    public void deleteAccount(@PathVariable UUID accountId) {
        bankAccountService.deleteAccount(accountId);
    }


}
