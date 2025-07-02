package com.eaglebank.feature.account.repository;

import com.eaglebank.feature.account.repository.domain.BankAccount;
import com.eaglebank.feature.common.exception.ResourceNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@Repository
public class BankAccountRepository {
    private final JdbcTemplate jdbcTemplate;

    public BankAccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public UUID createAccount(UUID userId, BankAccount bankAccount) {
        String sql = "INSERT INTO bank_account (account_id, name, account_type, account_number, sort_code, balance, currency, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, UUID.randomUUID());
            ps.setString(2, bankAccount.getName());
            ps.setString(3, bankAccount.getAccountType().name());
            ps.setString(4, bankAccount.getAccountNumber());
            ps.setString(5, bankAccount.getSortCode());
            ps.setObject(6, bankAccount.getBalance());
            ps.setString(7, bankAccount.getCurrency());
            ps.setObject(8, userId);
            return ps;
        }, keyHolder);
        return (UUID) keyHolder.getKeyList().getFirst().get("account_id");
    }

    public BankAccount getAccount(UUID accountId) {
        String sql = "SELECT * FROM bank_account WHERE account_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(BankAccount.class), accountId);
        } catch (DataAccessException e) {
            throw new ResourceNotFoundException("Resource not found for accountId: " + accountId);
        }
    }

    public void updateAccount(UUID accountId, BankAccount account) {
        String sql = "UPDATE bank_account SET name = ?, account_type = ?, balance = ?, " +
                "updated_timestamp = CURRENT_TIMESTAMP WHERE account_id = ?";
        jdbcTemplate.update(sql,
                account.getName(),
                account.getAccountType().name(),
                account.getBalance(),
                accountId
        );
    }

    public void deleteAccount(UUID accountId) {
        jdbcTemplate.update("DELETE FROM bank_account WHERE account_id = ?", accountId);
    }

    public BigDecimal getBalance(UUID accountId, UUID userId) {
        String sql = "SELECT balance FROM bank_account WHERE account_id = ? AND user_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{accountId, userId}, BigDecimal.class);
    }

    public void withdrawBalance(BigDecimal amount, UUID accountId) {
        String sql = "UPDATE bank_account SET balance = balance - ?, updated_timestamp = CURRENT_TIMESTAMP WHERE account_id = ? ";
        jdbcTemplate.update(sql, amount, accountId);
    }

    public void depositBalance(BigDecimal amount, UUID accountId) {
        String sql = "UPDATE bank_account SET balance = balance + ?, updated_timestamp = CURRENT_TIMESTAMP WHERE account_id = ?";
        jdbcTemplate.update(sql, amount, accountId);
    }

    public Integer countBankAccounts(UUID accountId, UUID userId) {
        String sql = "SELECT COUNT(*) FROM bank_account WHERE account_id = ? AND user_id = ? ";
        return jdbcTemplate.queryForObject(sql, Integer.class, accountId, userId);
    }

    public List<BankAccount> getAccountsByUserId(UUID userId) {
        String sql = "SELECT * FROM bank_account WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(BankAccount.class), userId);
    }
}
