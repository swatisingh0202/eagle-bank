package com.eaglebank.feature.account.service;


import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.account.repository.domain.BankAccount;
import com.eaglebank.feature.account.web.model.AccountType;
import com.eaglebank.feature.account.web.model.BankAccountResponse;
import com.eaglebank.feature.account.web.model.CreateBankAccountRequest;
import com.eaglebank.feature.account.web.model.UpdateBankAccountRequest;
import com.eaglebank.feature.common.exception.IdentityException;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.eaglebank.feature.account.service.BankAccountUtils.generateAccountNumber;
import static com.eaglebank.feature.account.service.BankAccountUtils.generateSortCode;

@Service
public class BankAccountService {
    //TODO: Add support for user to update currency
    public static final String CURRENCY = "GBP";
    private final BankAccountRepository bankAccountRepository;


    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public BankAccountResponse createAccount(UUID userId, CreateBankAccountRequest createBankAccountRequest) {
        BankAccount bankAccount = bankAccount(userId, createBankAccountRequest.getAccountType(), createBankAccountRequest.getName(),
                generateAccountNumber(), generateSortCode());
        UUID accountId = bankAccountRepository.createAccount(userId, bankAccount);
        return bankAccountResponse(userId, accountId, bankAccount);
    }

    public BankAccountResponse getAccount(UUID accountId) {
        BankAccount bankAccount = bankAccountRepository.getAccount(accountId);
        return bankAccountResponse(bankAccount.getUserId(), accountId, bankAccount);
    }

    public List<BankAccountResponse> getAccountsByUserId(UUID userId) {
        List<BankAccount> accounts = bankAccountRepository.getAccountsByUserId(userId);
        return accounts.stream()
                .map(acc -> bankAccountResponse(acc.getUserId(), acc.getAccountId(), acc))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateAccount(UUID accountId, UpdateBankAccountRequest updateBankAccountRequest) {
        BankAccount bankAccount = bankAccountRepository.getAccount(accountId);
        if (bankAccount != null) {
            bankAccount.setName(updateBankAccountRequest.getName());
            bankAccount.setAccountType(updateBankAccountRequest.getAccountType());
            bankAccountRepository.updateAccount(accountId, bankAccount);
        }
    }

    @Transactional
    public void deleteAccount(UUID accountId) {
        //TODO: validations -- is balance zero?
        bankAccountRepository.deleteAccount(accountId);
    }

    private BankAccount bankAccount(UUID userId, AccountType accountType, String name, String accountNumber, String sortcode) {
        return BankAccount.builder()
                .userId(userId)
                .accountNumber(accountNumber)
                .accountType(accountType)
                .sortCode(sortcode)
                .balance(BigDecimal.ZERO)
                .currency(CURRENCY)
                .name(name)
                .build();
    }

    private BankAccountResponse bankAccountResponse(UUID userId, UUID accountId, BankAccount bankAccount) {
        return BankAccountResponse.builder()
                .accountId(accountId)
                .userId(userId)
                .accountNumber(bankAccount.getAccountNumber())
                .sortCode(bankAccount.getSortCode())
                .accountType(bankAccount.getAccountType())
                .name(bankAccount.getName())
                .balance(bankAccount.getBalance())
                .currency(bankAccount.getCurrency())
                .build();
    }

    public BankAccountResponse getAccountForUser(UUID accountId, UUID userId) {
        BankAccount bankAccount;
        try {
            bankAccount = bankAccountRepository.getAccount(accountId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Bank account not found");
        }
        if (bankAccount == null) {
            throw new ResourceNotFoundException("Bank account not found");
        }
        if (!bankAccount.getUserId().equals(userId)) {
            throw new IdentityException("You are not authorized to access this bank account.");
        }
        return bankAccountResponse(userId, accountId, bankAccount);
    }
}