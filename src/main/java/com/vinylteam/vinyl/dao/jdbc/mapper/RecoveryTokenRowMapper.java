package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.RecoveryToken;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class RecoveryTokenRowMapper implements RowMapper<RecoveryToken> {

    @Override
    public RecoveryToken mapRow(ResultSet resultSet) {
        RecoveryToken recoveryToken = new RecoveryToken();
        try {
            recoveryToken.setId(resultSet.getInt("id"));
            recoveryToken.setUserId(resultSet.getLong("user_id"));
            recoveryToken.setToken(resultSet.getObject("token", java.util.UUID.class));
            recoveryToken.setCreatedAt(resultSet.getTimestamp("created_at"));
            log.debug("Resulting RecoveryToken object {'user':{}}", recoveryToken);
            return recoveryToken;
        } catch (SQLException e) {
            log.error("Error while getting data from result set into RecoveryToken object {'recoveryToken':{}}", recoveryToken, e);
            throw new RuntimeException(e);
        }
    }

}
