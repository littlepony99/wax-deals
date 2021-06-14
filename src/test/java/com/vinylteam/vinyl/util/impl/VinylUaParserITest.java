
/*package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VinylUaParserITest {

    private static final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private VinylUaParser vinylUaParser = new VinylUaParser();
    private HashSet<String> oneGenreTestLink = new HashSet<>();
    private HashSet<String> onePageTestLink = new HashSet<>();
    private HashSet<String> offerLinksSet = new HashSet<>();
    private String validOfferLink = "http://vinyl.ua/release/3372/georg-levin-everything-must-change%20%7D%7D";
    private String invalidOfferLink = "http://vinyl.ua/release/0";
    private Document validLinkDocument;
    private Document invalidLinkDocument;

    @BeforeAll
    void beforeAll() throws IOException {
        oneGenreTestLink.add("http://vinyl.ua/showcase/reggae");
        onePageTestLink.add("http://vinyl.ua/showcase/reggae?page=1");
        offerLinksSet.add(validOfferLink);
        offerLinksSet.add(invalidOfferLink);
        validLinkDocument = Jsoup.connect(validOfferLink).get();
        invalidLinkDocument = Jsoup.connect(invalidOfferLink).get();
    }

    @Test
    @DisplayName("Checks that returned hashset of genres links isn't empty after parsing.")
    void getGenresLinksTest() {
        assertFalse(vinylUaParser.getGenresLinks().isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of pages' links in one genre isn't empty after parsing.")
    void getPageLinksTest() {
        assertFalse(vinylUaParser.getPageLinks(oneGenreTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of offers' links in one page isn't empty after parsing.")
    void getOfferLinksTest() {
        assertFalse(vinylUaParser.getOfferLinks(onePageTestLink).isEmpty());
    }

    @Test
    @DisplayName("Checks that returned hashset of raw offers isn't empty and doesn't contain invalid offers after parsing.")
    void readRawOffersFromAllOfferLinksTest() {
        Set<RawOffer> rawOffers = vinylUaParser.readRawOffersFromAllOfferLinks(offerLinksSet);
        assertEquals(1, rawOffers.size());
        for (RawOffer rawOffer : rawOffers) {
            assertTrue(rawOffer.getPrice() > 0.);
            assertTrue(rawOffer.getCurrency().isPresent());
            assertFalse(rawOffer.getRelease().isEmpty());
        }
    }

    @Test
    @DisplayName("Checks that returned raw offer is filled after parsing not valid offer link.")
    void getRawOfferFromInvalidOfferLinkTest() {
        RawOffer actualRawOffer = vinylUaParser.getRawOfferFromOfferLink(invalidOfferLink);
        assertNotNull(actualRawOffer.getCurrency());
        assertNotNull(actualRawOffer.getRelease());
        assertEquals("Various Artists", actualRawOffer.getArtist());
        assertNotNull(actualRawOffer.getGenre());
        assertNotNull(actualRawOffer.getCatNumber());
        assertEquals("img/goods/no_image.jpg", actualRawOffer.getImageLink());
    }

    @Test
    @DisplayName("Checks that when price==0, isValid returns false")
    void isValidZeroPrice() {
        //prepare
        RawOffer rawOfferZeroPrice = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferZeroPrice.setPrice(0.);
        //when
        assertFalse(vinylUaParser.isValid(rawOfferZeroPrice));
    }

    @Test
    @DisplayName("Checks that when currency.isEmpty(), isValid returns false")
    void isValidEmptyCurrency() {
        //prepare
        RawOffer rawOfferEmptyCurrency = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferEmptyCurrency.setCurrency(Optional.empty());
        //when
        assertFalse(vinylUaParser.isValid(rawOfferEmptyCurrency));
    }

    @Test
    @DisplayName("Checks that when release is an empty string, isValid returns false")
    void isValidEmptyRelease() {
        //prepare
        RawOffer rawOfferEmptyRelease = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferEmptyRelease.setRelease("");
        //when
        assertFalse(vinylUaParser.isValid(rawOfferEmptyRelease));
    }

    @Test
    @DisplayName("Checks that when offerLink==null, isValid returns false")
    void isValidNullOfferLink() {
        //prepare
        RawOffer rawOfferNullOfferLink = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferNullOfferLink.setOfferLink(null);
        //when
        assertFalse(vinylUaParser.isValid(rawOfferNullOfferLink));
    }

    @Test
    @DisplayName("Checks that when price!=0, currency.isPresent(), release is not an empty string, and offerLink isn't null, isValid returns true")
    void isValidValidRawOffer() {
        //prepare
        RawOffer rawOfferValidOffer = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        //when
        assertTrue(vinylUaParser.isValid(rawOfferValidOffer));
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Valid-Document returns not empty release")
    void getReleaseFromValidDocument() {
        assertFalse(vinylUaParser.getReleaseFromDocument(validLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Not Valid-Document returns empty release")
    void getReleaseFromNotValidDocument() {
        assertTrue(vinylUaParser.getReleaseFromDocument(invalidLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Valid-Document returns artist != Various Artists")
    void getArtistFromValidDocument() {
        assertNotEquals("Various Artists", vinylUaParser.getArtistFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Not Valid-Document returns artist == Various Artists")
    void getArtistFromNotValidDocument() {
        assertEquals("Various Artists", vinylUaParser.getArtistFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Valid-Document returns price != 0.")
    void getPriceFromValidDocument() {
        assertTrue(vinylUaParser.getPriceFromDocument(validLinkDocument) > 0);
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Not Valid-Document returns price == 0.")
    void getPriceFromNotValidDocument() {
        assertEquals(0., vinylUaParser.getPriceFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns not-empty optional")
    void getOptionalCurrencyFromValidDocument() {
        assertTrue(vinylUaParser.getOptionalCurrencyFromDocument(validLinkDocument).isPresent());
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns empty optional")
    void getOptionalCurrencyFromNotValidDocument() {
        assertTrue(vinylUaParser.getOptionalCurrencyFromDocument(invalidLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Valid-Document returns not default link to high resolution image")
    void getHighResImageLinkFromValidDocument() {
        assertNotEquals("img/goods/no_image.jpg", vinylUaParser.getHighResImageLinkFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Not Valid-Document returns null link to high resolution image")
    void getHighResImageLinkFromNotValidDocument() {
        assertEquals("img/goods/no_image.jpg", vinylUaParser.getHighResImageLinkFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Valid-Document returns not empty genre")
    void getGenreFromValidDocument() {
        assertFalse(vinylUaParser.getGenreFromDocument(validLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Not Valid-Document returns empty genre")
    void getGenreFromNotValidDocument() {
        assertTrue(vinylUaParser.getGenreFromDocument(invalidLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getCatNumberFrom-Valid-Document returns not empty catNumber")
    void getCatNumberFromValidDocument() {
        assertFalse(vinylUaParser.getCatNumberFromDocument(validLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getCatNumberFrom-Not Valid-Document returns empty catNumber")
    void getCatNumberFromNotValidDocument() {
        assertTrue(vinylUaParser.getCatNumberFromDocument(invalidLinkDocument).isEmpty());
    }

}*/
