package com.vinylteam.vinyl.web.controller;

import com.vinylteam.vinyl.entity.UniqueVinyl;
import com.vinylteam.vinyl.entity.User;
import com.vinylteam.vinyl.service.UniqueVinylService;
import com.vinylteam.vinyl.service.WantListService;
import com.vinylteam.vinyl.util.impl.UniqueVinylMapper;
import com.vinylteam.vinyl.web.dto.UniqueVinylDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
                                                 HttpServletRequest request) {
        List<UniqueVinyl> filteredUniqueVinyls = vinylService.findByFilter(matcher);

        if (null != request.getAttribute("userEntity")) {
            User user = (User) request.getAttribute("userEntity");
            List<UniqueVinylDto> uniqueVinylDtos = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(filteredUniqueVinyls);
            Long userId = null;
            if (null != user) {
                userId = user.getId();
            }
            return wantListService.mergeSearchResult(userId, uniqueVinylDtos);
        }
        return uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(filteredUniqueVinyls);
    }

}
