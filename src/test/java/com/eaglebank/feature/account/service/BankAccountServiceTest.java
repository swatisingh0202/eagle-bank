package com.eaglebank.feature.account.service;

import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.account.repository.domain.BankAccount;
import com.eaglebank.feature.account.web.model.AccountType;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.account.web.model.CreateBankAccountRequest;
import com.eaglebank.feature.account.web.model.UpdateBankAccountRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private BankAccountService bankAccountService;
    private UUID userId;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        accountId = UUID.randomUUID();
    }

    @Test
    void createAccount() {
        CreateBankAccountRequest request = CreateBankAccountRequest.builder()
                .name("Test Account")
                .accountType(AccountType.CURRENT)
                .build();
        when(bankAccountRepository.createAccount(any(), any())).thenReturn(accountId);
        BankAccountResponse response = bankAccountService.createAccount(userId, request);
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertEquals(userId, response.getUserId());
        verify(bankAccountRepository, times(1)).createAccount(any(), any());
    }

    @Test
    void getAccount() {
        BankAccount bankAccount = BankAccount.builder()
                .accountId(accountId)
                .userId(userId)
                .name("Test Account")
                .accountType(AccountType.PERSONAL)
                .accountNumber("12345678")
                .sortCode("12-34-56")
                .balance(BigDecimal.ZERO)
                .currency("GBP")
                .build();
        when(bankAccountRepository.getAccount(accountId)).thenReturn(bankAccount);
        BankAccountResponse response = bankAccountService.getAccount(accountId);
        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertEquals(userId, response.getUserId());
        assertEquals("Test Account", response.getName());
        verify(bankAccountRepository, times(1)).getAccount(accountId);
    }

    @Test
    void updateAccount() {
        BankAccount bankAccount = BankAccount.builder()
                .accountId(accountId)
                .userId(userId)
                .name("Old Name")
                .accountType(AccountType.PERSONAL)
                .accountNumber("12345678")
                .sortCode("12-34-56")
                .balance(BigDecimal.ZERO)
                .currency("GBP")
                .build();
        UpdateBankAccountRequest updateRequest = UpdateBankAccountRequest.builder()
                .name("New Name")
                .accountType(AccountType.CURRENT)
                .build();
        when(bankAccountRepository.getAccount(accountId)).thenReturn(bankAccount);
        bankAccountService.updateAccount(accountId, updateRequest);
        assertEquals("New Name", bankAccount.getName());
        assertEquals(AccountType.CURRENT, bankAccount.getAccountType());
        verify(bankAccountRepository, times(1)).updateAccount(eq(accountId), any(BankAccount.class));
    }

    @Test
    void deleteAccount() {
        doNothing().when(bankAccountRepository).deleteAccount(accountId);
        bankAccountService.deleteAccount(accountId);
        verify(bankAccountRepository, times(1)).deleteAccount(accountId);
    }

}