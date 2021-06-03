package com.vinylteam.vinyl.util.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeejayDeParserITest {

    private static final String START_PAGE_LINK = DeejayDeParser.BASE_LINK + "/m_All/sm_News/substyles_211_223_312_305_309_403_405/stock_1/perpage_160";
    private final DeejayDeParser parser = new DeejayDeParser();

    @Test
    @DisplayName("Checks whether RawOffer is received from HTML Element that represents one vinyl Item")
    void getRawOffersFromAllOfferLinksTest() {
        var rawOffer = parser.getRawOfferFromOfferLink("https://www.deejay.de/Bazmann_Dixon_Ep_RWL007_Vinyl__969033");
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
        String nonExistingOfferLink = "https://www.deejay.de/void";
        var actualRawOffer = parser.getRawOfferFromOfferLink(nonExistingOfferLink);
        assertNotNull(actualRawOffer);
        assertFalse(parser.isValid(actualRawOffer));
    }

    @Test
    @DisplayName("Checks whether page links are fully returned")
    void getAllPageLinksSetTest() {
        var allPageLinks = parser.getAllPages(START_PAGE_LINK);
        assertFalse(allPageLinks.isEmpty());
    }

    @Test
    @DisplayName("Checks whether page links are fully returned to the specified set")
    void getAllPageLinksToSetTest() {
        Set<String> links = new HashSet<>();
        var allPageLinks = parser.getAllPages(links, START_PAGE_LINK);
        assertFalse(links.isEmpty());
        assertEquals(links, allPageLinks);
    }

    @Test
    @DisplayName("Checks whether offers are received by specified set of links")
    void readOffersFromAllPagesTest() {
        var allPageLinks = parser.readOffersFromAllPages(Set.of(START_PAGE_LINK, "https://www.deejay.de/m_All/sm_News/stock_1/perpage_120/page_4"));
        assertFalse(allPageLinks.isEmpty());
    }

}