package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class CloneNlParserTest {

    private CloneNlParser parser;
    private Element vinylElement;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void init() throws IOException {
        parser = new CloneNlParser();
        setVinylElement("HtmlPages/cloneNLItem.html");
    }

    private void setVinylElement(String fromResourceFile) throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource(fromResourceFile).getPath());
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
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from the listed on the page")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromElement(vinylElement, new CloneNlParser().getListParser());
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
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from one vinyl page")
    void getRawOffersFromOneVinylPageLinkTest() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("HtmlPages/cloneNLItemOnePage.html").getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("DIV").first();
        var rawOffer = parser.getRawOfferFromElement(vinylElement, new CloneNlParser().getOnePageParser());
        assertNotNull(rawOffer);
        assertEquals("The Paul Breitner EP", rawOffer.getRelease());
        assertTrue(rawOffer.getOfferLink().isEmpty());
        assertEquals("https://clone.nl/platen/artwork/large/plaatimage36037.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(11.49d, rawOffer.getPrice());
        assertEquals("Various Artists", rawOffer.getArtist());
        assertTrue(rawOffer.getGenre().contains("House"));
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
    @DisplayName("Checks for actual price of vinyl, the vinyl is not in stock")
    void getActualPriceWhenNoOffer() throws IOException {
        String fromResourceFile = "HtmlPages/cloneNLItemNotInStock.html";
        setVinylElement(fromResourceFile);
        double oldPrice = 0.44;
        String offerUrl = "https://clone.nl/item63738.html";
        parser = spy(parser);
        Mockito.doReturn(Optional.of(vinylElement.ownerDocument())).when(parser).getDocument(offerUrl);
        double actualPrice = parser.getRawOfferFromOfferLink(offerUrl).getPrice();
        assertEquals(0, actualPrice);
    }

    @Test
    @DisplayName("Checks for actual price of vinyl, the vinyl is non-accessible due to network issues")
    void getActualPriceWhenNoResponseForPriceRequest() throws IOException {
        String fromResourceFile = "HtmlPages/cloneNLItemNotInStock.html";
        setVinylElement(fromResourceFile);
        String offerUrl = "https://clone.nl/item63738.html";
        parser = spy(parser);
        Mockito.doReturn(Optional.ofNullable(null)).when(parser).getDocument(offerUrl);
        RawOffer rawOfferFromOfferLink = parser.getRawOfferFromOfferLink(offerUrl);
        double actualPrice = rawOfferFromOfferLink.getPrice();
        assertFalse(parser.isValid(rawOfferFromOfferLink));
        assertEquals(0, actualPrice);
    }

}