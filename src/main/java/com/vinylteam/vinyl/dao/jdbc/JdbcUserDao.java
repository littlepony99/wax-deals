package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserResultSetExtractor;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
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

    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();
    private static final UserResultSetExtractor USER_RESULT_SET_EXTRACTOR = new UserResultSetExtractor();

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void add(User user) {
        log.info("add log");
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("email", user.getEmail());
        sqlParameterSource.addValue("password", user.getPassword());
        sqlParameterSource.addValue("salt", user.getSalt());
        sqlParameterSource.addValue("iterations", user.getIterations());
        sqlParameterSource.addValue("role", user.getRole().toString());
        sqlParameterSource.addValue("status", user.getStatus());
        sqlParameterSource.addValue("discogs_user_name", user.getDiscogsUserName());
        namedParameterJdbcTemplate.update(INSERT, sqlParameterSource);
    }

    @Override
    public void delete(User user) {
        log.info("add log");
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
        sqlParameterSource.addValue("role", user.getRole().toString());
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
                USER_RESULT_SET_EXTRACTOR));
    }

    @Override
    public Optional<User> findById(long id) {
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("id", id);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(
                    FIND_BY_ID,
                    sqlParameterSource,
                    USER_ROW_MAPPER));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
