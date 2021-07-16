package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import com.vinylteam.vinyl.util.impl.CloneNlParser;
import com.vinylteam.vinyl.util.impl.VinylParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultOfferServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final OfferService offerService = new DefaultOfferService(null, null, null);
    private final List<Offer> offers = dataGenerator.getOffersList();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();
    private final VinylParser parser = new CloneNlParser();

    @Test
    @DisplayName("Checks that when id is null Runtime exception is thrown")
    void findManyByUniqueVinylIdNotValidIdTest() {
        //prepare
        String id = null;
        //when
        assertThrows(RuntimeException.class, () -> offerService.findManyByUniqueVinylId(id));
    }

    @Test
    @DisplayName("Throws Runtime Exception when null uniqueVinyls is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllNullUniqueVinylsTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(null, offers));
    }

    @Test
    @DisplayName("Throws Runtime Exception when null offers is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllNullOffersTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, null));
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty uniqueVinyls is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllEmptyUniqueVinylsTest() {
        //prepare
        List<UniqueVinyl> emptyUniqueVinyls = new ArrayList<>();
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(emptyUniqueVinyls, offers));
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty offers is passed")
    void updateUniqueVinylsRewriteAllEmptyOffersTest() {
        //prepare
        List<Offer> emptyOffers = new ArrayList<>();
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, emptyOffers));
    }


    @Test
    @DisplayName("Gets list of unique shop id-s from list of offers with duplicate shop id-s")
    void getListOfShopsIdsWithDuplicateShopIdVinylsTest() {
        //prepare
        List<Integer> expectedIds = new ArrayList<>(List.of(1, 2));
        //when
        List<Integer> actualIds = offerService.getListOfShopIds(offers);
        //then
        assertEquals(expectedIds, actualIds);
    }

    @Test
    @DisplayName("Gets empty list of unique shop id-s from empty list of offers")
    void getListOfShopsIdsWithEmptyOffersListTest() {
        //when
        List<Integer> actualIds = offerService.getListOfShopIds(new ArrayList<>());
        //then
        assertTrue(actualIds.isEmpty());
    }

    @Test
    @DisplayName("Checks that when list of offers is null method returns empty list")
    void getListOfShopsIdsWithNullOffersListTest() {
        //when
        List<Integer> actualIds = offerService.getListOfShopIds(null);
        //then
        assertTrue(actualIds.isEmpty());
    }

    @Test
    @DisplayName("Checks that offers are merged during dynamic update")
    void mergeOfferChangesTest() {
        //prepare
        Offer dbOffer = dataGenerator.getOfferWithUniqueVinylIdAndShopId(12, 4);
        RawOffer webOffer = dataGenerator.getRawOfferWithShopIdAndNumber(4, 12);
        double newWebPrice = webOffer.getPrice() - 2d;
        webOffer.setPrice(newWebPrice);
        assertNotEquals(webOffer.getPrice(), dbOffer.getPrice());
        //when
        offerService.mergeOfferChanges(dbOffer, parser, webOffer);
        //then
        assertEquals(webOffer.getPrice(), dbOffer.getPrice());
        assertEquals(webOffer.getCurrency(), dbOffer.getCurrency());
        assertEquals(newWebPrice, dbOffer.getPrice());
    }

    @Test
    @DisplayName("Checks that offers are not merged during dynamic update since new offer is not valid (as though the link is no more existing)")
    void noMergeOfferChangesTest() {
        //prepare
        Offer dbOffer = dataGenerator.getOfferWithUniqueVinylIdAndShopId(12, 4);
        RawOffer webOffer = new RawOffer();
        assertTrue(dbOffer.isInStock());
        double dbPrice = dbOffer.getPrice();
        assertNotEquals(webOffer.getPrice(), dbOffer.getPrice());
        //when
        offerService.mergeOfferChanges(dbOffer, parser, webOffer);
        //then
        assertNotEquals(webOffer.getPrice(), dbOffer.getPrice());
        assertEquals(dbPrice, dbOffer.getPrice());
        assertFalse(dbOffer.isInStock());
    }

}
