package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong("id"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setDiscogsUserName(resultSet.getString("discogs_user_name"));
        user.setSalt(resultSet.getString("salt"));
        user.setIterations(resultSet.getInt("iterations"));
        user.setRole(Role.valueOf(resultSet.getString("role")));
        user.setStatus(resultSet.getBoolean("status"));
        log.debug("Resulting User object {'user':{}}", user);
        return user;
    }
}
}
