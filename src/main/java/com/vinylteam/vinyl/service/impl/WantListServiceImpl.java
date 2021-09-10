package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.WantListRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WantListServiceImpl implements WantListService {

    private final DiscogsService discogsService;
    private final UniqueVinylService uniqueVinylService;
    private final WantListRepository wantListRepository;

    @Override
    public List<UniqueVinylDto> mergeSearchResult(User user, List<UniqueVinylDto> foundVinyls) {
        List<WantedVinyl> wantList = getWantList(user.getId());
        if (wantList != null && !wantList.isEmpty()) {
            for (UniqueVinylDto foundVinyl : foundVinyls) {
                for (WantedVinyl wantedVinyl : wantList) {
                    boolean isInWantList = foundVinyl.getId().equals(wantedVinyl.getVinylId());
                    foundVinyl.setIsWantListItem(isInWantList);
                }
            }
        }
        return foundVinyls;
    }

    @Override
    public WantedVinyl addWantedVinyl(User user, UniqueVinylDto vinylDto) {
        WantedVinyl wantedVinyl = WantedVinyl.builder()
                .userId(user.getId())
                .addedAt(Date.from(Instant.now()))
                .vinylId(vinylDto.getId())
                .release(vinylDto.getRelease())
                .artist(vinylDto.getArtist())
                .imageLink(vinylDto.getImageLink())
                .build();
        return wantListRepository.save(wantedVinyl);
    }

    @Override
    public void importWantList(User user) {
        if (user.getDiscogsUserName() != null) {
            List<UniqueVinyl> allVinyls = uniqueVinylService.findAll();
            List<UniqueVinyl> discogsMatchList = discogsService.getDiscogsMatchList(user.getDiscogsUserName(), allVinyls);
            saveImportedWantList(user.getId(), discogsMatchList);
        }
    }

    private void saveImportedWantList(Long id, List<UniqueVinyl> wantList) {
        for (UniqueVinyl wantedVinyl : wantList) {
            wantListRepository.save(WantedVinyl.builder()
                    .userId(id)
                    .addedAt(Date.from(Instant.now()))
                    .vinylId(wantedVinyl.getId())
                    .release(wantedVinyl.getRelease())
                    .artist(wantedVinyl.getArtist())
                    .imageLink(wantedVinyl.getImageLink())
                    .build());
        }
    }

    @Override
    public List<WantedVinyl> getWantList(Long userId) {
        return wantListRepository.findAllByUserId(userId);
    }

}
