package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.impl.DefaultShopService;
import com.vinylteam.vinyl.web.dto.ShopDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShopControllerTest {
    @InjectMocks
    private ShopController shopController;
    @Mock
    private DefaultShopService shopService;

    @Test
    @DisplayName("Checks that controller returns shop list as from service")
    void returnsFullListFromService() {
        //prepare
        Shop tequila = Shop.builder()
                .id(1)
                .name("Tequila Shop")
                .imageLink("www.tequila.com/image.png")
                .mainPageLink("www.tequila.com/main-image.png")
                .smallImageLink("www.tequila.com/main-image.png")
                .build();
        Shop discarik = Shop.builder()
                .id(2)
                .name("Discarik")
                .imageLink("www.discarik.label.org/image.png")
                .mainPageLink("www.tequila.label.org/main-image.png")
                .smallImageLink("www.tequila.label.org/main-image.png")
                .build();
        List<Shop> shopList = new ArrayList();
        shopList.add(tequila);
        shopList.add(discarik);
        when(shopService.findAll()).thenReturn(shopList);
        //when
        List<ShopDto> shopDtoList = shopController.getShopPage();
        //then
        Assertions.assertNotNull(shopDtoList);
        Assertions.assertFalse(shopDtoList.isEmpty());
        Assertions.assertEquals(shopList.size(), shopDtoList.size());
        Assertions.assertEquals(shopList.get(0).getId(), shopDtoList.get(0).getId());
        Assertions.assertEquals(shopList.get(0).getName(), shopDtoList.get(0).getName());
        Assertions.assertEquals(shopList.get(0).getSmallImageLink(), shopDtoList.get(0).getSmallImageLink());
        Assertions.assertEquals(shopList.get(0).getImageLink(), shopDtoList.get(0).getImageLink());
        Assertions.assertEquals(shopList.get(0).getMainPageLink(), shopDtoList.get(0).getMainPageLink());
        Assertions.assertEquals(shopList.get(1).getId(), shopDtoList.get(1).getId());
        Assertions.assertEquals(shopList.get(1).getName(), shopDtoList.get(1).getName());
        Assertions.assertEquals(shopList.get(1).getSmallImageLink(), shopDtoList.get(1).getSmallImageLink());
        Assertions.assertEquals(shopList.get(1).getImageLink(), shopDtoList.get(1).getImageLink());
        Assertions.assertEquals(shopList.get(1).getMainPageLink(), shopDtoList.get(1).getMainPageLink());
    }

    @Test
    @DisplayName("Checks that controller returns empty result if service returns empty list")
    void returnsEmptyArrayLikeFromService() {
        //prepare
        when(shopService.findAll()).thenReturn(new ArrayList<>());
        //when
        List<ShopDto> shopDtoList = shopController.getShopPage();
        //then
        Assertions.assertNotNull(shopDtoList);
    }
}
