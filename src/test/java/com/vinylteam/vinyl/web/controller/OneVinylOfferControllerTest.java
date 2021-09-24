package com.vinylteam.vinyl.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.service.impl.DefaultOneVinylOffersService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class OneVinylOfferControllerTest {
    @Autowired
    private OneVinylOfferController vinylOfferController;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UniqueVinylMapper mockedUniqueVinylMapper;

    @MockBean
    private DefaultOneVinylOffersService oneVinylOffersService;

    @MockBean
    private WantListService wantListService;

    DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void getOneVinyl() throws Exception {
        // prepare
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        HashMap<String, List> offersAndShopsMap = dataGenerator.getOneVinylOffersAndShopsMap();
        List<Offer> offersList = offersAndShopsMap.get("offers");
        List<Shop> shopList = offersAndShopsMap.get("shops");

        UniqueVinylDto vinylDto = UniqueVinylDto.builder()
                .id("12")
                .release("release")
                .artist("artist")
                .imageLink("link")
                .isWantListItem(Boolean.TRUE)
                .build();
        List<UniqueVinylDto> resultList = new ArrayList<>();
        resultList.add(vinylDto);

        List<UniqueVinyl> authorVinyls = dataGenerator.getUniqueVinylsByArtistList(uniqueVinyl.getArtist());

        when(oneVinylOffersService.getUniqueVinyl(anyString())).thenReturn(uniqueVinyl);
        when(oneVinylOffersService.getSortedInStockOffersAndShops(anyString())).thenReturn(offersAndShopsMap);
        when(oneVinylOffersService.findOfferShop(shopList, offersList.get(0))).thenReturn(shopList.get(0));
        when(oneVinylOffersService.findOfferShop(shopList, offersList.get(1))).thenReturn(shopList.get(1));
        when(oneVinylOffersService.addAuthorVinyls(any(UniqueVinyl.class))).thenReturn(authorVinyls);
        when(oneVinylOffersService.getDiscogsLink(any(UniqueVinyl.class))).thenReturn("www.discogs.com");
        when(mockedUniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(any())).thenReturn(resultList);
        when(wantListService.mergeVinylsWithWantList(any(), eq(resultList))).thenReturn(resultList);
        // when
        MockHttpServletResponse response = mockMvc.perform(get("/oneVinyl/1").param("id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.offersList").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.id").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.release").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.artist").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.imageLink").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].id").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].release").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].artist").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList[0].imageLink").isNotEmpty())
//                .andExpect(jsonPath("$.vinylsByArtistList[1].id").isNotEmpty())
//                .andExpect(jsonPath("$.vinylsByArtistList[1].release").isNotEmpty())
//                .andExpect(jsonPath("$.vinylsByArtistList[1].artist").isNotEmpty())
//                .andExpect(jsonPath("$.vinylsByArtistList[1].imageLink").isNotEmpty())
                .andExpect(jsonPath("$.discogsLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        // then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    public void getOneVinylNoVinylsByArtist() throws Exception {
        // prepare
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        HashMap<String, List> offersAndShopsMap = dataGenerator.getOneVinylOffersAndShopsMap();
        List<Offer> offersList = offersAndShopsMap.get("offers");
        List<Shop> shopList = offersAndShopsMap.get("shops");
        List<UniqueVinylDto> vinylDtoList = dataGenerator.getUniqueVinylDtoListFromUniqueVinylList(List.of(uniqueVinyl));
        List<UniqueVinyl> authorVinyls = new ArrayList<>();

        when(oneVinylOffersService.getUniqueVinyl(anyString())).thenReturn(uniqueVinyl);
        when(oneVinylOffersService.getSortedInStockOffersAndShops(anyString())).thenReturn(offersAndShopsMap);
        when(oneVinylOffersService.findOfferShop(shopList, offersList.get(0))).thenReturn(shopList.get(0));
        when(oneVinylOffersService.findOfferShop(shopList, offersList.get(1))).thenReturn(shopList.get(1));
        when(oneVinylOffersService.addAuthorVinyls(any(UniqueVinyl.class))).thenReturn(authorVinyls);
        when(oneVinylOffersService.getDiscogsLink(any(UniqueVinyl.class))).thenReturn("www.discogs.com");
        when(mockedUniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(anyList())).thenReturn(vinylDtoList);
        when(wantListService.mergeVinylsWithWantList(anyLong(), anyList())).thenReturn(vinylDtoList);
        // when
        MockHttpServletResponse response = mockMvc.perform(get("/oneVinyl/1").param("id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.offersList").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[0].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].price").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].currency").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].catNumber").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].inStock").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].offerLink").isNotEmpty())
                .andExpect(jsonPath("$.offersList[1].shopImageLink").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.id").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.release").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.artist").isNotEmpty())
                .andExpect(jsonPath("$.mainVinyl.imageLink").isNotEmpty())
                .andExpect(jsonPath("$.vinylsByArtistList").isEmpty())
                .andExpect(jsonPath("$.discogsLink").isNotEmpty())
                .andExpect(status().isOk()).andReturn().getResponse();
        // then
        Assertions.assertNotNull(response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getHeader("Content-Type"));
        Assertions.assertEquals("application/json", response.getContentType());
        Assertions.assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Controller do not query watListService for anonymous user oneVinyl request")
    void WantListImportRequestNonExistingUserTest() throws Exception {
        UniqueVinylDto vinylDto = UniqueVinylDto.builder().id("1234").build();
        String jsonRequest = (new ObjectMapper()).writeValueAsString(vinylDto);
        mockMvc.perform(get("/oneVinyl/1").param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest));
        //then
        verify(wantListService, never()).mergeVinylsWithWantList(any(Long.class), anyList());
    }
}
