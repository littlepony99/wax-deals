package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import org.springframework.ui.Model;

import java.util.List;

public interface UniqueVinylService {

    List<UniqueVinyl> findAll();

    UniqueVinyl findById(long id);

    List<UniqueVinyl> findManyRandom(int amount);

    List<UniqueVinyl> findManyFiltered(String matcher);

    List<UniqueVinyl> findManyByArtist(String artist);

    UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl);

    void prepareCatalog(User user, Model model, String wantList);

}
