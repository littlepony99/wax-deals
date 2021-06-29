
/*
package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DecksParserTest {

    private final DecksParser decksParser = new DecksParser();
    private Document startPageDocument;
    private Document offerPageDocument;

    @BeforeAll
    void beforeAll() throws IOException {
        File startHtml = new File(this.getClass().getClassLoader().getResource("HtmlPages/decks/decks_start_page.html").getPath());
        startPageDocument = Jsoup.parse(startHtml, null);
        File offerHtml = new File(this.getClass().getClassLoader().getResource("HtmlPages/decks/decks_release.html").getPath());
        offerPageDocument = Jsoup.parse(offerHtml, null);

    }

    @Test
    @DisplayName("Get genre from document")
    void getGenreFromDocument() {
        //when
        String actualGenreFromDocument = decksParser.getGenreFromDocument(startPageDocument);
        //then
        assertNotNull(actualGenreFromDocument);
    }

    @Test
    @DisplayName("Get optional currency from document")
    void getOptionalCurrencyFromDocument() {
        //when
        Optional<Currency> actualOptionalCurrencyFromDocument = decksParser.getOptionalCurrencyFromDocument(offerPageDocument);
        //then
        assertTrue(actualOptionalCurrencyFromDocument.isPresent());
    }

    @Test
    @DisplayName("Get price from document")
    void getPriceFromDocument() {
        //when
        double actualPriceFromDocument = decksParser.getPriceFromDocument(offerPageDocument);
        //then
        assertNotEquals(0.0d, actualPriceFromDocument);
    }

    @Test
    @DisplayName("Get artist from documents")
    void getArtistFromDocument() {
        //when
        String actualArtistFromDocument = decksParser.getArtistFromDocument(offerPageDocument);
        //then
        assertNotNull(actualArtistFromDocument);
    }

    @Test
    @DisplayName("Get release from document")
    void getReleaseFromDocument() {
        //when
        String actualReleaseFromDocument = decksParser.getReleaseFromDocument(offerPageDocument);
        //then
        assertNotNull(actualReleaseFromDocument);
    }

    @Test
    @DisplayName("Get catalog number from document")
    void getCatNumberFromDocument() {
        //when
        String actualCatalogNumberFromDocument = decksParser.getCatNumberFromDocument(offerPageDocument);
        //then
        assertNotNull(actualCatalogNumberFromDocument);
    }

    @Test
    @DisplayName("Get stock info from document")
    void getInStockInfoFromDocument() {
        //when
        boolean actualStockInfoFromDocument = decksParser.getInStockInfoFromDocument(offerPageDocument);
        //then
        assertTrue(actualStockInfoFromDocument);
    }

    @Test
    @DisplayName("Get link to image")
    void getHighResImageLinkFromDocument() {
        //when
        String imageLink = decksParser.getHighResImageLinkFromDocument(offerPageDocument);
        //then
        assertNotNull(imageLink);
    }

    @Test
    @DisplayName("Validate row offer")
    void isValid() {
        //prepare
        RawOffer testRawOffer = new RawOffer();
        testRawOffer.setCurrency(Optional.of(Currency.USD));
        testRawOffer.setOfferLink("/offer-link");
        testRawOffer.setPrice(100);
        testRawOffer.setRelease("Release");
        //when
        boolean actual = decksParser.isValid(testRawOffer);
        //then
        assertTrue(actual);
    }

    @Test
    @DisplayName("Validate row offer if currency is not valid")
    void isValidNotValidCurrency() {
        //prepare
        RawOffer testRawOffer = new RawOffer();
        testRawOffer.setCurrency(Optional.empty());
        testRawOffer.setOfferLink("/offer-link");
        testRawOffer.setPrice(100);
        testRawOffer.setRelease("Release");
        //when
        boolean actual = decksParser.isValid(testRawOffer);
        //then
        assertFalse(actual);
    }

    @Test
    @DisplayName("Validate row offer if offer link is not valid")
    void isValidNotValidOfferLink() {
        //prepare
        RawOffer testRawOffer = new RawOffer();
        testRawOffer.setCurrency(Optional.of(Currency.USD));
        testRawOffer.setOfferLink(null);
        testRawOffer.setPrice(100);
        testRawOffer.setRelease("Release");
        //when
        boolean actual = decksParser.isValid(testRawOffer);
        //then
        assertFalse(actual);
    }

    @Test
    @DisplayName("Validate row offer if price is not valid")
    void isValidNotValidPrice() {
        //prepare
        RawOffer testRawOffer = new RawOffer();
        testRawOffer.setCurrency(Optional.of(Currency.USD));
        testRawOffer.setOfferLink("/offer-link");
        testRawOffer.setPrice(0);
        testRawOffer.setRelease("Release");
        //when
        boolean actual = decksParser.isValid(testRawOffer);
        //then
        assertFalse(actual);
    }

    @Test
    @DisplayName("Validate row offer if price is not valid")
    void isValidNotValidRelease() {
        //prepare
        RawOffer testRawOffer = new RawOffer();
        testRawOffer.setCurrency(Optional.of(Currency.USD));
        testRawOffer.setOfferLink("/offer-link");
        testRawOffer.setPrice(100);
        testRawOffer.setRelease("");
        //when
        boolean actual = decksParser.isValid(testRawOffer);
        //then
        assertFalse(actual);
    }

}*/
