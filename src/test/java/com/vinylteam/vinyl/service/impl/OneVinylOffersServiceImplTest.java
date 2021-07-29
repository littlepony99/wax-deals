package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.web.dto.OneVinylOfferDto;
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
class OneVinylOffersServiceImplTest {

    @Autowired
    @InjectMocks
    private OneVinylOffersServiceImpl oneVinylService;

    @Autowired
    @MockBean
    private OfferService offerService;

    @Autowired
    @MockBean
    private UniqueVinylService uniqueVinylService;

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @Test
    @DisplayName("Checks whether vinyl.hasOffers flag is true when there are offers and is false when there ias no any offer for the vinyl")
    void givenVinylListWithNoOffer_whenHasOffersSetToFalse_thenCorrect() {
        //given
        var vinylsList = dataGenerator
                .getUniqueVinylsList()
                .stream()
                .filter(UniqueVinyl::isHasOffers)
                .collect(Collectors.toList());
        List<OneVinylOfferDto> offersResponseList = new ArrayList<>();
        for (UniqueVinyl uniqueVinyl : vinylsList) {
            assertTrue(uniqueVinyl.isHasOffers());
        }
        //when
        offersResponseList.add(new OneVinylOfferDto());
        UniqueVinyl testedUniqueVinyl = vinylsList.get(0);
        //then
        oneVinylService.checkIsVinylInStock(testedUniqueVinyl, offersResponseList);
        verify(uniqueVinylService, never()).updateOneUniqueVinylAsHavingNoOffer(testedUniqueVinyl);
        assertTrue(testedUniqueVinyl.isHasOffers());

        offersResponseList.clear();
        oneVinylService.checkIsVinylInStock(testedUniqueVinyl, offersResponseList);
        verify(uniqueVinylService).updateOneUniqueVinylAsHavingNoOffer(testedUniqueVinyl);
        assertFalse(testedUniqueVinyl.isHasOffers());
    }

    @Test
    @DisplayName("Checks whether OneVinylOffersServletResponse`s list is prepared based on offers list")
    void prepareOffersSection() {
        List<Shop> shopsList = dataGenerator.getShopsList();
        List<Offer> offers = dataGenerator.getOffersList();
        when(offerService.getActualizedOffer(any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        when(offerService.mergeOfferChanges(any(), any(), any())).thenAnswer((Answer<Offer>) invocation -> (Offer) invocation.getArguments()[0]);
        List<OneVinylOfferDto> offerResponseList = oneVinylService.prepareOffersSection(offers, shopsList);
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
        when(uniqueVinylService.findManyByArtist(vinyl.getArtist())).thenReturn(vinylListToBeReturned);
        List<UniqueVinyl> vinylsList = oneVinylService.prepareVinylsSection(vinyl);
        assertEquals(0, vinylsList.indexOf(vinyl));
        assertEquals(vinylListToBeReturned.size() + 1, vinylsList.size());
        for (UniqueVinyl uniqueVinyl : vinylListToBeReturned) {
            assertTrue(vinylsList.contains(uniqueVinyl));
        }
    }
}