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
            int percentMatching = 75;
            String uniqueVinylRelease = uniqueVinyl.getRelease();
            char lastCharInRawRelease = uniqueVinylRelease.charAt(uniqueVinylRelease.length() - 1);
            if (lastCharInRawRelease > '0' && lastCharInRawRelease < '9'){
                percentMatching = 90;
            }
            String[] preparedFullNameForMatching = Arrays.stream(uniqueVinyl.getFullName().split("[- ()!@$%^&*_={}:;\"']")).filter(e -> e.trim().length() > 0).toArray(String[]::new);
            Iterator<RawOffer> rawOfferIterator = rawOffers.iterator();
            while (rawOfferIterator.hasNext()) {

                RawOffer rawOffer = rawOfferIterator.next();
                String rawOfferFullName = rawOffer.getArtist() + " - " + rawOffer.getRelease();
                int currentMatching = 0;
                for (String prepareItem : preparedFullNameForMatching) {
                    if (rawOfferFullName.toLowerCase().contains(prepareItem.toLowerCase())){
                        currentMatching++;
                    }
                }
                if (Objects.equals(getParametersForComparison(uniqueVinyl.getRelease()), getParametersForComparison(rawOffer.getRelease())) &&
                        Objects.equals(getParametersForComparison(uniqueVinyl.getArtist()), getParametersForComparison(rawOffer.getArtist()))){
                    if (((float) currentMatching)/preparedFullNameForMatching.length*100 > percentMatching){
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
        } else {
            RuntimeException e = new NullPointerException();
            log.error("At least one of passed arguments is null {'rawOffers':{}, 'uniqueVinyl':{}, 'offers':{}}",
                    rawOffers, uniqueVinyl, offers, e);
            throw e;
        }
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