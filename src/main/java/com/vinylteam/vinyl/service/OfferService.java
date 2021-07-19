package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.RawOffer;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.util.impl.VinylParser;

import java.util.List;

public interface OfferService {

    List<Offer> findManyByUniqueVinylId(String uniqueVinylId);

    void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers);

    List<Integer> getListOfShopIds(List<Offer> offers);

    Offer mergeOfferChanges(Offer offer, VinylParser shopParser, RawOffer dynamicOffer);

    Offer getActualizedOffer(Offer dbOffer);

}
