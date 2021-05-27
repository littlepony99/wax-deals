package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.Shop;

import java.util.List;

public interface ShopService {

    List<Shop> getManyByListOfIds(List<Integer> ids);

    List<Shop> findAll();

}
