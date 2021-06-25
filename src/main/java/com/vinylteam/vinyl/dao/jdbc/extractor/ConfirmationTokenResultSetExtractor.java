
package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class ConfirmationTokenResultSetExtractor implements ResultSetExtractor<ConfirmationToken> {

    @Override
    public ConfirmationToken extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        ConfirmationToken confirmationToken = null;
        if (resultSet.next()){
            confirmationToken = ConfirmationToken.builder()
                    .id(resultSet.getLong("id"))
                    .userId(resultSet.getLong("user_id"))
                    .token(resultSet.getObject("token", java.util.UUID.class))
                    .timestamp(resultSet.getTimestamp("created_at"))
                    .build();
            log.debug("Resulting ConfirmationToken object {'user':{}}", confirmationToken);
        }
        return confirmationToken;
    }

}
