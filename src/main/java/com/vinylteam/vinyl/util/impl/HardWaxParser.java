package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.ParserConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
public class HardWaxParser extends VinylParser {

    protected static final String BASE_LINK = "https://hardwax.com";
    private static final String URL_FILTER_PARAMETER = "?filter=vinyl";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/" + URL_FILTER_PARAMETER;
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK;
    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("page=([0-9]+)");
    private static final Pattern GENRE_PATTERN = Pattern.compile("https://hardwax.com/([^\\/]+)");

    private static final int SHOP_ID = 8;
    private static final String GENRES_SELECTOR = "DIV#sidebar H3.navcategory:contains(Tags) + UL.navlist > LI > A";
    private static final String ONE_VINYL_SELECTOR = "DIV.listing.block";
    private static final String OFFER_LIST_SELECTOR = "DIV#content";
    private static final String ONE_VINYL_FROM_ONE_PAGE_SELECTOR = "DIV#content";
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "DIV.fleft A:contains(Last)";


    private final ParserConfiguration conf = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV.listing.block DIV.picture > A > IMG.thumbnail")//x. -> xbig
            .offerLinkSelector("DIV.listing.block DIV.picture > A")
            .artistSelector("DIV.listing.block DIV.textblock DIV.linebig > STRONG > A")
            .releaseSelector("DIV.listing.block DIV.textblock DIV.linebig")
            .vinylGenresSelector("DIV.product_footer_a > DIV.style > A")
            .priceDetailsSelector("DIV.listing.block DIV.textblock DIV.add_order ")
            .catalogNumberSelector("DIV.listing.block DIV.textblock DIV.linesmall > A")
            .inStockMarker("out of stock")
            .inStockMarkerSelector("DIV.add_order")
            .build();

    private final ParserConfiguration onePageConf = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV#content DIV.record.block DIV.picture.big > IMG[id*=bigimage.x]")
            .offerLinkSelector("DIV#content DIV.record.block DIV.linesmall > A[href*=label] + A")
            .artistSelector("DIV#content DIV.record.block DIV.textblock DIV.linebig > STRONG > A")
            .releaseSelector("DIV#content DIV.record.block DIV.textblock DIV.linebig")
            .vinylGenresSelector("DIV#content DIV.product_footer_a > DIV.style > A")
            .priceDetailsSelector("DIV#content DIV.record.block DIV.textblock DIV.add_order A")
            .catalogNumberSelector("DIV#content DIV.record.block DIV.textblock DIV.linesmall > A")
            .inStockMarker("out of stock")
            .inStockMarkerSelector("DIV#content DIV.add_order")
            .build();

    @Getter
    private final DetailedVinylParser detailedParser = new HardWaxDetailedParserImpl(conf);

    @Getter
    private final DetailedVinylParser onePageDetailedParser = new HardWaxDetailedParserImpl(onePageConf);

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        return getDocument(offerLink)
                .stream()
                .flatMap(doc -> doc.select(ONE_VINYL_FROM_ONE_PAGE_SELECTOR).stream())
                .map(oneVinyl -> getRawOfferFromElement(oneVinyl, onePageDetailedParser))
                .findFirst()
                .orElse(new RawOffer());
    }

    @Override
    protected long getShopId() {
        return SHOP_ID;
    }

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> allGenres = getAllGenresLinks();
        log.info("Got genres links totally: {'allGenresLinks':{}}", allGenres.size());
        Set<String> pageLinks = getAllPagesByGenres(allGenres);
        log.info("Got page links {'pageLinks':{}}", pageLinks.size());
        Set<RawOffer> rawOffersSet = readOffersFromAllPages(pageLinks);

        log.info("Read {} rawOffers from all offer pages", rawOffersSet.size());
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        log.debug("Resulting list of vinyls from hardwax.com is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    public RawOffer getRawOfferFromElement(Element releaseElement, DetailedVinylParser detailedParser) {
        String imageLink = detailedParser.getHighResImageLinkFromDocument(releaseElement);
        imageLink = imageLink.replace("x.j", "xbig.j");
        String offerLink = BASE_LINK + detailedParser.getOfferLinkFromDocument(releaseElement);
        double price = detailedParser.getPriceFromDocument(releaseElement);
        Optional<Currency> priceCurrency = detailedParser.getOptionalCurrencyFromDocument(releaseElement);
        String artist = detailedParser.getArtistFromDocument(releaseElement);
        String release = detailedParser.getReleaseFromDocument(releaseElement);
        release = release.replace(artist, "").trim();
        artist = artist.replace(":", "");
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

    Set<String> getAllGenresLinks() {
        Set<String> allGenresLinks = getDocument(START_PAGE_LINK)
                .stream()
                .flatMap(document -> document.select(GENRES_SELECTOR).stream())
                .map(link -> BASE_LINK + link.attr("href"))
                .collect(toSet());
        log.debug("Got genres links {'allGenresLinks':{}}", allGenresLinks);
        return allGenresLinks;
    }

    Set<String> getAllPagesByGenres(Set<String> allGenres) {
        return allGenres
                .stream()
                .flatMap(genre -> getAllPagesByGenre(genre+URL_FILTER_PARAMETER).stream())
                .collect(toSet());
    }

    Set<String> getAllPagesByGenre(String genreLink) {
        Set<String> allPages = getDocument(genreLink)
                .stream()
                .flatMap(document -> document.select(PRELIMINARY_PAGE_LINK_SELECTOR).stream())
                .map(pageLink -> genreLink + URL_FILTER_PARAMETER + pageLink.attr("href").replace("?","&"))
                .collect(toSet());
        if (allPages.isEmpty()) {
            return Set.of(genreLink);
        }
        return getAllPageLinksSet(allPages);
    }

    Set<String> getAllPageLinksSet(Set<String> pageLinks) {
        int maxPageNumber = countPageLinks(pageLinks);
        String pageLinkPattern = pageLinks.iterator().next();
        log.debug("Pages found {'maxPageNumber':{}}", maxPageNumber);
        Set<String> fullListOfPageLinks =
                IntStream.rangeClosed(1, maxPageNumber)
                        .mapToObj(pageNumber -> pageLinkPattern.replaceAll(PAGE_NUMBER_PATTERN.toString(), "page=" + pageNumber))
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

    Set<RawOffer> readOffersFromAllPages(Set<String> pageLinks) {
        return pageLinks
                .stream()
                .flatMap(link -> readOffersFromOneLink(link).stream())
                .collect(toSet());
    }

    Set<RawOffer> readOffersFromOneLink(String pageLink) {
        String genre = getGenreFromLink(pageLink);
        Set<RawOffer> offerLinks = this.getDocument(pageLink)
                .stream()
                .map(document -> document.select(OFFER_LIST_SELECTOR))
                .flatMap(offersList -> offersList.select(ONE_VINYL_SELECTOR).stream())
                .map(releaseElement -> getRawOfferFromElement(releaseElement, detailedParser))
                .filter(this::isValid)
                .collect(toSet());
        offerLinks.forEach(offer -> offer.setGenre(genre));
        log.info("Resulting set of offers for {'pageLink':{}} has size {'offerLinks.size':{}}", pageLink, offerLinks.size());
        log.debug("Resulting set of offers for {'pageLink':{}} is {'offerLinks':{}}", pageLink, offerLinks);
        return offerLinks;
    }

    String getGenreFromLink(String pageLink) {
        Matcher matcher = GENRE_PATTERN.matcher(pageLink);
        if (matcher.find()){
            return matcher.group(1);
        }
        return "";
    }

    class HardWaxDetailedParserImpl extends DetailedVinylParserImpl {

        public HardWaxDetailedParserImpl(ParserConfiguration config) {
            super(config);
        }

        @Override
        public List<String> getPriceDetailsFromDocument(Element document) {
            var priceDetails = super.getPriceDetailsFromDocument(document);
            var firstPrice = priceDetails.get(0);
            firstPrice = firstPrice.substring(firstPrice.indexOf(" €") + 1);
            priceDetails.set(0, firstPrice);
            return priceDetails;
        }

        @Override
        public Boolean getInStockInfoFromDocument(Element document) {
            String inStockText = document.select(config.getInStockMarkerSelector()).text();
            return !inStockText.contains(config.getInStockMarker());
        }
    }
}
