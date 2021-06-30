package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.ParserConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class DeejayDeParser extends VinylParser {

    protected static final String BASE_LINK = "https://www.deejay.de";
    protected static final String START_PAGE_LINK = BASE_LINK + "/m_All/sm_News/stock_1/perpage_160";

    private static final int SHOP_ID = 6;
    private static final String PRELIMINARY_PAGE_LINK_SELECTOR = "UL.pagination";
    private static final String PAGE_LINK_SELECTOR = "LI>A.setPage[title=»]";
    private static final String IFRAME_SELECTOR = "IFRAME";
    private static final String OFFER_LIST_SELECTOR = "DIV#theList";
    private static final String ONE_VINYL_FROM_ONE_PAGE_SELECTOR = "DIV#content";

    private static final String ONE_VINYL_SELECTOR = "ARTICLE.product";
    private final ParserConfiguration conf = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV.cover DIV.img A")
            .offerLinkSelector("DIV.artikel > h3.title > A")
            .artistSelector("DIV.artikel > h2.artist > INTERPRETS > A")
            .releaseSelector("DIV.artikel > h3.title > A")
            .vinylGenresSelector("DIV.product_footer_a > DIV.style > A")
            .priceDetailsSelector("DIV.order DIV.kaufen > SPAN.price")
            .catalogNumberSelector("DIV.label > STRONG")
            .inStockMarker("In Stock")
            .inStockMarkerSelector("DIV.order DIV.stock")
            .build();

    private final ParserConfiguration onePageConf = ParserConfiguration
            .builder()
            .highResolutionImageSelector("DIV#content DIV.cover.tipp DIV.img.allbig A")
            .offerLinkSelector("DIV#content DIV.ordertitel A")
            .artistSelector("DIV#content DIV.artist > H1[itemprop=publisher]")
            .releaseSelector("DIV#content DIV.title > H1[itemprop=inalbum name]")
            .vinylGenresSelector("DIV#content DIV.artikel > DIV.styles[itemprop=genre] > A")
            .priceDetailsSelector("DIV#content DIV.order DIV.kaufen > SPAN.price")
            .catalogNumberSelector("DIV#content DIV.label > H1[itemprop=alternateName]")
            .inStockMarker("In Stock")
            .inStockMarkerSelector("DIV#content DIV.order DIV.stock SPAN.first")
            .build();

    @Getter
    private final DetailedVinylParser detailedParser = new DetailedVinylParserImpl(conf);

    @Getter
    private final DetailedVinylParser onePageDetailedParser = new DetailedVinylParserImpl(onePageConf);

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer offer = getDocument(offerLink)
                .stream()
                .map(document -> document.select(IFRAME_SELECTOR))
                .map(t -> t.attr("src"))
                .flatMap(contentLink -> getDocument(BASE_LINK + contentLink).stream())
                .flatMap(doc -> doc.select(ONE_VINYL_FROM_ONE_PAGE_SELECTOR).stream())
                .map(oneVinyl -> getRawOfferFromElement(oneVinyl, onePageDetailedParser))
                .findFirst()
                .orElse(new RawOffer());
        offer.setOfferLink(offerLink);
        return offer;
    }

    @Override
    protected long getShopId() {
        return SHOP_ID;
    }

    @Override
    public List<RawOffer> getRawOffersList() {
        Set<String> pageLinks = getAllPages(START_PAGE_LINK);
        log.info("got page links {'pageLinks':{}}", pageLinks.size());

        Set<RawOffer> rawOffersSet = readOffersFromAllPages(pageLinks);

        log.info("Read {} rawOffers from all offer pages", rawOffersSet.size());
        List<RawOffer> rawOffersList = new ArrayList<>(rawOffersSet);
        log.debug("Resulting list of vinyls from www.juno.co.uk is {'rawOffersList':{}}", rawOffersList);
        return rawOffersList;
    }

    Set<RawOffer> readOffersFromAllPages(Set<String> pageLinks) {
        Set<RawOffer> offerLinks = pageLinks
                .stream()
                .flatMap(link -> this.getDocument(link).stream())
                .map(document -> document.select(IFRAME_SELECTOR))
                .map(t -> t.attr("src"))
                .flatMap(contentLink -> getDocument(BASE_LINK + contentLink).stream())
                .map(document -> document.select(OFFER_LIST_SELECTOR))
                .flatMap(offersList -> offersList.select(ONE_VINYL_SELECTOR).stream())
                .map(releaseElement -> getRawOfferFromElement(releaseElement, detailedParser))
                .filter(this::isValid)
                .collect(toSet());
        log.debug("Resulting set of offer links is {'offerLinks':{}}", offerLinks);
        return offerLinks;
    }

    Set<String> getAllPages(String baseLink) {
        Set<String> linkSet = new HashSet<>();
        return getAllPages(linkSet, baseLink);
    }

    Set<String> getAllPages(Set<String> linkSet, String baseLink) {
        linkSet.addAll(getDocument(baseLink)
                .stream()
                .map(document -> document.select(IFRAME_SELECTOR))
                .map(t -> t.attr("src"))
                .flatMap(contentLink -> getDocument(BASE_LINK + contentLink).stream())
                .flatMap(document -> document.select(PRELIMINARY_PAGE_LINK_SELECTOR).stream())
                .flatMap(pagesBlock -> pagesBlock.select(PAGE_LINK_SELECTOR).stream())
                .map(pageLink -> BASE_LINK + pageLink.attr("href"))
                .map(url -> getAllPages(linkSet, url))
                .findFirst()
                .orElseGet(HashSet::new));

        linkSet.add(baseLink);
        return linkSet;
    }

    public RawOffer getRawOfferFromElement(Element releaseElement, DetailedVinylParser detailedParser) {
        String imageLink = BASE_LINK + detailedParser.getHighResImageLinkFromDocument(releaseElement);
        String offerLink = BASE_LINK + detailedParser.getOfferLinkFromDocument(releaseElement);
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

}
