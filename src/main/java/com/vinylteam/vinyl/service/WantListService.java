package com.vinylteam.vinyl.service;

import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.exception.DiscogsBadRequestException;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.NotFoundException;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;

import java.util.List;

public interface WantListService {

    List<UniqueVinylDto> mergeVinylsWithWantList(Long userId, List<UniqueVinylDto> foundVinyls);

    WantedVinyl addWantedVinyl(User user, UniqueVinylDto vinylDto) throws ForbiddenException, NotFoundException;

    void importWantList(User user) throws DiscogsBadRequestException;

    List<WantedVinyl> getWantList(Long userId);

    List<UniqueVinylDto> getWantListUniqueVinyls(Long userId);
}
