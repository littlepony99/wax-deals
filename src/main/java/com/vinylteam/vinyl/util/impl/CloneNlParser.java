package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.ParserConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class CloneNlParser extends VinylParser {

    static final String BASE_LINK = "https://clone.nl";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/genres";
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK;
    private static final String GENRES_SELECTOR = "DIV > H1:contains(Genres) + P > A[href*=genre/]";
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "UL.pagination > LI > A";

    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("&page=([0-9]+)");
    private static final String OFFER_LIST_SELECTOR = "DIV.content-container > DIV.main-content";

    private static final String ONE_VINYL_SELECTOR = "DIV.release";
    private static final String ONE_VINYL_FROM_ONE_PAGE_SELECTOR = "DIV.musicrelease";
    private static final int SHOP_ID = 4;
    public static final String IN_STOCK = "in stock";


    private final ParserConfiguration listParserConfiguration = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV.release IMG")
            .offerLinkSelector("DIV.coverimage > A")
            .artistSelector("DIV.description > H2 > A")
            .releaseSelector("DIV.description > H2 + H3 > A")
            .vinylGenresSelector("DIV.tagsbuttons > A.label")
            .priceDetailsSelector("DIV.release TABLE.availability A.addtocart")
            .catalogNumberSelector("span[itemprop=catalogNumber]")
            .inStockMarker(IN_STOCK)
            .inStockMarkerSelector(".col-xs-2.status")
            .build();

    private final ParserConfiguration onePageParserConfiguration = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV.release IMG")
            .offerLinkSelector("DIV#content DIV.record.block DIV.linesmall > A[href*=label] + A")
            .artistSelector("H1[itemprop=author] > A")
            .releaseSelector("H1[itemprop=author] + H2")
            .vinylGenresSelector("DIV.tagsbuttons > A.label")
            .priceDetailsSelector("DIV.release TABLE.availability A.addtocart")
            .catalogNumberSelector("span[itemprop=catalogNumber]")
            .inStockMarker(IN_STOCK)
            .inStockMarkerSelector(".col-xs-2.status")
            .build();

    @Getter
    private final DetailedVinylParser listParser = new DetailedVinylParserImpl(listParserConfiguration);

    @Getter
    private final DetailedVinylParser onePageParser = new DetailedVinylParserImpl(onePageParserConfiguration);

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

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer offer = getDocument(offerLink)
                .stream()
                .flatMap(doc -> doc.select(ONE_VINYL_FROM_ONE_PAGE_SELECTOR).stream())
                .map(oneVinyl -> getRawOfferFromElement(oneVinyl, onePageParser))
                .findFirst()
                .orElse(new RawOffer());
        offer.setOfferLink(offerLink);
        return offer;
    }

    Set<String> getAllPagesByGenre(String genreLink) {
        Set<String> allPages = getDocument(genreLink)
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
                .map(link -> link.replace("/all/", "/instock/"))
                .flatMap(genre -> getAllPagesByGenre(genre).stream())
                .collect(toSet());
    }

    Set<RawOffer> readOffersFromAllPages(Set<String> pageLinks) {
        Set<RawOffer> offerLinks = pageLinks
                .stream()
                .flatMap(link -> this.getDocument(link).stream())
                .map(document -> document.select(OFFER_LIST_SELECTOR))
                .flatMap(offersList -> offersList.select(ONE_VINYL_SELECTOR).stream())
                .map((Element releaseElement) -> getRawOfferFromElement(releaseElement, listParser))
                .filter(this::isValid)
                .collect(toSet());
        log.debug("Resulting set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    Set<String> getAllPageLinksSet(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        String pageLinkPattern = pageLinks.iterator().next();
        log.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        Set<String> fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> pageLinkPattern.replaceAll(PAGE_NUMBER_PATTERN.toString(), "&page=" + pageNumber))
                        .collect(toSet());
        log.debug("Resulting set of page links (with no gaps in sequence) is {'pageLinks':{}}", pageLinks);
        return fullListOfPageLinks;
    }

    Set<String> getAllGenreLinks() {
        Optional<Document> startDocument;
        startDocument = getDocument(START_PAGE_LINK);
        Set<String> allGenresLinks = startDocument
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

    public RawOffer getRawOfferFromElement(Element releaseElement, DetailedVinylParser detailedParser) {
        String imageLink = detailedParser.getHighResImageLinkFromDocument(releaseElement);
        String offerLink = detailedParser.getOfferLinkFromDocument(releaseElement);
        offerLink = !offerLink.isEmpty()? BASE_LINK + offerLink: offerLink;
        double price = detailedParser.getPriceFromDocument(releaseElement);
        Optional<Currency> priceCurrency = detailedParser.getOptionalCurrencyFromDocument(releaseElement);
        String artist = detailedParser.getArtistFromDocument(releaseElement);
        String release = detailedParser.getReleaseFromDocument(releaseElement);
        String genre = detailedParser.getGenreFromDocument(releaseElement);
        String catalogNumber = detailedParser.getCatNumberFromDocument(releaseElement);
        Boolean inStock = detailedParser.getInStockInfoFromDocument(releaseElement);

        RawOffer rawOffer = new RawOffer();
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
    public long getShopId() {
        return SHOP_ID;
    }
}
