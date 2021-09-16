package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.JwtUser;
import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchResultsController {

    private final UniqueVinylService vinylService;
    private final UniqueVinylMapper uniqueVinylMapper;
    private final WantListService wantListService;

    @GetMapping
    public List<UniqueVinylDto> getSearchResults(@RequestParam(value = "matcher") String matcher,
                                                 @AuthenticationPrincipal JwtUser authUser) {
        List<UniqueVinyl> filteredUniqueVinyls = vinylService.findByFilter(matcher);
        if (!Objects.isNull(authUser)) {
            List<UniqueVinylDto> uniqueVinylDtos = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(filteredUniqueVinyls);
            return wantListService.mergeSearchResult(authUser.getId(), uniqueVinylDtos);
        }
        return uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(filteredUniqueVinyls);
    }

}
