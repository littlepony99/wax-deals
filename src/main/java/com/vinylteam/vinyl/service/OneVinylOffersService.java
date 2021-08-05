package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.HashMap;
import java.util.List;

public interface OneVinylOffersService {

    HashMap<String, List> getSortedInStockOffersAndShops(String identifier);

    List<UniqueVinyl> addAuthorVinyls(UniqueVinyl uniqueVinyl);

    String getDiscogsLink(UniqueVinyl uniqueVinyl);

}
