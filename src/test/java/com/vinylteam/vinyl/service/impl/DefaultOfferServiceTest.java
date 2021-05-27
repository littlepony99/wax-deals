package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultOfferServiceTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final OfferDao mockedOfferDao = mock(OfferDao.class);
    private final OfferService offerService = new DefaultOfferService(mockedOfferDao);
    private final List<Offer> offers = dataGenerator.getOffersList();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();

    @BeforeEach
    void beforeEach() {
        reset(mockedOfferDao);
    }

    @Test
    @DisplayName("Checks that when id>0 VinylDao.findManyByUniqueVinylId(id) is called, it's result is returned")
    void findManyByUniqueVinylIdValidIdTest() {
        //prepare
        long id = 1;
        List<Offer> expectedOffers = new ArrayList<>(offers.subList(0, 2));
        when(mockedOfferDao.findManyByUniqueVinylId(id)).thenReturn(expectedOffers);
        //when
        List<Offer> actualOffers = offerService.findManyByUniqueVinylId(id);
        //then
        verify(mockedOfferDao).findManyByUniqueVinylId(id);
        assertSame(expectedOffers, actualOffers);
    }

    @Test
    @DisplayName("Checks that when id<=0 findManyByUniqueVinylId(id) is not called, Runtime exception is thrown")
    void findManyByUniqueVinylIdNotValidIdTest() {
        //prepare
        long id = -1;
        //when
        assertThrows(RuntimeException.class, () -> offerService.findManyByUniqueVinylId(id));
        //then
        verify(mockedOfferDao, never()).findManyByUniqueVinylId(id);
    }

    @Test
    @DisplayName("Throws Runtime Exception when null uniqueVinyls is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllNullUniqueVinylsTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(null, offers));
        //then
        verify(mockedOfferDao, never()).updateUniqueVinylsRewriteAll(null, offers);
    }

    @Test
    @DisplayName("Throws Runtime Exception when null offers is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllNullOffersTest() {
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, null));
        //then
        verify(mockedOfferDao, never()).updateUniqueVinylsRewriteAll(uniqueVinyls, null);
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty uniqueVinyls is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllEmptyUniqueVinylsTest() {
        //prepare
        List<UniqueVinyl> emptyUniqueVinyls = new ArrayList<>();
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(emptyUniqueVinyls, offers));
        //then
        verify(mockedOfferDao, never()).updateUniqueVinylsRewriteAll(emptyUniqueVinyls, offers);
    }

    @Test
    @DisplayName("Throws Runtime Exception when empty offers is passed, offerDao.updateUniqueVinylsRewriteAll() isn't called")
    void updateUniqueVinylsRewriteAllEmptyOffersTest() {
        //prepare
        List<Offer> emptyOffers = new ArrayList<>();
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, emptyOffers));
        //then
        verify(mockedOfferDao, never()).updateUniqueVinylsRewriteAll(uniqueVinyls, emptyOffers);
    }

    @Test
    @DisplayName("Checks that when uniqueVinyls and offers are not empty and not null and unaddedOffers empty, " +
            "offerDao.updateUniqueVinylsRewriteAll() is called, exception isn't thrown")
    void updateUniqueVinylsRewriteAllUnaddedZeroOffers() {
        //prepare
        when(mockedOfferDao.updateUniqueVinylsRewriteAll(uniqueVinyls, offers)).thenReturn(new ArrayList<>());
        //when
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
        //then
        verify(mockedOfferDao).updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
    }

    @Test
    @DisplayName("Checks that when uniqueVinyls and offers are not empty and not null and unaddedOffers has some but not all offers, " +
            "offerDao.updateUniqueVinylsRewriteAll() is called, exception isn't thrown")
    void updateUniqueVinylsRewriteAllUnaddedSomeOffers() {
        //prepare
        when(mockedOfferDao.updateUniqueVinylsRewriteAll(uniqueVinyls, offers)).thenReturn(new ArrayList<>(offers.subList(0, 3)));
        //when
        offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
        //then
        verify(mockedOfferDao).updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
    }

    @Test
    @DisplayName("Checks that when uniqueVinyls and offers are not empty and not null and unaddedOffers same size as offers, " +
            "offerDao.updateUniqueVinylsRewriteAll() is called, RuntimeException is thrown")
    void updateUniqueVinylsRewriteAllUnaddedAllOffers() {
        //prepare
        when(mockedOfferDao.updateUniqueVinylsRewriteAll(uniqueVinyls, offers)).thenReturn(new ArrayList<>(offers));
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, offers));
        //then
        verify(mockedOfferDao).updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
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

}