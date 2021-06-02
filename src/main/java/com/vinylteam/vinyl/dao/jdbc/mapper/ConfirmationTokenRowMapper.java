package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class ConfirmationTokenRowMapper implements RowMapper<ConfirmationToken> {

    @Override
    public ConfirmationToken mapRow(ResultSet resultSet) {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        try {
            confirmationToken.setId(resultSet.getLong("id"));
            confirmationToken.setUserId(resultSet.getLong("user_id"));
            confirmationToken.setToken(resultSet.getObject("token", java.util.UUID.class));
            confirmationToken.setTimestamp(resultSet.getTimestamp("created_at"));
        } catch (SQLException e) {
            log.error("Error while getting data from result set into ConfirmationToken object {'confirmationToken':{}}", confirmationToken, e);
            throw new RuntimeException(e);
        }
        return confirmationToken;
    }

}
