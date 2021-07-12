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
    private static final String UPDATE_USER_STATUS = "UPDATE users SET status = true WHERE id = :id";

    private static final ResultSetExtractor<User> RESULT_SET_EXTRACTOR = new UserResultSetExtractor();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        return namedParameterJdbcTemplate.update(INSERT, sqlParameterSource, keyHolder);
    }

    @Override
    public void delete(User user) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("email", user.getEmail());
        namedParameterJdbcTemplate.update(DELETE, sqlParameterSource);
    }

    @Override
    public void update(String email, User user) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("email", user.getEmail());
        sqlParameterSource.addValue("password", user.getPassword());
        sqlParameterSource.addValue("salt", user.getSalt());
        sqlParameterSource.addValue("iterations", user.getIterations());
        sqlParameterSource.addValue("role", user.getRole().getName());
        sqlParameterSource.addValue("status", user.getStatus());
        sqlParameterSource.addValue("discogs_user_name", user.getDiscogsUserName());
        sqlParameterSource.addValue("old_email", email);
        namedParameterJdbcTemplate.update(UPDATE, sqlParameterSource);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("email", email);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                FIND_BY_EMAIL,
                sqlParameterSource,
                RESULT_SET_EXTRACTOR));
    }

    @Override
    public Optional<User> findById(long id) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", id);
        return Optional.ofNullable(namedParameterJdbcTemplate.query(
                FIND_BY_ID,
                sqlParameterSource,
                RESULT_SET_EXTRACTOR));
    }

    @Override
    public void setUserStatusTrue(long id) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", id);
        namedParameterJdbcTemplate.update(UPDATE_USER_STATUS, sqlParameterSource);
    }

}
