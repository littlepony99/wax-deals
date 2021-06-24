package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.RecoveryTokenResultSetExtractor;
import com.vinylteam.vinyl.entity.RecoveryToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcRecoveryPasswordDao implements RecoveryPasswordDao {

    private static final String INSERT_TOKEN = "INSERT INTO recovery_password_tokens" +
            " (user_id, token, created_at)" +
            " VALUES (:user_id, :token, :created_at)" +
            " ON CONFLICT (user_id) DO UPDATE SET token = :token, created_at = :created_at";
    private static final String FIND_BY_TOKEN = "SELECT id, user_id, token, created_at FROM recovery_password_tokens" +
            " WHERE token = :token";
    private static final String REMOVE_TOKEN = "DELETE FROM recovery_password_tokens" +
            " WHERE id = :id";

    private static final RecoveryTokenResultSetExtractor RECOVERY_ROW_MAPPER = new RecoveryTokenResultSetExtractor();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public boolean add(RecoveryToken token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource
                .addValue("user_id", token.getUserId())
                .addValue("token", token.getToken())
                .addValue("created_at", Timestamp.from(Instant.now()));
        return namedParameterJdbcTemplate.update(INSERT_TOKEN, sqlParameterSource) > 0;
    }

    @Override
    public Optional<RecoveryToken> findByToken(UUID token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("token", token);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                FIND_BY_TOKEN,
                sqlParameterSource,
                RECOVERY_ROW_MAPPER));
    }

    @Override
    public boolean deleteById(long id) {
        MapSqlParameterSource deleteSqlParameterSource = new MapSqlParameterSource();
        deleteSqlParameterSource.addValue("id", id);
        return namedParameterJdbcTemplate.update(REMOVE_TOKEN, deleteSqlParameterSource) > 0;
    }

}