package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
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

    private final UniqueVinylRepository uniqueVinylRepository;

    @Override
    public UniqueVinyl updateOneUniqueVinylAsHavingNoOffer(UniqueVinyl vinyl) {
        if (vinyl.isHasOffers()) {
            return vinyl;
        }
        return uniqueVinylRepository.save(vinyl);
    }

    @Override
    public void prepareCatalog(User user, Model model, String wantList) {

    }

    @Override
    public List<UniqueVinyl> findAll() {
        List<UniqueVinyl> gottenUniqueVinyls = new ArrayList<>(uniqueVinylRepository.findAll());
        log.debug("Resulting list with that amount of unique vinyls from db is {'uniqueVinyls':{}}", gottenUniqueVinyls);
        return gottenUniqueVinyls;
    }

    @Override
    public List<UniqueVinyl> findManyRandom(int amount) {
        if (amount <= 0) {
            return new ArrayList<>();
        }
        return uniqueVinylRepository.findManyRandom(amount);
    }

    @Override
    public List<UniqueVinyl> findManyFiltered(String matcher) {
        if (matcher == null || matcher.isEmpty()) {
            log.error("Matcher is null, returning empty list.");
            return new ArrayList<>();
        }
        var result = uniqueVinylRepository.findManyFiltered(matcher);
        if (result.isEmpty()) {
            result = uniqueVinylRepository.findByFullNameIgnoreCaseContainingAndHasOffers(matcher, true);
        }
        return result;
    }

    @Override
    public List<UniqueVinyl> findManyByArtist(String artist) {
        if (artist == null || artist.isEmpty()) {
            log.error("Artist is null, returning empty list.");
            return new ArrayList<>();
        }
        return uniqueVinylRepository.findByArtist(artist);
    }

    @Override
    public UniqueVinyl findById(String id) {
        if (id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Incorrect id value " + id);
        }
        UniqueVinyl uniqueVinyl = uniqueVinylRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No uniqueVinyl with that id in table {'id':{" + id + "}}"));
        log.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", uniqueVinyl);
        return uniqueVinyl;
    }

}
