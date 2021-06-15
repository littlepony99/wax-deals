package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.entity.RawOffer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class DecksParser extends VinylParser {

    private static final String BASE_LINK = "https://www.decks.de";
    private static final String START_PAGE_LINK = BASE_LINK + "/decks/workfloor/lists/list_db.php";
    private static final int SHOP_ID = 5;

    @Override
    public List<RawOffer> getRawOffersList() {
        HashSet<String> genresLinks = getGenresLinks();
        HashSet<String> pageLinks = getPageLinks(genresLinks);
        HashSet<String> offerLinks = getOfferLinks(pageLinks);
        HashSet<RawOffer> rawOfferSet = readRawOffersFromAllOfferLinks(offerLinks);
        return new ArrayList<>(rawOfferSet);
    }

    @Override
    public RawOffer getRawOfferFromOfferLink(String offerLink) {
        RawOffer rawOffer = new RawOffer();
        Optional<Document> offerDocument = getDocument(offerLink);
        if (offerDocument.isPresent()) {
            rawOffer.setShopId(SHOP_ID);
            rawOffer.setRelease(getReleaseFromDocument(offerDocument.get()));
            rawOffer.setArtist(getArtistFromDocument(offerDocument.get()));
            rawOffer.setPrice(getPriceFromDocument(offerDocument.get()));
            rawOffer.setCurrency(getOptionalCurrencyFromDocument(offerDocument.get()));
            rawOffer.setGenre(getGenreFromDocument(offerDocument.get()));
            rawOffer.setCatNumber(getCatNumberFromDocument(offerDocument.get()));
            rawOffer.setInStock(getInStockInfoFromDocument(offerDocument.get()));
            rawOffer.setOfferLink(offerLink);
            rawOffer.setImageLink(getHighResImageLinkFromDocument(offerDocument.get()));
        }
        return rawOffer;
    }

    HashSet<String> getGenresLinks() {
        HashSet<String> genreLinks = new HashSet<>();
        Optional<Document> startDocument = getDocument(START_PAGE_LINK);
        if (startDocument.isPresent()) {
            Element iframeElement = startDocument.get().select("iframe").first();
            Elements elementsWithGenreLinks = iframeElement.parents().get(0).getElementsByClass("menueBodySub")
                    .select("a");
            for (Element elementWithGenreLink : elementsWithGenreLinks) {
                String genresLink = elementWithGenreLink.attr("href");
                if (!genresLink.equals("javascript:void(0);")) {
                    genreLinks.add(BASE_LINK + genresLink);
                }
            }
        }
        log.info("Got genres from Decks.de: {}", genreLinks.size());
        return genreLinks;
    }

    String getCatNumberFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String[] catNumbers = iframeElement.parents().get(0).getElementsByClass("detail_label").text().split("[/ ]");
        String catNumber = catNumbers[catNumbers.length - 1];
        return catNumber;
    }

    boolean getInStockInfoFromDocument(Document offerDocument) {
        boolean inStock = true;
        Element iframeElement = offerDocument.select("iframe").first();
        String inStockText = iframeElement.parents().get(0).getElementsByClass("stockBlockaround").text();
        if (inStockText.contains("out of stock")){
            inStock = false;
        }
        return inStock;
    }

    String getHighResImageLinkFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String imageLink = iframeElement.parents().get(0).getElementsByClass("bigCoverDetail").select("img")
                .attr("src");
        if (imageLink != null && !Objects.equals(imageLink, "")){
            log.debug("Got high resolution image link from page by offer link {'highResImageLink':{}, 'offerLink':{}}", imageLink, offerDocument.location());
        } else {
            log.warn("Can't find image link from page by offer link, returning default {'offerLink':{}}", offerDocument.location());
            imageLink = "img/goods/no_image.jpg";
        }
        return imageLink;
    }

    String getGenreFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("LStylehead").text();
    }

    Optional<Currency> getOptionalCurrencyFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1) {
            String currency = priceDetails.substring(priceDetails.indexOf(' ') + 1, priceDetails.indexOf('*') - 1);
            return Currency.getCurrency(currency);
        }
        return Optional.empty();
    }

    double getPriceFromDocument(Document offerDocument) {
        double price = 0.;
        Element iframeElement = offerDocument.select("iframe").first();
        String priceDetails = iframeElement.parents().get(0).getElementsByClass("preisschild").text();
        if (priceDetails.indexOf(' ') != -1) {
            price = Double.parseDouble(priceDetails.substring(0, priceDetails.indexOf(' ')));
        }
        return price;
    }

    String getArtistFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("detail_artist  lightCol mainColBG ")
                .select("h1").text();
    }

    String getReleaseFromDocument(Document offerDocument) {
        Element iframeElement = offerDocument.select("iframe").first();
        return iframeElement.parents().get(0).getElementsByClass("detail_titel  lightCol mainCol ")
                .select("h1").text();
    }

    public HashSet<RawOffer> readRawOffersFromAllOfferLinks(HashSet<String> offerLinks) {
        HashSet<RawOffer> rawOfferSet = new HashSet<>();
        for (String offerLink : offerLinks) {
            RawOffer rawOffer = getRawOfferFromOfferLink(offerLink);
            if (isValid(rawOffer)) {
                rawOfferSet.add(rawOffer);
            } else {
                log.warn("Can't fill raw offer by offer link, not adding it to set {'rawOffer':{}, 'offerLink':{}}",
                        rawOffer, offerLink);
            }
        }
        log.info("Got row offers from Decks.de: {}", rawOfferSet.size());
        return rawOfferSet;
    }

    public HashSet<String> getOfferLinks(HashSet<String> pageLinks) {
        HashSet<String> offerLinks = new HashSet<>();
        for (String pageLink : pageLinks) {
            Optional<Document> pageDocument = getDocument(pageLink);
            if (pageDocument.isPresent()) {
                Element iframeElement = pageDocument.get().select("iframe").first();
                Elements elementsWithOfferLinks = iframeElement.parents().get(0)
                        .getElementsByClass("cover1").select("a");
                for (Element elementWithOfferLink : elementsWithOfferLinks) {
                    String offerLink = elementWithOfferLink.attr("href");
                    if (offerLink.contains(BASE_LINK)) {
                        offerLinks.add(offerLink);
                    }
                }
            }
        }
        log.info("Got offer links from Decks.de: {}", offerLinks.size());
        return offerLinks;
    }

    HashSet<String> getPageLinks(HashSet<String> genresLinks) {
        LinkedHashSet<String> pageLinks = new LinkedHashSet<>();
        String pageCount;
        for (String genreLink : genresLinks) {
            List<String> pageCountList = new ArrayList<>();
            Optional<Document> genreDocument = getDocument(genreLink);
            if (genreDocument.isPresent()) {
                Element iframeElement = genreDocument.get().select("iframe").first();
                Elements elementsWithLinkToPages = iframeElement.parents().get(0).
                        getElementsByClass("pager").select("a");
                String templateLink = START_PAGE_LINK + elementsWithLinkToPages.get(0).attr("href");
                for (Element elementWithLinkToPages : elementsWithLinkToPages) {
                    pageCountList.add(elementWithLinkToPages.text());
                }
                if (pageCountList.size() > 1) {
                    pageCount = pageCountList.get(pageCountList.size() - 2);
                } else {
                    pageCount = pageCountList.get(pageCountList.size() - 1);
                }
                for (int i = 0; i < Integer.parseInt(pageCount); i++) {
                    pageLinks.add(templateLink.replace("aktuell=0", "aktuell=" + i));
                }
            }
        }
        log.info("Got pages from Decks.de: {}", pageLinks.size());
        return pageLinks;
    }

    @Override
    public long getShopId() {
        return SHOP_ID;
    }
}
