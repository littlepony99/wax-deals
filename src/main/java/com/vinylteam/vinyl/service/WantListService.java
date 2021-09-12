package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;

import java.util.List;

public interface WantListService {

    List<UniqueVinylDto> mergeSearchResult(Long userId, List<UniqueVinylDto> foundVinyls);

    WantedVinyl addWantedVinyl(User user, UniqueVinylDto vinylDto);

    void importWantList(User user);

    List<WantedVinyl> getWantList(Long userId);

    List<UniqueVinylDto> getWantListUniqueVinyls(Long userId);
}
