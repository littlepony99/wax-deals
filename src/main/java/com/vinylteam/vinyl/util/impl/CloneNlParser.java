package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.PriceUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class CloneNlParser extends VinylParser {

    protected static final String BASE_LINK = "https://clone.nl";
    private static final String CATALOG_ROOT_LINK = BASE_LINK + "/genres";
    private static final String START_PAGE_LINK = CATALOG_ROOT_LINK;
    private static final String GENRES_SELECTOR = "DIV > H1:contains(Genres) + P > A[href*=genre/]";
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "UL.pagination > LI > A";

    private static final Pattern PAGE_NUMBER_PATTERN = Pattern.compile("&page=([0-9]+)");
    private static final String OFFER_LIST_SELECTOR = "DIV.content-container > DIV.main-content";

    private static final String ONE_VINYL_SELECTOR = "DIV.release";
    private static final String ONE_VINYL_FROM_ONE_PAGE_SELECTOR = "DIV.musicrelease";
    private static final int SHOP_ID = 4;

    private final DetailedVinylParser batchParser = new DefaultDetailedVinylParser();
    private final DetailedVinylParser onePageParser = new OnePageDetailedVinylParser();

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
                .map((Element releaseElement) -> getRawOfferFromElement(releaseElement, batchParser))
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

    public static class DefaultDetailedVinylParser implements DetailedVinylParser {

        private static final String HIGH_RES_IMAGE_LINK_SELECTOR = "DIV.release IMG";
        private static final String OFFER_LINK_SELECTOR = "DIV.coverimage > A";
        private static final String ARTIST_SELECTOR = "DIV.description > H2 > A";
        private static final String RELEASE_SELECTOR = "DIV.description > H2 + H3 > A";
        private static final String VINYL_GENRES_SELECTOR = "DIV.tagsbuttons > A.label";
        private static final String PRICE_DETAILS_SELECTOR = "DIV.release TABLE.availability A.addtocart";
        public static final String CATALOG_NUMBER_SELECTOR = "span[itemprop=catalogNumber]";
        public static final String OUT_OF_STOCK = "out of stock";

        @Override
        public String getGenreFromDocument(Element document) {
            return document.select(VINYL_GENRES_SELECTOR).text();
        }

        public String getReleaseFromDocument(Element document) {
            return document.select(RELEASE_SELECTOR).text();
        }

        public String getArtistFromDocument(Element document) {
            return document.select(ARTIST_SELECTOR).text();
        }

        public String getCatNumberFromDocument(Element document) {
            return document.select(CATALOG_NUMBER_SELECTOR).text();
        }

        public Boolean getInStockInfoFromDocument(Element document) {
            boolean inStock = true;
            String inStockText = document.getElementsByClass("col-xs-2 status").text();
            if (OUT_OF_STOCK.contains(inStockText)) {
                inStock = false;
            }
            return inStock;
        }

        public Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
            List<String> pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return Optional.empty();
            }
            String fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getCurrencyFromString(fullPriceDetails);
        }

        public double getPriceFromDocument(Element document) {
            List<String> pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return 0d;
            }
            String fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getPriceFromString(fullPriceDetails);
        }

    public String getHighResImageLinkFromDocument(Element document) {
        String imageLink = document.select(HIGH_RES_IMAGE_LINK_SELECTOR).attr("src");
        if (imageLink != null && !Objects.equals(imageLink, "")){
            if (!imageLink.contains("no-cover")){
                log.debug("Got high resolution image link {'highResImageLink':{}}", imageLink);
            } else {
                imageLink = "img/goods/no_image.jpg";
            }
        } else {
            log.warn("Can't find image link, returning default");
            imageLink = "img/goods/no_image.jpg";
        }
        return imageLink;
    }

        public String getOfferLinkFromDocument(Element document) {
            return BASE_LINK + document.select(OFFER_LINK_SELECTOR).attr("href");
        }
    }

    public static class OnePageDetailedVinylParser implements DetailedVinylParser {

        private static final String HIGH_RES_IMAGE_LINK_SELECTOR = "DIV.release IMG";
        private static final String ARTIST_SELECTOR = "H1[itemprop=author] > A";
        private static final String RELEASE_SELECTOR = "H1[itemprop=author] + H2";
        private static final String VINYL_GENRES_SELECTOR = "DIV.tagsbuttons > A.label";
        private static final String PRICE_DETAILS_SELECTOR = "DIV.release TABLE.availability A.addtocart";
        public static final String OUT_OF_STOCK = "out of stock";

        @Override
        public String getGenreFromDocument(Element document) {
            return document.select(VINYL_GENRES_SELECTOR).text();
        }

        public String getReleaseFromDocument(Element document) {
            return document.select(RELEASE_SELECTOR).text();
        }

        public String getArtistFromDocument(Element document) {
            return document.select(ARTIST_SELECTOR).text();
        }

        public String getCatNumberFromDocument(Element document) {
            return document.select("span[itemprop=catalogNumber]").text();
        }

        public Boolean getInStockInfoFromDocument(Element document) {
            boolean inStock = true;
            String inStockText = document.getElementsByClass("col-xs-2 status").text();
            if (OUT_OF_STOCK.contains(inStockText)) {
                inStock = false;
            }
            return inStock;
        }

        public Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
            List<String> pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return Optional.empty();
            }
            String fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getCurrencyFromString(fullPriceDetails);
        }

        public double getPriceFromDocument(Element document) {
            List<String> pricesBlock = document.select(PRICE_DETAILS_SELECTOR).eachText();
            if (pricesBlock.isEmpty()) {
                return 0d;
            }
            String fullPriceDetails = pricesBlock.get(0);
            log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
            return PriceUtils.getPriceFromString(fullPriceDetails);
        }

        public String getHighResImageLinkFromDocument(Element document) {
            String imageLink = document.select(HIGH_RES_IMAGE_LINK_SELECTOR).attr("src");
            if (imageLink != null && !Objects.equals(imageLink, "")){
                if (!imageLink.contains("no-cover")){
                    log.debug("Got high resolution image link {'highResImageLink':{}}", imageLink);
                } else {
                    imageLink = "img/goods/no_image.jpg";
                }
            } else {
                log.warn("Can't find image link, returning default");
                imageLink = "img/goods/no_image.jpg";
            }
            return imageLink;
        }

        public String getOfferLinkFromDocument(Element document) {
            return "";
        }
    }
}
