package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.Offer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface OfferRepository extends ElasticsearchRepository<Offer, String> {

    List<Offer> findByUniqueVinylId(String uniqueVinylId);

}
