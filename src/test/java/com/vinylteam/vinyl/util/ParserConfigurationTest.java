/*
package com.vinylteam.vinyl.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParserConfigurationTest {

    @Test
    @DisplayName("Checks whether builder built configuration correctly")
    void testParserConfigurationBuilder() {
        String highResolutionImageSelector = "DIV.cover DIV.img A";
        String offerLinkSelector = "DIV.artikel > h3.title > A";
        String artistSelector = "DIV.artikel > h2.artist > INTERPRETS > A";
        String releaseSelector = "DIV.artikel > h2.artist > INTERPRETS > A";
        String vinylGenresSelector = "DIV.product_footer_a > DIV.style > A";
        String priceDetailsSelector = "DIV.order DIV.kaufen > SPAN.price";
        String catalogNumberSelector = "DIV.label > STRONG";
        String inStockMarker = "In Stock";
        String inStockMarkerSelector = "DIV.order DIV.stock";
        ParserConfiguration conf = ParserConfiguration
                .builder()
                .highResolutionImageSelector(highResolutionImageSelector)
                .offerLinkSelector(offerLinkSelector)
                .artistSelector(artistSelector)
                .releaseSelector(releaseSelector)
                .vinylGenresSelector(vinylGenresSelector)
                .priceDetailsSelector(priceDetailsSelector)
                .catalogNumberSelector(catalogNumberSelector)
                .inStockMarker(inStockMarker)
                .inStockMarkerSelector(inStockMarkerSelector)
                .build();
        assertEquals(highResolutionImageSelector, conf.getHighResolutionImageSelector());
        assertEquals(offerLinkSelector, conf.getOfferLinkSelector());
        assertEquals(artistSelector, conf.getArtistSelector());
        assertEquals(releaseSelector, conf.getReleaseSelector());
        assertEquals(vinylGenresSelector, conf.getVinylGenresSelector());
        assertEquals(priceDetailsSelector, conf.getPriceDetailsSelector());
        assertEquals(catalogNumberSelector, conf.getCatalogNumberSelector());
        assertEquals(inStockMarker, conf.getInStockMarker());
        assertEquals(inStockMarkerSelector, conf.getInStockMarkerSelector());
    }

}*/
