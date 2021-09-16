package com.vinylteam.vinyl.util.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RedEyeRecordsParserTest {

    private final RedEyeRecordsParser redEyeRecordsParser = new RedEyeRecordsParser();

    @Test
    @DisplayName("Gets set of links to all catalogue pages.")
    void getAllPages() {
        //when
        var allPages = redEyeRecordsParser.getAllPages();
        //then
        assertFalse(allPages.isEmpty());
    }

    @Test
    @DisplayName("Gets set of all links to offers when offers in stock exist")
    void getAllOfferLinksInStockExist() {
        //prepare
        Set<String> newReleasesBassFirstPage = Set.of("https://www.redeyerecords.co.uk/bass-music/new-releases");
        //when
        var offerLinks = redEyeRecordsParser.getAllOfferLinks(newReleasesBassFirstPage);
        //then
        assertFalse(offerLinks.isEmpty());
    }

    @Test
    @DisplayName("Gets empty set of all links to offers when offers in stock don't exist")
    void getAllOfferLinksNoneInStock() {
        //prepare
        Set<String> preOrderReleasesBassFirstPage = Set.of("https://www.redeyerecords.co.uk/bass-music/pre-orders");
        //when
        var offerLinks = redEyeRecordsParser.getAllOfferLinks(preOrderReleasesBassFirstPage);
        //then
        assertTrue(offerLinks.isEmpty());
    }

    @Test
    @DisplayName("Gets raw offer from link")
    void getRawOfferFromOfferLink() {
        //prepare
        var offerLink = "https://www.redeyerecords.co.uk/vinyl/101381-fr237--simbad-take-my-hand-ep-feat--brian-temba-inc--jimpster-and-smbd-remixes";
        //when
        var rawOffer = redEyeRecordsParser.getRawOfferFromOfferLink(offerLink);
        //then
        assertNotNull(rawOffer);
        assertFalse(rawOffer.getOfferLink().isEmpty());
        assertFalse(rawOffer.getArtist().isEmpty());
        assertFalse(rawOffer.getRelease().isEmpty());
        assertFalse(rawOffer.getImageLink().isEmpty());
        assertTrue(rawOffer.getPrice() > 0d);
        assertTrue(rawOffer.getCurrency().isPresent());
        assertTrue(rawOffer.getGenre().isEmpty());
    }

    @Test
    @DisplayName("Gets raw offers from set of offer links where valid offers exist")
    void getRawOffersList() {
        //prepare
        Set<String> newReleasesBassFirstPage = Set.of("https://www.redeyerecords.co.uk/bass-music/new-releases");
        Set<String> offerLinks = redEyeRecordsParser.getAllOfferLinks(newReleasesBassFirstPage);
        //when
        var rawOffersList = redEyeRecordsParser.getRawOffersListFromOfferLinksSet(offerLinks);
        //then
        assertFalse(rawOffersList.isEmpty());
    }

}