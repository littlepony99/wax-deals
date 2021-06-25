package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.UserPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserPostResultSetExtractor implements ResultSetExtractor<UserPost> {

    @Override
    public UserPost extractData(ResultSet resultSet) throws SQLException {
        UserPost userPost = null;
        if (resultSet.next()) {
            userPost = UserPost.builder()
                    .id(resultSet.getLong("id"))
                    .name(resultSet.getString("name"))
                    .email(resultSet.getString("email"))
                    .theme(resultSet.getString("theme"))
                    .message(resultSet.getString("message"))
                    .createdAt(resultSet.getTimestamp("created_at").toLocalDateTime())
                    .build();
        }
        log.debug("Resulting user post object {'userPost':{}}", userPost);
        return userPost;
    }
}