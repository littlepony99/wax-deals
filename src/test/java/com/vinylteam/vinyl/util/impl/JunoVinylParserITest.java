package com.vinylteam.vinyl.util.impl;

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
class JunoVinylParserITest {

    private final JunoVinylParser parser = new JunoVinylParser();
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();
    private final String validOfferLink = "https://www.juno.co.uk/products/michel-legrand-suburbia-suite-evergreen-review-ep/702627-01/";
    private final String invalidOfferLink = "https://www.juno.co.uk";
    private final Set<String> offerLinksSet = new HashSet<>(Set.of(validOfferLink, invalidOfferLink));
    private Document validLinkDocument;
    private Document invalidLinkDocument;

    @BeforeAll
    void beforeAll() throws IOException {
        validLinkDocument = Jsoup.connect(validOfferLink).get();
        invalidLinkDocument = Jsoup.connect(invalidOfferLink).get();
    }

    @Test
    @DisplayName("Checks whether the set of page links that are present on the start page contains elements (links)")
    void getPageLinks() {
        var pageLinks = parser.getAllLinksFromStartPage();
        assertFalse(pageLinks.isEmpty());
    }

    @Test
    @DisplayName("Checks whether pages links are counted correctly")
    void countPageLinksTest() {
        Set<String> links = Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/333/?media_type=vinyl");
        Integer number = parser.countPageLinks(links);
        assertEquals(333, number);
    }

    @Test
    @DisplayName("Checks whether gaps in sequenced page links are filled")
    void givenLinksToPagesWhenFullListFromOneToMAxPageIsReceivedThenCorrect() {
        var items = parser.getAllPageLinksSet(Set.of("https://www.juno.co.uk/all/back-cat/3/?media_type=vinyl", "https://www.juno.co.uk/all/back-cat/12/?media_type=vinyl"));
        assertEquals(12, items.size());
    }

    @Test
    @DisplayName("Checks whether offerLinks set is returned non empty as a result of parsing a page link")
    void readOfferLinksFromAllPagesTest() {
        var offerLinks = parser.readOfferLinksFromAllPages(Set.of("https://www.juno.co.uk/all/back-cat/1/?media_type=vinyl"));
        assertEquals(50, offerLinks.size());
    }

    @Test
    @DisplayName("Checks whether raw set is returned non empty and doesn't include invalid offer from set with one valid and one invalid link")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffers = parser.getValidRawOffersFromAllOfferLinks(offerLinksSet);
        assertEquals(1, rawOffers.size());
        for (RawOffer rawOffer : rawOffers) {
            assertNotEquals("", rawOffer.getRelease());
        }
    }

    @Test
    @DisplayName("Checks that returned raw offer is filled with not-default values that aren't null after parsing valid offer link.")
    void getRawOfferFromValidOfferLinkTest() {
        RawOffer actualRawOffer = parser.getRawOfferFromOfferLink(validOfferLink);
        assertNotEquals("img/goods/no_image.jpg", actualRawOffer.getImageLink());
        assertNotNull(actualRawOffer.getImageLink());
    }

    @Test
    @DisplayName("Checks that returned raw offer is filled but with default values after parsing not valid offer link.")
    void getRawOfferFromInvalidOfferLinkTest() {
        assertEquals("img/goods/no_image.jpg", parser.getRawOfferFromOfferLink(invalidOfferLink).getImageLink());
    }

    @Test
    @DisplayName("Checks that when price==0, isValid returns false")
    void isValidZeroPrice() {
        //prepare
        RawOffer rawOfferZeroPrice = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferZeroPrice.setPrice(0.);
        //when
        assertFalse(parser.isValid(rawOfferZeroPrice));
    }

    @Test
    @DisplayName("Checks that when currency.isEmpty(), isValid returns false")
    void isValidEmptyCurrency() {
        //prepare
        RawOffer rawOfferEmptyCurrency = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferEmptyCurrency.setCurrency(Optional.empty());
        //when
        assertFalse(parser.isValid(rawOfferEmptyCurrency));
    }

    @Test
    @DisplayName("Checks that when release is an empty string, isValid returns false")
    void isValidEmptyRelease() {
        //prepare
        RawOffer rawOfferEmptyRelease = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferEmptyRelease.setRelease("");
        //when
        assertFalse(parser.isValid(rawOfferEmptyRelease));
    }

    @Test
    @DisplayName("Checks that when offerLink==null, isValid returns false")
    void isValidNullOfferLink() {
        //prepare
        RawOffer rawOfferNullOfferLink = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        rawOfferNullOfferLink.setOfferLink(null);
        //when
        assertFalse(parser.isValid(rawOfferNullOfferLink));
    }

    @Test
    @DisplayName("Checks that when price!=0, currency.isPresent(), release is not an empty string, and offerLink isn't null, isValid returns true")
    void isValidValidRawOffer() {
        //prepare
        RawOffer rawOfferValidOffer = dataGenerator.getRawOfferWithShopIdAndNumber(1, 1);
        //when
        assertTrue(parser.isValid(rawOfferValidOffer));
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Valid-Document returns not empty release")
    void getReleaseFromValidDocument() {
        assertNotEquals("", parser.getReleaseFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getReleaseFrom-Not Valid-Document returns empty release")
    void getReleaseFromNotValidDocument() {
        assertEquals("", parser.getReleaseFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Valid-Document returns artist != Various Artists")
    void getArtistFromValidDocument() {
        assertNotEquals("Various Artists", parser.getArtistFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getArtistFrom-Not Valid-Document returns artist == Various Artists")
    void getArtistFromNotValidDocument() {
        assertEquals("Various Artists", parser.getArtistFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Valid-Document returns price != 0.")
    void getPriceFromValidDocument() {
        assertTrue(parser.getPriceFromDocument(validLinkDocument) > 0);
    }

    @Test
    @DisplayName("Checks that getPriceFrom-Not Valid-Document returns price == 0.")
    void getPriceFromNotValidDocument() {
        assertEquals(0., parser.getPriceFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns not-empty optional")
    void getOptionalCurrencyFromValidDocument() {
        assertTrue(parser.getOptionalCurrencyFromDocument(validLinkDocument).isPresent());
    }

    @Test
    @DisplayName("Checks that getOptionalCurrencyFrom-Valid-Document returns empty optional")
    void getOptionalCurrencyFromNotValidDocument() {
        assertTrue(parser.getOptionalCurrencyFromDocument(invalidLinkDocument).isEmpty());
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Valid-Document returns not default link to high resolution image")
    void getHighResImageLinkFromValidDocument() {
        assertNotEquals("img/goods/no_image.jpg", parser.getHighResImageLinkFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getHighResImageLinkFrom-Not Valid-Document returns null link to high resolution image")
    void getHighResImageLinkFromNotValidDocument() {
        assertEquals("img/goods/no_image.jpg", parser.getHighResImageLinkFromDocument(invalidLinkDocument));
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Valid-Document returns not empty genre")
    void getGenreFromValidDocument() {
        assertNotEquals("", parser.getGenreFromDocument(validLinkDocument));
    }

    @Test
    @DisplayName("Checks that getGenreFrom-Not Valid-Document returns empty genre")
    void getGenreFromNotValidDocument() {
        assertEquals("", parser.getGenreFromDocument(invalidLinkDocument));
    }

}