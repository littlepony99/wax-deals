package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.Currency;
import com.vinylteam.vinyl.util.DetailedVinylParser;
import com.vinylteam.vinyl.util.ParserConfiguration;
import com.vinylteam.vinyl.util.PriceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DetailedVinylParserImpl implements DetailedVinylParser {

    private final ParserConfiguration config;

    @Override
    public String getGenreFromDocument(Element document) {
        return document.select(config.getVinylGenresSelector()).text();
    }

    @Override
    public String getReleaseFromDocument(Element document) {
        return document.select(config.getReleaseSelector()).text();
    }

    @Override
    public String getArtistFromDocument(Element document) {
        return document.select(config.getArtistSelector()).text();
    }

    @Override
    public String getCatNumberFromDocument(Element document) {
        return document.select(config.getCatalogNumberSelector()).text();
    }

    @Override
    public Boolean getInStockInfoFromDocument(Element document) {
        boolean inStock = false;
        String inStockText = document.select(config.getInStockMarkerSelector()).text();
        if (config.getInStockMarker().equals(inStockText)) {
            inStock = true;
        }
        return inStock;
    }

    @Override
    public Optional<Currency> getOptionalCurrencyFromDocument(Element document) {
        List<String> pricesBlock = document.select(config.getPriceDetailsSelector()).eachText();
        if (pricesBlock.isEmpty()) {
            return Optional.empty();
        }
        String fullPriceDetails = pricesBlock.get(0);
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
        return PriceUtils.getCurrencyFromString(fullPriceDetails);
    }

    @Override
    public double getPriceFromDocument(Element document) {
        List<String> pricesBlock = document.select(config.getPriceDetailsSelector()).eachText();
        if (pricesBlock.isEmpty()) {
            return 0d;
        }
        String fullPriceDetails = pricesBlock.get(0);
        log.debug("Got price details from page by offer link {'priceDetails':{}, 'offerLink':{}}", fullPriceDetails, document.ownerDocument().location());
        return PriceUtils.getPriceFromString(fullPriceDetails);
    }

    @Override
    public String getHighResImageLinkFromDocument(Element document) {

        String imageLink = document.select(config.getHighResolutionImageSelector()).attr("src");
        if (imageLink.isEmpty()) {
            imageLink = document.select(config.getHighResolutionImageSelector()).attr("href");
        }

        if (imageLink != null && !Objects.equals(imageLink, "")) {
            if (!imageLink.contains("no-cover")) {
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

    @Override
    public String getOfferLinkFromDocument(Element document) {
        return document.select(config.getOfferLinkSelector()).attr("href");
    }

}
