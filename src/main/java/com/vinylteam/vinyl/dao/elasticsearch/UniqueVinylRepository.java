package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UniqueVinylRepository
        extends ElasticsearchRepository<UniqueVinyl, String> {

    List<UniqueVinyl> findAll();

    List<UniqueVinyl> findByArtistIgnoreCaseAndHasOffers(String artist, boolean hasOffer);

    List<UniqueVinyl> findByFullNameIgnoreCaseContainingAndHasOffers(String fullName, boolean hasOffer);
}