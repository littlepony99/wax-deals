package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.ShopDao;
import com.vinylteam.vinyl.entity.Shop;
import com.vinylteam.vinyl.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultShopService implements ShopService {

    private final ShopDao shopDao;

    public List<Shop> getManyByListOfIds(List<Integer> ids) {
        List<Shop> shops;
        if (ids != null) {
            shops = shopDao.getManyByListOfIds(ids);
        } else {
            log.error("List of ids is null, returning empty list.");
            shops = new ArrayList<>();
        }
        log.debug("Resulting list of shops is {'shops':{}}", shops);
        return shops;
    }

    @Override
    public List<Shop> findAll() {
        List<Shop> shops = shopDao.findAll();
        log.debug("Resulting list of shops is {'shops':{}}", shops);
        return shops;
    }

}
