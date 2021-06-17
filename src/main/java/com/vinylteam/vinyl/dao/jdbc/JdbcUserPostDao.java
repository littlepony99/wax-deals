package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserPostDao;
import com.vinylteam.vinyl.entity.UserPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcUserPostDao implements UserPostDao {

    private static final String INSERT_USER_POST = "INSERT INTO user_posts" +
            " (name, email, theme, message, created_at)" +
            " VALUES (:name, :email, :theme, :message, :created_at)";

    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public boolean add(UserPost post) {
        //FIXME add normal return
        Map<String, Object> params = new HashMap<>();
        params.put("name", post.getName());
        params.put("email", post.getEmail());
        params.put("theme", post.getTheme());
        params.put("message", post.getMessage());
        params.put("created_at", Timestamp.valueOf(post.getCreatedAt()));
        namedJdbcTemplate.update(
                INSERT_USER_POST,
                params
        );
        return true;
    }

}