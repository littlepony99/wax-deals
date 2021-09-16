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
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private final UniqueVinylService uniqueVinylService;
    private final UniqueVinylMapper uniqueVinylMapper;
    private final WantListService wantListService;

    @GetMapping
    public List<UniqueVinylDto> getCatalogPage(HttpServletRequest request) {
        List<UniqueVinyl> uniqueVinyls = uniqueVinylService.findRandom(50);
        List<UniqueVinylDto> uniqueVinylDtos = uniqueVinylMapper.uniqueVinylsToUniqueVinylDtoList(uniqueVinyls);

        if (null != request.getAttribute("userEntity")) {
            User user = (User) request.getAttribute("userEntity");
            Long userId = null;
            if (null != user) {
                uniqueVinylDtos = wantListService.mergeSearchResult(userId, uniqueVinylDtos);
            }
        }
        return uniqueVinylDtos;
    }

}
