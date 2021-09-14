package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.dao.elasticsearch.WantListRepository;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.entity.WantedVinyl;
import com.vinylteam.vinyl.exception.ForbiddenException;
import com.vinylteam.vinyl.exception.entity.VinylErrors;
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
    public WantedVinyl addWantedVinyl(User user, UniqueVinylDto vinylDto) throws ForbiddenException {
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
        throw new ForbiddenException(VinylErrors.NOT_FOUND_ERROR.getMessage());
    }

    @Override
    public void importWantList(User user) {
        log.info("WantList import. Start task, userId={}", user.getId());
        if (user.getDiscogsUserName() != null) {
            List<UniqueVinyl> allVinyls = uniqueVinylService.findAll();
            List<UniqueVinyl> discogsMatchList = discogsService.getDiscogsMatchList(user.getDiscogsUserName(), allVinyls);
            log.info("WantList import. Matches {} items with our unique vinyls", discogsMatchList.size());
            saveImportedWantList(user.getId(), discogsMatchList);
        }
    }

    private void saveImportedWantList(Long userId, List<UniqueVinyl> wantList) {
        int counter = 0;
        for (UniqueVinyl uniqueVinyl : wantList) {
            Optional<WantedVinyl> existingItem = wantListRepository.findByVinylIdAndUserId(uniqueVinyl.getId(), userId);
            if (existingItem.isEmpty()) {
                wantListRepository.save(WantedVinyl.builder()
                        .userId(userId)
                        .addedAt(Date.from(Instant.now()))
                        .vinylId(uniqueVinyl.getId())
                        .release(uniqueVinyl.getRelease())
                        .artist(uniqueVinyl.getArtist())
                        .imageLink(uniqueVinyl.getImageLink())
                        .build());
                counter++;
            }
        }
        log.info("WantList import. Added {} new items ", counter);
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
