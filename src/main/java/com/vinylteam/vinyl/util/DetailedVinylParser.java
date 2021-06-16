package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Currency;
import org.jsoup.nodes.Element;

import java.util.List;
import java.util.Optional;

public interface DetailedVinylParser {

    String getGenreFromDocument(Element document);

    String getReleaseFromDocument(Element document);

    String getArtistFromDocument(Element document);

    String getCatNumberFromDocument(Element document);

    Boolean getInStockInfoFromDocument(Element document);

    Optional<Currency> getOptionalCurrencyFromDocument(Element document);

    double getPriceFromDocument(Element document);

    List<String> getPriceDetailsFromDocument(Element document);

    String getHighResImageLinkFromDocument(Element document);

    String getOfferLinkFromDocument(Element document);

}
