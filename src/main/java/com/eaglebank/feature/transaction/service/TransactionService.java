package com.eaglebank.feature.transaction.service;

import com.eaglebank.feature.account.repository.BankAccountRepository;
import com.eaglebank.feature.common.exception.InsufficientFundsException;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import com.eaglebank.feature.transaction.domain.Transaction;
import com.eaglebank.feature.transaction.repository.TransactionRepository;
import com.eaglebank.feature.transaction.web.model.TransactionRequest;
import com.eaglebank.feature.transaction.web.model.TransactionResponse;
import com.eaglebank.feature.transaction.web.model.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public void createTransaction(UUID accountId, UUID userId, TransactionRequest request) throws AccessDeniedException {
        // Validate account ownership and get current balance
        BigDecimal balance = bankAccountRepository.getBalance(accountId, userId);
        if (balance == null) {
            throw new AccessDeniedException("Account not found or access denied");
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        if (TransactionType.WITHDRAW == request.getType()) {
            if (balance.compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            bankAccountRepository.withdrawBalance(amount, accountId);

        } else if (TransactionType.DEPOSIT == request.getType()) {
            bankAccountRepository.depositBalance(amount, accountId);
        } else {
            throw new IllegalArgumentException("Transaction type must be DEPOSIT or WITHDRAWAL");
        }
        Transaction transaction = transaction(accountId, request, amount);
        transactionRepository.createTransaction(transaction);
    }

    public List<TransactionResponse> getTransactions(UUID accountId, UUID userId) throws AccessDeniedException {
        Integer count = bankAccountRepository.countBankAccounts(accountId, userId);
        if (count == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        if (count == 0) {
            throw new AccessDeniedException("Account not found or access denied");
        }
        List<Transaction> transactions = transactionRepository.getTransactions(accountId);
        return transactions.stream()
                .map(TransactionService::getTransactionResponse)
                .toList();
    }

    public TransactionResponse getTransaction(UUID transactionId, UUID accountId, UUID userId) throws AccessDeniedException {
        Integer count = bankAccountRepository.countBankAccounts(accountId, userId);
        if (count == null) {
            throw new ResourceNotFoundException("Account not found");
        }
        if (count == 0) {
            throw new AccessDeniedException("Account not found or access denied");
        }
        Transaction transaction = transactionRepository.getTransaction(transactionId, accountId);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found for this account");
        }
        return getTransactionResponse(transaction);
    }

    private static TransactionResponse getTransactionResponse(Transaction txn) {
        return TransactionResponse.builder()
                .transactionId(txn.getTransactionId())
                .amount(txn.getAmount())
                .type(txn.getType())
                .timestamp(txn.getTimestamp())
                .build();
    }

    private static Transaction transaction(UUID accountId, TransactionRequest request, BigDecimal amount) {
        return Transaction.builder()
                .accountId(accountId)
                .amount(amount)
                .type(request.getType())
                .build();
    }
}
