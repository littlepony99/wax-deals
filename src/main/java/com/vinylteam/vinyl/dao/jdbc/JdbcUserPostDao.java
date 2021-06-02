package com.vinylteam.vinyl.dao.jdbc;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.sql.*;

@Slf4j
public class JdbcUserPostDao implements UserPostDao {

    private static final String INSERT_USER_MESSAGE = "INSERT INTO public.user_posts" +
            " (name, email, theme, message, created_at)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_RECOVERY_TOKEN = "INSERT INTO public.recovery_password" +
            " (user_id, token)" +
            " VALUES (?, ?)";
    private static final String UPDATE_RECOVERY_TOKEN = "UPDATE public.recovery_password" +
            " SET token = ?" +
            " WHERE user_id = ?";
    private static final String FIND_RECOVERY_TOKEN = "SELECT token FROM public.recovery_password" +
            " WHERE user_id = ?";
    private static final String FIND_USER_ID = "SELECT user_id FROM public.recovery_password" +
            " WHERE token = ?";

    private final HikariDataSource dataSource;

    public JdbcUserPostDao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean add(UserPost post) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(INSERT_USER_MESSAGE)) {
            insertStatement.setString(1, post.getName());
            insertStatement.setString(2, post.getEmail());
            insertStatement.setString(3, post.getTheme());
            insertStatement.setString(4, post.getMessage());
            insertStatement.setTimestamp(5, Timestamp.valueOf(post.getCreatedAt()));
            log.debug("Prepared statement {'preparedStatement':{}}.", insertStatement);
            int result = insertStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            log.error("Database error while adding user post to public.user_posts", e);
            isAdded = false;
        } catch (SQLException e) {
            log.error("Error while adding user post to public.user_posts", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.info("User post is added to the database {'userPost':{}}.", post);
        } else {
            log.info("Failed to add user post to the database {'userPost':{}}.", post);
        }
        return isAdded;
    }

    @Override
    public boolean updateRecoveryUserToken(long userId, String recoveryToken) {
        boolean isUpdate = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateTokenStatement = connection.prepareStatement(UPDATE_RECOVERY_TOKEN)) {
            updateTokenStatement.setString(1, recoveryToken);
            updateTokenStatement.setLong(2, userId);
            log.debug("Prepared statement {'preparedStatement':{}}.", updateTokenStatement);
            int result = updateTokenStatement.executeUpdate();
            if (result > 0) {
                isUpdate = true;
            }
        } catch (PSQLException e) {
            log.error("Database error while updating recovery token to public.recovery_password", e);
            isUpdate = false;
        } catch (SQLException e) {
            log.error("Error while updating recovery token to public.recovery_password", e);
            throw new RuntimeException(e);
        }
        if (isUpdate) {
            log.info("Recovery token is updated to the database {'recoveryToken':{}, 'userId':{}}.", recoveryToken, userId);
        } else {
            log.info("Failed to update recovery token to the database {'recoveryToken':{}, 'userId':{}}.", recoveryToken, userId);
        }
        return isUpdate;
    }

    @Override
    public boolean addRecoveryUserToken(long userId, String recoveryToken) {
        boolean isAdded = false;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement addTokenStatement = connection.prepareStatement(INSERT_RECOVERY_TOKEN)) {
            addTokenStatement.setLong(1, userId);
            addTokenStatement.setString(2, recoveryToken);
            log.debug("Prepared statement {'preparedStatement':{}}.", addTokenStatement);
            int result = addTokenStatement.executeUpdate();
            if (result > 0) {
                isAdded = true;
            }
        } catch (PSQLException e) {
            log.error("Database error while adding recovery token to public.recovery_password", e);
            isAdded = false;
        } catch (SQLException e) {
            log.error("Error while adding recovery token to public.recovery_password", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.info("Recovery token is added to the database {'recoveryToken':{}, 'userId':{}}.", recoveryToken, userId);
        } else {
            log.info("Failed to add recovery token to the database {'recoveryToken':{}, 'userId':{}}.", recoveryToken, userId);
        }
        return isAdded;
    }

    @Override
    public String getRecoveryUserToken(long userId) {
        String recoveryToken = "";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectTokenStatement = connection.prepareStatement(FIND_RECOVERY_TOKEN)) {
            selectTokenStatement.setLong(1, userId);
            log.debug("Prepared statement {'preparedStatement':{}}.", selectTokenStatement);
            try (ResultSet resultSet = selectTokenStatement.executeQuery()){
                if (resultSet.next()){
                    recoveryToken = resultSet.getString("token");
                    log.info("Get recovery token from db {'recoveryToken':{}}.", recoveryToken);
                }
            }

        } catch (PSQLException e) {
            log.error("Database error while adding recovery token to public.recovery_password", e);
        } catch (SQLException e) {
            log.error("Error while adding recovery token to public.recovery_password", e);
            throw new RuntimeException(e);
        }
        return recoveryToken;
    }

    @Override
    public long getRecoveryUserId(String token){
        long userId = 0L;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectUserIdStatement = connection.prepareStatement(FIND_USER_ID)) {
            selectUserIdStatement.setString(1, token);
            log.debug("Prepared statement {'preparedStatement':{}}.", selectUserIdStatement);
            try (ResultSet resultSet = selectUserIdStatement.executeQuery()){
                if (resultSet.next()){
                    userId = resultSet.getLong("user_id");
                    log.info("Get user id from recovery_password table in db {'userId':{}}.", userId);
                }
            }
        } catch (PSQLException e) {
            log.error("Database error while getting user id from public.recovery_password", e);
        } catch (SQLException e) {
            log.error("Error while getting user id from public.recovery_password", e);
            throw new RuntimeException(e);
        }
        return userId;
    }

}