package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.WantListRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.service.DiscogsService;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WantListServiceImpl implements WantListService {

    private final DiscogsService discogsService;
    private final UniqueVinylService uniqueVinylService;
    private final WantListRepository wantListRepository;
    private final UniqueVinylMapper uniqueVinylMapper;

    @Override
    public List<UniqueVinylDto> mergeSearchResult(Long userId, List<UniqueVinylDto> foundVinyls) {
        List<WantedVinyl> wantList = getWantList(userId);
        if (wantList != null && !wantList.isEmpty()) {
            for (UniqueVinylDto foundVinyl : foundVinyls) {
                for (WantedVinyl wantedVinyl : wantList) {
                    boolean isInWantList = foundVinyl.getId().equals(wantedVinyl.getVinylId());
                    foundVinyl.setIsWantListItem(isInWantList);
                }
            }
        } else {
            for (UniqueVinylDto foundVinyl : foundVinyls) {
                foundVinyl.setIsWantListItem(Boolean.FALSE);
            }
        }
        return foundVinyls;
    }

    @Override
    public WantedVinyl addWantedVinyl(User user, UniqueVinylDto vinylDto) {
        Optional<WantedVinyl> existingWantListItem = wantListRepository.findByVinylIdAndUserId(vinylDto.getId(), user.getId());
        if (existingWantListItem.isPresent()) {
            wantListRepository.deleteById(existingWantListItem.get().getId());
            return WantedVinyl.builder().build();
        }
        UniqueVinyl existingVinyl = uniqueVinylService.findById(vinylDto.getId());

        if (null != existingVinyl) {
            WantedVinyl wantedVinyl = WantedVinyl.builder()
                    .userId(user.getId())
                    .addedAt(Date.from(Instant.now()))
                    .vinylId(vinylDto.getId())
                    .release(existingVinyl.getRelease())
                    .artist(existingVinyl.getArtist())
                    .imageLink(existingVinyl.getImageLink())
                    .build();
            return wantListRepository.save(wantedVinyl);
        }
        log.error("Can't find vinyl with id={}", vinylDto.getId());
        // TODO add not found vinyl exception
        return null;
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

    @Override
    public List<UniqueVinylDto> getWantListUniqueVinyls(Long userId) {
        List<WantedVinyl> wantList = getWantList(userId);
        List<UniqueVinylDto> result = uniqueVinylMapper.wantedVinylsToUniqueVinylDtoList(wantList);
        for (UniqueVinylDto uniqueVinylDto : result) {
            uniqueVinylDto.setIsWantListItem(Boolean.TRUE);
        }
        return result;
    }

}
