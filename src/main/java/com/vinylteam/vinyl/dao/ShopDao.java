package com.vinylteam.vinyl.dao;

import com.vinylteam.vinyl.entity.Shop;

import java.util.List;

public interface ShopDao {

    List<Shop> findByListOfIds(List<Integer> ids);

    List<Shop> findAll();

}
