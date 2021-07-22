package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UniqueVinylRepository
        extends ElasticsearchRepository<UniqueVinyl, String>, CustomUniqueVinylRepository {

    List<UniqueVinyl> findAll();

    default List<UniqueVinyl> findByArtist(String artist){
        return findByArtistIgnoreCaseAndHasOffers(artist, true);
    }

    List<UniqueVinyl> findByArtistIgnoreCaseAndHasOffers(String artist, boolean hasOffer);

    List<UniqueVinyl> findByFullNameIgnoreCaseContainingAndHasOffers(String fullName, boolean hasOffer);

}