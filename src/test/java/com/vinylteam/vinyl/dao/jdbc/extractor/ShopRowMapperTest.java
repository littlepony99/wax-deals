package com.vinylteam.vinyl.dao.jdbc.extractor;

import com.vinylteam.vinyl.entity.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ShopRowMapperTest {

    @Test
    @DisplayName("Checks if shop created from resultSet has all fields right.")
    void mapRow() throws SQLException {
        //prepare
        RowMapper<Shop> rowMapper = new ShopRowMapper();
        ResultSet mockedFilledResultSet = mock(ResultSet.class);
        when(mockedFilledResultSet.getInt("id")).thenReturn(1);
        when(mockedFilledResultSet.getString("link_to_main_page")).thenReturn("shop1/main");
        when(mockedFilledResultSet.getString("link_to_image")).thenReturn("shop1/image.png");
        when(mockedFilledResultSet.getString("name")).thenReturn("shop1");
        when(mockedFilledResultSet.getString("link_to_small_image")).thenReturn("shop1/small_image.png");
        //when
        Shop actualShop = rowMapper.mapRow(mockedFilledResultSet, 0);
        //then
        assertEquals(1, actualShop.getId());
        assertEquals("shop1/main", actualShop.getMainPageLink());
        assertEquals("shop1/image.png", actualShop.getImageLink());
        assertEquals("shop1", actualShop.getName());
        assertEquals("shop1/small_image.png", actualShop.getSmallImageLink());
    }

}
