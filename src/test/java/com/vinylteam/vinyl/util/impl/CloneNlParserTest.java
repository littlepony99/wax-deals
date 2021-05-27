package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CloneNlParserTest {

    private CloneNlParser parser = new CloneNlParser();
    private Element vinylElement;
    private DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeAll
    void init() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("cloneNLItem.html").getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("DIV.release").first();
    }

    @Test
    @DisplayName("Checks whether page links are counted correctly")
    void countPageLinksTest() {
        Set<String> links = Set.of("https://clone.nl/all/genre/Disco?sort=datum&order=desc&page=4", "https://clone.nl/all/genre/Disco?sort=datum&order=desc&page=333");
        Integer number = parser.countPageLinks(links);
        assertEquals(333, number);
    }

    @Test
    @DisplayName("Checks whether page links are fully returned")
    void getAllPageLinksSetTest() {
        Set<String> links = Set.of("https://clone.nl/all/genre/Disco?sort=datum&order=desc&page=4", "https://clone.nl/all/genre/Disco?sort=datum&order=desc&page=333");
        var allPageLinks = parser.getAllPageLinksSet(links);
        assertEquals(333, allPageLinks.size());
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromElement(vinylElement);
        assertNotNull(rawOffer);
        assertEquals("The Paul Breitner EP", rawOffer.getRelease());
        assertEquals("https://clone.nl/item36449.html", rawOffer.getOfferLink());
        assertEquals("https://clone.nl/platen/artwork/large/plaatimage36037.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(11.49d, rawOffer.getPrice());
        assertEquals("Various Artists", rawOffer.getArtist());
        assertTrue(rawOffer.getGenre().contains("House"));
    }

    @Test
    @DisplayName("Checks whether genres are received from HTML Element that represents one vinyl Item")
    void getGenreFromDocument() throws IOException {
        var genre = parser.getGenreFromDocument(vinylElement);
        assertNotNull(genre);
    }

    @Test
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getReleaseFromDocument() throws IOException {
        String release = parser.getReleaseFromDocument(vinylElement);
        assertEquals("The Paul Breitner EP", release);
    }

    @Test
    @DisplayName("Checks whether artist is received from HTML Element that represents one vinyl Item")
    void getArtistFromDocument() throws IOException {
        String artist = parser.getArtistFromDocument(vinylElement);
        assertNotNull(artist);
    }

    @Test
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getCatNumberFromDocument() throws IOException {
        String catalogNumber = parser.getCatNumberFromDocument(vinylElement);
        assertEquals("Rothmans7", catalogNumber);
    }

    @Test
    @DisplayName("Checks whether release is received from HTML Element that represents one vinyl Item")
    void getInStockInfoFromDocument() throws IOException {
        boolean inStock = parser.getInStockInfoFromDocument(vinylElement);
        assertTrue(inStock);
    }

    @Test
    @DisplayName("Checks whether currency is received from HTML Element that represents one vinyl Item")
    void getOptionalCurrencyFromDocumentTest() throws IOException {
        var currency = parser.getOptionalCurrencyFromDocument(vinylElement);
        assertTrue(currency.isPresent());
    }

    @Test
    @DisplayName("Checks whether price is received from HTML Element that represents one vinyl Item")
    void getPriceFromDocumentTest() throws IOException {
        var price = parser.getPriceFromDocument(vinylElement);
        assertEquals(11.49d, price);
    }

    @Test
    @DisplayName("Checks whether high resolution image link is received from HTML Element that represents one vinyl Item")
    void getHighResImageLinkFromDocumentTest() throws IOException {
        String highResolutionImageLink = parser.getHighResImageLinkFromDocument(vinylElement);
        assertNotNull(highResolutionImageLink);
        assertFalse(highResolutionImageLink.isEmpty());
    }

    @Test
    @DisplayName("Checks whether offer link is received from HTML Element that represents one vinyl Item")
    void getgetOfferLinkFromDocumentTest() throws IOException {
        String offerLink = parser.getOfferLinkFromDocument(vinylElement);
        assertNotNull(offerLink);
        assertFalse(offerLink.isEmpty());
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

}