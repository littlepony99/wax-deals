package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DataGeneratorForTests;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeejayDeParserTest {

    private static final String START_PAGE_LINK = DeejayDeParser.BASE_LINK + "/m_All/sm_News/substyles_211_223_312_305_309_403_405/stock_1/perpage_160";

    private DeejayDeParser parser;

    private Element vinylElement;
    private final DataGeneratorForTests dataGenerator = new DataGeneratorForTests();

    @BeforeEach
    void init() throws IOException {
        parser = new DeejayDeParser();
        setVinylElement("HtmlPages/deejayDeItem.html");
    }

    private void setVinylElement(String fromResourceFile) throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource(fromResourceFile).getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("ARTICLE").first();
    }

    @Test
    void getAllPages() {
        Set<String> pages = parser.getAllPages(START_PAGE_LINK);
        assertFalse(pages.isEmpty());
        System.out.println(pages);
        System.out.println(pages.size());
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from the listed on the page")
    void getRawOffersFromAllOfferLinksTest() {
        RawOffer rawOffer = parser.getRawOfferFromElement(vinylElement, parser.getDetailedParser());
        assertNotNull(rawOffer);
        assertEquals("Dixon Ep", rawOffer.getRelease());
        assertEquals("https://www.deejay.de/Bazmann_Dixon_Ep_RWL007__969033", rawOffer.getOfferLink());
        assertEquals("https://www.deejay.de/images/xl/3/3//969033.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(10.04d, rawOffer.getPrice());
        assertEquals("Bazmann", rawOffer.getArtist());
        assertTrue(rawOffer.getGenre().contains("House"));
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from one vinyl page")
    void getRawOffersFromOneVinylPageLinkTest() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("HtmlPages/deejayDeItemOnePage.html").getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("DIV").first();
        RawOffer rawOffer = parser.getRawOfferFromElement(vinylElement, parser.getOnePageDetailedParser());
        assertNotNull(rawOffer);
        assertEquals("Dixon Ep", rawOffer.getRelease());
        //assertTrue(rawOffer.getOfferLink().isEmpty());
        assertEquals("https://www.deejay.de/images/xl/3/3//969033.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(10.04d, rawOffer.getPrice());
        assertEquals("Bazmann", rawOffer.getArtist());
        assertTrue(rawOffer.getGenre().contains("House"));
    }

    @Test
    @DisplayName("Check")
    void getShopTest(){
        long shop = parser.getShopId();
        assertEquals(6, shop);
    }
}