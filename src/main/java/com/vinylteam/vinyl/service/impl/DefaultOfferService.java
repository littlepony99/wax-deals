package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.OfferDao;
import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.service.OfferService;
import com.vinylteam.vinyl.util.impl.VinylParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultOfferService implements OfferService {

    private final OfferDao offerDao;

    @Override
    public List<Offer> findManyByUniqueVinylId(long uniqueVinylId) {
        List<Offer> offers;
        if (uniqueVinylId > 0) {
            offers = offerDao.findManyByUniqueVinylId(uniqueVinylId);
        } else {
            IllegalArgumentException e = new IllegalArgumentException();
            log.error("uniqueVinylId is 0 or less {'uniqueVinylId':{}}", uniqueVinylId, e);
            throw new RuntimeException(e);
        }
        log.debug("Resulting list of vinyls is {'vinyls':{}}", offers);
        return offers;
    }

    @Override
    public void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers) {

    }

    @Override
    public List<Integer> getListOfShopIds(List<Offer> offers) {
        List<Integer> shopsIds = new ArrayList<>();
        if (offers != null) {
            for (Offer offer : offers) {
                if (!shopsIds.contains(offer.getShopId())) {
                    shopsIds.add(offer.getShopId());
                }
            }
        } else {
            log.error("List of offers is null, returning empty list.");
        }
        log.debug("Resulting list of shop id-s is {'shopIds':{}}", shopsIds);
        return shopsIds;
    }

    public void mergeOfferChanges(Offer offer, VinylParser shopParser, RawOffer dynamicOffer) {
        if (shopParser.isValid(dynamicOffer)) {
            var actualPrice = dynamicOffer.getPrice();
            var actualCurrency = dynamicOffer.getCurrency();
            offer.setInStock(dynamicOffer.isInStock());
            offer.setCurrency(actualCurrency);
            offer.setPrice(actualPrice);
        } else {
            offer.setInStock(false);
        }
    }

}
