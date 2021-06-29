package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUniqueVinylService implements UniqueVinylService {


    @Override
    public List<UniqueVinyl> findAll() {
        return null;
    }

    @Override
    public UniqueVinyl findById(long id) {
        return null;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        return null;
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        return null;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        return null;
    }

    @Override
    public UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl) {
        return null;
    }

    @Override
    public void prepareCatalog(User user, Model model, String wantList) {

    }
}
