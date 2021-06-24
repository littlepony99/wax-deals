
/*
package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserRowMapperTest {

    private final RowMapper<User> rowMapper = new UserRowMapper();

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapFilledRowTest() throws SQLException {
        //prepare
        ResultSet mockedFilledResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getString("email")).thenReturn("testuser@vinyl.com");
        when(mockedFilledResultSet.getString("password")).thenReturn("HASH");
        when(mockedFilledResultSet.getString("discogs_user_name")).thenReturn("discogsUserName");
        when(mockedFilledResultSet.getString("salt")).thenReturn("salt");
        when(mockedFilledResultSet.getInt("iterations")).thenReturn(1);
        when(mockedFilledResultSet.getString("role")).thenReturn("USER");
        when(mockedFilledResultSet.getBoolean("status")).thenReturn(true);
        //when
        User actualUser = rowMapper.mapRow(mockedFilledResultSet);
        //then
        assertEquals("testuser@vinyl.com", actualUser.getEmail());
        assertEquals("HASH", actualUser.getPassword());
        assertEquals("discogsUserName", actualUser.getDiscogsUserName());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(1, actualUser.getIterations());
        assertEquals(Role.USER, actualUser.getRole());
        assertTrue(actualUser.getStatus());
    }

}*/
