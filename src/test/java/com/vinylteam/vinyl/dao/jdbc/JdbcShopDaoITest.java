
package com.vinylteam.vinyl.dao.jdbc;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.data.TestShopProvider;
import com.vinylteam.vinyl.entity.Shop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@SpringBootTest
class JdbcShopDaoITest {

    @Autowired
    private ShopDao shopDao;

    @Test
    @DataSet(provider = TestShopProvider.ShopProvider.class, cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets list of shops with id-s with list of id-s from db")
    void getManyByListOfIds() {
        //prepare
        List<Integer> ids = List.of(1, 2);
        //when
        List<Shop> actualShops = shopDao.findByListOfIds(ids);
        //then
        assertEquals(2, actualShops.size());
    }

    @Test
    @DataSet(provider = TestShopProvider.ShopProvider.class, cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets list of all shops from db`s non-empty table")
    void findAllShops() {
        //when
        List<Shop> actualShops = shopDao.findAll();
        //then
        assertEquals(3, actualShops.size());
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets empty list of all shops from empty table")
    void findAllShopsFromEmptyTable() throws SQLException {
        //when
        List<Shop> actualShops = shopDao.findAll();
        //then
        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DataSet(provider = TestShopProvider.ShopProvider.class, cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets list of shops with id-s with list of id-s where some ids do not exist in db")
    void getManyByListOfIdsWithSomeNonExistentIds() {
        //prepare
        List<Integer> ids = List.of(1, 2, 4);
        //when
        List<Shop> actualShops = shopDao.findByListOfIds(ids);
        //then
        assertEquals(2, actualShops.size());
    }

    @Test
    @DataSet(provider = TestShopProvider.ShopProvider.class, cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets empty list of shops with empty list of id-s from db")
    void getManyByListOfIdsEmptyList() {
        //prepare
        List<Integer> ids = new ArrayList<>();
        //when
        List<Shop> actualShops = shopDao.findByListOfIds(ids);
        //then
        assertTrue(actualShops.isEmpty());
    }

    @Test
    @DataSet(cleanBefore = true, cleanAfter = true, skipCleaningFor = {"public.flyway_schema_history"})
    @DisplayName("Gets empty list of shops with list id-s from empty table")
    void getManyByListOfIdsFromEmptyTable() throws SQLException {
        //prepare
        List<Integer> ids = List.of(1, 2);
        //when
        List<Shop> actualShops = shopDao.findByListOfIds(ids);
        //then
        assertTrue(actualShops.isEmpty());
    }

}
