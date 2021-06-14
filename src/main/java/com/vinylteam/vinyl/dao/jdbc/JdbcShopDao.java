package com.vinylteam.vinyl.dao.jdbc;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.jdbc.mapper.ShopRowMapper;
import com.vinylteam.vinyl.entity.Shop;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class JdbcShopDao implements ShopDao {

    private static final ShopRowMapper shopRowMapper = new ShopRowMapper();
    private static final String SELECT_SHOPS_BY_IDS = "SELECT id, link_to_main_page, link_to_image, name, link_to_small_image " +
            "FROM shops WHERE id IN ()  ORDER BY shop_order NULLS FIRST";

    private static final String SELECT_ALL_SHOPS = "SELECT id, link_to_main_page, link_to_image, name, link_to_small_image FROM shops ORDER BY shop_order NULLS FIRST";

    private final DataSource dataSource;

    public JdbcShopDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Shop> getManyByListOfIds(List<Integer> ids) {
        List<Shop> shops = new ArrayList<>();
        if (!ids.isEmpty()) {
            String queryToExecute = fillSelectManyByIdsStatement(ids);
            try (Connection connection = dataSource.getConnection();
                 Statement getManyByListOfIdsStatement = connection.createStatement();
                 ResultSet resultSet = getManyByListOfIdsStatement.executeQuery(queryToExecute)) {
                log.debug("Executed statement {'statement':{}}", getManyByListOfIdsStatement);
                while (resultSet.next()) {
                    Shop shop = shopRowMapper.mapRow(resultSet);
                    shops.add(shop);
                }
            } catch (SQLException e) {
                log.error("Error while getting list of shops by ids list from db {'ids':{}, 'shops':{}}",
                        ids, shops, e);
                throw new RuntimeException(e);
            }
        }
        log.debug("List of shops received by ids list is {'shops':{}}", shops);
        return shops;
    }

    @Override
    public List<Shop> findAll() {
        List<Shop> shops = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             Statement findAllStatement = connection.createStatement();
             ResultSet resultSet = findAllStatement.executeQuery(SELECT_ALL_SHOPS)) {
            log.debug("Executed statement {'statement':{}}", findAllStatement);
            while (resultSet.next()) {
                Shop shop = shopRowMapper.mapRow(resultSet);
                shops.add(shop);
            }
        } catch (SQLException e) {
            log.error("Error while getting list of all shops from db {'shops':{}}",
                    shops, e);
            throw new RuntimeException(e);
        }
        return shops;
    }

    String fillSelectManyByIdsStatement(List<Integer> ids) {
        StringBuffer stringBuffer = new StringBuffer(SELECT_SHOPS_BY_IDS);
        for (Integer id : ids) {
            if (stringBuffer.lastIndexOf(")") - stringBuffer.lastIndexOf("(") > 1) {
                stringBuffer.insert(stringBuffer.lastIndexOf(")"), ", ");
            }
            stringBuffer.insert(stringBuffer.lastIndexOf(")"), id);
        }
        log.debug("Resulting string from string buffer is {'string':{}}", stringBuffer);
        return stringBuffer.toString();
    }

}
