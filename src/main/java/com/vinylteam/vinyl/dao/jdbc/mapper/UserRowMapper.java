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
        return User.builder()
                .id(resultSet.getLong("id"))
                .email(resultSet.getString("email"))
                .password(resultSet.getString("password"))
                .discogsUserName(resultSet.getString("discogs_user_name"))
                .salt(resultSet.getString("salt"))
                .iterations(resultSet.getInt("iterations"))
                .role(Role.valueOf(resultSet.getString("role")))
                .status(resultSet.getBoolean("status")).build();

    }

}
