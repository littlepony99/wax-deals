package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.ConfirmationToken;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfirmationTokenResultSetExtractorTest {

    @Test
    void extractData() throws SQLException {
        //prepare
        ResultSetExtractor<ConfirmationToken> resultSetExtractor = new ConfirmationTokenResultSetExtractor();
        ResultSet mockedResultSet = mock(ResultSet.class);
        UUID expectedToken = UUID.randomUUID();
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());
        when(mockedResultSet.next()).thenReturn(true);
        when(mockedResultSet.getLong("id")).thenReturn(1L);
        when(mockedResultSet.getLong("user_id")).thenReturn(1L);
        when(mockedResultSet.getObject("token", java.util.UUID.class)).thenReturn(expectedToken);
        when(mockedResultSet.getTimestamp("created_at")).thenReturn(expectedTimestamp);
        //when
        ConfirmationToken confirmationToken = resultSetExtractor.extractData(mockedResultSet);
        //then
        assertEquals(1L, confirmationToken.getId());
        assertEquals(1L, confirmationToken.getUserId());
        assertEquals(expectedToken, confirmationToken.getToken());
        assertEquals(expectedTimestamp, confirmationToken.getTimestamp());
    }

}
