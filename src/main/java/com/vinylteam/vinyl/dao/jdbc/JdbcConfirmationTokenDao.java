package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.ConfirmationTokenResultSetExtractor;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.SQLException;
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
    private static final String UPDATE = "UPDATE confirmation_tokens SET token = :token, created_at = :created_at WHERE id = :id";
    private static final String DELETE = "DELETE FROM confirmation_tokens WHERE user_id = :user_id ";
    private static final String UPDATE_USER_STATUS = "UPDATE users set status = true WHERE id = :id";

    private static final ConfirmationTokenResultSetExtractor CONFIRMATION_TOKEN_RESULT_SET_EXTRACTOR = new ConfirmationTokenResultSetExtractor();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<ConfirmationToken> findByToken(UUID token) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("token", token);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                SELECT_BY_TOKEN,
                sqlParameterSource,
                CONFIRMATION_TOKEN_RESULT_SET_EXTRACTOR));
    }

    @Override
    public Optional<ConfirmationToken> findByUserId(long userId) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                SELECT_BY_USER_ID,
                sqlParameterSource,
                CONFIRMATION_TOKEN_RESULT_SET_EXTRACTOR));
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
                .addValue("id", token.getId());
        namedParameterJdbcTemplate.update(UPDATE, sqlParameterSource);
    }

    @Override
    public void deleteByUserId(long userId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        Connection connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
        try {
            try {
                connection.setAutoCommit(false);

                MapSqlParameterSource deleteSqlParameterSource = new MapSqlParameterSource();
                deleteSqlParameterSource.addValue("user_id", userId);

                MapSqlParameterSource updateSqlParameterSource = new MapSqlParameterSource();
                updateSqlParameterSource.addValue("id", userId);

                int deleteResult = namedParameterJdbcTemplate.update(DELETE, deleteSqlParameterSource);
                int updateResult = namedParameterJdbcTemplate.update(UPDATE_USER_STATUS, updateSqlParameterSource);

                if (deleteResult > 0 && updateResult > 0) {
                    connection.commit();
                    log.info("Confirmation token by user id was deleted from database {'userId':{}}", userId);
                } else {
                    connection.rollback();
                    log.info("Failed to delete confirmation token by userId from database {'userId':{}, " +
                            "'countRemoved':{}, 'countUpdatedUser':{}}, transaction roll backed", userId, deleteResult, updateResult);
                }
            } catch (SQLException e) {
                connection.rollback();
                log.error("Error while deleting confirmation token from confirmation_tokens - rollback transaction", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.error("Error while deleting confirmation token from confirmation_tokens", e);
        }

    }

}
