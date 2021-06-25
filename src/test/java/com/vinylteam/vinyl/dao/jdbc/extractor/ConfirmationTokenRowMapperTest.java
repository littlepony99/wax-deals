/*
package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.ConfirmationToken;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfirmationTokenRowMapperTest {

    private final RowMapper<ConfirmationToken> rowMapper = new ConfirmationTokenRowMapper();

    @Test
    void mapRow() throws SQLException {
        //prepare
        ResultSet mockedResultSet = mock(ResultSet.class);
        UUID expectedToken = UUID.randomUUID();
        Timestamp expectedTimestamp = Timestamp.from(Instant.now());
        when(mockedResultSet.getLong("id")).thenReturn(1L);
        when(mockedResultSet.getLong("user_id")).thenReturn(1L);
        when(mockedResultSet.getObject("token", java.util.UUID.class)).thenReturn(expectedToken);
        when(mockedResultSet.getTimestamp("created_at")).thenReturn(expectedTimestamp);
        //when
        ConfirmationToken confirmationToken = rowMapper.mapRow(mockedResultSet);
        //then
        assertEquals(1L, confirmationToken.getId());
        assertEquals(1L, confirmationToken.getUserId());
        assertEquals(expectedToken, confirmationToken.getToken());
        assertEquals(expectedTimestamp, confirmationToken.getTimestamp());
    }

}*/
