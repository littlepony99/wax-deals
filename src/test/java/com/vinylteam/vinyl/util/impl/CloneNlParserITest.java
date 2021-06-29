
/*
package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CloneNlParserITest {

    private final CloneNlParser parser = new CloneNlParser();

    @Test
    @DisplayName("Checks whether genre links can be received")
    void getAllGenreLinks() {
        var allGenres = parser.getAllGenreLinks();
        assertFalse(allGenres.isEmpty());
        for (String genreLink : allGenres) {
            assertTrue(genreLink.startsWith(CloneNlParser.BASE_LINK));
        }
    }

    @Test
    @DisplayName("Checks whether page links can be received by the set of genre links, at least one for each genre")
    void getAllPagesByGenresTest() {
        Set<String> genreLink = Set.of("https://clone.nl/all/genre/Disco?sort=datum&order=desc", "https://clone.nl/all/genre/Dubstep?sort=datum&order=desc");
        var allGenrePages = parser.getAllPagesByGenres(genreLink);
        assertFalse(allGenrePages.isEmpty());
        assertTrue(allGenrePages.size() >= 2);
    }

    @Test
    @DisplayName("Checks whether at least one page link can be received by one genre link")
    void getAllPagesByGenreTest() {
        String genreLink = "https://clone.nl/all/genre/Dubstep?sort=datum&order=desc";
        var allGenrePages = parser.getAllPagesByGenre(genreLink);
        assertFalse(allGenrePages.isEmpty());
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromOfferLink("https://clone.nl/item36449.html");
        assertNotNull(rawOffer);
        assertFalse(rawOffer.getRelease().isEmpty());
        assertFalse(rawOffer.getOfferLink().isEmpty());
        assertFalse(rawOffer.getImageLink().isEmpty());
        assertTrue(rawOffer.getCurrency().isPresent());
        assertTrue(rawOffer.getPrice() > 0d);
        assertFalse(rawOffer.getArtist().isEmpty());
        assertFalse(rawOffer.getGenre().isEmpty());
    }

    @Test
    @DisplayName("Checks whether RawOffer is received from the non-valid link that must have represented on vinyl page")
    void getRawOffersFromNonValidLinksTest() {
        String nonExistingOfferLink = "https://clone.nl/iitem36449.html";
        var actualRawOffer = parser.getRawOfferFromOfferLink(nonExistingOfferLink);
        assertNotNull(actualRawOffer);
        RawOffer expectedRawOffer = new RawOffer();
        expectedRawOffer.setOfferLink(nonExistingOfferLink);
        assertEquals(expectedRawOffer, actualRawOffer);
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

}*/
