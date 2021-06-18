package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;

@Slf4j
@Repository
public class JdbcUserPostDao implements UserPostDao {

    private static final String INSERT_USER_POST = "INSERT INTO user_posts" +
            " (name, email, theme, message, created_at)" +
            " VALUES (?, ?, ?, ?, ?)";

    @Autowired
    private DataSource dataSource;
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

    @Override
    public boolean add(UserPost post) {
        if (jdbcTemplate.update(INSERT_USER_POST, post.getName(), post.getEmail(),
                post.getTheme(), post.getMessage(), Timestamp.valueOf(post.getCreatedAt())) > 0) {
            log.info("User post is added to the database {'userPost':{}}.", post);
            return true;
        } else {
            log.info("Failed to add user post to the database {'userPost':{}}.", post);
            return false;
        }
    }
//TODO: DataAccessException - add subset of that in catch.
    //TODO: analog of RowMapper.

}