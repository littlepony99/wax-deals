package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class RawOffersSorter {

    public List<Offer> getOffersUpdateUniqueVinyls(List<RawOffer> rawOffers, List<UniqueVinyl> uniqueVinyls) {
        if (rawOffers != null && uniqueVinyls != null) {
            log.info("Received {} rawOffers from parser", rawOffers.size());
            List<Offer> offers = new ArrayList<>();
            if (!rawOffers.isEmpty()) {
                ListIterator<UniqueVinyl> vinylIterator = uniqueVinyls.listIterator();
                while (!rawOffers.isEmpty()) {
                    if (!vinylIterator.hasNext()) {
                        long lastVinylId = uniqueVinyls.isEmpty() ? 1 : (uniqueVinyls.get(uniqueVinyls.size() - 1).getId() + 1);
                        UniqueVinyl uniqueVinyl = new UniqueVinyl();
                        uniqueVinyl.setId(lastVinylId);
                        uniqueVinyl.setRelease(rawOffers.get(0).getRelease());
                        uniqueVinyl.setArtist(rawOffers.get(0).getArtist());
                        uniqueVinyl.setFullName(uniqueVinyl.getRelease() + " - " + uniqueVinyl.getArtist());
                        uniqueVinyl.setImageLink(rawOffers.get(0).getImageLink());
                        vinylIterator.add(uniqueVinyl);
                        log.debug("Added new uniqueVinyl {'uniqueVinyl':{}}", uniqueVinyl);
                        vinylIterator.previous();
                    }
                    addOffersSortingByVinyl(rawOffers, vinylIterator.next(), offers);
                }
            } else {
                log.warn("Passed to RawOfferSorter.getOffersUpdateUniqueVinyls() list of raw offers is empty");
            }
            log.info("Sorting complete, {} unique vinyls, {} offers", uniqueVinyls.size(), offers.size());
            log.debug("Resulting list of offers and updated list of uniqueVinyls are {'offers':{}, 'uniqueVinyls':{}}", offers, uniqueVinyls);
            return offers;
        } else {
            RuntimeException e = new NullPointerException();
            log.error("At least one of passed arguments is null {'rawOffers':{}, 'uniqueVinyls':{}}}",
                    rawOffers, uniqueVinyls, e);
            throw e;
        }
    }

    void addOffersSortingByVinyl(List<RawOffer> rawOffers, UniqueVinyl uniqueVinyl, List<Offer> offers) {
        if (rawOffers != null && uniqueVinyl != null && offers != null) {
            Iterator<RawOffer> rawOfferIterator = rawOffers.iterator();
            while (rawOfferIterator.hasNext()) {
                RawOffer rawOffer = rawOfferIterator.next();
                if (Objects.equals(uniqueVinyl.getRelease(), rawOffer.getRelease()) &&
                        Objects.equals(uniqueVinyl.getArtist(), rawOffer.getArtist())) {
                    Offer offer = new Offer();
                    offer.setUniqueVinylId(uniqueVinyl.getId());
                    offer.setShopId(rawOffer.getShopId());
                    offer.setPrice(rawOffer.getPrice());
                    offer.setCurrency(rawOffer.getCurrency());
                    offer.setGenre(rawOffer.getGenre());
                    offer.setOfferLink(rawOffer.getOfferLink());
                    offers.add(offer);
                    uniqueVinyl.setHasOffers(true);
                    log.debug("Added new offer {'offer':{}}", offer);
                    rawOfferIterator.remove();
                }
            }
        } else {
            RuntimeException e = new NullPointerException();
            log.error("At least one of passed arguments is null {'rawOffers':{}, 'uniqueVinyl':{}, 'offers':{}}",
                    rawOffers, uniqueVinyl, offers, e);
            throw e;
        }
    }

}