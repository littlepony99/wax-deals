package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Offer;
import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.List;

public interface OfferDao {

    List<Offer> findManyByUniqueVinylId(long uniqueVinylId);

    List<Offer> updateUniqueVinylsRewriteAll(List<UniqueVinyl> uniqueVinyls, List<Offer> offers);

}
