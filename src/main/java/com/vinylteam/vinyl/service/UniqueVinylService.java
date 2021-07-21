package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import org.springframework.ui.Model;

import java.util.List;

public interface UniqueVinylService {

    List<UniqueVinyl> findAll();

    UniqueVinyl findById(String id);

    List<UniqueVinyl> findRandom(int amount);

    List<UniqueVinyl> findByFilter(String matcher);

    List<UniqueVinyl> findByArtist(String artist);

    void updateOneUniqueVinyl(UniqueVinyl vinyl);

    void prepareCatalog(User user, Model model, String wantList);

}
