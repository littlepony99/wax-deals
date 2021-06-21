package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Offer;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Slf4j
public class OfferRowMapper implements RowMapper<Offer> {

    @Override
    public Offer mapRow(ResultSet resultSet) {
        Offer offer = new Offer();
        try {
            offer.setId(resultSet.getInt("id"));
            offer.setUniqueVinylId(resultSet.getLong("unique_vinyl_id"));
            offer.setShopId(resultSet.getInt("shop_id"));
            offer.setPrice(resultSet.getDouble("price"));
            offer.setCurrency(Optional.of(Currency.valueOf(resultSet.getString("currency"))));
            offer.setGenre(resultSet.getString("genre"));
            offer.setCatNumber(resultSet.getString("cat_number"));
            offer.setInStock(resultSet.getBoolean("in_stock"));
            offer.setOfferLink(resultSet.getString("link_to_offer"));
            log.debug("Resulting Offer object {'offer':{}}", offer);
            return offer;
        } catch (SQLException e) {
            log.error("Error while getting data from result set into Offer object {'offer':{}}", offer, e);
            throw new RuntimeException(e);
        }

    }

}