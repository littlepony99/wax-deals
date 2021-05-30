package com.vinylteam.vinyl.util.impl;

import com.vinylteam.vinyl.entity.RawOffer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class VinylParser {

    public boolean isValid(RawOffer rawOffer) {
        boolean isValid = false;
        if (rawOffer.getPrice() != 0.
                && rawOffer.getCurrency().isPresent()
                && !rawOffer.getRelease().isEmpty()
                && rawOffer.getOfferLink() != null) {
            isValid = true;
        }
        return isValid;
    }

    public abstract RawOffer getRawOfferFromOfferLink(String offerLink);;

    protected abstract long getShopId();

    protected abstract List<RawOffer> getRawOffersList();

    protected Optional<Document> getDocument(String url) {
        try {
            return Optional.ofNullable(Jsoup.connect(url).get());
        } catch (IOException e) {
            log.warn("Page represented by the link will be skipped, since some error happened while getting document by link {'link':{}}", url, e);
            return Optional.empty();
        }
    }

}
