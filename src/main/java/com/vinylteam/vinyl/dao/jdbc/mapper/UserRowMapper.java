package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;

@Slf4j
public class UserRowMapper implements ResultSetExtractor<User> {

    @Override
    public User extractData(ResultSet resultSet) {
        User user = new User();
        try {
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
        } catch (Exception e) {
            log.error("Error while getting data from result set into User object {'user':{}}", user, e);
            throw new RuntimeException(e);
        }
    }

}
