package com.vinylteam.vinyl.dao.elasticsearch;

import com.vinylteam.vinyl.entity.UniqueVinyl;

import java.util.List;

public interface CustomUniqueVinylRepository {

    List<UniqueVinyl> findByFilter(String matcher);

    List<UniqueVinyl> findRandom(int amount);

}
