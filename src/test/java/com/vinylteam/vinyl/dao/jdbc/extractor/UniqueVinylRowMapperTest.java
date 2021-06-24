
/*
package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UniqueVinylRowMapperTest {

    private final RowMapper<UniqueVinyl> rowMapper = new UniqueVinylRowMapper();

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapRowWithFilledResultSetTest() throws SQLException {
        //prepare
        ResultSet mockedResultSet = mock(ResultSet.class);
        when(mockedResultSet.getInt("id")).thenReturn(1);
        when(mockedResultSet.getString("release")).thenReturn("release1");
        when(mockedResultSet.getString("artist")).thenReturn("artist1");
        when(mockedResultSet.getString("full_name")).thenReturn("release1 - artist1");
        when(mockedResultSet.getString("link_to_image")).thenReturn("https://imagestore.com/somewhere/image1.jpg");
        //when
        UniqueVinyl uniqueVinyl = rowMapper.mapRow(mockedResultSet);
        //then
        assertEquals(1, uniqueVinyl.getId());
        assertEquals("release1", uniqueVinyl.getRelease());
        assertEquals("artist1", uniqueVinyl.getArtist());
        assertEquals("release1 - artist1", uniqueVinyl.getFullName());
        assertEquals("https://imagestore.com/somewhere/image1.jpg", uniqueVinyl.getImageLink());
    }

}*/
