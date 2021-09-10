package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.WantedVinyl;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface WantListRepository extends ElasticsearchRepository<WantedVinyl, String> {

    List<WantedVinyl> findAllByUserId(Long userId);

}
