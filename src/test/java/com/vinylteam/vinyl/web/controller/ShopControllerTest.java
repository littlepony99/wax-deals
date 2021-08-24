package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.impl.DefaultShopService;
import com.vinylteam.vinyl.web.dto.ShopDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ShopControllerTest {

    @Autowired
    private ShopController shopController;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private DefaultShopService shopService;

    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

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
        List<ShopDto> shopDtoList = shopController.getShopInfo();
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
    @DisplayName("Checks that controller returns correct json with valid values")
    void returnCorrectJson() throws Exception {
        //prepare
        Shop tequila = Shop.builder()
                .id(1)
                .name("Tequila Shop")
                .imageLink("www.tequila.com/image.png")
                .mainPageLink("www.tequila.com/main-image.png")
                .smallImageLink("www.tequila.com/small-image.png")
                .build();
        Shop discarik = Shop.builder()
                .id(2)
                .name("Discarik")
                .imageLink("www.discarik.label.org/image.png")
                .mainPageLink("www.tequila.label.org/main-image.png")
                .smallImageLink("www.tequila.label.org/small-image.png")
                .build();
        List<Shop> shopList = new ArrayList();
        shopList.add(tequila);
        shopList.add(discarik);
        when(shopService.findAll()).thenReturn(shopList);
        //when
        MockHttpServletResponse response = mockMvc.perform(get("/stores"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].name").isNotEmpty())
                .andExpect(jsonPath("$[0].imageLink").isNotEmpty())
                .andExpect(jsonPath("$[0].mainPageLink").isNotEmpty())
                .andExpect(jsonPath("$[0].smallImageLink").isNotEmpty())
                .andExpect(jsonPath("$[1].id").isNotEmpty())
                .andExpect(jsonPath("$[1].name").isNotEmpty())
                .andExpect(jsonPath("$[1].imageLink").isNotEmpty())
                .andExpect(jsonPath("$[1].mainPageLink").isNotEmpty())
                .andExpect(jsonPath("$[1].smallImageLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        //then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
        Assertions.assertNotNull(response.getContentAsString().contains("1"));
        Assertions.assertNotNull(response.getContentAsString().contains("Tequila Shop"));
        Assertions.assertNotNull(response.getContentAsString().contains("www.tequila.com/image.png"));
        Assertions.assertNotNull(response.getContentAsString().contains("www.tequila.com/main-image.png"));
        Assertions.assertNotNull(response.getContentAsString().contains("www.tequila.com/small-image.png"));
    }


    @Test
    @DisplayName("Checks that controller returns empty result if service returns empty list")
    void returnsEmptyArrayLikeFromService() {
        //prepare
        when(shopService.findAll()).thenReturn(new ArrayList<>());
        //when
        List<ShopDto> shopDtoList = shopController.getShopInfo();
        //then
        Assertions.assertNotNull(shopDtoList);
    }
}
