
package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.RecoveryToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecoveryTokenResultSetExtractorTest {

    @Test
    @DisplayName("Check if RecoveryToken entity created and all necessary info has been received from ResultSet")
    void extractData() throws SQLException {
        //prepare
        ResultSetExtractor<RecoveryToken> resultSetExtractor = new RecoveryTokenResultSetExtractor();
        ResultSet mockedResultSet = mock(ResultSet.class);
        LocalDateTime createdAt = LocalDateTime.now();
        UUID token = UUID.randomUUID();
        when(mockedResultSet.next()).thenReturn(true);
        when(mockedResultSet.getLong("id")).thenReturn(1L);
        when(mockedResultSet.getLong("user_id")).thenReturn(1L);
        when(mockedResultSet.getObject("token", java.util.UUID.class)).thenReturn(token);
        when(mockedResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(createdAt));
        //when
        RecoveryToken recoveryToken = resultSetExtractor.extractData(mockedResultSet);
        //then
        assertEquals(1L, recoveryToken.getId());
        assertEquals(1L, recoveryToken.getUserId());
        assertEquals(token, recoveryToken.getToken());
        assertEquals(Timestamp.valueOf(createdAt), recoveryToken.getCreatedAt());
    }

}
