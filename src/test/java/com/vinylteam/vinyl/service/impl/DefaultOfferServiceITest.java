package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.OfferRepository;
import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefaultOfferServiceITest {

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

        offers.get(0).setCurrency(Optional.empty());

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
        List<Offer> actualOffers = offerService.findByUniqueVinylId(id);
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
        List<Offer> actualOffers = offerService.findByUniqueVinylId(id);
        //then
        assertTrue(actualOffers.isEmpty());
    }

    @Test
    @DisplayName("Rewrite all - check uniqueVinyl changes")
    void updateUniqueVinylsRewriteAll() {
        //prepare
        List<UniqueVinyl> vinylsToUpdate = new ArrayList<>(uniqueVinyls);
        List<Offer> offersToUpdate = new ArrayList<>(offers);

        UniqueVinyl newUniqueVinyl = UniqueVinyl.builder()
                .id("10")
                .offers(false)
                .fullName("Test fullname")
                .artist("Artist new")
                .build();
        vinylsToUpdate.add(newUniqueVinyl);
        vinylsToUpdate.get(3).setHasOffers(true);
        vinylsToUpdate.remove(0);

        Offer newOffer = new Offer();
        newOffer.setId("25");
        newOffer.setUniqueVinylId("10");
        newOffer.setPrice(25.5);
        newOffer.setCurrency(Optional.of(Currency.UAH));
        newOffer.setCatNumber("Test category");
        offersToUpdate.add(newOffer);
        offersToUpdate.remove(0);
        offersToUpdate.get(0).setPrice(0.1);
        //when
        offerService.updateUniqueVinylsRewriteAll(vinylsToUpdate, offersToUpdate);
        //then
        List<UniqueVinyl> vinylsAfterUpdate = uniqueVinylRepository.findAll();
        assertTrue(vinylsAfterUpdate.containsAll(vinylsToUpdate));
        assertTrue(vinylsAfterUpdate.contains(uniqueVinyls.get(0)));

        List<Offer> offersAfterUpdate = offerRepository.findAll();
        assertTrue(offersAfterUpdate.containsAll(offersToUpdate));
        assertFalse(offersAfterUpdate.contains(offers.get(0)));
    }

}
