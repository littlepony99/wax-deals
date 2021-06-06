package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.RecoveryToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecoveryRowMapperTest {

    @Test
    @DisplayName("Check if RecoveryToken entity created and all necessary info has been received from ResultSet")
    void mapRowTest() throws SQLException {
        //prepare
        RecoveryRowMapper recoveryRowMapper = new RecoveryRowMapper();
        ResultSet mockedResultSet = mock(ResultSet.class);
        LocalDateTime createdAt = LocalDateTime.now();
        when(mockedResultSet.getInt("id")).thenReturn(1);
        when(mockedResultSet.getLong("user_id")).thenReturn(1L);
        when(mockedResultSet.getString("token")).thenReturn("user-recovery-token");
        when(mockedResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(createdAt));
        when(mockedResultSet.getTimestamp("token_lifetime")).thenReturn(Timestamp.valueOf(createdAt.plusDays(1)));
        //when
        RecoveryToken recoveryToken = recoveryRowMapper.mapRow(mockedResultSet);
        //then
        assertEquals(1, recoveryToken.getId());
        assertEquals(1L, recoveryToken.getUserId());
        assertEquals("user-recovery-token", recoveryToken.getToken());
        assertEquals(Timestamp.valueOf(createdAt), recoveryToken.getCreatedAt());
        assertEquals(Timestamp.valueOf(createdAt.plusDays(1)), recoveryToken.getLifeTime());
    }
}