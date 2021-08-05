package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultOneVinylOffersServiceTest {

    @Autowired
    @InjectMocks
    private DefaultOneVinylOffersService oneVinylService;

    @Autowired
    @MockBean
    private OfferService mockedOfferService;

    @Autowired
    @MockBean
    private ShopService mockedShopService;

    @Autowired
    @MockBean
    private UniqueVinylService uniqueVinylService;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void beforeEach() {
        reset(mockedOfferService);
        reset(mockedShopService);
    }

    @Test
    @DisplayName("Checks whether vinyl.hasOffers flag is true when there are offers and is false when there ias no any offer for the vinyl")
    void givenVinylListWithNoOffer_whenHasOffersSetToFalse_thenCorrect() {
        //prepare
        var vinylsList = dataGenerator
                .getUniqueVinylsList()
                .stream()
                .filter(UniqueVinyl::isHasOffers)
                .collect(Collectors.toList());
        List<Offer> offers = new ArrayList<>();
        UniqueVinyl testedUniqueVinyl = vinylsList.get(0);
        //when
        oneVinylService.checkIsVinylInStock(testedUniqueVinyl, offers);
        //then
        verify(uniqueVinylService).updateOneUniqueVinyl(testedUniqueVinyl);
        assertFalse(testedUniqueVinyl.isHasOffers());
    }

    @Test
    @DisplayName("Checks whether OneVinylOffersServletResponse`s list is prepared based on offers list")
    void prepareOffersSection() {
        List<Offer> offers = dataGenerator.getOffersList();
        when(mockedOfferService.actualizeOffer(any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        when(mockedOfferService.mergeOfferChanges(any(), any(), any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        List<Offer> sortedInStockOffers = oneVinylService.getInStockOffersByPrice(offers);
        assertFalse(sortedInStockOffers.isEmpty());
        assertEquals(offers.size(), sortedInStockOffers.size());
    }

    @Test
    @DisplayName("checks whether shop can be found cby offer`s shop id")
    void findOfferShop() {
        var offer = dataGenerator.getOffersList().get(0);
        assertTrue(offer.getShopId() > 0);
        List<Shop> shopsList = dataGenerator.getShopsList();
        var shop = oneVinylService.findOfferShop(shopsList, offer);
        assertEquals(shop.getId(), offer.getShopId());
    }


    @Test
    @DisplayName("Checks whether another vinyls of the same artist are prepared to be shown on the 'one vinyl page'")
    void prepareVinylsSection() {
        List<UniqueVinyl> vinylListToBeReturned = dataGenerator.getUniqueVinylsList();
        UniqueVinyl vinyl = dataGenerator.getUniqueVinylsList().get(1);
        vinyl.setId("111");
        when(uniqueVinylService.findByArtist(vinyl.getArtist())).thenReturn(vinylListToBeReturned);
        List<UniqueVinyl> vinylsList = oneVinylService.prepareVinylsSection(vinyl);
        assertEquals(-1, vinylsList.indexOf(vinyl));
        assertEquals(vinylListToBeReturned.size(), vinylsList.size());
        for (UniqueVinyl uniqueVinyl : vinylListToBeReturned) {
            assertTrue(vinylsList.contains(uniqueVinyl));
        }
    }

    @Test
    @DisplayName("gets sorted in stock offers and shops for them when unique vinyl has currently valid offers")
    void getSortedInStockOffersAndShops_whenUniqueVinylHasValidOffers() {
        //prepare
        String id = "1";
        UniqueVinyl uniqueVinyl = dataGenerator.getUniqueVinylWithNumber(1);
        List<Offer> offers = dataGenerator.getOffersList().subList(0, 2);
        List<Integer> shopIds = new ArrayList<>(List.of(1, 2));
        List<Shop> shops = dataGenerator.getShopsList().subList(0, 2);
        when(uniqueVinylService.findById(id)).thenReturn(uniqueVinyl);
        when(mockedOfferService.findByUniqueVinylId(id)).thenReturn(offers);
        when(mockedOfferService.actualizeOffer(offers.get(0))).thenReturn(offers.get(0));
        when(mockedOfferService.actualizeOffer(offers.get(1))).thenReturn(offers.get(1));
        when(mockedOfferService.findShopIds(offers)).thenReturn(shopIds);
        when(mockedShopService.findShopsByListOfIds(shopIds)).thenReturn(shops);
        HashMap<String, List> expectedSortedOffersAndShopsMap = new HashMap<>();
        expectedSortedOffersAndShopsMap.put("offers", offers);
        expectedSortedOffersAndShopsMap.put("shops", shops);
        //when
        HashMap<String, List> actualSortedOffersAndShopsMap = oneVinylService.getSortedInStockOffersAndShops(id);
        //then
        assertEquals(expectedSortedOffersAndShopsMap, actualSortedOffersAndShopsMap);
        verify(uniqueVinylService).findById(id);
        verify(mockedOfferService).findByUniqueVinylId(id);
        verify(mockedOfferService).actualizeOffer(offers.get(0));
        verify(mockedOfferService).actualizeOffer(offers.get(1));
        verify(mockedOfferService).findShopIds(offers);
        verify(mockedShopService).findShopsByListOfIds(shopIds);
    }

    @Test
    @DisplayName("Finds shop that corresponds to the offer from list")
    void findOfferShop_ShopExists() {

    }

}