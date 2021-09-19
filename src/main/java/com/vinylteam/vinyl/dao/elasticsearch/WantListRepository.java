package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.WantedVinyl;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface WantListRepository extends ElasticsearchRepository<WantedVinyl, String> {

    List<WantedVinyl> findAllByUserId(Long userId);

    Optional<WantedVinyl> findByVinylIdAndUserId(String id, Long userId);

}
