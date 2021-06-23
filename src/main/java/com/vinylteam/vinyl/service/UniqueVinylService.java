package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.List;

public interface UniqueVinylService {

    List<UniqueVinyl> findAll();

    UniqueVinyl findById(String id);

    List<UniqueVinyl> findManyRandom(int amount);

    List<UniqueVinyl> findManyFiltered(String matcher);

    List<UniqueVinyl> findManyByArtist(String artist);

    UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl);

}
