package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RawOffersSorterTest {

    private final RawOffersSorter sorter = new RawOffersSorter();
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final List<RawOffer> rawOffers = new ArrayList<>();
    private final List<UniqueVinyl> uniqueVinyls = new ArrayList<>();
    private final List<Offer> offers = new ArrayList<>();

    @BeforeAll
    void beforeAll() {
        dataGenerator.fillListsForRawOffersSorterTest(rawOffers, uniqueVinyls, offers);
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match all raw offers to getOffers...(...) " +
            "makes uniqueVinyls hasOffers==true, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateAllMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers);
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        for (UniqueVinyl actualUniqueVinyl : actualUniqueVinyls) {
            actualUniqueVinyl.setHasOffers(false);
        }
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(offers);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match some but not all raw offers to getOffers...(...) " +
            "leaves uniqueVinyls without matches unchanged and changes uniqueVinyls with matches hasOffers==true, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateSomeMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers.subList(4, 6));
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        for (UniqueVinyl actualUniqueVinyl : actualUniqueVinyls) {
            actualUniqueVinyl.setHasOffers(false);
        }
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(actualUniqueVinyls);
        expectedUniqueVinyls.get(2).setHasOffers(true);
        List<Offer> expectedOffers = new ArrayList<>(offers.subList(4, 6));
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertTrue(actualRawOffers.isEmpty());
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
    }

    @Test
    @DisplayName("Passing filled rawOffers and filled uniqueVinyls that match no raw offers to getOffers...(...) " +
            "leaves uniqueVinyls unchanged, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateNoMatchesVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(List.of(dataGenerator.getRawOfferWithShopIdAndNumber(1, 4),
                dataGenerator.getRawOfferWithShopIdAndNumber(2, 4)));
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        for (UniqueVinyl actualUniqueVinyl : actualUniqueVinyls) {
            actualUniqueVinyl.setHasOffers(false);
        }
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(actualUniqueVinyls);
        expectedUniqueVinyls.add(dataGenerator.getUniqueVinylWithNumber(4));
        expectedUniqueVinyls.get(3).setHasOffers(true);
        List<Offer> expectedOffers = new ArrayList<>(List.of(dataGenerator.getOfferWithUniqueVinylIdAndShopId(4, 1),
                dataGenerator.getOfferWithUniqueVinylIdAndShopId(4, 2)));
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing filled rawOffers and empty uniqueVinyls to getOffers...(...) fills uniqueVinyls right, gets right offers, and leaves rawOffers empty.")
    void getOffersUpdateEmptyVinylsTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers);
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>();
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<Offer> expectedOffers = new ArrayList<>(offers);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(actualRawOffers, actualUniqueVinyls);
        //then
        assertEquals(expectedOffers, actualOffers);
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualRawOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing empty rawOffers to getOffers...(...) leaves uniqueVinyls the same and gets empty offers.")
    void getOffersUpdateVinylsWithEmptyRawOffersTest() {
        //prepare
        List<UniqueVinyl> actualUniqueVinyls = new ArrayList<>(uniqueVinyls);
        List<UniqueVinyl> expectedUniqueVinyls = new ArrayList<>(actualUniqueVinyls);
        //when
        List<Offer> actualOffers = sorter.getOffersUpdateUniqueVinyls(new ArrayList<>(), actualUniqueVinyls);
        //then
        assertEquals(expectedUniqueVinyls, actualUniqueVinyls);
        assertTrue(actualOffers.isEmpty());
    }

    @Test
    @DisplayName("Passing null rawOffers causes RuntimeException")
    void updateVinylsAndOffersWithNullRawOffersTest() {
        //when
        assertThrows(NullPointerException.class, () -> sorter.getOffersUpdateUniqueVinyls(null, new ArrayList<>()));
    }

    @Test
    @DisplayName("Passing null uniqueVinyls causes RuntimeException")
    void updateNullVinylsAndOffersTest() {
        //when
        assertThrows(NullPointerException.class, () -> sorter.getOffersUpdateUniqueVinyls(new ArrayList<>(), null));
    }

    @Test
    @DisplayName("Passing empty rawOffers doesn't change uniqueVinyl and offers")
    void addOffersSortingByVinylEmptyRawOffersTest() {
        //prepare
        UniqueVinyl actualUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        UniqueVinyl expectedUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        List<Offer> actualOffers = new ArrayList<>(offers.subList(0, 4));
        List<Offer> expectedOffers = new ArrayList<>(actualOffers);
        //when
        sorter.addOffersSortingByVinyl(new ArrayList<>(), actualUniqueVinyl, actualOffers);
        //then
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
        assertEquals(expectedOffers, actualOffers);
    }

    @Test
    @DisplayName("Passing filled rawOffers that have matches for uniqueVinyl to addOffers...(...) changes uniqueVinyl.hasOffers to true," +
            " adds offers for that vinyl to list of offers, and reduces size of rawOffers")
    void addOffersSortingByVinylRawOffersSomeMatchesTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers.subList(4, 6));
        UniqueVinyl actualUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        UniqueVinyl expectedUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        expectedUniqueVinyl.setHasOffers(true);
        List<Offer> actualOffers = new ArrayList<>(offers.subList(0, 4));
        List<Offer> expectedOffers = new ArrayList<>(offers);
        //when
        sorter.addOffersSortingByVinyl(actualRawOffers, actualUniqueVinyl, actualOffers);
        //then
        assertTrue(actualRawOffers.isEmpty());
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
        assertEquals(expectedOffers, actualOffers);
    }

    @Test
    @DisplayName("Passing filled rawOffers that don't have a match for uniqueVinyl to addOffers...(...) leaves uniqueVinyl.hasOffers==false," +
            " doesn't change sizes of lists of offers and rawOffers")
    void addOffersSortingByVinylRawOffersZeroMatchesTest() {
        //prepare
        List<RawOffer> actualRawOffers = new ArrayList<>(rawOffers.subList(0, 4));
        List<RawOffer> expectedRawOffers = new ArrayList<>(actualRawOffers);
        UniqueVinyl actualUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        UniqueVinyl expectedUniqueVinyl = dataGenerator.getUniqueVinylWithNumber(3);
        List<Offer> actualOffers = new ArrayList<>(offers.subList(0, 4));
        List<Offer> expectedOffers = new ArrayList<>(actualOffers);
        //when
        sorter.addOffersSortingByVinyl(actualRawOffers, actualUniqueVinyl, actualOffers);
        //then
        assertEquals(expectedRawOffers, actualRawOffers);
        assertEquals(expectedUniqueVinyl, actualUniqueVinyl);
        assertEquals(expectedOffers, actualOffers);
    }

}