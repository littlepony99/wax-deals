package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcUserDao implements UserDao {

    private static final RowMapper<User> ROW_MAPPER = new UserRowMapper();
    private static final String FIND_BY_EMAIL = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE email ILIKE ?";
    private static final String FIND_BY_ID = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE id=?";
    private static final String INSERT = "INSERT INTO users" +
            " (email, password, salt, iterations, role, status, discogs_user_name)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
    private static final String DELETE = "DELETE FROM users WHERE email ILIKE ?";
    private static final String UPDATE = "UPDATE users" +
            " SET email = ?, password = ?, salt = ?, iterations = ?, role = ?, status = ?, discogs_user_name = ?" +
            " WHERE email ILIKE ?";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long add(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
            insertStatement.setString(1, user.getEmail());
            insertStatement.setString(2, user.getPassword());
            insertStatement.setString(3, user.getSalt());
            insertStatement.setInt(4, user.getIterations());
            insertStatement.setString(5, user.getRole().toString());
            insertStatement.setBoolean(6, user.getStatus());
            insertStatement.setString(7, user.getDiscogsUserName());
            log.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            ResultSet resultSet = insertStatement.executeQuery();
            if (resultSet.next()) {
                long userId = resultSet.getLong(1);
                log.info("User is added to the database {'user':{} 'id':{}}.", user, userId);
                return userId;
            }
            log.info("Failed to add user to the database {'user':{}}.", user);
            return -1;
        } catch (SQLException e) {
            log.error("Error while add user {'user':{}}.", user, e);
            return -1;
        }
    }

    @Override
    public boolean delete(User user) {
        boolean isDeleted = false;
        try {
            int result = jdbcTemplate.update(DELETE, user.getEmail());
            if (result > 0) {
                isDeleted = true;
                log.info("User was deleted from database {'user':{}}.", user);
            } else {
                log.info("Failed delete user from database {'user':{}}.", user);
            }
        } catch (DataAccessException e) {
            log.error("Error while delete user from users {'user':{}}", user, e);
            isDeleted = false;
        }
        return isDeleted;
    }

    @Override
    public boolean update(String email, User user) {
        boolean isUpdated = false;
        try {
            int result = jdbcTemplate.update(UPDATE,
                    user.getEmail(),
                    user.getPassword(),
                    user.getSalt(),
                    user.getIterations(),
                    user.getRole().toString(),
                    user.getStatus(),
                    user.getDiscogsUserName(),
                    email);
            if (result > 0) {
                isUpdated = true;
                log.info("User was updated in the database {'user':{}}.", user);
            } else {
                log.info("Failed to update user in the database {'user':{}}.", user);
            }
        } catch (DataAccessException e) {
            log.error("Error while updating user in users by old email to {'email': {}, 'updatedUser':{}}", email, user, e);
            isUpdated = false;
        }
        return isUpdated;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_EMAIL)) {
            User user = null;
            findByEmailStatement.setString(1, email);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = ROW_MAPPER.mapRow(resultSet);
                    if (resultSet.next()) {
                        log.error("More than one user was found for email: {}", email);
                        throw new RuntimeException("More than one user was found for email " + email);
                    }
                }
            }
            log.debug("Resulting optional with user is {'Optional.ofNullable(user)':{}}", Optional.ofNullable(user));
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            log.error("SQLException retrieving user by email from users", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByIdStatement = connection.prepareStatement(FIND_BY_ID)) {
            User user = null;
            findByIdStatement.setLong(1, id);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByIdStatement);
            try (ResultSet resultSet = findByIdStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = ROW_MAPPER.mapRow(resultSet);
                    if (resultSet.next()) {
                        throw new RuntimeException("More than one user was found for id");
                    }
                }
            }
            log.debug("Resulting optional with user is {'Optional.ofNullable(user)':{}}", Optional.ofNullable(user));
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            log.error("SQLException retrieving user by id from users", e);
            throw new RuntimeException(e);
        }
    }

}