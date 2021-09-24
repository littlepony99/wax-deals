package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.UniqueVinylRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.exception.entity.CatalogErrors;
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
    public void updateOneUniqueVinyl(UniqueVinyl vinyl) {
        if (vinyl.hasOffers()) {
            return;
        }
        uniqueVinylRepository.save(vinyl);
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
    public List<UniqueVinyl> findRandom(int amount) {
        if (amount <= 0) {
            return new ArrayList<>();
        }
        return uniqueVinylRepository.findRandom(amount);
    }

    @Override
    public List<UniqueVinyl> findByFilter(String matcher) {
        if (matcher == null || matcher.isEmpty()) {
            log.error("Matcher is null, returning empty list.");
            return new ArrayList<>();
        }
        var result = uniqueVinylRepository.findByFilter(matcher);
        if (result.isEmpty()) {
            result = uniqueVinylRepository.findByFullNameIgnoreCaseContainingAndHasOffers(matcher, true);
        }
        return result;
    }

    @Override
    public List<UniqueVinyl> findByArtist(String artist) {
        if (artist == null || artist.isEmpty()) {
            log.error("Artist is null, returning empty list.");
            return new ArrayList<>();
        }
        return uniqueVinylRepository.findByArtist(artist);
    }

    @Override
    public UniqueVinyl findById(String id) throws NotFoundException {
        if (id == null) {
            log.error("Id is null");
            throw new IllegalArgumentException("Incorrect id value " + id);
        }
        UniqueVinyl uniqueVinyl = uniqueVinylRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(CatalogErrors.VINYL_BY_ID_NOT_FOUND.getMessage()));
        log.debug("Resulting uniqueVinyl is {'uniqueVinyl':{}}", uniqueVinyl);
        return uniqueVinyl;
    }

}
