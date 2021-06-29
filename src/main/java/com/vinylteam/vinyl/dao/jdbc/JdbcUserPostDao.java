package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcUserPostDao implements UserPostDao {

    private static final String INSERT_USER_POST = "INSERT INTO user_posts" +
            " (name, email, theme, message, created_at)" +
            " VALUES (:name , :email, :theme, :message, :create_at)";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void add(UserPost post) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource
                .addValue("name", post.getName())
                .addValue("email", post.getEmail())
                .addValue("theme", post.getTheme())
                .addValue("message", post.getMessage())
                .addValue("create_at", post.getCreatedAt());
        jdbcTemplate.update(INSERT_USER_POST, sqlParameterSource);
    }

}