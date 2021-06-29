package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.ConfirmationTokenResultSetExtractor;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcConfirmationTokenDao implements ConfirmationTokenDao {

    private static final String INSERT = "INSERT INTO confirmation_tokens (user_id, token, created_at) VALUES (:user_id, :token, :created_at)";
    private static final String SELECT_BY_TOKEN = "SELECT id, user_id, token, created_at FROM confirmation_tokens WHERE token = :token";
    private static final String SELECT_BY_USER_ID = "SELECT id, user_id, token, created_at FROM confirmation_tokens WHERE user_id = :user_id";
    private static final String UPDATE = "UPDATE confirmation_tokens SET token = :token, created_at = :created_at WHERE user_id = :user_id";
    private static final String DELETE = "DELETE FROM confirmation_tokens WHERE user_id = :user_id ";

    private static final ResultSetExtractor<ConfirmationToken> RESULT_SET_EXTRACTOR = new ConfirmationTokenResultSetExtractor();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<ConfirmationToken> findByToken(UUID token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("token", token);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                SELECT_BY_TOKEN,
                sqlParameterSource,
                RESULT_SET_EXTRACTOR));
    }

    @Override
    public Optional<ConfirmationToken> findByUserId(long userId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                SELECT_BY_USER_ID,
                sqlParameterSource,
                RESULT_SET_EXTRACTOR));
    }

    @Override
    public void add(ConfirmationToken token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource
                .addValue("user_id", token.getUserId())
                .addValue("token", token.getToken())
                .addValue("created_at", Timestamp.from(Instant.now()));
        namedParameterJdbcTemplate.update(INSERT, sqlParameterSource);
    }

    @Override
    public void update(ConfirmationToken token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource
                .addValue("token", token.getToken())
                .addValue("created_at", Timestamp.from(Instant.now()))
                .addValue("user_id", token.getUserId());
        namedParameterJdbcTemplate.update(UPDATE, sqlParameterSource);
    }

    @Override
    public void deleteByUserId(long userId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        namedParameterJdbcTemplate.update(DELETE, sqlParameterSource);
    }

}
