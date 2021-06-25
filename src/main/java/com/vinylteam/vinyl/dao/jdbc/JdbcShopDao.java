package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.ShopRowMapper;
import com.vinylteam.vinyl.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcShopDao implements ShopDao {

    private static final String SELECT_SHOPS_BY_IDS = "SELECT id, link_to_main_page, link_to_image, name, link_to_small_image " +
            "FROM shops WHERE id IN (:ids) ORDER BY shop_order NULLS FIRST";

    private static final String SELECT_ALL_SHOPS = "SELECT id, link_to_main_page, link_to_image, name, link_to_small_image FROM shops ORDER BY shop_order NULLS FIRST";

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Shop> rowMapper = new ShopRowMapper();

    @Override
    public List<Shop> findByListOfIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(SELECT_SHOPS_BY_IDS, sqlParameterSource, rowMapper);
    }

    @Override
    public List<Shop> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SHOPS, rowMapper);
    }

}

