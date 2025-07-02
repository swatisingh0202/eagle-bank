package com.eaglebank.feature.auth.repository;


import com.eaglebank.feature.auth.repository.domain.Identity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class IdentityRepository {
    private final JdbcTemplate jdbcTemplate;

    public IdentityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public List<Identity> getIdentityByEmail(String email) {
        String sql = "SELECT * FROM identity WHERE email = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Identity.class), email);
    }

    public void createIdentity(UUID userId, String email, String password) {
        jdbcTemplate.update("INSERT INTO identity(identity_id, email, password, user_id) VALUES (?, ?, ?, ?)",
                UUID.randomUUID(), email, password, userId);
    }
}
