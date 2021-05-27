package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.List;

public interface OfferService {

    List<Offer> findManyByUniqueVinylId(long uniqueVinylId);

    void updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers);

    List<Integer> getListOfShopIds(List<Offer> offers);

}
