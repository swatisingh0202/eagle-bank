package com.eaglebank.feature.transaction.repository;

import com.eaglebank.feature.transaction.domain.Transaction;
import com.eaglebank.feature.transaction.web.model.TransactionType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {
    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createTransaction(Transaction transaction) {
        jdbcTemplate.update("INSERT INTO transaction (transaction_id, amount, type, timestamp, account_id) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?) ",
                UUID.randomUUID(), transaction.getAmount(), transaction.getType().name(), transaction.getAccountId());
    }

    public List<Transaction> getTransactions(UUID accountId) {
        String sql = "SELECT * FROM transaction WHERE account_id = ? ORDER BY timestamp DESC ";
        return jdbcTemplate.query(sql, new Object[]{accountId},
                (rs, rowNum) -> Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .amount(rs.getBigDecimal("amount"))
                        .type(TransactionType.valueOf(rs.getString("type")))
                        .timestamp(rs.getTimestamp("timestamp").toInstant().atZone(ZoneId.of("UTC")))
                        .build());
    }

    public Transaction getTransaction(UUID transactionId, UUID accountId) {
        String sql = "SELECT * FROM transaction WHERE transaction_id = ? AND account_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{transactionId, accountId},
                (rs, rowNum) -> Transaction.builder()
                        .transactionId(UUID.fromString(rs.getString("transaction_id")))
                        .accountId(UUID.fromString(rs.getString("account_id")))
                        .amount(rs.getBigDecimal("amount"))
                        .type(TransactionType.valueOf(rs.getString("type")))
                        .timestamp(rs.getTimestamp("timestamp").toInstant().atZone(ZoneId.of("UTC")))
                        .build());
    }
}

