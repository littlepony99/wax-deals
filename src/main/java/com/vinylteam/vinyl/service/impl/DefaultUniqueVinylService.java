package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.jdbc.JdbcUniqueVinylDao;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultUniqueVinylService implements UniqueVinylService {

    @Autowired
    private JdbcUniqueVinylDao uniqueVinylDao;


    @Override
    public List<UniqueVinyl> findAll() {
        return uniqueVinylDao.findAll();
    }

    @Override
    public UniqueVinyl findById(long id) {
        return uniqueVinylDao.findById(id);
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        return null;
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        return uniqueVinylDao.findManyFiltered(matcher);
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        List<UniqueVinyl> gottenUniqueVinyls;
        if (artist != null) {
            gottenUniqueVinyls = uniqueVinylDao.findManyByArtist(artist);
        } else {
            log.error("Artist is null, returning empty list.");
            gottenUniqueVinyls = new ArrayList<>();
        }
        log.debug("Resulting list of random unique vinyls is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl) {
        return null;
    }

    @Override
    public void prepareCatalog(User user, Model model, String wantList) {

    }
}
