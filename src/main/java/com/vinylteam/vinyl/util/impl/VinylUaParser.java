package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.VinylParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

@Slf4j
public class VinylUaParser implements VinylParser {

    private static final String START_LINK = "http://vinyl.ua";
    private static final String SELECTOR_SCRIPT_WITH_HIGH_RES_IMAGE_LINK = "script:containsData(openPhotoSwipe)";
    private static final String SELECTOR_RELEASE = "div.boxed div.col-sm-7 > h3.normal-text";
    private static final String SELECTOR_ARTIST = "div.boxed div.col-sm-7 > h4.normal-text > span.text-ellipsis > a";
    private static final String SELECTOR_PRICE_DETAILS = "div.boxed div.col-sm-4 > button.btn-success > b";
    private static final String SELECTOR_GENRE = "div.boxed div.col-xs-12 table.list-meta td.text-right:contains(Жанр:) + td";
    private static final String SELECTOR_GENRE_ANCHORS = "nav#intro div#bs-example-navbar-collapse-1 > ul.nav > li.dropdown > ul.dropdown-menu > li > a";
    private static final String SELECTOR_PAGE_ANCHORS = "div.pagination-wrapper > ul.pagination > li:not(.nav-pagi) > a";
    private static final String SELECTOR_OFFER_ANCHORS = "div.row > div.col-sm-9 > div.row div.vinyl-release > div.boxed > p > a";

    HashSet<String> getGenresLinks() {
        HashSet<String> genreLinks = new HashSet<>();
        Document document;
        try {
            document = Jsoup.connect(START_LINK).get();
        } catch (IOException e) {
            log.error("Error while getting document by link {'link':{}}", START_LINK, e);
            throw new RuntimeException("Fail while getting a document by " + START_LINK, e);
        }
        log.debug("Got document out of start link {'startLink':{}, 'document':{}",
                START_LINK, document);
        Elements genreAnchors = document.select(SELECTOR_GENRE_ANCHORS);
        log.debug("Got collection of genre anchors {'genreAnchors':{}}", genreAnchors);
        for (Element anchor : genreAnchors) {
            String anchorLink = anchor.attr("href");
            String link = START_LINK + anchorLink;
            genreLinks.add(link);
            log.debug("Added link to hash set of genre links {'link':{}}", link);
        }
        log.debug("Resulting hash set of genre links is {'genreLinks':{}}", genreLinks);
        if (genreLinks.isEmpty()) {
            log.error("Zero genre links by start link {'startLink':{}}", START_LINK);
        }
        return genreLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genreLinks) {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        for (String genreLink : genreLinks) {
            Document document;
            try {
                document = Jsoup.connect(genreLink).get();
            } catch (IOException e) {
                log.error("Error while getting document by link {'link':{}}", genreLink, e);
                throw new RuntimeException("Fail while getting a document by " + genreLink, e);
            }
            log.debug("Got document out of genre link {'genreLink':{}, 'document':{}", genreLink, document);
            Elements pageAnchors = document.select(SELECTOR_PAGE_ANCHORS);
            log.debug("Got collection of page anchors {'pageAnchors':{}}", pageAnchors);
            for (Element anchor : pageAnchors) {
                String anchorLink = anchor.attr("href");
                String link = START_LINK + anchorLink;
                pageLinks.add(link);
                log.debug("Added link to hash set of page links {'link':{}}", link);
            }
        }
        log.debug("Resulting hash set of page links is {'pageLinks':{}}", pageLinks);
        return pageLinks;
    }

    HashSet<String> getOfferLinks(HashSet<String> pageLinks) {
        HashSet<String> offerLinks = new HashSet<>();
        for (String pageLink : pageLinks) {
            Document document;
            try {
                document = Jsoup.connect(pageLink).get();
            } catch (IOException e) {
                log.error("Error while getting document by link {'link':{}}", pageLink, e);
                throw new RuntimeException("Fail while getting a document by " + pageLink, e);
            }
            log.debug("Got document out of page link {'pageLink':{}, 'document':{}", pageLink, document);
            Elements offerAnchors = document.select(SELECTOR_OFFER_ANCHORS);
            log.debug("Got collection of offer anchors with links to offers {'offerAnchors':{}}", offerAnchors);

            for (Element anchor : offerAnchors) {
                String offerLink = START_LINK + anchor.attr("href");
                offerLinks.add(offerLink);
                log.debug("Added link to hash set of offer links{'offerLink':{}}", offerLink);
            }
            log.debug("Parsed page link {'pageLink':{}}", pageLink);
        }
        log.debug("Resulting hash set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    HashSet<RawOffer> readRawOffersFromAllOfferLinks(HashSet<String> offerLinks) {
        HashSet<RawOffer> rawOfferSet = new HashSet<>();
        for (String offerLink : offerLinks) {
            RawOffer rawOffer = getRawOfferFromOfferLink(offerLink);
            if (isValid(rawOffer)) {
                rawOfferSet.add(rawOffer);
            } else {
                log.warn("Can't fill raw offer by offer link, not adding it to set {'rawOffer':{}, 'offerLink':{}}", rawOffer, offerLink);
            }
        }
        log.debug("Resulting hash set of rawOfferSet is {'rawOfferSet':{}}", rawOfferSet);
        return rawOfferSet;
    }

    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer rawOffer = new RawOffer();
        Document document;
        try {
            document = Jsoup.connect(offerLink).get();
            log.debug("Got document out of offer link {'offerLink':{}, 'document':{}", offerLink, document);
        } catch (IOException e) {
            log.error("Error while getting document by link {'link':{}}", offerLink, e);
            throw new RuntimeException("Fail while getting a document by " + offerLink, e);
        }
        rawOffer.setShopId(1);
        rawOffer.setRelease(getReleaseFromDocument(document));
        rawOffer.setArtist(getArtistFromDocument(document));
        rawOffer.setPrice(getPriceFromDocument(document));
        rawOffer.setCurrency(getOptionalCurrencyFromDocument(document));
        rawOffer.setGenre(getGenreFromDocument(document));
        rawOffer.setOfferLink(offerLink);
        rawOffer.setImageLink(getHighResImageLinkFromDocument(document));
        log.debug("Parsed page link {'offerLink':{}}", offerLink);
        return rawOffer;
    }

    String getReleaseFromDocument(Document document) {
        String release = document.select(SELECTOR_RELEASE).text();
        log.debug("Got release from page by offer link {'release':{}, 'offerLink':{}}", release, document.location());
        return release;
    }

    String getArtistFromDocument(Document document) {
        String artist = document.select(SELECTOR_ARTIST).text();
        if (artist.equals("")) {
            artist = "Various Artists";
        }
        log.debug("Got artist from page by offer link  {'artist':{}, 'offerLink':{}}", artist, document.location());
        return artist;
    }

    Double getPriceFromDocument(Document document) {
        String priceDetails = document.select(SELECTOR_PRICE_DETAILS).text();
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", priceDetails, document.location());
        if (priceDetails.indexOf(' ') != -1) {
            String priceNumber = priceDetails.substring(0, priceDetails.indexOf(' '));
            double price = Double.parseDouble(priceNumber);
            log.debug("Got price from price details {'price':{}, 'priceDetails':{}}", price, priceDetails);
            return price;
        }
        return 0.;
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Document document) {
        String priceDetails = document.select(SELECTOR_PRICE_DETAILS).text();
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", priceDetails, document.location());
        if (priceDetails.indexOf(' ') != -1) {
            String currency = priceDetails.substring(priceDetails.indexOf(' ') + 1);
            Optional<Currency> optionalCurrency = Currency.getCurrency(currency);
            log.debug("Got optional with currency from price details {'optionalCurrency':{}, 'priceDetails':{}}", optionalCurrency, priceDetails);
            return optionalCurrency;
        }
        log.warn("Can't find currency description from price details, returning empty optional {'priceDetails':{}}", priceDetails);
        return Optional.empty();
    }

    String getHighResImageLinkFromDocument(Document document) {
        List<DataNode> scriptDataNodes = document.select(SELECTOR_SCRIPT_WITH_HIGH_RES_IMAGE_LINK).dataNodes();
        if (scriptDataNodes.isEmpty()) {
            log.warn("No script containing high resolution image link by offer link, returning default {'offerLink':{}}", document.location());
            return "img/goods/no_image.jpg";
        }
        String scriptWithHighResImageLink = scriptDataNodes.get(0).getWholeData();
        log.debug("Got script containing high resolution image link from page by offer link {'script':{}, 'offerLink':{}}", scriptWithHighResImageLink, document.location());
        String highResImageLink;
        int beginIndexOfImageLink = scriptWithHighResImageLink.indexOf("src: '") + "src: '".length();
        if (beginIndexOfImageLink != -1) {
            int endIndexOfImageLink = scriptWithHighResImageLink.indexOf('\'', beginIndexOfImageLink);
            highResImageLink = scriptWithHighResImageLink.substring(beginIndexOfImageLink, endIndexOfImageLink);
            log.debug("Got high resolution image link from page by offer link {'highResImageLink':{}, 'offerLink':{}}", highResImageLink, document.location());
        } else {
            log.warn("Can't find image link from page by offer link, returning default {'offerLink':{}}", document.location());
            highResImageLink = "img/goods/no_image.jpg";
        }
        return highResImageLink;
    }

    String getGenreFromDocument(Document document) {
        String genre = document.select(SELECTOR_GENRE).text();
        log.debug("Got genre from page by offer link {'genre':{}, 'offerLink':{}}", genre, document.location());
        return genre;
    }

    boolean isValid(RawOffer rawOffer) {
        boolean isValid = false;
        if (rawOffer.getPrice() != 0.
                && rawOffer.getCurrency().isPresent()
                && !("".equals(rawOffer.getRelease()))
                && rawOffer.getOfferLink() != null) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public List<RawOffer> getRawOffersList() {
        HashSet<String> genresLinks = getGenresLinks();
        log.debug("got genre links {'genreLinks':{}}", genresLinks);
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        log.debug("got page links {'pageLinks':{}}", pageLinks);
        HashSet<String> offerLinks = getOfferLinks(pageLinks);
        log.debug("got offer links {'offerLinks':{}}", offerLinks);
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(offerLinks);
        log.info("read {} rawOffers from all offer links", rawOfferSet.size());
        List<RawOffer> rawOffers = new ArrayList<>(rawOfferSet);
        log.debug("Resulting list of raw offers from vinyl.ua is {'rawOffers':{}}", rawOffers);
        return rawOffers;
    }

}