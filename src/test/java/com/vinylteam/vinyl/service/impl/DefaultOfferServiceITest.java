package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.OfferRepository;
import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.AbstractElasticsearchContainerBaseTest;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultOfferServiceITest extends AbstractElasticsearchContainerBaseTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<Offer> offers = dataGenerator.getOffersList();
    private final List<UniqueVinyl> uniqueVinyls = dataGenerator.getUniqueVinylsList();

    @Autowired
    private OfferService offerService;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private UniqueVinylRepository uniqueVinylRepository;

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void beforeEach() {
        uniqueVinylRepository.deleteAll();
        offerRepository.deleteAll();

        uniqueVinylRepository.saveAll(uniqueVinyls);
        offerRepository.saveAll(offers);
    }

    @Test
    @DisplayName("Find by uniqueVinylId")
    void findManyByUniqueVinylIdValidIdTest() {
        //prepare
        String id = "1";
        List<Offer> expectedOffers = new ArrayList<>(offers.subList(0, 2));
        //when
        List<Offer> actualOffers = offerService.findManyByUniqueVinylId(id);
        //then
        assertEquals(2, actualOffers.size());
        assertTrue(expectedOffers.containsAll(actualOffers));
    }

    @Test
    @DisplayName("Find by uniqueVinylId - no offers")
    void findManyByUniqueVinylIdNoOffersTest() {
        //prepare
        String id = "11";
        //when
        List<Offer> actualOffers = offerService.findManyByUniqueVinylId(id);
        //then
        assertTrue(actualOffers.isEmpty());
    }
/*
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
    @DisplayName("Checks that when uniqueVinyls and offers are not empty and not null and unaddedOffers same size as offers, " +
            "offerDao.updateUniqueVinylsRewriteAll() is called, RuntimeException is thrown")
    void updateUniqueVinylsRewriteAllUnaddedAllOffers() {
        //prepare
        when(mockedOfferDao.updateUniqueVinylsRewriteAll(uniqueVinyls, offers)).thenReturn(new ArrayList<>(offers));
        //when
        assertThrows(RuntimeException.class, () -> offerService.updateUniqueVinylsRewriteAll(uniqueVinyls, offers));
        //then
        verify(mockedOfferDao).updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
    }*/

   /* private void recreateIndexes() {
        //elasticsearchOperations.indexOps(UniqueVinyl.class).refresh();
        elasticsearchOperations.indexOps(Offer.class).refresh();
    }*/

}
