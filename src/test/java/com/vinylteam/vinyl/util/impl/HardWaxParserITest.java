package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HardWaxParserITest {

    private final HardWaxParser parser = new HardWaxParser();

    @Test
    @DisplayName("Checks whether genre links can be received")
    void getAllGenreLinks() {
        var allGenres = parser.getAllGenresLinks();
        assertFalse(allGenres.isEmpty());
        for (String genreLink : allGenres) {
            assertTrue(genreLink.startsWith(HardWaxParser.BASE_LINK));
        }
    }

    @Test
    @DisplayName("Checks whether page links can be received by the set of genre links, at least one for each genre")
    void getAllPagesByGenresTest() {
        Set<String> genreLink = Set.of("https://hardwax.com/electronica/?filter=vinyl", "https://hardwax.com/digital/?filter=vinyl");
        var allGenrePages = parser.getAllPagesByGenres(genreLink);
        assertFalse(allGenrePages.isEmpty());
        assertTrue(allGenrePages.size() >= 2);
    }

    @Test
    @DisplayName("Checks whether page links can be received by the set of genre links, at least one for each genre")
    void getAllPagesByGenreTest() {
        String genreLink = "https://hardwax.com/electronica/?filter=vinyl";
        var allGenrePages = parser.getAllPagesByGenre(genreLink);
        assertFalse(allGenrePages.isEmpty());
        assertTrue(allGenrePages.size() >= 2);
    }

    @Test
    @DisplayName("Checks whether offers are received by specified set of links")
    void readOffersFromAllPagesTest() {
        var allPageLinks = parser.readOffersFromAllPages(Set.of("https://hardwax.com/electronica/?filter=vinyl&page=6", "https://hardwax.com/electronica/?filter=vinyl&page=8"));
        assertFalse(allPageLinks.isEmpty());
    }

    @Test
    @DisplayName("Checks whether offers are received by specified link")
    void readOffersFromOneLinkTest() {
        var rawOffers = parser.readOffersFromOneLink("https://hardwax.com/electronica/?filter=vinyl&page=6");
        assertFalse(rawOffers.isEmpty());
        for (RawOffer rawOffer : rawOffers) {
            assertEquals("electronica", rawOffer.getGenre());
        }
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromOfferLink("https://hardwax.com/15649/measure-divide/green-parallel/");
        assertNotNull(rawOffer);
        assertFalse(rawOffer.getRelease().isEmpty());
        assertFalse(rawOffer.getOfferLink().isEmpty());
        assertFalse(rawOffer.getImageLink().isEmpty());
        assertTrue(rawOffer.getCurrency().isPresent());
        assertTrue(rawOffer.getPrice() > 0d);
        assertFalse(rawOffer.getArtist().isEmpty());
        assertTrue(rawOffer.getGenre().isEmpty());
    }

}