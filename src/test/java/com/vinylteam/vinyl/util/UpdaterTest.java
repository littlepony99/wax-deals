
/*
package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.util.impl.ShopsParser;
import com.vinylteam.vinyl.util.impl.VinylParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdaterTest {

    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<VinylParser> vinylParsers = new ArrayList<>();
    private final List<RawOffer> rawOffers = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();
    private final UniqueVinylService mockedUniqueVinylService = mock(UniqueVinylService.class);
    private final OfferService mockedOfferService = mock(OfferService.class);
    private final ShopsParser mockedShopsParser = mock(ShopsParser.class);
    private final RawOffersSorter mockedRawOffersSorter = mock(RawOffersSorter.class);
    private final Updater updater = new Updater(mockedUniqueVinylService, mockedOfferService, mockedShopsParser, vinylParsers, mockedRawOffersSorter);

    @BeforeAll
    void beforeAll() {
        dataGenerator.fillListsForRawOffersSorterTest(rawOffers, uniqueVinyls, offers);
    }

    @Test
    @DisplayName("Checks that all the right methods are called")
    void updateUniqueVinylsRewriteOffersTest() {
        //prepare
        when(mockedUniqueVinylService.findAll()).thenReturn(uniqueVinyls);
        when(mockedShopsParser.getRawOffersFromAll(vinylParsers)).thenReturn(rawOffers);
        when(mockedRawOffersSorter.getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls)).thenReturn(offers);
        //when
        updater.updateUniqueVinylsRewriteOffers();
        //then
        verify(mockedUniqueVinylService).findAll();
        verify(mockedShopsParser).getRawOffersFromAll(vinylParsers);
        verify(mockedRawOffersSorter).getOffersUpdateUniqueVinyls(rawOffers, uniqueVinyls);
        verify(mockedOfferService).updateUniqueVinylsRewriteAll(uniqueVinyls, offers);
    }

}*/
