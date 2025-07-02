package com.eaglebank.feature.account.service;

import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.account.repository.domain.BankAccount;
import com.eaglebank.feature.account.web.model.AccountType;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.common.exception.IdentityException;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private BankAccountService bankAccountService;
    private UUID userId;
    private UUID otherUserId;
    private UUID accountId;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        bankAccount = BankAccount.builder()
                .accountId(accountId)
                .userId(userId)
                .name("Test Account")
                .accountType(AccountType.PERSONAL)
                .accountNumber("12345678")
                .sortCode("12-34-56")
                .balance(BigDecimal.TEN)
                .currency("GBP")
                .build();
    }

    @Test
    @DisplayName("Given a valid user and account, when getAccountForUser, then return account details")
    void getAccountForUser_success() {
        // Given
        when(bankAccountRepository.getAccount(accountId)).thenReturn(bankAccount);
        // When
        BankAccountResponse response = bankAccountService.getAccountForUser(accountId, userId);
        // Then
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertEquals(userId, response.getUserId());
    }

    @Test
    @DisplayName("Given a non-existent account, when getAccountForUser, then throw ResourceNotFoundException")
    void getAccountForUser_notFound() {
        // Given
        when(bankAccountRepository.getAccount(accountId)).thenReturn(null);
        // When & Then
        assertThrows(ResourceNotFoundException.class, () ->
                bankAccountService.getAccountForUser(accountId, userId));
    }

    @Test
    @DisplayName("Given an account not owned by user, when getAccountForUser, then throw AccessDeniedException")
    void getAccountForUser_forbidden() {
        // Given
        BankAccount otherAccount = BankAccount.builder()
                .accountId(accountId)
                .userId(otherUserId)
                .build();
        when(bankAccountRepository.getAccount(accountId)).thenReturn(otherAccount);
        // When & Then
        assertThrows(IdentityException.class, () ->
                bankAccountService.getAccountForUser(accountId, userId));
    }
}

