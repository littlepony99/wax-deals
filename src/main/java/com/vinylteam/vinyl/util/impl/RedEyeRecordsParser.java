package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.ParserConfiguration;
import com.vinylteam.vinyl.util.PriceUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedEyeRecordsParser extends VinylParser {

    private static final String BASE_LINK = "https://www.redeyerecords.co.uk/custom-view/page-1";
    private static final String PAGE_LINK_ELEMENTS_SELECTOR = "SELECT#pageNumber>OPTION[value!='1']";
    private static final String VINYL_BLOCK_SELECTOR = "DIV.releaseGrid";
    private static final String VINYL_BLOCK_STATUS_SELECTOR = "DIV.right>DIV.under>DIV.status";
    private static final String OFFER_LINK_SELECTOR = "DIV.right>DIV.under>A";

    private static final String RELEASE_AND_ARTIST_SELECTOR = "DIV.productInfo H1.artist";
    private static final String STATUS_SELECTOR = "DIV.productInfo DIV.status";
    private static final String CATNUMBER_SELECTOR = "DIV.productInfo DIV.details>P:last-child>SPAN";
    private static final String PRICE_DETAILS_SELECTOR = "DIV.productInfo DIV.price";
    private static final String IMG_SELECTOR = "DIV.productInfo DIV.image>IMG";
    private static final String IN_STOCK_MARKER = "In Stock";
    private static final int SHOP_ID = 9;

    private final RedEyeRecordsDetailedParser oneOfferParser = new RedEyeRecordsDetailedParser(createParserConfiguration());

    Set<String> getAllPages() {
        Set<String> pageLinks = getDocument(BASE_LINK)
                .stream()
                .map(document -> document.select(PAGE_LINK_ELEMENTS_SELECTOR))
                .map(pageNumberOption -> pageNumberOption.attr("value"))
                .collect(Collectors.toSet());
        pageLinks.add(BASE_LINK);
        return pageLinks;
    }

    Set<String> getAllOfferLinks(Set<String> pages) {
        return pages
                .stream()
                .flatMap(page -> this.getDocument(page).stream())
                .flatMap(document -> document.select(VINYL_BLOCK_SELECTOR).stream())
                .filter(vinylBlock -> IN_STOCK_MARKER.equals(vinylBlock.select(VINYL_BLOCK_STATUS_SELECTOR).text()))
                .map(vinylBlock -> vinylBlock.select(OFFER_LINK_SELECTOR).attr("href"))
                .collect(Collectors.toSet());
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        Optional<Document> optionalDocument = getDocument(offerLink);
        if (optionalDocument.isEmpty()) {
            log.error("Can`t get document by: {'offerLink':{}}", offerLink);
            return new RawOffer();
        } else {
            Document document = optionalDocument.get();
            String imageLink = oneOfferParser.getHighResImageLinkFromDocument(document);
            String priceDetails = oneOfferParser.getPriceDetailsFromDocumentAsString(document, PRICE_DETAILS_SELECTOR);
            double price = oneOfferParser.getPriceFromPriceDetails(priceDetails);
            Optional<Currency> priceCurrency = oneOfferParser.getCurrencyFromPriceDetails(priceDetails);
            String artistAndRelease = oneOfferParser.getArtistAndReleaseFromDocument(document, RELEASE_AND_ARTIST_SELECTOR);
            String artist = oneOfferParser.getArtistFromArtistAndRelease(artistAndRelease);
            String release = oneOfferParser.getReleaseFromArtistAndRelease(artistAndRelease);
            String catNumber = oneOfferParser.getCatNumberFromDocument(document);
            boolean inStock = oneOfferParser.getInStockInfoFromDocument(document);

            RawOffer rawOffer = new RawOffer();
            rawOffer.setShopId(SHOP_ID);
            rawOffer.setRelease(release);
            rawOffer.setArtist(artist);
            rawOffer.setPrice(price);
            rawOffer.setCurrency(priceCurrency);
            rawOffer.setOfferLink(offerLink);
            rawOffer.setImageLink(imageLink);
            rawOffer.setGenre("");
            rawOffer.setCatNumber(catNumber);
            rawOffer.setInStock(inStock);
            return rawOffer;
        }
    }

    @Override
    public long getShopId() {
        return SHOP_ID;
    }

    protected List<RawOffer> getRawOffersListFromOfferLinksSet(Set<String> offerLinks) {
        return offerLinks
                .stream()
                .map(this::getRawOfferFromOfferLink)
                .filter(this::isValid)
                .collect(Collectors.toList());
    }

    @Override
    public List<RawOffer> getRawOffersList() {
        return getRawOffersListFromOfferLinksSet(getAllOfferLinks(getAllPages()));
    }

    private class RedEyeRecordsDetailedParser extends DetailedVinylParserImpl {

        public RedEyeRecordsDetailedParser(ParserConfiguration parserConfiguration) {
            super(parserConfiguration);
        }

        String getPriceDetailsFromDocumentAsString(Element document, String selector) {
            return document.select(selector).text();
        }

        String getArtistAndReleaseFromDocument(Element document, String selector) {
            return document.select(selector).text();
        }

        String getArtistFromArtistAndRelease(String artistAndRelease) {
            String artist;
            if (artistAndRelease.indexOf("-", 2) == -1) {
                log.warn("Artist from link is empty");
                artist = "Various Artists";
            } else {
                int firstIndexOfDash = artistAndRelease.indexOf("-");
                if (artistAndRelease.startsWith(" -") || artistAndRelease.startsWith("-")) {
                    int secondIndexOfDash = artistAndRelease.indexOf("-", firstIndexOfDash + 1);
                    artist = artistAndRelease.substring(firstIndexOfDash + 1, secondIndexOfDash).trim();
                } else {
                    artist = artistAndRelease.substring(0, firstIndexOfDash).trim();
                }
                log.debug("Got artist from page by offer link {'artist':{}}", artist);
            }
            return artist;
        }

        String getReleaseFromArtistAndRelease(String artistAndRelease) {
            String release;
            if (artistAndRelease.indexOf("-", 2) == -1) {
                release = artistAndRelease.substring(artistAndRelease.indexOf("-") + 1).trim();
            } else {
                int firstIndexOfDash = artistAndRelease.indexOf("-");
                if (artistAndRelease.startsWith(" -") || artistAndRelease.startsWith("-")) {
                    int secondIndexOfDash = artistAndRelease.indexOf("-", firstIndexOfDash + 1);
                    release = artistAndRelease.substring(secondIndexOfDash + 1).trim();
                } else {
                    release = artistAndRelease.substring(firstIndexOfDash + 1).trim();
                }
                log.debug("Got release from page by offer link {'artist':{}}", release);
            }
            return release;
        }

        double getPriceFromPriceDetails(String priceDetails) {
            String priceAsString;
            if (priceDetails.contains("(") && priceDetails.contains(")")) {
                int indexOfBrace = priceDetails.indexOf("(");
                int indexOfInc = priceDetails.indexOf("inc");
                priceAsString = priceDetails.substring(indexOfBrace + 2, indexOfInc).trim();
            } else {
                priceAsString = priceDetails.substring(1).trim();
            }
            double price = Double.parseDouble(priceAsString);
            log.debug("Got price from price details {'price':{}, 'priceDetails':{}}", price, priceDetails);
            return price;
        }

        Optional<Currency> getCurrencyFromPriceDetails(String priceDetails) {
            return PriceUtils.getCurrencyFromString(priceDetails);
        }
    }

    private static ParserConfiguration createParserConfiguration() {
        return ParserConfiguration.builder()
                .catalogNumberSelector(CATNUMBER_SELECTOR)
                .highResolutionImageSelector(IMG_SELECTOR)
                .inStockMarkerSelector(STATUS_SELECTOR)
                .inStockMarker(IN_STOCK_MARKER)
                .build();
    }

}
