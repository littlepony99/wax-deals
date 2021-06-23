package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UniqueVinylRepository
        extends ElasticsearchRepository<UniqueVinyl, String> {

    List<UniqueVinyl> findAll();

    Page<UniqueVinyl> findByHasOffers(boolean hasOffers, PageRequest pageRequest);

    List<UniqueVinyl> findByArtistIgnoreCaseAndHasOffers(String artist, boolean hasOffer);

}