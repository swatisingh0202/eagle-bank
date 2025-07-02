package com.eaglebank.feature.transaction.service;

import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.common.exception.InsufficientFundsException;
import com.eaglebank.feature.transaction.domain.Transaction;
import com.eaglebank.feature.transaction.repository.TransactionRepository;
import com.eaglebank.feature.transaction.web.model.TransactionRequest;
import com.eaglebank.feature.transaction.web.model.TransactionResponse;
import com.eaglebank.feature.transaction.web.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private TransactionService transactionService;
    private UUID accountId;
    private UUID userId;
    private UUID transactionId;

    @BeforeEach
    void setUpMocks() {
        MockitoAnnotations.openMocks(this);
        accountId = UUID.randomUUID();
        userId = UUID.randomUUID();
        transactionId = UUID.randomUUID();
    }

    @Test
    void createTransaction_deposit_success() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.DEPOSIT)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(BigDecimal.valueOf(200));
        transactionService.createTransaction(accountId, userId, request);
        verify(bankAccountRepository).depositBalance(BigDecimal.valueOf(100), accountId);
        verify(transactionRepository).createTransaction(any(Transaction.class));
    }

    @Test
    void createTransaction_withdraw_success() throws Exception {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(50))
                .type(TransactionType.WITHDRAW)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(BigDecimal.valueOf(100));
        transactionService.createTransaction(accountId, userId, request);
        verify(bankAccountRepository).withdrawBalance(BigDecimal.valueOf(50), accountId);
        verify(transactionRepository).createTransaction(any(Transaction.class));
    }

    @Test
    void createTransaction_withdraw_insufficientFunds() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(150))
                .type(TransactionType.WITHDRAW)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(BigDecimal.valueOf(100));
        assertThrows(InsufficientFundsException.class, () ->
                transactionService.createTransaction(accountId, userId, request));
        verify(transactionRepository, never()).createTransaction(any());
    }

    @Test
    void createTransaction_invalidAmount() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.ZERO)
                .type(TransactionType.DEPOSIT)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(BigDecimal.valueOf(100));
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(accountId, userId, request));
    }

    @Test
    void createTransaction_invalidType() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(10))
                .type(null)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(BigDecimal.valueOf(100));
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(accountId, userId, request));
    }

    @Test
    void createTransaction_accountNotFound() {
        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(10))
                .type(TransactionType.DEPOSIT)
                .build();
        when(bankAccountRepository.getBalance(accountId, userId)).thenReturn(null);
        assertThrows(AccessDeniedException.class, () ->
                transactionService.createTransaction(accountId, userId, request));
    }

    @Test
    void getTransactions_success() throws Exception {
        when(bankAccountRepository.countBankAccounts(accountId, userId)).thenReturn(1);
        Transaction txn = Transaction.builder().transactionId(transactionId).amount(BigDecimal.TEN).type(TransactionType.DEPOSIT).build();
        when(transactionRepository.getTransactions(accountId)).thenReturn(List.of(txn));
        List<TransactionResponse> responses = transactionService.getTransactions(accountId, userId);
        assertEquals(1, responses.size());
        assertEquals(transactionId, responses.get(0).getTransactionId());
    }

    @Test
    void getTransactions_accessDenied() {
        when(bankAccountRepository.countBankAccounts(accountId, userId)).thenReturn(0);
        assertThrows(AccessDeniedException.class, () ->
                transactionService.getTransactions(accountId, userId));
    }

    @Test
    void getTransaction_success() throws Exception {
        when(bankAccountRepository.countBankAccounts(accountId, userId)).thenReturn(1);
        Transaction txn = Transaction.builder().transactionId(transactionId).amount(BigDecimal.TEN).type(TransactionType.DEPOSIT).build();
        when(transactionRepository.getTransaction(transactionId, accountId)).thenReturn(txn);
        TransactionResponse response = transactionService.getTransaction(transactionId, accountId, userId);
        assertEquals(transactionId, response.getTransactionId());
    }

    @Test
    void getTransaction_accessDenied() {
        when(bankAccountRepository.countBankAccounts(accountId, userId)).thenReturn(0);
        assertThrows(AccessDeniedException.class, () ->
                transactionService.getTransaction(transactionId, accountId, userId));
    }
}