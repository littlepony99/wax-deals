package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.PriceUtils;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class CloneNlParser implements VinylParser {

    //TODO: Tests for PriceUtils
    protected static final String BASE_LINK = "https://clone.nl";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/genres";
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK;
    private static final String GENRES_SELECTOR = "DIV > H1:contains(Genres) + P > A[href*=genre/]";
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "UL.pagination > LI > A";

    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("&page=([0-9]+)");
    private static final String OFFER_LIST_SELECTOR = "DIV.content-container > DIV.main-content";
    private static final String HIGH_RES_IMAGE_LINK_SELECTOR = "DIV.release IMG";
    private static final String OFFER_LINK_SELECTOR = "DIV.coverimage > A";
    private static final String ARTIST_SELECTOR = "DIV.description > H2 > A";
    private static final String RELEASE_SELECTOR = "DIV.description > H2 + H3 > A";
    private static final String VINYL_GENRES_SELECTOR = "DIV.tagsbuttons > A.label";
    private static final String PRICE_DETAILS_SELECTOR = "DIV.release TABLE.availability A.addtocart";
    private static final String ONE_VINYL_SELECTOR = "DIV.release";
    private static final int SHOP_ID = 4;

    private final AtomicInteger documentCounter = new AtomicInteger(0);

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> allGenres = getAllGenreLinks();
        Set<String> pageLinks = getAllPagesByGenres(allGenres);
        log.info("got page links {'pageLinks':{}}", pageLinks.size());

        Set<RawOffer> rawOffersSet = readOffersFromAllPages(pageLinks);

        log.info("Read {} rawOffers from all offer pages", rawOffersSet.size());
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        log.debug("Resulting list of vinyls from www.juno.co.uk is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    Set<String> getAllPagesByGenre(String genreLink) {
        var allPages = getDocument(genreLink)
                .stream()
                .flatMap(document -> document.select(PRELIMINARY_PAGE_LINK_SELECTOR).stream())
                .filter(supposedPageLink -> supposedPageLink.text().matches("[0-9]+"))
                .map(pageLink -> pageLink.attr("href"))
                .collect(toSet());
        if (allPages.isEmpty()) {
            return Set.of(genreLink);
        }
        return getAllPageLinksSet(allPages);
    }

    Set<String> getAllPagesByGenres(Set<String> allGenres) {
        return allGenres
                .stream()
                .map(link -> link.replace("/all/","/instock/"))
                .flatMap(genre -> getAllPagesByGenre(genre).stream())
                .collect(toSet());
    }

    boolean isValid(RawOffer rawOffer) {
        boolean isValid = rawOffer.getPrice() != 0d
                && rawOffer.getCurrency().isPresent()
                && !rawOffer.getRelease().isEmpty()
                && rawOffer.getOfferLink() != null;
        if (!isValid) {
            log.error("Raw offer isn't valid {'rawOffer':{}}", rawOffer);
        }
        return isValid;
    }

    Set<RawOffer> readOffersFromAllPages(Set<String> pageLinks) {
        var offerLinks = pageLinks
                .stream()
                .flatMap(link -> this.getDocument(link).stream())
                .map(document -> document.select(OFFER_LIST_SELECTOR))
                .flatMap(offersList -> offersList.select(ONE_VINYL_SELECTOR).stream())
                .map(this::getRawOfferFromElement)
                .filter(this::isValid)
                .collect(toSet());
        log.debug("Resulting set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    Set<String> getAllPageLinksSet(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        var pageLinkPattern = pageLinks.iterator().next();
        log.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        var fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> pageLinkPattern.replaceAll(PAGE_NUMBER_PATTERN.toString(), "&page=" + pageNumber))
                        .collect(toSet());
        log.debug("Resulting set of page links (with no gaps in sequence) is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    Set<String> getAllGenreLinks() {
        //TODO: "/all/tags" can contains vinyls too
        var startDocument = getDocument(START_PAGE_LINK);
        var allGenresLinks = startDocument
                .stream()
                .flatMap(document -> document.select(GENRES_SELECTOR).stream())
                .map(link -> BASE_LINK + link.attr("href"))
                .collect(toSet());
        log.debug("Got genres links {'allGenresLinks':{}}", allGenresLinks);
        log.info("Got genres links totally: {'allGenresLinks':{}}", allGenresLinks.size());
        return allGenresLinks;
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

    public RawOffer getRawOfferFromElement(Element releaseElement) {
        var imageLink = getHighResImageLinkFromDocument(releaseElement);
        var offerLink = getOfferLinkFromDocument(releaseElement);
        var price = getPriceFromDocument(releaseElement);
        var priceCurrency = getOptionalCurrencyFromDocument(releaseElement);
        var artist = getArtistFromDocument(releaseElement);
        var release = getReleaseFromDocument(releaseElement);
        var genre = getGenreFromDocument(releaseElement);
        var catalogNumber = getCatNumberFromDocument(releaseElement);
        var inStock = getInStockInfoFromDocument(releaseElement);

        var rawOffer = new RawOffer();
        rawOffer.setShopId(SHOP_ID);
        rawOffer.setRelease(release);
        rawOffer.setArtist(artist);
        rawOffer.setPrice(price);
        rawOffer.setCurrency(priceCurrency);
        rawOffer.setOfferLink(offerLink);
        rawOffer.setImageLink(imageLink);
        rawOffer.setGenre(genre);
        rawOffer.setCatNumber(catalogNumber);
        rawOffer.setInStock(inStock);
        log.debug("New Raw Offer is Formed {'rawOffer': {}}", rawOffer);
        return rawOffer;
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        return new RawOffer();
    }

    String getGenreFromDocument(Element document) {
        return document.select(VINYL_GENRES_SELECTOR).text();
    }

    String getReleaseFromDocument(Element document) {
        return document.select(RELEASE_SELECTOR).text();
    }

    String getArtistFromDocument(Element document) {
        return document.select(ARTIST_SELECTOR).text();
    }

    String getCatNumberFromDocument(Element document) {
        return document.select("span[itemprop=catalogNumber]").text();
    }

    Boolean getInStockInfoFromDocument(Element document) {
        boolean inStock = true;
        String inStockText = document.getElementsByClass("col-xs-2 status").text();
        if ("out of stock".contains(inStockText)){
            inStock = false;
        }
        return inStock;
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
        var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
        if (pricesBlock.isEmpty()) {
            return Optional.empty();
        }
        var fullPriceDetails = pricesBlock.get(0);
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
        return PriceUtils.getCurrencyFromString(fullPriceDetails);
    }

    double getPriceFromDocument(Element document) {
        var pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
        if (pricesBlock.isEmpty()) {
            return 0d;
        }
        var fullPriceDetails = pricesBlock.get(0);
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
        return PriceUtils.getPriceFromString(fullPriceDetails);
    }

    String getHighResImageLinkFromDocument(Element document) {
        return document.select(HIGH_RES_IMAGE_LINK_SELECTOR).attr("src");
    }

    String getOfferLinkFromDocument(Element document) {
        return BASE_LINK + document.select(OFFER_LINK_SELECTOR).attr("href");
    }

    Optional<Document> getDocument(String url) {
        try {
            documentCounter.addAndGet(1);
            log.info("Document {} was read", documentCounter.get());
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}, 'totalDocuments':{}}", url, documentCounter.get(), e);
            return Optional.empty();
        }
    }
}
