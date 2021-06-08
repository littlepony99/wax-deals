package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ConfirmationTokenDao;
import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.jdbc.mapper.ConfirmationTokenRowMapper;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JdbcConfirmationTokenDao implements ConfirmationTokenDao {

    private static final String INSERT = "INSERT INTO confirmation_tokens (user_id, token, created_at) VALUES (?, ?, ?)";
    private static final String SELECT_BY_TOKEN = "SELECT id, user_id, token, created_at FROM confirmation_tokens WHERE token=?";
    private static final String SELECT_BY_USER_ID = "SELECT id, user_id, token, created_at FROM confirmation_tokens WHERE user_id=?";
    private static final String UPDATE = "UPDATE confirmation_tokens SET token=?, created_at=? WHERE id=?";
    private static final String DELETE = "DELETE FROM confirmation_tokens WHERE user_id=? ";
    private static final String UPDATE_USER_STATUS = "UPDATE users set status = true WHERE id =?";
    private static final RowMapper<ConfirmationToken> ROW_MAPPER = new ConfirmationTokenRowMapper();
    private final HikariDataSource dataSource;

    public JdbcConfirmationTokenDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<ConfirmationToken> findByToken(UUID token) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(SELECT_BY_TOKEN)) {
            ConfirmationToken confirmationToken = null;
            findByEmailStatement.setObject(1, token);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    confirmationToken = ROW_MAPPER.mapRow(resultSet);
                }
            }
            log.debug("Resulting optional with confirmation token is {'Optional.ofNullable(confirmationToken)':{}}", Optional.ofNullable(confirmationToken));
            return Optional.ofNullable(confirmationToken);
        } catch (SQLException e) {
            log.error("SQLException retrieving confirmation token by email from confirmation_tokens", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ConfirmationToken> findByUserId(long userId) {
        ConfirmationToken confirmationToken = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(SELECT_BY_USER_ID)) {
            findByEmailStatement.setLong(1, userId);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    confirmationToken = ROW_MAPPER.mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            log.error("SQLException retrieving confirmation token by email from confirmation_tokens", e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting optional with confirmation token is {'Optional.ofNullable(confirmationToken)':{}}", Optional.ofNullable(confirmationToken));
        return Optional.ofNullable(confirmationToken);
    }

    @Override
    public boolean add(ConfirmationToken token) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setLong(1, token.getUserId());
            insertStatement.setObject(2, token.getToken());
            insertStatement.setTimestamp(3, Timestamp.from(Instant.now()));
            log.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            insertStatement.executeUpdate();
            log.info("Confirmation token is added to the database {'confirmationToken':{}}", token);
            return true;
        } catch (SQLException e) {
            log.error("Error while adding confirmation token to db {'confirmationToken':{}}", token, e);
            throw new RuntimeException("Error while adding confirmation token to db: " + token, e);
        }
    }

    @Override
    public boolean update(ConfirmationToken token) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
            updateStatement.setObject(1, token.getToken());
            updateStatement.setTimestamp(2, Timestamp.from(Instant.now()));
            updateStatement.setLong(3, token.getId());
            log.debug("Prepared statement {'preparedStatement':{}}.", updateStatement);
            int countRows = updateStatement.executeUpdate();
            log.info("Confirmation token is updated in the database {'confirmationToken':{}}, rows updated {}", token, countRows);
            return (countRows > 0);
        } catch (SQLException e) {
            log.error("Error while updating confirmation_token with {'confirmationToken':{}}", token, e);
            throw new RuntimeException("Error while updating confirmation_token");
        }
    }

    @Override
    public boolean deleteByUserId(long userId) {
        boolean isDeleted = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement removeStatement = connection.prepareStatement(DELETE);
             PreparedStatement updateUserStatement = connection.prepareStatement(UPDATE_USER_STATUS)
        ) {
            connection.setAutoCommit(false);

            removeStatement.setLong(1, userId);
            updateUserStatement.setLong(1, userId);
            log.debug("Prepared statement {'preparedStatement':{}}.", removeStatement);
            int result = removeStatement.executeUpdate();
            int resultUpdateUser = updateUserStatement.executeUpdate();
            if (result > 0 && resultUpdateUser > 0) {
                isDeleted = true;
                connection.commit();
            } else {
                connection.rollback();
            }

            connection.setAutoCommit(true);
        } catch (SQLException e) {
            log.error("Error while deleting confirmation token from confirmation_tokens", e);
            isDeleted = false;
        }
        if (isDeleted) {
            log.info("Confirmation token by user id was deleted from database {'userId':{}}", userId);
        } else {
            log.info("Failed to delete confirmation token by userId from database {'userId':{}}", userId);
        }
        return isDeleted;
    }

}
