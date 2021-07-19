package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import java.util.List;

public interface UniqueVinylRepositoryCustom {

    List<UniqueVinyl> findManyFiltered(String matcher);

    List<UniqueVinyl> findManyRandom(int amount);

}
