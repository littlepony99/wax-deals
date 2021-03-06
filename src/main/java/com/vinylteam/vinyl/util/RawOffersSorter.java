package com.vinylteam.vinyl.util;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RawOffersSorter {

    public List<Offer> getOffersUpdateUniqueVinyls(List<RawOffer> rawOffers, List<UniqueVinyl> uniqueVinyls) {
        if (rawOffers != null && uniqueVinyls != null) {
            log.info("Received {} rawOffers from parser", rawOffers.size());
            List<Offer> offers = new ArrayList<>();
            if (!rawOffers.isEmpty()) {
                ListIterator<UniqueVinyl> vinylIterator = uniqueVinyls.listIterator();
                while (!rawOffers.isEmpty()) {
                    if (!vinylIterator.hasNext()) {
                        String lastVinylId = uniqueVinyls.isEmpty() ? "1" :
                                (Integer.toString(Integer.valueOf(uniqueVinyls.get(uniqueVinyls.size() - 1).getId()) + 1));
                        UniqueVinyl uniqueVinyl = UniqueVinyl.builder().id(lastVinylId)
                                .release(rawOffers.get(0).getRelease())
                                .artist(rawOffers.get(0).getArtist())
                                .fullName(rawOffers.get(0).getRelease() + " - " + rawOffers.get(0).getArtist())
                                .imageLink(rawOffers.get(0).getImageLink())
                                .build();
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
        int minMatchingRate = 75;
        String uniqueVinylRelease = uniqueVinyl.getRelease();
        char lastCharInRawRelease = uniqueVinylRelease.charAt(uniqueVinylRelease.length() - 1);
        if (lastCharInRawRelease > '0' && lastCharInRawRelease < '9') {
            minMatchingRate = 90;
        }
        String[] preparedFullNameForMatching = Arrays
                .stream(uniqueVinyl.getFullName().split("[- ()!@$%^&*_={}:;\"']"))
                .filter(e -> e.trim().length() > 0)
                .toArray(String[]::new);
        String preparedVinylRelease = getParametersForComparison(uniqueVinyl.getRelease());
        String preparedVinylArtist = getParametersForComparison(uniqueVinyl.getArtist());
        Iterator<RawOffer> rawOfferIterator = rawOffers.iterator();
        uniqueVinyl.setHasOffers(false);
        while (rawOfferIterator.hasNext()) {
            RawOffer rawOffer = rawOfferIterator.next();
            String rawOfferFullName = rawOffer.getArtist() + " - " + rawOffer.getRelease();
            int currentMatchingNumber = getMatchingNumber(preparedFullNameForMatching, rawOfferFullName);
            if (isEqualByArtistAndRelease(preparedVinylRelease, preparedVinylArtist, rawOffer) &&
                    getMatchingRate(currentMatchingNumber, preparedFullNameForMatching.length) > minMatchingRate) {
                Offer offer = new Offer();
                offer.setUniqueVinylId(uniqueVinyl.getId());
                offer.setShopId(rawOffer.getShopId());
                offer.setPrice(rawOffer.getPrice());
                offer.setCurrency(rawOffer.getCurrency());
                offer.setGenre(rawOffer.getGenre());
                offer.setCatNumber(rawOffer.getCatNumber());
                offer.setInStock(rawOffer.isInStock());
                offer.setOfferLink(rawOffer.getOfferLink());
                offers.add(offer);
                uniqueVinyl.setHasOffers(true);
                log.debug("Added new offer {'offer':{}}", offer);
                rawOfferIterator.remove();
            }
        }
    }

    boolean isEqualByArtistAndRelease(String vinylRelease, String vinylArtist, RawOffer rawOffer) {
        return (Objects.equals(vinylRelease, getParametersForComparison(rawOffer.getRelease())) &&
                Objects.equals(vinylArtist, getParametersForComparison(rawOffer.getArtist())));
    }

    float getMatchingRate(int currentMatching, int total) {
        if (total == 0) {
            return 0;
        }
        return ((float) currentMatching) / total * 100;
    }

    int getMatchingNumber(String[] preparedFullNameForMatching, String rawOfferFullName) {
        return (int) Arrays.stream(preparedFullNameForMatching)
                .map(preparedItem -> preparedItem.toLowerCase().trim())
                .filter(preparedItem -> rawOfferFullName.toLowerCase().contains(preparedItem))
                .count();
    }

    String getParametersForComparison(String param) {
        if (param == null) {
            return "";
        }
        String[] paramArray = param.split(" ");
        log.debug("Split param into param array {'param':{}, 'paramArray':{}}", param, paramArray);
        if (paramArray.length > 1 && (paramArray[0].equalsIgnoreCase("the") || paramArray[0].equalsIgnoreCase("a"))) {
            paramArray[0] = paramArray[1];
        }
        log.debug("Resulting string is {'resultParam':{}}", paramArray[0]);
        return paramArray[0].toLowerCase();
    }

}