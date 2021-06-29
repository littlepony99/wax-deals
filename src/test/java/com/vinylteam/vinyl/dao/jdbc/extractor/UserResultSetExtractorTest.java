package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.Role;
import com.vinylteam.vinyl.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserResultSetExtractorTest {

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void extractData() throws SQLException {
        //prepare
        ResultSetExtractor<User> resultSetExtractor = new UserResultSetExtractor();
        ResultSet mockedResultSet = mock(ResultSet.class);
        when(mockedResultSet.next()).thenReturn(true);
        when(mockedResultSet.getString("email")).thenReturn("testuser@vinyl.com");
        when(mockedResultSet.getString("password")).thenReturn("HASH");
        when(mockedResultSet.getString("discogs_user_name")).thenReturn("discogsUserName");
        when(mockedResultSet.getString("salt")).thenReturn("salt");
        when(mockedResultSet.getInt("iterations")).thenReturn(1);
        when(mockedResultSet.getString("role")).thenReturn("USER");
        when(mockedResultSet.getBoolean("status")).thenReturn(true);
        //when
        User actualUser = resultSetExtractor.extractData(mockedResultSet);
        //then
        assertEquals("testuser@vinyl.com", actualUser.getEmail());
        assertEquals("HASH", actualUser.getPassword());
        assertEquals("discogsUserName", actualUser.getDiscogsUserName());
        assertEquals("salt", actualUser.getSalt());
        assertEquals(1, actualUser.getIterations());
        assertEquals(Role.USER, actualUser.getRole());
        assertTrue(actualUser.getStatus());
    }

}
