package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HardWaxParserTest {

    private HardWaxParser parser;
    private Element vinylElement;

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from the listed on the page")
    void getRawOffersFromAllOfferLinksTest() {
        RawOffer rawOffer = parser.getRawOfferFromElement(vinylElement, parser.getListParser());
        assertNotNull(rawOffer);
        assertEquals("Sleepwalker", rawOffer.getRelease());
        assertEquals("https://hardwax.com/14863/o-yuki-conjugate/sleepwalker/", rawOffer.getOfferLink());
        assertEquals("https://media.hardwax.com/images/14863xbig.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(35.0d, rawOffer.getPrice());
        assertEquals("O Yuki Conjugate", rawOffer.getArtist());
        assertEquals("Utter 008", rawOffer.getCatNumber());
        assertTrue(rawOffer.getGenre().isEmpty());
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item from one vinyl page")
    void getRawOffersFromOneVinylPageLinkTest() throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource("HtmlPages/hardwaxOnePageItem.html").getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("DIV#content").first();
        RawOffer rawOffer = parser.getRawOfferFromElement(vinylElement, parser.getOnePageParser());
        assertNotNull(rawOffer);
        assertEquals("Sleepwalker", rawOffer.getRelease());
        assertEquals("https://hardwax.com/14863/o-yuki-conjugate/sleepwalker/", rawOffer.getOfferLink());
        assertEquals("https://media.hardwax.com/images/14863xbig.jpg", rawOffer.getImageLink());
        assertEquals(Currency.EUR, rawOffer.getCurrency().get());
        assertEquals(35.0d, rawOffer.getPrice());
        assertEquals("O Yuki Conjugate", rawOffer.getArtist());
        assertEquals("Utter 008", rawOffer.getCatNumber());
        assertTrue(rawOffer.getGenre().isEmpty());
    }

    @Test
    @DisplayName("Checks whether getAllPageLinksSet returns whole link set in respect of max number page")
    void test() {
        Set<String> links = parser.getAllPageLinksSet(Set.of("https://hardwax.com/electronica/?filter=vinyl&page=6"));
        assertEquals(6, links.size());
    }

    @BeforeEach
    void init() throws IOException {
        parser = new HardWaxParser();
        setVinylElement("HtmlPages/hardwaxItem.html");
    }

    private void setVinylElement(String fromResourceFile) throws IOException {
        File testHtml = new File(this.getClass().getClassLoader().getResource(fromResourceFile).getPath());
        vinylElement = Jsoup.parse(testHtml, null).select("DIV.block").first();
    }

    @Test
    @DisplayName("Checks whether pages links are counted correctly")
    void countPageLinksTest() {
        Set<String> links = Set.of("https://hardwax.com/electronica/?filter=vinyl&page=6");
        Integer number = parser.countPageLinks(links);
        assertEquals(6, number);
    }

    @Test
    @DisplayName("Checks whether genre is taken from the specified URL correctly")
    void getGenreFromLinkTest() {
        String genre = parser.getGenreFromLink("https://hardwax.com/digital/?page=10");
        assertEquals("digital", genre);
    }

}