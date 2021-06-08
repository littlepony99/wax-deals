package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Slf4j
public class JdbcUserPostDao implements UserPostDao {

    private static final String INSERT_USER_MESSAGE = "INSERT INTO user_posts" +
            " (name, email, theme, message, created_at)" +
            " VALUES (?, ?, ?, ?, ?)";

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
            log.error("Database error while adding user post to user_posts", e);
            isAdded = false;
        } catch (SQLException e) {
            log.error("Error while adding user post to user_posts", e);
            throw new RuntimeException(e);
        }
        if (isAdded) {
            log.info("User post is added to the database {'userPost':{}}.", post);
        } else {
            log.info("Failed to add user post to the database {'userPost':{}}.", post);
        }
        return isAdded;
    }

}