
/*

package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShopRowMapperTest {

    private final RowMapper<Shop> rowMapper = new ShopRowMapper();

    @Test
    @DisplayName("Checks if shop created from resultSet has all fields right.")
    void mapFilledRowTest() throws SQLException {
        //prepare
        ResultSet mockedFilledResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getInt("id")).thenReturn(1);
        when(mockedFilledResultSet.getString("link_to_main_page")).thenReturn("shop1/main");
        when(mockedFilledResultSet.getString("link_to_image")).thenReturn("shop1/image.png");
        when(mockedFilledResultSet.getString("name")).thenReturn("shop1");
        when(mockedFilledResultSet.getString("link_to_small_image")).thenReturn("shop1/small_image.png");
        //when
        Shop actualShop = rowMapper.mapRow(mockedFilledResultSet);
        //then
        assertEquals(1, actualShop.getId());
        assertEquals("shop1/main", actualShop.getMainPageLink());
        assertEquals("shop1/image.png", actualShop.getImageLink());
        assertEquals("shop1", actualShop.getName());
        assertEquals("shop1/small_image.png", actualShop.getSmallImageLink());
    }

}*/
