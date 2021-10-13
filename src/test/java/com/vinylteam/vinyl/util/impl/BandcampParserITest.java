package com.vinylteam.vinyl.util.impl;

import com.sun.source.tree.AssertTree;
import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BandcampParserITest {

    @Autowired
    BandcampParser bandcampParser;
    private final JSONParser jsonParser = new JSONParser();

    @Test
    @DisplayName("Gets items by valid url")
    void getItemsArrayByValidUrl() throws IOException, ParseException {
        //prepare
        URL validUrl = new URL("https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=0&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com");
        JSONParser parser = new JSONParser();
        //when
        JSONArray actualItemsArray = bandcampParser.getItemsArrayByUrl(parser, validUrl);
        //then
        System.out.println(actualItemsArray);
        assertFalse(actualItemsArray.isEmpty());
    }

    @Test
    @DisplayName("Gets empty items by valid url")
    void getEmptyItemsArrayByValidUrl() throws IOException, ParseException {
        //prepare
        URL validUrl = new URL("https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=1000&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com");
        JSONParser parser = new JSONParser();
        //when
        JSONArray actualItemsArray = bandcampParser.getItemsArrayByUrl(parser, validUrl);
        //then
        System.out.println(actualItemsArray);
        assertTrue(actualItemsArray.isEmpty());
    }

    @Test
    @DisplayName("Gets null items by invalid url")
    void getItemsArrayByInvalidUrl() throws IOException, ParseException {
        //prepare
        URL validUrl = new URL("https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com");
        JSONParser parser = new JSONParser();
        //when
        JSONArray actualItemsArray = bandcampParser.getItemsArrayByUrl(parser, validUrl);
        //then
        System.out.println(actualItemsArray);
        assertNull(actualItemsArray);
    }

    @Test
    @DisplayName("Gets offer link from item with url hints in it with subdomain and slug in present in url hints")
    void getOfferLinkFromItemWithUrlHintsWithSubdomainAndSlug() {
        //prepare
        JSONObject item = new JSONObject();
        JSONObject urlHints = new JSONObject();
        urlHints.put("subdomain", "subdomain");
        urlHints.put("slug", "slug");
        item.put("url_hints", urlHints);
        String expectedOfferLink = "https://subdomain.bandcamp.com/album/slug";
        //when
        String actualOfferLink = bandcampParser.getOfferLinkFromItem(item);
        //then
        assertEquals(expectedOfferLink, actualOfferLink);
    }

    @Test
    @DisplayName("Gets offer link from item with url hints in it without subdomain and slug in present in url hints")
    void getOfferLinkFromItemWithUrlHintsWithoutSubdomainAndSlug() {
        //prepare
        JSONObject item = new JSONObject();
        JSONObject urlHints = new JSONObject();
        urlHints.put("subdomain", null);
        urlHints.put("slug", null);
        item.put("url_hints", urlHints);
        String expectedOfferLink = "https://.bandcamp.com/album/";
        //when
        String actualOfferLink = bandcampParser.getOfferLinkFromItem(item);
        //then
        assertEquals(expectedOfferLink, actualOfferLink);
    }

    @Test
    @DisplayName("Gets offer link from item without url hints")
    void getOfferLinkFromItemWithoutUrlHints() {
        //prepare
        JSONObject item = new JSONObject();
        item.put("url_hints", null);
        String expectedOfferLink = "https://.bandcamp.com/album/";
        //when
        String actualOfferLink = bandcampParser.getOfferLinkFromItem(item);
        //then
        assertEquals(expectedOfferLink, actualOfferLink);
    }

    @Test
    @DisplayName("Gets offer links from Band camp")
    void getOfferLinks() throws IOException, ParseException {
        //when
        Set<String> actualOfferLinks = bandcampParser.getOfferLinks();
        //then
        assertFalse(actualOfferLinks.isEmpty());
    }

    @Test
    @DisplayName("Gets different product types' price details")
    void getProductTypesPriceDetails() throws IOException, ParseException {
        //prepare
        URL firstPageUrl = new URL("https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=0&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com");
        JSONArray items = bandcampParser.getItemsArrayByUrl(jsonParser, firstPageUrl);
        String offerLink = bandcampParser.getOfferLinkFromItem((JSONObject) items.get(0));
        Document documentToParse = bandcampParser.getDocument(offerLink).get();
        //when
        List<String> productTypesAndPriceDetails = bandcampParser.getProductTypesPriceDetails(documentToParse);
        //then
        assertFalse(productTypesAndPriceDetails.isEmpty());
    }

    @Test
    @DisplayName("Gets vinyl price details from different products types' price details list")
    void getVinylTypeFromProductsTypesPriceDetailsList() {
        //prepare
        List<String> productTypesPriceDetailsList = new ArrayList<>();
        productTypesPriceDetailsList.add("Buy T-Shirt/Apparel €15.95 EUR or more");
        productTypesPriceDetailsList.add("Pre-order Digital Album $1.01 AUD or more");
        productTypesPriceDetailsList.add("Pre-order Record/Vinyl £10.03 GBP or more");
        productTypesPriceDetailsList.add("Pre-order Record/Vinyl $12.03 USD or more");
        List<String> expectedVinylPriceDetailsList = new ArrayList<>(List.of(productTypesPriceDetailsList.get(2), productTypesPriceDetailsList.get(3)));
        //when
        List<String> actualVinylPriceDetailsList = bandcampParser.getVinylTypeFromProductsTypesPriceDetailsList(productTypesPriceDetailsList);
        //then
        assertEquals(expectedVinylPriceDetailsList, actualVinylPriceDetailsList);
    }

    @Test
    @DisplayName("Sets raw offer's price and currency to lowest price number")
    void setRawOfferPriceAndCurrencyToLowestPriceFromList() {
        //prepare
        List<String> vinylPriceDetailsList = new ArrayList<>();
        vinylPriceDetailsList.add("Buy Record/Vinyl €15.95 EUR or more");
        vinylPriceDetailsList.add("Pre-order Record/Vinyl $14.01 AUD");
        vinylPriceDetailsList.add("Pre-order Record/Vinyl £10.03 GBP or more");
        RawOffer rawOfferToChange = new RawOffer();
        double expectedPrice = 10.03;
        Optional<Currency> expectedCurrency = Optional.of(Currency.GBP);
        //when
        bandcampParser.setRawOfferPriceAndCurrencyToLowestPriceFromList(rawOfferToChange, vinylPriceDetailsList);
        //then
        assertEquals(expectedPrice, rawOfferToChange.getPrice());
        assertEquals(expectedCurrency, rawOfferToChange.getCurrency());
    }

    @Test
    @DisplayName("Sets raw offer's price and currency to default when list is empty")
    void setRawOfferPriceAndCurrencyToLowestPriceFromListEmptyList() {//prepare
        List<String> vinylPriceDetailsList = new ArrayList<>();
        RawOffer rawOfferToChange = new RawOffer();
        //when
        bandcampParser.setRawOfferPriceAndCurrencyToLowestPriceFromList(rawOfferToChange, vinylPriceDetailsList);
        //then
        assertEquals(0, rawOfferToChange.getPrice());
        assertNull(rawOfferToChange.getCurrency());
    }

    @Test
    @DisplayName("Gets raw offer from valid link")
    void getRawOfferFromOfferLink() throws IOException, ParseException {
        //prepare
        URL firstPageUrl = new URL("https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=0&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com");
        JSONArray items = bandcampParser.getItemsArrayByUrl(jsonParser, firstPageUrl);
        String offerLink = bandcampParser.getOfferLinkFromItem((JSONObject) items.get(0));
        //when
        RawOffer rawOffer = bandcampParser.getRawOfferFromOfferLink(offerLink);
        //then
        assertTrue(bandcampParser.isValid(rawOffer));
    }

}