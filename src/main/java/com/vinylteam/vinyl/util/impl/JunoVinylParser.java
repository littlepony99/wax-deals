package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.PriceUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class JunoVinylParser extends VinylParser {

    private static final String BASE_LINK = "https://www.juno.co.uk";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/all/";
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK + "back-cat/2/?media_type=vinyl";

    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "a[href^=" + CATALOG_ROOT_LINK + "]";
    private static final String SELECTOR_OFFER_LIST = "div.product-list";
    private static final String SELECTOR_OFFER_ANCHORS = "div.dv-item > div.row > div.order-1 > a";

    private static final String BASE_SELECTOR_OFFER_DETAILS = "div.juno-page.container-fluid.product-info > div.row";
    private static final String BASE_SELECTOR_OFFER_TEXT_DETAILS = BASE_SELECTOR_OFFER_DETAILS + " > div.order-1 > div.juno-section";
    private static final String SELECTOR_RELEASE = BASE_SELECTOR_OFFER_TEXT_DETAILS + " > div.row.gutters-sm + div.row.gutters-sm > div.col-12 > div.product-title > h2 > span";
    private static final String SELECTOR_ARTIST = BASE_SELECTOR_OFFER_TEXT_DETAILS + " > div.row.gutters-sm + div.row.gutters-sm > div.col-12 > div.product-artist > h2 > a";
    private static final String SELECTOR_IN_STOCK = "em[itemprop=\"availability\"]";
    private static final String SELECTOR_PRICE_DETAILS = BASE_SELECTOR_OFFER_TEXT_DETAILS + " > div.row.no-gutters.mt-2 > div.col-12.col-sm-7 > div.product-actions > div.product-pricing > span";
    private static final String SELECTOR_CATALOGUE_NUMBER_CONTAINER = BASE_SELECTOR_OFFER_TEXT_DETAILS + " > div.row.no-gutters.mt-2 > div.col-12.col-sm-5 > div.product-meta.mb-2";
    private static final String SELECTOR_GENRE = BASE_SELECTOR_OFFER_TEXT_DETAILS + " > div.row.no-gutters.mt-2 > div.col-12.col-sm-5 > div.product-meta.mb-2 > strong:contains(Genre:) + a";
    private static final String SELECTOR_SCRIPT_HIGH_RES_IMAGE_LINK = BASE_SELECTOR_OFFER_DETAILS + " > div.order-2 > div#artwork-carousel > div#artwork-carousel-jwc > div.jw-scroller.jws-transform > div.jw-page + div.jw-page > img";

    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("/([0-9]+)/");
    private static final int SHOP_ID = 2;

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> pageLinks = getAllLinksFromStartPage();
        log.info("got page links {'pageLinks':{}}", pageLinks.size());
        Set<String> offerLinks = readOfferLinksFromAllPages(pageLinks);
        log.info("got offer links {'offerLinks':{}}", offerLinks.size());
        Set<RawOffer> rawOffersSet = getValidRawOffersFromAllOfferLinks(offerLinks);
        log.info("read {} rawOffers from all offer links", rawOffersSet.size());
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        log.debug("Resulting list of vinyls from www.juno.co.uk is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    Set<String> getAllLinksFromStartPage() {
        var startDocument = getDocument(START_PAGE_LINK);
        var pageLinksShownFromStartList = startDocument
                .stream()
                .flatMap(document -> document.select(PRELIMINARY_PAGE_LINK_SELECTOR).stream())
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        return getAllPageLinksSet(pageLinksShownFromStartList);
    }

    Set<String> getAllPageLinksSet(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        log.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> START_PAGE_LINK.replaceAll(PAGE_NUMBER_PATTERN.toString(), "/" + pageNumber + "/"))
                        .collect(toSet());
        log.debug("Resulting set of page links (with no gaps in sequence) is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    int countPageLinks(Set<String> pageLinks) {
        return pageLinks
                .stream()
                .map(PAGE_NUMBER_PATTERN::matcher)
                .filter(Matcher::find)
                .map(pageLinkMatcher -> pageLinkMatcher.group(1))
                .map(Integer::parseInt)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    Set<String> readOfferLinksFromAllPages(Set<String> pageLinks) {
        var offerLinks = pageLinks
                .stream()
                .map(this::getDocument)
                .filter(Optional::isPresent)
                .map(document -> document.get().select(SELECTOR_OFFER_LIST))
                .flatMap(offerAnchorsList -> offerAnchorsList.select(SELECTOR_OFFER_ANCHORS).stream())
                .map(offerLink -> (BASE_LINK + offerLink.attr("href")))
                .collect(toSet());
        log.debug("Resulting set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    Set<RawOffer> getValidRawOffersFromAllOfferLinks(Set<String> offerLinks) {
        var rawOffers = offerLinks
                .stream()
                .map(this::getRawOfferFromOfferLink)
                .filter(this::isValid)
                .collect(toSet());
        log.debug("Resulting set of raw offers is {'rawOffers':{}}", rawOffers);
        return rawOffers;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        Optional<Document> optionalDocument = getDocument(offerLink);
        if (optionalDocument.isEmpty()) {
            log.error("Can`t get document by: {'offerLink':{}}", offerLink);
            return new RawOffer();
        } else {
            Document document = optionalDocument.get();
            var imageLink = getHighResImageLinkFromDocument(document);
            var price = getPriceFromDocument(document);
            var priceCurrency = getOptionalCurrencyFromDocument(document);
            var artist = getArtistFromDocument(document);
            var release = getReleaseFromDocument(document);
            var genre = getGenreFromDocument(document);
            var catNumber = getCatNumberFromDocument(document);
            var inStock = getInStockFromDocument(document);

            var rawOffer = new RawOffer();
            rawOffer.setShopId(SHOP_ID);
            rawOffer.setRelease(release);
            rawOffer.setArtist(artist);
            rawOffer.setPrice(price);
            rawOffer.setCurrency(priceCurrency);
            rawOffer.setOfferLink(offerLink);
            rawOffer.setImageLink(imageLink);
            rawOffer.setGenre(genre);
            rawOffer.setCatNumber(catNumber);
            rawOffer.setInStock(inStock);
            return rawOffer;
        }
    }

    String getReleaseFromDocument(Document document) {
        String release = document.select(SELECTOR_RELEASE).text();
        if (release.isEmpty()) {
            log.warn("Release from link is empty {'link':{}}", document.location());
        }
        log.debug("Got release from page by offer link {'release':{}, 'offerLink':{}}", release, document.location());
        return release;
    }

    String getArtistFromDocument(Document document) {
        String artist = document.select(SELECTOR_ARTIST).text();
        if (artist.isEmpty()) {
            log.warn("Artist from link is empty, returning default value {'link':{}}", document.location());
            artist = "Various Artists";
        }
        log.debug("Got artist from page by offer link {'artist':{}, 'offerLink':{}}", artist, document.location());
        return artist;
    }

    Double getPriceFromDocument(Document document) {
        String fullPriceDetails = document.select(SELECTOR_PRICE_DETAILS).text();
        String priceDetails = fullPriceDetails;
        if (fullPriceDetails.contains(" ")) {
            priceDetails = fullPriceDetails.substring(fullPriceDetails.lastIndexOf(" ") + 1);
        }
        return PriceUtils.getPriceFromString(priceDetails);
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Document document) {
        String fullPriceDetails = document.select(SELECTOR_PRICE_DETAILS).text();
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.location());
        if (fullPriceDetails.contains(" ")) {
            fullPriceDetails = fullPriceDetails.substring(fullPriceDetails.lastIndexOf(" ") + 1);
        }
        return PriceUtils.getCurrencyFromString(fullPriceDetails);
    }

    String getHighResImageLinkFromDocument(Document document) {
        String highResImageLink = document.select(SELECTOR_SCRIPT_HIGH_RES_IMAGE_LINK).attr("data-src-full");
        if (highResImageLink.length() > 0) {
            log.debug("Got high resolution image link from page by offer link {'highResImageLink':{}, 'offerLink':{}}", highResImageLink, document.location());
        } else {
            log.warn("Can't find image link from page by offer link, returning default {'offerLink':{}}", document.location());
            highResImageLink = "img/goods/no_image.jpg";
        }
        return highResImageLink;
    }

    String getGenreFromDocument(Document document) {
        String genre = document.select(SELECTOR_GENRE).text();
        if (genre.isEmpty()) {
            log.warn("Genre from link is empty {'link':{}}", document.location());
        }
        log.debug("Got genre from page by offer link {'genre':{}, 'offerLink':{}}", genre, document.location());
        return genre;
    }

    String getCatNumberFromDocument(Document document) {
        Element catNumberContainer = document.select(SELECTOR_CATALOGUE_NUMBER_CONTAINER).first();
        log.debug("Got the element that contains catNumber from page by offer link {'catNumberContainerText':{}, 'offerLink':{}}", catNumberContainer, document.location());
        if (catNumberContainer != null) {
            String catNumberContainerText = catNumberContainer.text();
            log.debug("Got text from the element that contains catNumber from page by offer link {'catNumberContainerText':{}, 'catNumberContainer':{}, 'offerLink':{}}",
                    catNumberContainerText, catNumberContainer, document.location());
            int catNumberBeginIndex = catNumberContainerText.indexOf("Cat: ") + 5;
            int catNumberEndIndex = catNumberContainerText.indexOf(" Released:");
            if (catNumberBeginIndex != -1 && catNumberEndIndex != 1) {
                String catNumber = catNumberContainerText.substring(catNumberBeginIndex, catNumberEndIndex);
                log.debug("Got catNumber from page by offer link {'catNumber':{}, 'offerLink':{}}", catNumber, document.location());
                return catNumber;
            } else {
                log.error("Text from element containing catNumber from offer link doesn't contain \"Cat: \" or \"Release\", returning empty catNumber " +
                        "{'catNumberContainerText':{}, 'offerLink': {}}", catNumberContainerText, document.location());
            }
        } else {
            log.error("Couldn't get element containing catNumber from offer link, returning empty catNumber {'offerLink': {}}", document.location());
        }
        return "";
    }

    boolean getInStockFromDocument(Document document) {
        String availabilityStatus = document.select(SELECTOR_IN_STOCK).text();
        if (!availabilityStatus.isEmpty()) {
            if (availabilityStatus.contains("in stock")) {
                log.debug("Offer is in stock by offer link where availability status is {'availability':{}, 'offerLink':{}}", availabilityStatus, document.location());
                return true;
            } else {
                log.debug("Offer is not in stock by offer link where availability status is {'availability':{}, 'offerLink':{}}", availabilityStatus, document.location());
                return false;
            }
        }
        log.warn("Can't find availability status by offer link, returning false {'offerLink':{}}", document.location());
        return false;
    }

    @Override
    public long getShopId() {
        return SHOP_ID;
    }

}
