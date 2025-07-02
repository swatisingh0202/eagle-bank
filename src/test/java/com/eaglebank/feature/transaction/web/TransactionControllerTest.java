package com.eaglebank.feature.transaction.web;

import com.eaglebank.feature.auth.JwtProvider;
import com.eaglebank.feature.common.exception.InsufficientFundsException;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import com.eaglebank.feature.transaction.service.TransactionService;
import com.eaglebank.feature.transaction.web.model.TransactionRequest;
import com.eaglebank.feature.transaction.web.model.TransactionResponse;
import com.eaglebank.feature.transaction.web.model.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TransactionService transactionService;
    @MockBean
    private JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private UUID accountId;
    private UUID userId;
    private UUID transactionId;
    private String token;
    private TransactionRequest depositRequest;
    private TransactionRequest withdrawRequest;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
        token = "Bearer test-token";
        depositRequest = TransactionRequest.builder().amount(BigDecimal.valueOf(100)).type(TransactionType.DEPOSIT).build();
        withdrawRequest = TransactionRequest.builder().amount(BigDecimal.valueOf(50)).type(TransactionType.WITHDRAW).build();
        transactionResponse = TransactionResponse.builder().transactionId(transactionId).amount(BigDecimal.valueOf(100)).type(TransactionType.DEPOSIT).build();
        when(jwtProvider.getUserId(anyString())).thenReturn(userId);
    }

    @Test
    @DisplayName("Given valid deposit, when POST, then return 201")
    void createTransaction_deposit_success() throws Exception {
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given valid withdrawal, when POST, then return 201")
    void createTransaction_withdraw_success() throws Exception {
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Given insufficient funds, when POST, then return 422")
    void createTransaction_insufficientFunds() throws Exception {
        doThrow(new InsufficientFundsException("Insufficient funds")).when(transactionService)
                .createTransaction(eq(accountId), eq(userId), any(TransactionRequest.class));
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("Given forbidden account, when POST, then return 403")
    void createTransaction_forbidden() throws Exception {
        doThrow(new AccessDeniedException("Forbidden")).when(transactionService)
                .createTransaction(eq(accountId), eq(userId), any(TransactionRequest.class));
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given non-existent account, when POST, then return 404")
    void createTransaction_accountNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Not found")).when(transactionService)
                .createTransaction(eq(accountId), eq(userId), any(TransactionRequest.class));
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given missing data, when POST, then return 400")
    void createTransaction_missingData() throws Exception {
        TransactionRequest invalidRequest = TransactionRequest.builder().amount(null).type(null).build();
        mockMvc.perform(post("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Given own account, when GET list, then return 200 and transactions")
    void listTransactions_success() throws Exception {
        when(transactionService.getTransactions(accountId, userId)).thenReturn(List.of(transactionResponse));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given forbidden account, when GET list, then return 403")
    void listTransactions_forbidden() throws Exception {
        when(transactionService.getTransactions(accountId, userId)).thenThrow(new AccessDeniedException("Forbidden"));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given non-existent account, when GET list, then return 404")
    void listTransactions_accountNotFound() throws Exception {
        when(transactionService.getTransactions(accountId, userId)).thenThrow(new ResourceNotFoundException("Not found"));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions", accountId)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given own account and valid transaction, when GET by id, then return 200 and transaction")
    void getTransactionById_success() throws Exception {
        when(transactionService.getTransaction(transactionId, accountId, userId)).thenReturn(transactionResponse);
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", accountId, transactionId)
                .header("Authorization", token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Given forbidden account, when GET by id, then return 403")
    void getTransactionById_forbidden() throws Exception {
        when(transactionService.getTransaction(transactionId, accountId, userId)).thenThrow(new AccessDeniedException("Forbidden"));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", accountId, transactionId)
                .header("Authorization", token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Given non-existent account, when GET by id, then return 404")
    void getTransactionById_accountNotFound() throws Exception {
        when(transactionService.getTransaction(transactionId, accountId, userId)).thenThrow(new ResourceNotFoundException("Not found"));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", accountId, transactionId)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Given non-existent transaction, when GET by id, then return 404")
    void getTransactionById_transactionNotFound() throws Exception {
        when(transactionService.getTransaction(transactionId, accountId, userId)).thenThrow(new ResourceNotFoundException("Transaction not found"));
        mockMvc.perform(get("/v1/accounts/{accountId}/transactions/{transactionId}", accountId, transactionId)
                .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}