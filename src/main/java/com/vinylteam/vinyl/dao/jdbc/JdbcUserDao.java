package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class JdbcUserDao implements UserDao {

    private static final RowMapper<User> ROW_MAPPER = new UserRowMapper();
    private static final String FIND_BY_EMAIL = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM public.users" +
            " WHERE email ILIKE ?";
    private static final String FIND_BY_ID = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM public.users" +
            " WHERE id=?";
    private static final String INSERT = "INSERT INTO public.users" +
            " (email, password, salt, iterations, role, status, discogs_user_name)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM users WHERE email ILIKE ?";
    private static final String UPDATE = "UPDATE public.users" +
            " SET email = ?, password = ?, salt = ?, iterations = ?, role = ?, status = ?, discogs_user_name = ?" +
            " WHERE email ILIKE ?";

    private final HikariDataSource dataSource;

    public JdbcUserDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(User user) {
        boolean isAdded = false;
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
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            log.warn("Database error while adding user to public.users", e);
            isAdded = false;
        } catch (SQLException e) {
            log.error("Error while adding user to public.users", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.info("User is added to the database {'user':{}}.", user);
        } else {
            log.info("Failed to add user to the database {'user':{}}.", user);
        }
        return isAdded;
    }

    @Override
    public boolean delete(User user) {
        boolean isDeleted = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement removeStatement = connection.prepareStatement(DELETE)) {
            removeStatement.setString(1, user.getEmail());
            log.debug("Prepared statement {'preparedStatement':{}}.", removeStatement);
            int result = removeStatement.executeUpdate();
            if (result > 0) {
                isDeleted = true;
            }
        } catch (PSQLException e) {
            log.error("Database error while delete user from public.users", e);
            isDeleted = false;
        } catch (SQLException e) {
            log.error("Error while delete user from public.users", e);
            isDeleted = false;
        }
        if (isDeleted) {
            log.info("User was deleted from database {'user':{}}.", user);
        } else {
            log.info("Failed delete user from database {'user':{}}.", user);
        }
        return isDeleted;
    }

    @Override
    public boolean update(String email, User user) {
        boolean isUpdated = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
            updateStatement.setString(1, user.getEmail());
            updateStatement.setString(2, user.getPassword());
            updateStatement.setString(3, user.getSalt());
            updateStatement.setInt(4, user.getIterations());
            updateStatement.setString(5, user.getRole().toString());
            updateStatement.setBoolean(6, user.getStatus());
            updateStatement.setString(7, user.getDiscogsUserName());
            updateStatement.setString(8, email);
            log.debug("Prepared statement {'preparedStatement':{}}.", updateStatement);
            int result = updateStatement.executeUpdate();
            if (result > 0) {
                isUpdated = true;
            }
        } catch (PSQLException e) {
            log.debug("Database error while edit user to public.users", e);
            isUpdated = false;
        } catch (SQLException e) {
            log.error("Error while updating user in public.users", e);
            throw new RuntimeException(e);
        }
        if (isUpdated) {
            log.info("User was updated in the database {'user':{}}.", user);
        } else {
            log.info("Failed to update user in the database {'user':{}}.", user);
        }
        return isUpdated;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_EMAIL)) {
            findByEmailStatement.setString(1, email);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = ROW_MAPPER.mapRow(resultSet);
                    if (resultSet.next()) {
                        RuntimeException e = new RuntimeException();
                        log.error("More than one user was found for email: {}", email, e);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            log.error("SQLException retrieving user by email from public.users", e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting optional with user is {'Optional.ofNullable(user)':{}}", Optional.ofNullable(user));
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findById(long id) {
        User user = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement findByEmailStatement = connection.prepareStatement(FIND_BY_ID)) {
            findByEmailStatement.setLong(1, id);
            log.debug("Prepared statement {'preparedStatement':{}}.", findByEmailStatement);
            try (ResultSet resultSet = findByEmailStatement.executeQuery()) {
                if (resultSet.next()) {
                    user = ROW_MAPPER.mapRow(resultSet);
                    if (resultSet.next()) {
                        RuntimeException e = new RuntimeException();
                        log.error("More than one user was found for id {'id':{}}", id, e);
                        throw e;
                    }
                }
            }
        } catch (SQLException e) {
            log.error("SQLException while retrieving user by id from public.users {'id':{}}", id, e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting optional with user is {'Optional.ofNullable(user)':{}}", Optional.ofNullable(user));
        return Optional.ofNullable(user);
    }

}