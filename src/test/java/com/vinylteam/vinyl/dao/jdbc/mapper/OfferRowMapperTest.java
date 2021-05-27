package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Offer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferRowMapperTest {

    private final RowMapper<Offer> rowMapper = new OfferRowMapper();

    @Test
    @DisplayName("Checks if user created from resultSet has all fields right.")
    void mapRowFilledResultSetTest() throws SQLException {
        //prepare
        ResultSet vinylResult = mock(ResultSet.class);
        when(vinylResult.getInt("id")).thenReturn(1);
        when(vinylResult.getLong("unique_vinyl_id")).thenReturn((long) 1);
        when(vinylResult.getInt("shop_id")).thenReturn(2);
        when(vinylResult.getDouble("price")).thenReturn(1000.0);
        when(vinylResult.getString("currency")).thenReturn("EUR");
        when(vinylResult.getString("genre")).thenReturn("rock");
        when(vinylResult.getString("link_to_offer")).thenReturn("https://vinylsite.com/there/release1");
        //when
        Offer offer = rowMapper.mapRow(vinylResult);
        //then
        assertEquals(1, offer.getId());
        assertEquals(1, offer.getUniqueVinylId());
        assertEquals(2, offer.getShopId());
        assertEquals(1000.0, offer.getPrice());
        assertEquals(Currency.EUR, offer.getCurrency().get());
        assertEquals("rock", offer.getGenre());
        assertEquals("https://vinylsite.com/there/release1", offer.getOfferLink());
    }

}