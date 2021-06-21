package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.UserDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.UserRowMapper;
import com.vinylteam.vinyl.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcUserDao implements UserDao {

    private static final String FIND_BY_EMAIL = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE email ILIKE ?";
    private static final String FIND_BY_ID = "SELECT id, email, password, salt, iterations, role, status, discogs_user_name" +
            " FROM users" +
            " WHERE id=?";
    private static final String INSERT = "INSERT INTO users" +
            " (email, password, salt, iterations, role, status, discogs_user_name)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?)";// RETURNING id";
    private static final String DELETE = "DELETE FROM users WHERE email ILIKE ?";
    private static final String UPDATE = "UPDATE users" +
            " SET email = :email, password = :password, salt = :salt, iterations = ?, role = ?, status = ?, discogs_user_name = ?" +
            " WHERE email ILIKE ?";

    private static final UserRowMapper USER_ROW_MAPPER = new UserRowMapper();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public long add(User user) {
        return jdbcTemplate.update(INSERT, user.getEmail(), user.getPassword(), user.getSalt(), user.getIterations(), user.getRole().toString(),
                user.getStatus(), user.getDiscogsUserName());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update(DELETE, user.getEmail());
    }

    @Override
    public void update(String email, User user) {

        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("email", user.getEmail());
        sqlParameterSource.addValue("password", user.getPassword());
        sqlParameterSource.addValue("salt", user.getSalt());

        namedParameterJdbcTemplate.update(UPDATE, sqlParameterSource);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_EMAIL, USER_ROW_MAPPER, email));
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_BY_ID, USER_ROW_MAPPER, id));
    }

}