package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.extractor.UserResultSetExtractor;
import com.vinylteam.vinyl.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class JdbcUserDao implements UserDao {

    private static final String FIND_BY_EMAIL = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE email ILIKE :email";
    private static final String FIND_BY_ID = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE id = :id";
    private static final String INSERT = "INSERT INTO users" +
            " (email, password, salt, iterations, role, status, discogs_user_name)" +
            " VALUES (:email, :password, :salt, :iterations, :role, :status, :discogs_user_name)";
    private static final String DELETE = "DELETE FROM users WHERE email ILIKE :email";
    private static final String UPDATE = "UPDATE users" +
            " SET email = :email, password = :password, salt = :salt, iterations = :iterations, role = :role, status = :status, discogs_user_name = :discogs_user_name" +
            " WHERE email ILIKE :old_email";
    private static final String UPDATE_PROFILE_FIELDS = "UPDATE users SET " +
            " discogs_user_name = :discogs_user_name, " +
            " email = CASE WHEN email <> :new_email THEN :new_email ELSE email END" +
            " WHERE email ILIKE :email";
    private static final String UPDATE_PASSWORD = "UPDATE users" +
            " SET password = :password" +
            " WHERE email ILIKE :email";

    private static final String UPDATE_USER_STATUS = "UPDATE users SET status = true WHERE id = :id";

    private static final ResultSetExtractor<User> RESULT_SET_EXTRACTOR = new UserResultSetExtractor();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public long add(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("salt", user.getSalt())
                .addValue("iterations", user.getIterations())
                .addValue("role", user.getRole().getName())
                .addValue("status", user.getStatus())
                .addValue("discogs_user_name", user.getDiscogsUserName());
        jdbcTemplate.update(INSERT, sqlParameterSource, keyHolder);
        return Long.parseLong(keyHolder.getKeys().get("id").toString());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update(DELETE, new MapSqlParameterSource()
                .addValue("email", user.getEmail()));
    }

    @Override
    public void update(String email, User user) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword())
                .addValue("salt", user.getSalt())
                .addValue("iterations", user.getIterations())
                .addValue("role", user.getRole().getName())
                .addValue("status", user.getStatus())
                .addValue("discogs_user_name", user.getDiscogsUserName())
                .addValue("old_email", email);
        jdbcTemplate.update(UPDATE, sqlParameterSource);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("email", email);
        return ofNullable(jdbcTemplate.query(
                FIND_BY_EMAIL,
                sqlParameterSource,
                RESULT_SET_EXTRACTOR));
    }

    @Override
    public Optional<User> findById(long id) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("id", id);
        return ofNullable(jdbcTemplate.query(FIND_BY_ID, sqlParameterSource, RESULT_SET_EXTRACTOR));
    }

    @Override
    public void setUserStatusTrue(long id) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("id", id);
        jdbcTemplate.update(UPDATE_USER_STATUS, sqlParameterSource);
    }

    @Override
    public void changeProfile(User user, String email, String discogsUserName) {
        jdbcTemplate.update(UPDATE_PROFILE_FIELDS,
                new MapSqlParameterSource()
                        .addValue("new_email", email)
                        .addValue("discogs_user_name", discogsUserName)
                        .addValue("email", user.getEmail()));
    }

    @Override
    public void changeUserPassword(User user) {
        jdbcTemplate.update(UPDATE_PASSWORD,
                new MapSqlParameterSource()
                        .addValue("email", user.getEmail())
                        .addValue("password", user.getPassword()));
    }

}
