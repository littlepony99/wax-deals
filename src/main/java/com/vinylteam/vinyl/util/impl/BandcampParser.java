package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.ParserConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BandcampParser extends VinylParser {

    private static final String BASE_LINK = "https://bandcamp.com/api/discover/3/get_web?g=all&s=top&p=&gn=0&f=vinyl&w=0&lo=true&lo_action_url=https%3A%2F%2Fbandcamp.com";
    private static final String BASE_OFFER_LINK = "https://.bandcamp.com/album/";
    private static final int SHOP_ID = 3;

    private static final String RELEASE_SELECTOR = "DIV#name-section>H2.trackTitle";
    private static final String ARTIST_SELECTOR = "DIV#name-section>H3>SPAN>A";
    private static final String HIGH_RES_IMAGE_SELECTOR = "DIV#tralbumArt>A.popupImage>IMG";
    //Bc there is no clear way to parse price of exclusively vinyls, we parse into set with price of digital albums and merch, too
    //And then choose only price of vinyl
    private static final String PRODUCT_TYPE_PRICE_DETAILS_SELECTOR = "H4.compound-button.main-button";

    private final ParserConfiguration offerPageParserConf = ParserConfiguration
            .builder()
            .artistSelector(ARTIST_SELECTOR)
            .releaseSelector(RELEASE_SELECTOR)
            .highResolutionImageSelector(HIGH_RES_IMAGE_SELECTOR)
            .build();

    private final DetailedVinylParser offerPageParser = new DetailedVinylParserImpl(offerPageParserConf);

    JSONArray getItemsArrayByUrl(JSONParser jsonParser, URL url) throws IOException, ParseException {
        try {
            URLConnection request = url.openConnection();
            request.connect();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            return (JSONArray) jsonObject.get("items");
        } catch (IOException e) {
            log.error("Failed to open connection or connect request by url in shop by id {'url':{}, 'shopId':{}}", url, SHOP_ID);
            return null;
        }
    }

    String getOfferLinkFromItem(JSONObject item) {
        StringBuilder offerLinkStringBuilder = new StringBuilder();
        offerLinkStringBuilder.append(BASE_OFFER_LINK);
        if (item.get("url_hints") == null) {
            log.warn("url hints in item is null in shop by id {'item':{}, 'shopId':{}}", item, SHOP_ID);
            return offerLinkStringBuilder.toString();
        }
        JSONObject urlHints = (JSONObject) item.get("url_hints");
        if (urlHints.get("subdomain") == null || urlHints.get("slug") == null) {
            log.warn("subdomain and/or slug in url hints in item is null in shop by id {'item':{}, 'shopId':{}}", item, SHOP_ID);
            return offerLinkStringBuilder.toString();
        }
        String subdomain = urlHints.get("subdomain").toString();
        offerLinkStringBuilder.insert(8, subdomain);
        offerLinkStringBuilder.append(urlHints.get("slug"));
        return offerLinkStringBuilder.toString();
    }

    public Set<String> getOfferLinks() {
        int i = 0;
        boolean isCatalogFinished = true;
        Set<String> offerLinks = new HashSet<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BASE_LINK);
        JSONParser jsonParser = new JSONParser();
        do {
            stringBuilder.insert(58, i);
            URL jsonUrl;
            try {
                jsonUrl = new URL(stringBuilder.toString());
            } catch (MalformedURLException e) {
                log.error("Malformed url from string in shop by id {'stringForUrl':{}, 'shopId':{}}", stringBuilder.toString(), SHOP_ID);
                break;
            }
            JSONArray items;
            try {
                items = getItemsArrayByUrl(jsonParser, jsonUrl);
            } catch (IOException | ParseException e) {
                log.error("Something went wrong when trying to obtain items json array by url in shop by id {'url':{}, 'shopId':{}}", jsonUrl, SHOP_ID);
                break;
            }
            log.debug("Obtained array of items by url in shop by id {'itemsSize':{}, 'shopId':{}}", items.size(), SHOP_ID);
            if (items != null) {
                if (items.isEmpty()) {
                    isCatalogFinished = false;
                } else {
                    for (Object item : items) {
                        JSONObject itemAsJsonObject = (JSONObject) item;
                        String offerLink = getOfferLinkFromItem(itemAsJsonObject);
                        offerLinks.add(offerLink);
                        log.debug("Added offer link to set of offer links in shop by id {'offerLink':{}, 'shopId':{}}", offerLink, SHOP_ID);
                    }
                }
            } else {
                log.error("can't connect by this URL, finishing collecting offer links for shop by id {'url':{}, 'shopId':{}}", jsonUrl, SHOP_ID);
                isCatalogFinished = false;
            }
            stringBuilder.delete(58, 58 + String.valueOf(i).length());
            i++;
        } while (isCatalogFinished);
        log.info("Finished collecting offer links in shop by id {'offerLinksSize':{}, 'shopId':{}}", offerLinks.size(), SHOP_ID);
        return offerLinks;
    }

    List<String> getProductTypesPriceDetails(Document document) {
        return document.select(PRODUCT_TYPE_PRICE_DETAILS_SELECTOR).eachText();
    }

    List<String> getVinylTypeFromProductsTypesPriceDetailsList(List<String> productTypesAndPriceDetails) {
        List<String> vinylPriceDetailsList = new ArrayList<>();
        for (String productPriceDetails : productTypesAndPriceDetails) {
            if (productPriceDetails.contains("Vinyl")) {
                vinylPriceDetailsList.add(productPriceDetails);
            }
        }
        return vinylPriceDetailsList;
    }

    void setRawOfferPriceAndCurrencyToLowestPriceFromList(RawOffer rawOffer, List<String> vinylPriceDetailsList) {
        for (String priceDetails : vinylPriceDetailsList) {
            log.debug("Trying to get price and currency from price details from the offer link {'priceDetails':{}, 'offerLink':{}, 'shopId':{}}",
                    priceDetails, rawOffer.getOfferLink(), SHOP_ID);
            try {
                String priceAsString;
                String currencyAsString;
                if (priceDetails.contains(" or more")) {
                    priceAsString = priceDetails.substring(priceDetails.indexOf("/Vinyl") + 8, priceDetails.indexOf(" or") - 4);
                    currencyAsString = priceDetails.substring(priceDetails.indexOf(priceAsString) + priceAsString.length() + 1, priceDetails.indexOf(" or more"));
                } else {
                    priceAsString = priceDetails.substring(priceDetails.indexOf("/Vinyl") + 8, priceDetails.length() - 5);
                    currencyAsString = priceDetails.substring(priceDetails.indexOf(priceAsString) + priceAsString.length() + 1);
                }
                double currentPrice = Double.parseDouble(priceAsString);
                if (rawOffer.getPrice() == 0 || currentPrice < rawOffer.getPrice()) {
                    rawOffer.setPrice(currentPrice);
                    rawOffer.setCurrency(Currency.getCurrency(currencyAsString));
                }
            }catch (StringIndexOutOfBoundsException e) {
                log.error("In raw offer error {'rawOffer':{}}", rawOffer, e);
            }
        }
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        Optional<Document> optionalDocument = getDocument(offerLink);
        if (optionalDocument.isEmpty()) {
            log.error("Can`t get document by: {'offerLink':{}}", offerLink);
            return new RawOffer();
        } else {
            Document document = optionalDocument.get();
            String imageLink = offerPageParser.getHighResImageLinkFromDocument(document);
            String artist = offerPageParser.getArtistFromDocument(document);
            String release = offerPageParser.getReleaseFromDocument(document);
            List<String> productTypesPriceDetails = getProductTypesPriceDetails(document);
            List<String> vinylPriceDetailsList = getVinylTypeFromProductsTypesPriceDetailsList(productTypesPriceDetails);

            RawOffer rawOffer = new RawOffer();
            rawOffer.setShopId(SHOP_ID);
            rawOffer.setOfferLink(offerLink);
            rawOffer.setRelease(release);
            rawOffer.setArtist(artist);
            setRawOfferPriceAndCurrencyToLowestPriceFromList(rawOffer, vinylPriceDetailsList);
            rawOffer.setImageLink(imageLink);
            rawOffer.setInStock(true);
            log.debug("New Raw Offer is Formed {'rawOffer': {}}", rawOffer);
            return rawOffer;
        }
    }

    @Override
    protected long getShopId() {
        return SHOP_ID;
    }

    @Override
    protected List<RawOffer> getRawOffersList() {
        return getOfferLinks()
                .stream()
                .map(this::getRawOfferFromOfferLink)
                .filter(rawOffer -> isValid(rawOffer))
                .collect(Collectors.toList());
    }

}
