package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.dao.jdbc.JdbcShopDao;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultShopServiceTest {

    private final ShopDao mockedShopDao = mock(JdbcShopDao.class);
    private final Shop mockedShop = mock(Shop.class);
    private final List<Shop> shops = List.of(mockedShop);
    private final ShopService shopService = new DefaultShopService(mockedShopDao);

    @BeforeAll
    void beforeAll() {
        when(mockedShopDao.getManyByListOfIds(anyList())).thenReturn(shops);
        when(mockedShopDao.findAll()).thenReturn(shops);
    }

    @Test
    @DisplayName("Checks that when list of id-s isn't null ShopDao.getManyByListOfIds() is called, it's result is returned")
    void getManyByValidListOfIdsTest() {
        List<Integer> ids = List.of(1, 2, 3);

        List<Shop> actualShops = shopService.getManyByListOfIds(ids);

        assertSame(shops, actualShops);
        verify(mockedShopDao).getManyByListOfIds(ids);
    }

    @Test
    @DisplayName("Checks that when ShopDao.findAll() is called, it's result is returned")
    void findAllTest() {
        List<Shop> actualShops = shopService.findAll();

        assertSame(shops, actualShops);
        verify(mockedShopDao).findAll();
    }

    @Test
    @DisplayName("Checks that when list of id-s is null ShopDao.getManyByListOfIds() is not called, empty list is returned")
    void getManyByNullListOfIdsTest() {
        List<Shop> actualShops = shopService.getManyByListOfIds(null);

        assertTrue(actualShops.isEmpty());
        verify(mockedShopDao, never()).getManyByListOfIds(null);
    }

}
