package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.RecoveryToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class RecoveryTokenResultSetExtractor implements ResultSetExtractor<RecoveryToken> {

    @Override
    public RecoveryToken extractData(ResultSet resultSet) throws SQLException {
        RecoveryToken recoveryToken = null;
            if (resultSet.next()) {
                recoveryToken = RecoveryToken.builder()
                        .id(resultSet.getLong("id"))
                        .userId(resultSet.getLong("user_id"))
                        .token(resultSet.getObject("token", java.util.UUID.class))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .build();
                log.debug("Resulting RecoveryToken object {'user':{}}", recoveryToken);
            }
            return recoveryToken;
    }

}
