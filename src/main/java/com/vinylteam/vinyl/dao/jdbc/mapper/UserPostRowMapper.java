package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.UserPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserPostRowMapper implements RowMapper<UserPost> {

    @Override
    public UserPost mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        UserPost userPost = new UserPost();
        userPost.setId(resultSet.getLong("id"));
        userPost.setName(resultSet.getString("name"));
        userPost.setEmail(resultSet.getString("email"));
        userPost.setTheme(resultSet.getString("theme"));
        userPost.setMessage(resultSet.getString("message"));
        userPost.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        log.debug("Resulting user post object {'userPost':{}}", userPost);
        return userPost;
    }
}