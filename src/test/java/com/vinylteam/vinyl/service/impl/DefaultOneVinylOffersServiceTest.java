package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.ShopService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.web.dto.OneVinylOffersServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DefaultOneVinylOffersServiceTest {

    @Autowired
    @InjectMocks
    private DefaultOneVinylOffersService oneVinylService;

    @MockBean
    private OfferService offerService;

    @MockBean
    private UniqueVinylService uniqueVinylService;

    @MockBean
    private ShopService shopService;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Checks whether vinyl.hasOffers flag is true when there are offers and is false when there ias no any offer for the vinyl")
    void givenVinylListWithNoOffer_whenHasOffersSetToFalse_thenCorrect() {
        //given
        var vinylsList = dataGenerator
                .getUniqueVinylsList()
                .stream()
                .filter(UniqueVinyl::getHasOffers)
                .collect(Collectors.toList());
        List<OneVinylOffersServletResponse> offersResponseList = new ArrayList<>();
        for (UniqueVinyl uniqueVinyl : vinylsList) {
            assertTrue(uniqueVinyl.getHasOffers());
        }
        //when
        offersResponseList.add(new OneVinylOffersServletResponse());
        UniqueVinyl testedUniqueVinyl = vinylsList.get(0);
        //then
        oneVinylService.checkIsVinylInStock(testedUniqueVinyl, offersResponseList);
        verify(uniqueVinylService, never()).updateOneUniqueVinyl(testedUniqueVinyl);
        assertTrue(testedUniqueVinyl.getHasOffers());

        offersResponseList.clear();
        oneVinylService.checkIsVinylInStock(testedUniqueVinyl, offersResponseList);
        verify(uniqueVinylService).updateOneUniqueVinyl(testedUniqueVinyl);
        assertFalse(testedUniqueVinyl.getHasOffers());
    }

    @Test
    @DisplayName("Checks whether OneVinylOffersServletResponse`s list is prepared based on offers list")
    void prepareOffersSection() {
        //prepare
        List<Shop> shopsList = dataGenerator.getShopsList();
        List<Offer> offers = dataGenerator.getOffersList();

        //when
        when(shopService.findShopsByListOfIds(any())).thenReturn(shopsList);
        when(offerService.actualizeOffer(any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        when(offerService.mergeOfferChanges(any(), any(), any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        when(offerService.findShopIds(any())).thenReturn(List.of(1, 2));//thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        List<OneVinylOffersServletResponse> offerResponseList = oneVinylService.prepareOffersSection(offers/*, shopsList*/);
        assertFalse(offerResponseList.isEmpty());
        assertEquals(offers.size(), offerResponseList.size());
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
        assertEquals(0, vinylsList.indexOf(vinyl));
        assertEquals(vinylListToBeReturned.size() + 1, vinylsList.size());
        for (UniqueVinyl uniqueVinyl : vinylListToBeReturned) {
            assertTrue(vinylsList.contains(uniqueVinyl));
        }
    }
}