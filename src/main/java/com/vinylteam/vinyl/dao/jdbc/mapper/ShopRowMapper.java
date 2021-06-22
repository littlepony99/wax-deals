package com.vinylteam.vinyl.dao.jdbc.mapper;

import com.vinylteam.vinyl.dao.RowMapper;
import com.vinylteam.vinyl.entity.Shop;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;

@Slf4j
public class ShopRowMapper implements RowMapper<Shop> {

    @Override
    public Shop mapRow(ResultSet resultSet) {
        Shop shop = new Shop();
        try {
            shop.setId(resultSet.getInt("id"));
            shop.setMainPageLink(resultSet.getString("link_to_main_page"));
            shop.setImageLink(resultSet.getString("link_to_image"));
            shop.setName(resultSet.getString("name"));
            shop.setSmallImageLink(resultSet.getString("link_to_small_image"));
            log.debug("Resulting Shop object {'shop':{}}", shop);
            return shop;
        } catch (Exception e) {
            log.error("Error while getting data from result set into Shop object {'shop':{}}", shop, e);
            throw new RuntimeException(e);
        }
    }

}
