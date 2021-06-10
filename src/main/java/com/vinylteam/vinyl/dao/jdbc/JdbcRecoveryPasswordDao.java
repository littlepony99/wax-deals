package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RecoveryPasswordDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.RecoveryRowMapper;
import com.vinylteam.vinyl.entity.RecoveryToken;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
public class JdbcRecoveryPasswordDao implements RecoveryPasswordDao {

    private static final String INSERT_TOKEN = "INSERT INTO recovery_password" +
            " (user_id, token, created_at)" +
            " VALUES (?, ?, ?)" +
            " ON CONFLICT (user_id) DO UPDATE SET token = ?, created_at = ?";
    private static final String FIND_BY_TOKEN = "SELECT id, user_id, token, created_at FROM recovery_password" +
            " WHERE token = ?";
    private static final String REMOVE_TOKEN = "DELETE FROM recovery_password" +
            " WHERE id = ?";

    private final RecoveryRowMapper recoveryRowMapper = new RecoveryRowMapper();
    private final HikariDataSource dataSource;

    public JdbcRecoveryPasswordDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(RecoveryToken recoveryToken) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement addTokenStatement = connection.prepareStatement(INSERT_TOKEN)) {
            addTokenStatement.setLong(1, recoveryToken.getUserId());
            addTokenStatement.setString(2, recoveryToken.getToken());
            addTokenStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            addTokenStatement.setString(4, recoveryToken.getToken());
            addTokenStatement.setTimestamp(5, Timestamp.from(Instant.now()));
            log.debug("Prepared statement {'preparedStatement':{}}.", addTokenStatement);
            int result = addTokenStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
                log.info("Recovery token is added to the database {'recoveryToken':{}, 'userId':{}}.",
                        recoveryToken.getToken(), recoveryToken.getUserId());
            } else {
                log.info("Failed to add recovery token to the database {'recoveryToken':{}, 'userId':{}}.",
                        recoveryToken.getToken(), recoveryToken.getUserId());
            }
        } catch (SQLException e) {
            log.error("Database error while adding recovery token to recovery_password", e);
            isAdded = false;
        }
        return isAdded;
    }

    @Override
    public Optional<RecoveryToken> findByToken(String token) {
        RecoveryToken recoveryToken = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectRecoveryTokenStatement = connection.prepareStatement(FIND_BY_TOKEN)) {
            selectRecoveryTokenStatement.setString(1, token);
            log.debug("Prepared statement {'preparedStatement':{}}.", selectRecoveryTokenStatement);
            try (ResultSet resultSet = selectRecoveryTokenStatement.executeQuery()) {
                if (resultSet.next()) {
                    recoveryToken = recoveryRowMapper.mapRow(resultSet);
                    log.info("Get RecoveryToken from recovery_password table in db {'recoveryToken':{}}.", recoveryToken);
                }
            }
        } catch (SQLException e) {
            log.error("Database error while getting RecoveryToken by token from recovery_password", e);
        }
        return Optional.ofNullable(recoveryToken);
    }

    @Override
    public boolean deleteById(int id) {
        boolean isRemoved = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement removeStatement = connection.prepareStatement(REMOVE_TOKEN)) {
            removeStatement.setInt(1, id);
            log.debug("Prepared statement {'preparedStatement':{}}.", removeStatement);
            int result = removeStatement.executeUpdate();
            if (result > 0) {
                isRemoved = true;
                log.info("Recovery token was deleted from the database {'recoveryToken':{}}.", id);
            } else {
                log.info("Failed to update recovery token to the database {'recoveryToken':{}}.", id);
            }
        } catch (SQLException e) {
            log.error("Database error while deleting token from recovery_password", e);
            isRemoved = false;
        }
        return isRemoved;
    }

}